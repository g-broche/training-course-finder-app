package com.example.finder.controller;

import com.example.finder.dto.input.RequestAnnounce;
import com.example.finder.dto.input.RequestDiscussion;
import com.example.finder.dto.input.RequestMessage;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.service.AnnounceService;
import com.example.finder.service.DiscussionService;
import com.example.finder.utils.logger.Printer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("api/discussions")
public class DiscussionController {

    private final DiscussionService discussionService;

    public DiscussionController(
            DiscussionService discussionService) {
        this.discussionService = discussionService;
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
}
