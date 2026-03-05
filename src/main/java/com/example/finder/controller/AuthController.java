package com.example.finder.controller;

import com.example.finder.dto.input.RequestLogin;
import com.example.finder.dto.input.RequestRefreshToken;
import com.example.finder.dto.input.RequestRegister;
import com.example.finder.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RequestRegister request) {
        return authService.registerNewUser(request);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody RequestLogin request) {
        return authService.logUser(request);
    }

    @PostMapping("/signoff")
    public ResponseEntity<?> logout(@RequestBody RequestRefreshToken request) {
        return authService.logout(request);
    }

    @GetMapping("/activate/{token}")
    public ResponseEntity<?> verifyEmail(@PathVariable String token) {
        return authService.validateRegistrationToken(token);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser() {
        return authService.getCurrentUser();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RequestRefreshToken request) {
        return authService.refreshToken(request.getRefreshToken());
    }
}