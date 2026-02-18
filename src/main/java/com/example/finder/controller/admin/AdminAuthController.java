package com.example.finder.controller.admin;

import com.example.finder.dto.input.RequestLogin;
import com.example.finder.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/logout")
    public ResponseEntity<?> adminLogout(HttpServletRequest request) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        return authService.logoutAdmin(refreshToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return authService.refreshAdminToken("");
        }

        return authService.refreshAdminToken(refreshToken);
    }
}
