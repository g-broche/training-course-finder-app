package com.example.finder.controller;

import com.example.finder.dto.input.RequestAnnounce;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.service.AnnounceService;
import com.example.finder.service.DiscussionService;
import com.example.finder.utils.logger.Printer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("api/discussions")
public class DiscussionController {

    private final DiscussionService discussionService;

    public DiscussionController(
            DiscussionService discussionService
    ) {
        this.discussionService = discussionService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> getDiscussion(@PathVariable UUID uuid){
        boolean mustHiddenRecordBeDisplayed = false;
        return discussionService.getDiscussion(uuid, mustHiddenRecordBeDisplayed);
    }
}
