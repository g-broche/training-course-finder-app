package com.example.finder.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class ApiController {

    public ApiController() {

    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("API is running");
    }
}