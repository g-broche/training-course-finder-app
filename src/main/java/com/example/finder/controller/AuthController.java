package com.example.finder.controller;

import com.example.finder.dto.input.RequestLogin;
import com.example.finder.dto.input.RequestRegister;
import com.example.finder.dto.output.ErrorDto;
import com.example.finder.response.ApiResponseFactory;
import com.example.finder.service.AuthService;
import com.example.finder.utils.SanitizerUtil;
import com.example.finder.utils.ValidatorUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
            ) {
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


    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyEmail(@PathVariable String token) {
        return authService.validateRegistrationToken(token);
    }
}