package com.example.finder.service;

import com.example.finder.dto.input.RequestDiscussion;
import com.example.finder.dto.input.RequestMessage;
import com.example.finder.dto.output.ErrorDto;
import com.example.finder.model.*;
import com.example.finder.repository.*;
import com.example.finder.repository.specification.AnnounceSpecifications;
import com.example.finder.repository.specification.DiscussionSpecifications;
import com.example.finder.response.ApiResponseFactory;
import com.example.finder.response.enums.DiscussionError;
import com.example.finder.utils.SanitizerUtil;
import com.example.finder.utils.validator.ValidatorAuth;
import com.example.finder.utils.validator.ValidatorDiscussionMessage;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class DiscussionService {
    private final DiscussionRepository discussionRepository;
    private final MessageRepository messageRepository;
    private final AnnounceRepository announceRepository;
    private final InteractivityStateRepository interactivityStateRepository;
    private final RecordStatusRepository recordStatusRepository;
    private final SanitizerUtil sanitizerUtil;
    private final ValidatorAuth validatorAuth;
    private final ValidatorDiscussionMessage validatorDiscussionMessage;

    public DiscussionService(
            AnnounceRepository announceRepository,
            DiscussionRepository discussionRepository,
            MessageRepository messageRepository,
            InteractivityStateRepository interactivityStateRepository,
            RecordStatusRepository recordStatusRepository,
            SanitizerUtil sanitizerUtil,
            ValidatorAuth validatorAuth,
            ValidatorDiscussionMessage validatorDiscussionMessage) {
        this.discussionRepository = discussionRepository;
        this.announceRepository = announceRepository;
        this.messageRepository = messageRepository;
        this.interactivityStateRepository = interactivityStateRepository;
        this.recordStatusRepository = recordStatusRepository;
        this.sanitizerUtil = sanitizerUtil;
        this.validatorAuth = validatorAuth;
        this.validatorDiscussionMessage = validatorDiscussionMessage;
    }

    @Transactional
    public ResponseEntity<?> createNewDiscussion(
            UUID uuid,
            RequestDiscussion request) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            Specification<Announce> spec = Specification.allOf(
                    AnnounceSpecifications.hasShownStatus(),
                    AnnounceSpecifications.hasId(uuid));
            Announce announce = announceRepository.findOne(spec).orElse(null);
            if (announce == null) {
                return ApiResponseFactory.notFound("no such announce exists");
            }
            boolean isAnnounceAuthorAlsoRequester = requester.getId() == announce.getAuthor().getId();
            if (isAnnounceAuthorAlsoRequester) {
                return ApiResponseFactory.badRequest(DiscussionError.AUTHOR_CANT_INITIATE_DISCUSSION.getErrorMessage());
            }

            Specification<Announce> existingDiscussionSpec = AnnounceSpecifications.hasDiscussionWithInterlocutor(uuid,
                    requester.getId());
            boolean discussionExists = announceRepository.exists(existingDiscussionSpec);
            if (discussionExists) {
                return ApiResponseFactory.badRequest(DiscussionError.DISCUSSION_ALREADY_OPEN.getErrorMessage());
            }

            RequestDiscussion sanitizedRequest = sanitizerUtil.sanitizeDiscussionInputs(request);
            List<ErrorDto> validationErrors = validatorDiscussionMessage.validateDiscussionMessage(sanitizedRequest);
            if (!validationErrors.isEmpty()) {
                return ApiResponseFactory.badRequest(
                        DiscussionError.INVALID_MESSAGE.getErrorMessage(),
                        validationErrors);
            }
            RecordStatus shownStatus = recordStatusRepository.getShownRecordStatusOrThrow();
            InteractivityState openState = interactivityStateRepository.getOpenInteractivityStateOrThrow();

            Discussion newDiscussion = new Discussion();
            newDiscussion.setAnnounce(announce);
            newDiscussion.setInterlocutor(requester);
            newDiscussion.setInteractivityState(openState);
            discussionRepository.save(newDiscussion);

            Message firstMessage = new Message();
            firstMessage.setAuthor(requester);
            firstMessage.setDiscussion(newDiscussion);
            firstMessage.setContent(sanitizedRequest.getMessage());
            firstMessage.setIndex(1);
            firstMessage.setRecordStatus(shownStatus);
            firstMessage.setReported(false);
            messageRepository.save(firstMessage);
            Discussion savedDiscussion = discussionRepository.findById(newDiscussion.getId())
                    .orElseThrow();
            return ApiResponseFactory.success(savedDiscussion.toDetailedDiscussionDTO());
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> getAnnounceDiscussions(
            UUID uuid,
            boolean withHiddenAnnounce) {
        try {

            AppUser requester = validatorAuth.getUserFromSecurityContext();
            List<Specification<Discussion>> specList = new ArrayList<>(
                    Arrays.asList(
                            DiscussionSpecifications.hasAnnounce(uuid),
                            DiscussionSpecifications.hasParticipant(requester.getId())));
            if (!withHiddenAnnounce) {
                specList.add(DiscussionSpecifications.mustHaveVisibleAnnounce());
            }
            Specification<Discussion> spec = Specification.allOf(specList);
            List<Discussion> discussions = discussionRepository.findAll(spec);

            return ApiResponseFactory.success(
                    discussions
                            .stream()
                            .map(Discussion::toDiscussionDTO)
                            .toList());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> getDiscussion(UUID uuid, boolean withHiddenAnnounce) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            Specification<Discussion> spec = Specification.allOf(
                    DiscussionSpecifications.hasId(uuid),
                    DiscussionSpecifications.hasParticipant(requester.getId()));
            if (!withHiddenAnnounce) {
                spec = spec.and(DiscussionSpecifications.mustHaveVisibleAnnounce());
            }

            Discussion discussion = discussionRepository.findOne(spec).orElse(null);
            if (discussion == null) {
                return ApiResponseFactory
                        .notFound("Either there is no such discussion or you are not a participant in it");
            }
            return ApiResponseFactory.success(discussion.toDetailedDiscussionDTO());

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseFactory.internalError();
        }
    };

    @Transactional
    public ResponseEntity<?> addMessageToDiscussion(
            UUID uuidDiscussion,
            RequestMessage request) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            Specification<Discussion> spec = Specification.allOf(
                    DiscussionSpecifications.hasId(uuidDiscussion),
                    DiscussionSpecifications.hasParticipant(requester.getId()),
                    DiscussionSpecifications.mustHaveVisibleAnnounce(),
                    DiscussionSpecifications.isOpen());
            Discussion discussion = discussionRepository.findOne(spec).orElse(null);
            if (discussion == null) {
                return ApiResponseFactory.notFound("no such ongoing discussion exists");
            }

            RequestDiscussion sanitizedRequest = sanitizerUtil.sanitizeMessageInputs(request);
            List<ErrorDto> validationErrors = validatorDiscussionMessage.validateDiscussionMessage(sanitizedRequest);
            if (!validationErrors.isEmpty()) {
                return ApiResponseFactory.badRequest(
                        DiscussionError.INVALID_MESSAGE.getErrorMessage(),
                        validationErrors);
            }

            RecordStatus shownStatus = recordStatusRepository.getShownRecordStatusOrThrow();
            int currentMaxIndex = messageRepository.findMaxIndexByDiscussionId(discussion.getId()).orElse(0);

            Message newMessage = new Message();
            newMessage.setAuthor(requester);
            newMessage.setDiscussion(discussion);
            newMessage.setContent(sanitizedRequest.getMessage());
            newMessage.setIndex(currentMaxIndex + 1);
            newMessage.setRecordStatus(shownStatus);
            newMessage.setReported(false);
            messageRepository.save(newMessage);

            return ApiResponseFactory.success("Message added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResponseFactory.internalError();
        }
    }

}
