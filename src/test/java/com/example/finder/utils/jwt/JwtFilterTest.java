package com.example.finder.utils.jwt;

import com.example.finder.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(jwtUtil, userDetailsService);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void doFilterInternal_ShouldExtractTokenFromAuthorizationHeader() throws ServletException, IOException {
        String token = "test-jwt-token";
        String email = "test@example.com";
        UserDetails userDetails = createUserDetails(email);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(token, userDetails)).thenReturn(true);

        Claims claims = new DefaultClaims();
        claims.put("roles", List.of("USER"));
        when(jwtUtil.extractAllClaims(token)).thenReturn(claims);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractUsername(token);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldExtractTokenFromCookie() throws ServletException, IOException {
        String token = "test-jwt-token";
        String email = "test@example.com";
        UserDetails userDetails = createUserDetails(email);
        Cookie[] cookies = { new Cookie("accessToken", token) };

        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(cookies);
        when(jwtUtil.extractUsername(token)).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(token, userDetails)).thenReturn(true);

        Claims claims = new DefaultClaims();
        claims.put("roles", List.of("USER"));
        when(jwtUtil.extractAllClaims(token)).thenReturn(claims);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractUsername(token);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldContinueFilterChainWhenNoToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticateWhenTokenIsInvalid() throws ServletException, IOException {
        String token = "invalid-jwt-token";
        String email = "test@example.com";
        UserDetails userDetails = createUserDetails(email);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(token, userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticateWhenAlreadyAuthenticated() throws ServletException, IOException {
        String token = "test-jwt-token";
        String email = "test@example.com";
        Authentication existingAuth = mock(Authentication.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(existingAuth);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldHandleExceptionInTokenExtraction() throws ServletException, IOException {
        String token = "malformed-token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        // Should not throw exception, should continue filter chain
        assertDoesNotThrow(() -> jwtFilter.doFilterInternal(request, response, filterChain));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldSkipWhenAuthorizationHeaderMissingBearer() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic somevalue");
        when(request.getCookies()).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil, never()).extractUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldIgnoreNonAccessTokenCookies() throws ServletException, IOException {
        Cookie[] cookies = {
                new Cookie("session", "session-value"),
                new Cookie("refreshToken", "refresh-token-value")
        };

        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(cookies);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil, never()).extractUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldSetAuthenticationWithRoles() throws ServletException, IOException {
        String token = "test-jwt-token";
        String email = "test@example.com";
        UserDetails userDetails = createUserDetails(email);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(token, userDetails)).thenReturn(true);

        Claims claims = new DefaultClaims();
        claims.put("roles", List.of("USER", "ADMIN"));
        when(jwtUtil.extractAllClaims(token)).thenReturn(claims);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(securityContext).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldCallUserDetailsService() throws ServletException, IOException {
        String token = "test-jwt-token";
        String email = "test@example.com";
        UserDetails userDetails = createUserDetails(email);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(token, userDetails)).thenReturn(true);

        Claims claims = new DefaultClaims();
        claims.put("roles", List.of("USER"));
        when(jwtUtil.extractAllClaims(token)).thenReturn(claims);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService).loadUserByUsername(email);
    }

    @Test
    void doFilterInternal_ShouldStripBearerPrefixCorrectly() throws ServletException, IOException {
        String token = "actual-token-without-bearer";
        String email = "test@example.com";
        UserDetails userDetails = createUserDetails(email);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(token, userDetails)).thenReturn(true);

        Claims claims = new DefaultClaims();
        claims.put("roles", List.of("USER"));
        when(jwtUtil.extractAllClaims(token)).thenReturn(claims);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractUsername(token);
    }

    private UserDetails createUserDetails(String email) {
        return User.builder()
                .username(email)
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }
}
