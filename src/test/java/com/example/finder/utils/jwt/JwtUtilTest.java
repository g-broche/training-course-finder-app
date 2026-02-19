package com.example.finder.utils.jwt;

import com.example.finder.config.JwtProperties;
import com.example.finder.model.AppUser;
import com.example.finder.model.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Mock
    private AppUser appUser;

    @Mock
    private Role role;

    private JwtUtil jwtUtil;
    private JwtProperties jwtProperties;
    private static final String TEST_SECRET = "testSecretKeyForJwtTokenGenerationThatIsLongEnough12345";
    private static final long TEST_EXPIRATION = 15 * 60 * 1000; // 15 minutes

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret(TEST_SECRET);
        jwtProperties.setAccessTokenExpirationMs(TEST_EXPIRATION);
        jwtUtil = new JwtUtil(jwtProperties);
    }

    @Test
    void generateAccessToken_ShouldGenerateNonNullToken() {
        mockAppUser();

        String token = jwtUtil.generateAccessToken(appUser);

        assertNotNull(token);
    }

    @Test
    void generateAccessToken_ShouldGenerateNonEmptyToken() {
        mockAppUser();

        String token = jwtUtil.generateAccessToken(appUser);

        assertFalse(token.isEmpty());
    }

    @Test
    void generateAccessToken_ShouldContainUserEmail() {
        mockAppUser();

        String token = jwtUtil.generateAccessToken(appUser);
        String extractedEmail = jwtUtil.extractUsername(token);

        assertEquals("test@example.com", extractedEmail);
    }

    @Test
    void generateAccessToken_ShouldContainUserClaims() {
        mockAppUser();

        String token = jwtUtil.generateAccessToken(appUser);
        Claims claims = jwtUtil.extractAllClaims(token);

        assertNotNull(claims.get("uuid"));
        assertNotNull(claims.get("firstName"));
        assertNotNull(claims.get("lastName"));
        assertNotNull(claims.get("displayName"));
        assertNotNull(claims.get("isVerified"));
        assertNotNull(claims.get("hasAcceptedGdpr"));
        assertNotNull(claims.get("userCreatedAt"));
    }

    @Test
    void generateAccessToken_ShouldContainRoles() {
        mockAppUser();

        String token = jwtUtil.generateAccessToken(appUser);
        Claims claims = jwtUtil.extractAllClaims(token);

        assertNotNull(claims.get("roles"));
    }

    @Test
    void extractUsername_ShouldExtractCorrectEmail() {
        mockAppUser();

        String token = jwtUtil.generateAccessToken(appUser);
        String username = jwtUtil.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void extractAllClaims_ShouldExtractAllTokenClaims() {
        mockAppUser();

        String token = jwtUtil.generateAccessToken(appUser);
        Claims claims = jwtUtil.extractAllClaims(token);

        assertNotNull(claims);
        assertEquals("test@example.com", claims.getSubject());
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        mockAppUser();
        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        String token = jwtUtil.generateAccessToken(appUser);
        boolean isValid = jwtUtil.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalseForDifferentUsername() {
        mockAppUser();
        UserDetails userDetails = User.builder()
                .username("different@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        String token = jwtUtil.generateAccessToken(appUser);
        boolean isValid = jwtUtil.isTokenValid(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalseForExpiredToken() throws InterruptedException {
        // Create JWT properties with very short expiration
        JwtProperties shortExpirationProps = new JwtProperties();
        shortExpirationProps.setSecret(TEST_SECRET);
        shortExpirationProps.setAccessTokenExpirationMs(1); // 1ms
        JwtUtil shortExpirationJwtUtil = new JwtUtil(shortExpirationProps);

        mockAppUser();
        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        String token = shortExpirationJwtUtil.generateAccessToken(appUser);
        Thread.sleep(10); // Wait for token to expire
        boolean isValid = shortExpirationJwtUtil.isTokenValid(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void extractAllClaims_ShouldContainIssuedAtDate() {
        mockAppUser();

        String token = jwtUtil.generateAccessToken(appUser);
        Claims claims = jwtUtil.extractAllClaims(token);

        assertNotNull(claims.getIssuedAt());
    }

    @Test
    void extractAllClaims_ShouldContainExpirationDate() {
        mockAppUser();

        String token = jwtUtil.generateAccessToken(appUser);
        Claims claims = jwtUtil.extractAllClaims(token);

        assertNotNull(claims.getExpiration());
    }

    @Test
    void generateAccessToken_ExpirationShouldBeInFuture() {
        mockAppUser();

        String token = jwtUtil.generateAccessToken(appUser);
        Claims claims = jwtUtil.extractAllClaims(token);
        Date expiration = claims.getExpiration();

        assertTrue(expiration.after(new Date()));
    }

    @Test
    void generateAccessToken_ShouldCallGetEmailOnUser() {
        mockAppUser();

        jwtUtil.generateAccessToken(appUser);

        verify(appUser, atLeastOnce()).getEmail();
    }

    @Test
    void generateAccessToken_ShouldCallGetRolesOnUser() {
        mockAppUser();

        jwtUtil.generateAccessToken(appUser);

        verify(appUser, atLeastOnce()).getRoles();
    }

    @Test
    void generateAccessToken_ShouldIncludeAllUserFields() {
        mockAppUser();

        jwtUtil.generateAccessToken(appUser);

        verify(appUser, atLeastOnce()).getId();
        verify(appUser, atLeastOnce()).getFirstName();
        verify(appUser, atLeastOnce()).getLastName();
        verify(appUser, atLeastOnce()).getDisplayName();
        verify(appUser, atLeastOnce()).getIsVerified();
        verify(appUser, atLeastOnce()).getHasAcceptGdpr();
        verify(appUser, atLeastOnce()).getCreatedAt();
    }

    @Test
    void extractUsername_ShouldHandleDifferentEmails() {
        when(appUser.getEmail()).thenReturn("different@test.com");
        when(appUser.getRoles()).thenReturn(Set.of(role));
        when(role.getName()).thenReturn("USER");
        when(appUser.getId()).thenReturn(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        when(appUser.getFirstName()).thenReturn("Test");
        when(appUser.getLastName()).thenReturn("User");
        when(appUser.getDisplayName()).thenReturn("TestUser");
        when(appUser.getIsVerified()).thenReturn(true);
        when(appUser.getHasAcceptGdpr()).thenReturn(true);
        when(appUser.getCreatedAt()).thenReturn(new Timestamp(System.currentTimeMillis()));

        String token = jwtUtil.generateAccessToken(appUser);
        String extractedEmail = jwtUtil.extractUsername(token);

        assertEquals("different@test.com", extractedEmail);
    }

    private void mockAppUser() {
        when(appUser.getEmail()).thenReturn("test@example.com");
        when(appUser.getRoles()).thenReturn(Set.of(role));
        when(role.getName()).thenReturn("USER");
        when(appUser.getId()).thenReturn(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));
        when(appUser.getFirstName()).thenReturn("John");
        when(appUser.getLastName()).thenReturn("Doe");
        when(appUser.getDisplayName()).thenReturn("JohnD");
        when(appUser.getIsVerified()).thenReturn(true);
        when(appUser.getHasAcceptGdpr()).thenReturn(true);
        when(appUser.getCreatedAt()).thenReturn(new Timestamp(System.currentTimeMillis()));
    }
}
