package com.example.finder.service;

import com.example.finder.model.*;
import com.example.finder.repository.*;
import com.example.finder.repository.specification.DiscussionSpecifications;
import com.example.finder.repository.specification.MessageSpecifications;
import com.example.finder.response.ApiResponseFactory;
import com.example.finder.utils.validator.ValidatorAuth;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.UUID;

@Service
public class MessageService {
    private final DiscussionRepository discussionRepository;
    private final MessageRepository messageRepository;
    private final RecordStatusRepository recordStatusRepository;
    private final ValidatorAuth validatorAuth;

    public MessageService(
            DiscussionRepository discussionRepository,
            MessageRepository messageRepository,
            RecordStatusRepository recordStatusRepository,
            ValidatorAuth validatorAuth) {
        this.discussionRepository = discussionRepository;
        this.messageRepository = messageRepository;
        this.recordStatusRepository = recordStatusRepository;
        this.validatorAuth = validatorAuth;
    }

    @Transactional
    public ResponseEntity<?> reportMessage(
            UUID discussionUuid,
            UUID messageUuid) {
        try {

            Specification<Message> messageSpec = Specification.allOf(
                    MessageSpecifications.hasId(messageUuid),
                    MessageSpecifications.belongToDiscussion(discussionUuid));
            Message message = messageRepository.findOne(messageSpec).orElse(null);

            if (message == null) {
                return ApiResponseFactory.notFound("no such message exists");
            }

            AppUser requester = validatorAuth.getUserFromSecurityContext();
            Specification<Discussion> discussionSpec = Specification.allOf(
                    DiscussionSpecifications.hasId(discussionUuid),
                    DiscussionSpecifications.hasParticipant(requester.getId()));
            Discussion discussion = discussionRepository.findOne(discussionSpec).orElse(null);
            if (discussion == null) {
                return ApiResponseFactory.unauthorized("you are not a participant of this discussion");
            }

            message.setReported(true);
            messageRepository.save(message);
            return ApiResponseFactory.success("message reported successfully");
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResponseFactory.internalError();
        }
    }
}
