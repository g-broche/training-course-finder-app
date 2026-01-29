package com.example.finder.controller;

import com.example.finder.dto.input.RequestAnnounce;
import com.example.finder.dto.input.RequestDiscussion;
import com.example.finder.model.AnnounceType;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.service.AnnounceService;
import com.example.finder.service.DiscussionService;
import com.example.finder.utils.EnumUtil;
import com.example.finder.utils.logger.Printer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("api/announces")
public class AnnounceController {

    private final AnnounceService announceService;
    private final DiscussionService discussionService;
    private final EnumUtil enumUtil;

    public AnnounceController(
            AnnounceService announceService,
            DiscussionService discussionService,
            EnumUtil enumUtil) {
        this.announceService = announceService;
        this.discussionService = discussionService;
        this.enumUtil = enumUtil;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> getAnnounceDetail(@PathVariable UUID uuid) {
        boolean mustHiddenRecordBeDisplayed = false;
        return announceService.getAnnounceDetail(uuid, mustHiddenRecordBeDisplayed);
    }

    @GetMapping("/paginated")
    public ResponseEntity<?> getPaginatedAnnounces(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId) {
        boolean mustHiddenRecordBeDisplayed = false;
        AvailableAnnounceTypes typeFilter = enumUtil.announceTypeMatcher(type);
        return announceService.getPaginatedAnnounces(
                page,
                size,
                typeFilter,
                search,
                categoryId,
                mustHiddenRecordBeDisplayed);
    }

    @PostMapping(value = "/found/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createNewFoundAnnounce(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("latitude") String latitude,
            @RequestParam("longitude") String longitude,
            @RequestParam("city") String city,
            @RequestParam("country") String country,
            @RequestParam("relevantDate") String relevantDate,
            @RequestParam("categoryId") String categoryId,
            @RequestParam("image") MultipartFile image) {
        try {
            RequestAnnounce requestAnnounce = new RequestAnnounce();
            requestAnnounce.setTitle(title);
            requestAnnounce.setDescription(description);
            requestAnnounce.setLatitude(latitude);
            requestAnnounce.setLongitude(longitude);
            requestAnnounce.setCity(city);
            requestAnnounce.setCountry(country);
            requestAnnounce.setRelevantDate(LocalDate.parse(relevantDate));
            requestAnnounce.setCategoryId(Long.valueOf(categoryId));

            return announceService.createNewFoundAnnounce(requestAnnounce, image);

        } catch (Exception e) {
            Printer.printErrorLogWithDetails(e);
            return ResponseEntity.badRequest().body("Error processing request: " + e.getMessage());
        }
    }

    @PostMapping("/{uuid}/discussions/new")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createNewDiscussion(
            @PathVariable UUID uuid,
            @RequestBody RequestDiscussion request) {
        return discussionService.createNewDiscussion(uuid, request);
    }

    @GetMapping("/{uuid}/discussions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAnnounceDiscussions(@PathVariable UUID uuid) {
        boolean withHiddenAnnounce = false;
        return discussionService.getAnnounceDiscussions(uuid, withHiddenAnnounce);
    }
}
