package com.example.finder.controller;

import com.example.finder.dto.input.RequestAnnounce;
import com.example.finder.dto.input.RequestRegister;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.service.AnnounceService;
import com.example.finder.service.AuthService;
import com.example.finder.utils.logger.Printer;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("api/announces")
public class AnnounceController {

    private final AnnounceService announceService;

    public AnnounceController(
            AnnounceService announceService
    ) {
        this.announceService = announceService;
    }

    @GetMapping("/paginated")
    public ResponseEntity<?> getPaginatedAnnounces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return announceService.getPaginatedAnnounces(page, size, null, null, null);
    }

    @GetMapping("/found/paginated")
    public ResponseEntity<?> getPaginatedFoundAnnounces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId
    ) {
        return announceService.getPaginatedAnnounces(page, size, AvailableAnnounceTypes.FOUND, search, categoryId);
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
            @RequestParam("image") MultipartFile image
    ) {
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
}
