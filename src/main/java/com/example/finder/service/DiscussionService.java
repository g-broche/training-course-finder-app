package com.example.finder.service;

import com.example.finder.config.PaginationConfig;
import com.example.finder.dto.input.RequestAnnounce;
import com.example.finder.dto.input.RequestDiscussion;
import com.example.finder.dto.output.AnnounceDto;
import com.example.finder.dto.output.ErrorDto;
import com.example.finder.exception.action.InvalidRequestException;
import com.example.finder.exception.entity.CategoryNotFoundException;
import com.example.finder.exception.entity.UserNotFoundException;
import com.example.finder.exception.file.FileException;
import com.example.finder.model.*;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.repository.*;
import com.example.finder.repository.specification.AnnounceSpecifications;
import com.example.finder.response.ApiResponseFactory;
import com.example.finder.response.PaginatedResponse;
import com.example.finder.response.enums.AnnounceError;
import com.example.finder.response.enums.DiscussionError;
import com.example.finder.utils.ImageUtil;
import com.example.finder.utils.SanitizerUtil;
import com.example.finder.utils.StringUtil;
import com.example.finder.utils.logger.Printer;
import com.example.finder.utils.validator.ValidatorAnnounce;
import com.example.finder.utils.validator.ValidatorAuth;
import com.example.finder.utils.validator.ValidatorDiscussionMessage;
import com.example.finder.utils.validator.ValidatorImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class DiscussionService {
    private final DiscussionRepository discussionRepository;
    private final MessageRepository messageRepository;
    private final AnnounceRepository announceRepository;
    private final AnnounceTypeRepository announceTypeRepository;
    private final AnnounceStatusRepository announceStatusRepository;
    private final CategoryRepository categoryRepository;
    private final InteractivityStateRepository interactivityStateRepository;
    private final RecordStatusRepository recordStatusRepository;
    private final SanitizerUtil sanitizerUtil;
    private final ValidatorAuth validatorAuth;
    private final ValidatorDiscussionMessage validatorDiscussionMessage;
    private final ValidatorImage validatorImage;
    private final ImageUtil imageUtil;
    private final PaginationConfig paginationConfig;

    public DiscussionService(
            AnnounceRepository announceRepository,
            DiscussionRepository discussionRepository,
            MessageRepository messageRepository,
            AnnounceTypeRepository announceTypeRepository,
            AnnounceStatusRepository announceStatusRepository,
            CategoryRepository categoryRepository,
            InteractivityStateRepository interactivityStateRepository,
            RecordStatusRepository recordStatusRepository,
            SanitizerUtil sanitizerUtil,
            ValidatorAuth validatorAuth,
            ValidatorDiscussionMessage validatorDiscussionMessage,
            ValidatorImage validatorImage,
            ImageUtil imageUtil,
            PaginationConfig paginationConfig
    ) {
        this.discussionRepository = discussionRepository;
        this.announceRepository = announceRepository;
        this.messageRepository = messageRepository;
        this.announceTypeRepository = announceTypeRepository;
        this.announceStatusRepository = announceStatusRepository;
        this.categoryRepository = categoryRepository;
        this.interactivityStateRepository = interactivityStateRepository;
        this.recordStatusRepository = recordStatusRepository;
        this.sanitizerUtil = sanitizerUtil;
        this.validatorAuth = validatorAuth;
        this.validatorDiscussionMessage = validatorDiscussionMessage;
        this.validatorImage = validatorImage;
        this.imageUtil = imageUtil;
        this.paginationConfig = paginationConfig;
    }

    @Transactional
    public ResponseEntity<?> createNewDiscussion(
            UUID uuid,
            RequestDiscussion request
    ) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            Specification<Announce> spec = Specification.allOf(
                    AnnounceSpecifications.hasShownStatus(),
                    AnnounceSpecifications.hasId(uuid)
            );
            Announce announce = announceRepository.findOne(spec).orElse(null);
            if (announce == null) {
                return ApiResponseFactory.notFound("no such announce exists");
            }
            boolean isAnnounceAuthorAlsoRequester = requester.getId() == announce.getAuthor().getId();
            if(isAnnounceAuthorAlsoRequester){
                return ApiResponseFactory.badRequest(DiscussionError.AUTHOR_CANT_INITIATE_DISCUSSION.getErrorMessage());
            }

            RequestDiscussion sanitizedRequest = sanitizerUtil.sanitizeDiscussionInputs(request);
            List<ErrorDto> validationErrors = validatorDiscussionMessage.validateDiscussionMessage(sanitizedRequest);
            if (!validationErrors.isEmpty()) {
                return ApiResponseFactory.badRequest(
                        DiscussionError.INVALID_MESSAGE.getErrorMessage(),
                        validationErrors
                );
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
            return  ApiResponseFactory.internalError();
        }
    }

}
