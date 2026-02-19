package com.example.finder.utils.validator;

import com.example.finder.exception.entity.UserNotFoundException;
import com.example.finder.model.AppUser;
import com.example.finder.repository.AppUserRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidatorAuthTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AppUser appUser;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private ValidatorAuth validatorAuth;

    @BeforeEach
    void setUp() {
        validatorAuth = new ValidatorAuth(appUserRepository);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getUserFromSecurityContext_ShouldReturnUserWhenAuthenticated() {
        String email = "user@example.com";
        UserDetails userDetails = User.builder()
                .username(email)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        AppUser result = validatorAuth.getUserFromSecurityContext();

        assertNotNull(result);
        assertEquals(appUser, result);
        verify(appUserRepository, times(1)).findByEmail(email);
    }

    @Test
    void getUserFromSecurityContext_ShouldThrowExceptionWhenPrincipalIsNotUserDetails() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("not a UserDetails object");

        assertThrows(UserNotFoundException.class, () -> {
            validatorAuth.getUserFromSecurityContext();
        });
    }

    @Test
    void getUserFromSecurityContext_ShouldThrowExceptionWhenUserNotFoundInRepository() {
        String email = "nonexistent@example.com";
        UserDetails userDetails = User.builder()
                .username(email)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            validatorAuth.getUserFromSecurityContext();
        });
    }

    @Test
    void getUserFromSecurityContext_ShouldExtractEmailFromUserDetails() {
        String email = "test@example.com";
        UserDetails userDetails = User.builder()
                .username(email)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        validatorAuth.getUserFromSecurityContext();

        verify(appUserRepository).findByEmail(email);
    }

    @Test
    void isCurrentUserAdmin_ShouldReturnTrueWhenUserIsAdmin() {
        String email = "admin@example.com";
        UserDetails userDetails = User.builder()
                .username(email)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));
        when(appUser.isAdmin()).thenReturn(true);

        boolean result = validatorAuth.isCurrentUserAdmin();

        assertTrue(result);
    }

    @Test
    void isCurrentUserAdmin_ShouldReturnFalseWhenUserIsNotAdmin() {
        String email = "user@example.com";
        UserDetails userDetails = User.builder()
                .username(email)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));
        when(appUser.isAdmin()).thenReturn(false);

        boolean result = validatorAuth.isCurrentUserAdmin();

        assertFalse(result);
    }

    @Test
    void isCurrentUserAdmin_ShouldCallIsAdminOnUser() {
        String email = "user@example.com";
        UserDetails userDetails = User.builder()
                .username(email)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));
        when(appUser.isAdmin()).thenReturn(false);

        validatorAuth.isCurrentUserAdmin();

        verify(appUser, times(1)).isAdmin();
    }

    @Test
    void isCurrentUserAdmin_ShouldThrowExceptionWhenUserNotFound() {
        String email = "nonexistent@example.com";
        UserDetails userDetails = User.builder()
                .username(email)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            validatorAuth.isCurrentUserAdmin();
        });
    }

    @Test
    void getUserFromSecurityContext_ShouldWorkWithRealUserDetailsImplementation() {
        String email = "real@example.com";
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                email,
                "password",
                Collections.emptyList());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        AppUser result = validatorAuth.getUserFromSecurityContext();

        assertNotNull(result);
        assertEquals(appUser, result);
    }
}
