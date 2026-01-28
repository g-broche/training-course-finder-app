package com.example.finder.controller.admin;

import com.example.finder.dto.input.RequestLogin;
import com.example.finder.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/auth")
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(
            AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> adminLogin(@RequestBody RequestLogin request) {
        return authService.logAdmin(request);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> adminLogout() {
        return authService.logout();
    }
}
