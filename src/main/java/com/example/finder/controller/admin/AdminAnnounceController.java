package com.example.finder.controller.admin;

import com.example.finder.dto.input.RequestAnnounceStatus;
import com.example.finder.dto.input.RequestAnnounceType;
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
@RequestMapping("api/admin/announces")
public class AdminAnnounceController {

    private final AnnounceService announceService;
    private final DiscussionService discussionService;
    private final EnumUtil enumUtil;

    public AdminAnnounceController(
            AnnounceService announceService,
            DiscussionService discussionService,
            EnumUtil enumUtil) {
        this.announceService = announceService;
        this.discussionService = discussionService;
        this.enumUtil = enumUtil;
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAnnounceDetail(@PathVariable UUID uuid) {
        boolean mustHiddenRecordBeDisplayed = true;
        return announceService.getAnnounceDetail(uuid, mustHiddenRecordBeDisplayed);
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPaginatedAnnounces(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Long categoryId) {
        boolean mustHiddenRecordBeDisplayed = true;
        AvailableAnnounceTypes typeFilter = enumUtil.announceTypeMatcher(type);
        return announceService.getPaginatedAnnounces(
                page,
                size,
                typeFilter,
                title,
                city,
                categoryId,
                mustHiddenRecordBeDisplayed);
    }

    @GetMapping("/{uuid}/discussions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAnnounceDiscussions(@PathVariable UUID uuid) {
        boolean withHiddenAnnounce = true;
        return discussionService.getAnnounceDiscussions(uuid, withHiddenAnnounce);
    }

    @PutMapping("/{uuid}/type")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateType(
            @PathVariable UUID uuid,
            @RequestBody RequestAnnounceType request) {
        AvailableAnnounceTypes announceType = enumUtil.announceTypeMatcher(request.getAnnounceType());
        if (announceType == null) {
            return ApiResponseFactory
                    .badRequest("Invalid announceType parameter. Must be 'found' or 'lost'.");
        }
        return announceService.forceChangeAnnounceType(uuid, announceType);
    }

    @PutMapping("/{uuid}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable UUID uuid,
            @RequestBody RequestAnnounceStatus request) {
        AvailableAnnounceStatus announceStatus = enumUtil.announceStatusMatcher(request.getAnnounceStatus());
        if (announceStatus == null) {
            return ApiResponseFactory
                    .badRequest("Invalid announceStatus parameter. Must be 'unsolved' or 'solved'.");
        }
        return announceService.forceChangeAnnounceStatus(uuid, announceStatus);
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
        return announceService.forceChangeInteractivityState(uuid, interactivityState);
    }

    @PutMapping("/{uuid}/recordstatus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRecordStatus(
            @PathVariable UUID uuid,
            @RequestBody RequestRecordStatus request) {
        AvailableRecordStatus recordStatus = enumUtil.recordStatusMatcher(request.getRecordStatus());
        if (recordStatus == null) {
            return ApiResponseFactory
                    .badRequest("Invalid recordStatus parameter. Must be 'shown', 'hidden', or 'to delete'.");
        }
        return announceService.forceChangeRecordStatus(uuid, recordStatus);
    }
}
