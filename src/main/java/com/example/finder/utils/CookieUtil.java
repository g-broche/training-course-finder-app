package com.example.finder.utils;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseCookie;

import com.example.finder.model.AppUser;
import com.example.finder.model.RefreshToken;
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
     * Generates the cookie containing the access token for logged user
     * (short-lived)
     * 
     * @param loggedUser
     * @return ResponseCookie
     */
    public ResponseCookie generateAccessTokenCookie(AppUser loggedUser) {
        String accessToken = JwtUtil.generateAccessToken(loggedUser);
        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(MustCookieBeSecure)
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .sameSite("Lax")
                .build();
    }

    /**
     * Generates the cookie containing the refresh token (long-lived)
     * 
     * @param refreshToken
     * @return ResponseCookie
     */
    public ResponseCookie generateRefreshTokenCookie(RefreshToken refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(MustCookieBeSecure)
                .path("/")
                .maxAge(Duration.ofDays(7))
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

    /**
     * Generates expired access token cookie
     * 
     * @return ResponseCookie
     */
    public ResponseCookie generateExpiredAccessTokenCookie() {
        return ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(MustCookieBeSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

    /**
     * Generates expired refresh token cookie
     * 
     * @return ResponseCookie
     */
    public ResponseCookie generateExpiredRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(MustCookieBeSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

}
