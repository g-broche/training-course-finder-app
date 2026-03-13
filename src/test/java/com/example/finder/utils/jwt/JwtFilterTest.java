package com.example.finder.utils.jwt;

import com.example.finder.model.AppUser;
import com.example.finder.model.Role;
import com.example.finder.repository.AppUserRepository;
import com.example.finder.service.CustomUserDetailsService;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private AppUserRepository appUserRepository;

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
        jwtFilter = new JwtFilter(jwtUtil, userDetailsService, appUserRepository);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void doFilterInternal_ShouldAuthenticateFromAuthorizationHeader() throws ServletException, IOException {
        String token = "test-jwt-token";
        AppUser user = createUser(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "test@example.com", "USER");
        UserDetails userDetails = createUserDetails(user.getEmail());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(jwtUtil.extractUserId(token)).thenReturn(user.getId());
        when(appUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(token, user)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractUserId(token);
        verify(appUserRepository).findById(user.getId());
        verify(securityContext).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldAuthenticateFromCookieFallback() throws ServletException, IOException {
        String token = "cookie-jwt-token";
        AppUser user = createUser(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"), "cookie@example.com",
                "ADMIN");
        UserDetails userDetails = createUserDetails(user.getEmail());
        Cookie[] cookies = { new Cookie("accessToken", token) };

        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(cookies);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(jwtUtil.extractUserId(token)).thenReturn(user.getId());
        when(appUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(token, user)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).extractUserId(token);
        verify(appUserRepository).findById(user.getId());
        verify(securityContext).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldContinueWhenNoToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil, never()).extractUserId(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldContinueWhenTokenExtractionFails() throws ServletException, IOException {
        String token = "malformed-token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenThrow(new RuntimeException("Invalid token"));

        assertDoesNotThrow(() -> jwtFilter.doFilterInternal(request, response, filterChain));

        verify(appUserRepository, never()).findById(any());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldSkipWhenAlreadyAuthenticated() throws ServletException, IOException {
        String token = "test-jwt-token";
        Authentication existingAuth = mock(Authentication.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUserId(token)).thenReturn(UUID.fromString("123e4567-e89b-12d3-a456-426614174009"));
        when(securityContext.getAuthentication()).thenReturn(existingAuth);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(appUserRepository, never()).findById(any());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticateWhenTokenIsInvalidForUser() throws ServletException, IOException {
        String token = "invalid-jwt-token";
        AppUser user = createUser(UUID.fromString("123e4567-e89b-12d3-a456-426614174010"), "test@example.com", "USER");
        UserDetails userDetails = createUserDetails(user.getEmail());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(securityContext.getAuthentication()).thenReturn(null);
        when(jwtUtil.extractUserId(token)).thenReturn(user.getId());
        when(appUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);
        when(jwtUtil.isTokenValid(token, user)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldSkipWhenAuthorizationHeaderIsNotBearer() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic somevalue");
        when(request.getCookies()).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil, never()).extractUserId(anyString());
        verify(filterChain).doFilter(request, response);
    }

    private UserDetails createUserDetails(String email) {
        return User.builder()
                .username(email)
                .password("password")
                .authorities(Set.of())
                .build();
    }

    private AppUser createUser(UUID id, String email, String roleName) {
        AppUser user = new AppUser();
        user.setId(id);
        user.setEmail(email);

        Role role = new Role();
        role.setName(roleName);
        user.setRoles(Set.of(role));

        return user;
    }
}
