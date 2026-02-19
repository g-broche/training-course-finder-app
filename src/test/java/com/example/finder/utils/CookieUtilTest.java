package com.example.finder.utils;

import com.example.finder.model.AppUser;
import com.example.finder.model.RefreshToken;
import com.example.finder.utils.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CookieUtilTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AppUser appUser;

    @Mock
    private RefreshToken refreshToken;

    private CookieUtil cookieUtil;
    private CookieUtil cookieUtilSecure;

    @BeforeEach
    void setUp() {
        cookieUtil = new CookieUtil(jwtUtil, false);
        cookieUtilSecure = new CookieUtil(jwtUtil, true);
    }

    @Test
    void generateAccessTokenCookie_ShouldReturnCookieWithCorrectName() {
        when(jwtUtil.generateAccessToken(appUser)).thenReturn("test-access-token");

        ResponseCookie cookie = cookieUtil.generateAccessTokenCookie(appUser);

        assertEquals("accessToken", cookie.getName());
    }

    @Test
    void generateAccessTokenCookie_ShouldReturnCookieWithGeneratedToken() {
        when(jwtUtil.generateAccessToken(appUser)).thenReturn("test-access-token");

        ResponseCookie cookie = cookieUtil.generateAccessTokenCookie(appUser);

        assertEquals("test-access-token", cookie.getValue());
    }

    @Test
    void generateAccessTokenCookie_ShouldSetHttpOnlyFlag() {
        when(jwtUtil.generateAccessToken(appUser)).thenReturn("test-access-token");

        ResponseCookie cookie = cookieUtil.generateAccessTokenCookie(appUser);

        assertTrue(cookie.isHttpOnly());
    }

    @Test
    void generateAccessTokenCookie_ShouldSetCorrectPath() {
        when(jwtUtil.generateAccessToken(appUser)).thenReturn("test-access-token");

        ResponseCookie cookie = cookieUtil.generateAccessTokenCookie(appUser);

        assertEquals("/", cookie.getPath());
    }

    @Test
    void generateAccessTokenCookie_ShouldSetMaxAgeTo15Minutes() {
        when(jwtUtil.generateAccessToken(appUser)).thenReturn("test-access-token");

        ResponseCookie cookie = cookieUtil.generateAccessTokenCookie(appUser);

        assertEquals(Duration.ofMinutes(15), cookie.getMaxAge());
    }

    @Test
    void generateAccessTokenCookie_ShouldSetSameSiteToLax() {
        when(jwtUtil.generateAccessToken(appUser)).thenReturn("test-access-token");

        ResponseCookie cookie = cookieUtil.generateAccessTokenCookie(appUser);

        assertEquals("Lax", cookie.getSameSite());
    }

    @Test
    void generateAccessTokenCookie_ShouldSetSecureFlagWhenConfigured() {
        when(jwtUtil.generateAccessToken(appUser)).thenReturn("test-access-token");

        ResponseCookie cookie = cookieUtilSecure.generateAccessTokenCookie(appUser);

        assertTrue(cookie.isSecure());
    }

    @Test
    void generateAccessTokenCookie_ShouldNotSetSecureFlagWhenNotConfigured() {
        when(jwtUtil.generateAccessToken(appUser)).thenReturn("test-access-token");

        ResponseCookie cookie = cookieUtil.generateAccessTokenCookie(appUser);

        assertFalse(cookie.isSecure());
    }

    @Test
    void generateRefreshTokenCookie_ShouldReturnCookieWithCorrectName() {
        when(refreshToken.getToken()).thenReturn("test-refresh-token");

        ResponseCookie cookie = cookieUtil.generateRefreshTokenCookie(refreshToken);

        assertEquals("refreshToken", cookie.getName());
    }

    @Test
    void generateRefreshTokenCookie_ShouldReturnCookieWithToken() {
        when(refreshToken.getToken()).thenReturn("test-refresh-token");

        ResponseCookie cookie = cookieUtil.generateRefreshTokenCookie(refreshToken);

        assertEquals("test-refresh-token", cookie.getValue());
    }

    @Test
    void generateRefreshTokenCookie_ShouldSetHttpOnlyFlag() {
        when(refreshToken.getToken()).thenReturn("test-refresh-token");

        ResponseCookie cookie = cookieUtil.generateRefreshTokenCookie(refreshToken);

        assertTrue(cookie.isHttpOnly());
    }

    @Test
    void generateRefreshTokenCookie_ShouldSetMaxAgeTo7Days() {
        when(refreshToken.getToken()).thenReturn("test-refresh-token");

        ResponseCookie cookie = cookieUtil.generateRefreshTokenCookie(refreshToken);

        assertEquals(Duration.ofDays(7), cookie.getMaxAge());
    }

    @Test
    void generateRefreshTokenCookie_ShouldSetSecureFlagWhenConfigured() {
        when(refreshToken.getToken()).thenReturn("test-refresh-token");

        ResponseCookie cookie = cookieUtilSecure.generateRefreshTokenCookie(refreshToken);

        assertTrue(cookie.isSecure());
    }

    @Test
    void generateExpiredCookie_ShouldReturnCookieWithJwtName() {
        ResponseCookie cookie = cookieUtil.generateExpiredCookie();

        assertEquals("jwt", cookie.getName());
    }

    @Test
    void generateExpiredCookie_ShouldReturnCookieWithEmptyValue() {
        ResponseCookie cookie = cookieUtil.generateExpiredCookie();

        assertEquals("", cookie.getValue());
    }

    @Test
    void generateExpiredCookie_ShouldSetMaxAgeToZero() {
        ResponseCookie cookie = cookieUtil.generateExpiredCookie();

        assertEquals(Duration.ZERO, cookie.getMaxAge());
    }

    @Test
    void generateExpiredCookie_ShouldSetHttpOnlyFlag() {
        ResponseCookie cookie = cookieUtil.generateExpiredCookie();

        assertTrue(cookie.isHttpOnly());
    }

    @Test
    void generateExpiredAccessTokenCookie_ShouldReturnCookieWithCorrectName() {
        ResponseCookie cookie = cookieUtil.generateExpiredAccessTokenCookie();

        assertEquals("accessToken", cookie.getName());
    }

    @Test
    void generateExpiredAccessTokenCookie_ShouldReturnCookieWithEmptyValue() {
        ResponseCookie cookie = cookieUtil.generateExpiredAccessTokenCookie();

        assertEquals("", cookie.getValue());
    }

    @Test
    void generateExpiredAccessTokenCookie_ShouldSetMaxAgeToZero() {
        ResponseCookie cookie = cookieUtil.generateExpiredAccessTokenCookie();

        assertEquals(Duration.ZERO, cookie.getMaxAge());
    }

    @Test
    void generateExpiredRefreshTokenCookie_ShouldReturnCookieWithCorrectName() {
        ResponseCookie cookie = cookieUtil.generateExpiredRefreshTokenCookie();

        assertEquals("refreshToken", cookie.getName());
    }

    @Test
    void generateExpiredRefreshTokenCookie_ShouldReturnCookieWithEmptyValue() {
        ResponseCookie cookie = cookieUtil.generateExpiredRefreshTokenCookie();

        assertEquals("", cookie.getValue());
    }

    @Test
    void generateExpiredRefreshTokenCookie_ShouldSetMaxAgeToZero() {
        ResponseCookie cookie = cookieUtil.generateExpiredRefreshTokenCookie();

        assertEquals(Duration.ZERO, cookie.getMaxAge());
    }

    @Test
    void generateAccessTokenCookie_ShouldCallJwtUtilGenerateAccessToken() {
        when(jwtUtil.generateAccessToken(appUser)).thenReturn("test-token");

        cookieUtil.generateAccessTokenCookie(appUser);

        verify(jwtUtil, times(1)).generateAccessToken(appUser);
    }

    @Test
    void generateRefreshTokenCookie_ShouldCallGetTokenOnRefreshToken() {
        when(refreshToken.getToken()).thenReturn("test-token");

        cookieUtil.generateRefreshTokenCookie(refreshToken);

        verify(refreshToken, times(1)).getToken();
    }
}
