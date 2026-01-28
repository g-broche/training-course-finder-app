package com.example.finder.utils;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.Cookie;

import com.example.finder.model.AppUser;
import com.example.finder.utils.jwt.JwtUtil;

@Component
public class CookieUtil {
    private final JwtUtil JwtUtil;
    private final boolean MustCookieBeSecure;

    public CookieUtil(JwtUtil jwtUtil, @Value("${app.cookie.secure}") boolean cookieSecure) {
        this.JwtUtil = jwtUtil;
        this.MustCookieBeSecure = cookieSecure;
    }

    /**
     * Generates the cookie containing the token representing a logged user
     * 
     * @param loggedUser
     * @return cookie
     */
    public ResponseCookie generateCookieFromUser(AppUser loggedUser) {
        String token = JwtUtil.generateToken(loggedUser);
        return ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(MustCookieBeSecure)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Lax")
                .build();
    }

    /**
     * Generates a expired jwt cookie to be send and remove the jwt cookie
     * 
     * @return ResponseCookie
     */
    public ResponseCookie generateExpiredCookie() {
        return ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(MustCookieBeSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

}
