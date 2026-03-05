package com.example.finder.controller;

import com.example.finder.service.CategoryService;
import com.example.finder.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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