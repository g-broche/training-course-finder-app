package com.example.finder.controller;

import com.example.finder.config.PaginationConfig;
import com.example.finder.dto.input.RequestLogin;
import com.example.finder.dto.input.RequestRegister;
import com.example.finder.service.AuthService;
import com.example.finder.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;
    private final PaginationConfig paginationConfig;


    public UserController(
            UserService userService,
            PaginationConfig paginationConfig
    ) {
        this.userService = userService;
        this.paginationConfig = paginationConfig;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAll() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/paginated")
    public ResponseEntity<?> getPaginatedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return userService.getPaginatedUsers(page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        return userService.getUserDetailsById(id);
    }
}
