package com.example.finder.service;

import com.example.finder.config.PaginationConfig;
import com.example.finder.dto.input.RequestDiscussion;
import com.example.finder.dto.input.RequestMessage;
import com.example.finder.dto.input.RequestRecordStatus;
import com.example.finder.dto.output.AdminDetailedDiscussionDTO;
import com.example.finder.dto.output.AdminDiscussionDTO;
import com.example.finder.dto.output.AnnounceDto;
import com.example.finder.dto.output.DetailedDiscussionDTO;
import com.example.finder.dto.output.ErrorDto;
import com.example.finder.exception.action.InvalidRequestException;
import com.example.finder.exception.action.UnauthorizedException;
import com.example.finder.model.*;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.model.enums.AvailableInteractivityState;
import com.example.finder.model.enums.AvailableRecordStatus;
import com.example.finder.repository.*;
import com.example.finder.repository.specification.AnnounceSpecifications;
import com.example.finder.repository.specification.DiscussionSpecifications;
import com.example.finder.response.ApiResponseFactory;
import com.example.finder.response.PaginatedResponse;
import com.example.finder.response.enums.DiscussionError;
import com.example.finder.utils.ImageUtil;
import com.example.finder.utils.SanitizerUtil;
import com.example.finder.utils.validator.ValidatorAuth;
import com.example.finder.utils.validator.ValidatorDiscussionMessage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

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
    private final ImageUtil imageUtil;
    private final PaginationConfig paginationConfig;

    public DiscussionService(
            AnnounceRepository announceRepository,
            DiscussionRepository discussionRepository,
            MessageRepository messageRepository,
            InteractivityStateRepository interactivityStateRepository,
            RecordStatusRepository recordStatusRepository,
            SanitizerUtil sanitizerUtil,
            ValidatorAuth validatorAuth,
            ValidatorDiscussionMessage validatorDiscussionMessage,
            ImageUtil imageUtil,
            PaginationConfig paginationConfig) {
        this.discussionRepository = discussionRepository;
        this.announceRepository = announceRepository;
        this.messageRepository = messageRepository;
        this.interactivityStateRepository = interactivityStateRepository;
        this.recordStatusRepository = recordStatusRepository;
        this.sanitizerUtil = sanitizerUtil;
        this.validatorAuth = validatorAuth;
        this.validatorDiscussionMessage = validatorDiscussionMessage;
        this.imageUtil = imageUtil;
        this.paginationConfig = paginationConfig;
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

    /**
     * get page of discussions depending on given arguments and returns the data in
     * a
     * ApiResponse
     * 
     * @param page           page to get
     * @param size           amount of discussions per page
     * @param mustShowHidden false only get discussions with a record status of
     *                       Shown,
     *                       true doesn't filter based on such status (intended for
     *                       admin board)
     * @return api response with data according the the given parameters
     */
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPaginatedDiscussions(
            int page,
            int size,
            Boolean mustShowHidden) {
        try {
            size = Math.min(size, paginationConfig.getMaxResultsPerPage());
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            List<Specification<Discussion>> specList = new ArrayList<>();
            if (!mustShowHidden) {
                specList.add(DiscussionSpecifications.hasShownStatus());
            }
            Specification<Discussion> spec = Specification.allOf(specList);

            Page<Discussion> discussionPage = discussionRepository.findAll(spec, pageable);
            Page<DetailedDiscussionDTO> discussionDtoPage = discussionPage.map((it) -> it.toDetailedDiscussionDTO());
            var paginatedResult = PaginatedResponse.from(discussionDtoPage);
            return ApiResponseFactory.success(paginatedResult);
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

    /**
     * get page of discussions depending on given arguments and returns the data in
     * a
     * ApiResponse
     * 
     * @param page           page to get
     * @param size           amount of discussions per page
     * @param mustShowHidden false only get discussions with a record status of
     *                       Shown,
     *                       true doesn't filter based on such status (intended for
     *                       admin board)
     * @return api response with data according the the given parameters
     */
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPaginatedDiscussionsForModeration(
            int page,
            int size) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            boolean isAdmin = requester.isAdmin();
            if (!isAdmin) {
                UnauthorizedException unauthorizedException = new UnauthorizedException();
                System.out.println(unauthorizedException.getMessage());
                return ApiResponseFactory.unauthorized(unauthorizedException.getMessage());
            }
            size = Math.min(size, paginationConfig.getMaxResultsPerPage());
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            Page<Discussion> discussionPage = discussionRepository.findAll(pageable);
            Page<AdminDiscussionDTO> discussionDtoPage = discussionPage.map((it) -> it.toAdminDiscussionDTO());
            var paginatedResult = PaginatedResponse.from(discussionDtoPage);
            return ApiResponseFactory.success(paginatedResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> getDiscussionForModeration(UUID uuid) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            boolean isAdmin = requester.isAdmin();
            if (!isAdmin) {
                UnauthorizedException unauthorizedException = new UnauthorizedException();
                System.out.println(unauthorizedException.getMessage());
                return ApiResponseFactory.unauthorized(unauthorizedException.getMessage());
            }

            Discussion discussion = discussionRepository.findById(uuid).orElse(null);
            if (discussion == null) {
                return ApiResponseFactory
                        .notFound("There is no discussion matching the request");
            }
            return ApiResponseFactory.success(discussion.toAdminDetailedDiscussionDTO());
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

    public ResponseEntity<?> getRelatedAnnounce(UUID discussionUuid, boolean withHiddenData) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            Specification<Announce> spec = Specification.allOf(
                    AnnounceSpecifications.hasDiscussion(discussionUuid));
            if (!withHiddenData) {
                spec = spec.and(AnnounceSpecifications.hasShownStatus());
            }
            Announce announce = announceRepository.findOne(spec).orElse(null);
            if (announce == null) {
                System.out.println("No announce found for discussion UUID: " + discussionUuid + "; withHiddenData: "
                        + withHiddenData);
                return ApiResponseFactory.notFound("There is no announce matching this discussion");
            }

            AnnounceDto announceDto = new AnnounceDto(
                    announce,
                    imageUtil.getBaseWebPathForPhotos());
            return ApiResponseFactory.success(announceDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> forceChangeInteractivityState(UUID uuid, AvailableInteractivityState interactivityState) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            if (!requester.isAdmin()) {
                return ApiResponseFactory.unauthorized("You are not authorized to perform this action");
            }
            Specification<Discussion> spec = Specification.allOf(
                    DiscussionSpecifications.hasId(uuid));

            Discussion discussion = discussionRepository.findOne(spec).orElse(null);
            if (discussion == null) {
                return ApiResponseFactory
                        .notFound("There is no discussion with id: " + uuid);
            }
            InteractivityState newInteractivityState = interactivityStateRepository
                    .findByName(interactivityState.getDisplayName())
                    .orElseThrow(() -> new InvalidRequestException(
                            "The provided interactivity state is invalid"));
            discussion.setInteractivityState(newInteractivityState);
            discussionRepository.save(discussion);
            return ApiResponseFactory.success(new AdminDetailedDiscussionDTO(discussion));
        } catch (InvalidRequestException e) {
            return ApiResponseFactory.badRequest(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseFactory.internalError();
        }
    };
}
