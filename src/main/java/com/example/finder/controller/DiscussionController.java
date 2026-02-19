package com.example.finder.controller;

import com.example.finder.dto.input.RequestMessage;
import com.example.finder.service.DiscussionService;
import com.example.finder.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/discussions")
public class DiscussionController {

    private final DiscussionService discussionService;
    private final MessageService messageService;

    public DiscussionController(
            DiscussionService discussionService,
            MessageService messageService) {
        this.discussionService = discussionService;
        this.messageService = messageService;
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDiscussion(@PathVariable UUID uuid) {
        boolean withHiddenAnnounce = false;
        return discussionService.getDiscussion(uuid, withHiddenAnnounce);
    }

    @PostMapping("/{uuid}/messages/new")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addMessageToDiscussion(
            @PathVariable UUID uuid,
            @RequestBody RequestMessage request) {
        return discussionService.addMessageToDiscussion(uuid, request);
    }

    @PostMapping("/{uuidDiscussion}/messages/{uuidMessage}/report")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> reportDiscussionMessage(
            @PathVariable UUID uuidDiscussion,
            @PathVariable UUID uuidMessage) {
        return messageService.reportMessage(uuidDiscussion, uuidMessage);
    }
}
