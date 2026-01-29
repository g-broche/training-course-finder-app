package com.example.finder.controller.admin;

import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.service.AnnounceService;
import com.example.finder.service.DiscussionService;
import com.example.finder.utils.EnumUtil;
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
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getAnnounceDetail(@PathVariable UUID uuid) {
        boolean mustHiddenRecordBeDisplayed = false;
        return announceService.getAnnounceDetail(uuid, mustHiddenRecordBeDisplayed);
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getPaginatedAnnounces(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId) {
        boolean mustHiddenRecordBeDisplayed = true;
        AvailableAnnounceTypes typeFilter = enumUtil.announceTypeMatcher(type);
        return announceService.getPaginatedAnnounces(
                page,
                size,
                typeFilter,
                search,
                categoryId,
                mustHiddenRecordBeDisplayed);
    }

    @GetMapping("/{uuid}/discussions")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getAnnounceDiscussions(@PathVariable UUID uuid) {
        boolean withHiddenAnnounce = true;
        return discussionService.getAnnounceDiscussions(uuid, withHiddenAnnounce);
    }
}
