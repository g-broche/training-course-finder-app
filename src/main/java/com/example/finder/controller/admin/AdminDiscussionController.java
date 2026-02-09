package com.example.finder.controller.admin;

import com.example.finder.dto.input.RequestAnnounceStatus;
import com.example.finder.dto.input.RequestInteractivityState;
import com.example.finder.dto.input.RequestRecordStatus;
import com.example.finder.model.enums.AvailableAnnounceStatus;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.model.enums.AvailableInteractivityState;
import com.example.finder.model.enums.AvailableRecordStatus;
import com.example.finder.response.ApiResponseFactory;
import com.example.finder.service.AnnounceService;
import com.example.finder.service.DiscussionService;
import com.example.finder.utils.EnumUtil;

import org.apache.catalina.connector.Request;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/admin/discussions")
public class AdminDiscussionController {

    private final AnnounceService announceService;
    private final DiscussionService discussionService;
    private final EnumUtil enumUtil;

    public AdminDiscussionController(
            AnnounceService announceService,
            DiscussionService discussionService,
            EnumUtil enumUtil) {
        this.announceService = announceService;
        this.discussionService = discussionService;
        this.enumUtil = enumUtil;
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDiscussionDetail(@PathVariable UUID uuid) {
        return discussionService.getDiscussionForModeration(uuid);
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPaginatedDiscussions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return discussionService.getPaginatedDiscussionsForModeration(
                page,
                size);
    }

    @GetMapping("/{uuid}/related-announce")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRelatedAnnounce(@PathVariable UUID uuid) {
        boolean withHiddenData = true;
        return discussionService.getRelatedAnnounce(uuid, withHiddenData);
    }

    @PutMapping("/{uuid}/interactivity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateInteractivity(
            @PathVariable UUID uuid,
            @RequestBody RequestInteractivityState request) {
        AvailableInteractivityState interactivityState = enumUtil
                .interactivityStateMatcher(request.getInteractivityState());
        if (interactivityState == null) {
            return ApiResponseFactory
                    .badRequest("Invalid interactivityState parameter. Must be 'open' or 'close'.");
        }
        return discussionService.forceChangeInteractivityState(uuid, interactivityState);
    }
}
