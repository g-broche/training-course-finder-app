package com.example.finder.utils.jwt;

import com.example.finder.config.JwtProperties;
import com.example.finder.model.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String TEST_SECRET = "testSecretKeyForJwtTokenGenerationThatIsLongEnough12345";
    private static final long TEST_EXPIRATION = 15 * 60 * 1000;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret(TEST_SECRET);
        jwtProperties.setAccessTokenExpirationMs(TEST_EXPIRATION);
        jwtUtil = new JwtUtil(jwtProperties);
    }

    @Test
    void generateAccessToken_ShouldGenerateNonEmptyToken() {
        AppUser user = createUserWithId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"));

        String token = jwtUtil.generateAccessToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void generateAccessToken_ShouldUseUserIdAsSubjectOnly() {
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        AppUser user = createUserWithId(userId);

        String token = jwtUtil.generateAccessToken(user);
        Claims claims = parseClaims(token);

        assertEquals(userId.toString(), claims.getSubject());
        assertNull(claims.get("email"));
        assertNull(claims.get("firstName"));
        assertNull(claims.get("lastName"));
        assertNull(claims.get("displayName"));
        assertNull(claims.get("roles"));
    }

    @Test
    void extractUserId_ShouldExtractCorrectUuid() {
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");
        AppUser user = createUserWithId(userId);

        String token = jwtUtil.generateAccessToken(user);

        UUID extractedUserId = jwtUtil.extractUserId(token);
        assertEquals(userId, extractedUserId);
    }

    @Test
    void isTokenValid_ShouldReturnTrueForMatchingUser() {
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174004");
        AppUser user = createUserWithId(userId);

        String token = jwtUtil.generateAccessToken(user);

        assertTrue(jwtUtil.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_ShouldReturnFalseForDifferentUser() {
        UUID tokenUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174005");
        UUID otherUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174006");
        AppUser tokenUser = createUserWithId(tokenUserId);
        AppUser otherUser = createUserWithId(otherUserId);

        String token = jwtUtil.generateAccessToken(tokenUser);

        assertFalse(jwtUtil.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenValid_ShouldReturnFalseForExpiredToken() throws InterruptedException {
        JwtProperties shortExpirationProps = new JwtProperties();
        shortExpirationProps.setSecret(TEST_SECRET);
        shortExpirationProps.setAccessTokenExpirationMs(1);
        JwtUtil shortExpirationJwtUtil = new JwtUtil(shortExpirationProps);

        AppUser user = createUserWithId(UUID.fromString("123e4567-e89b-12d3-a456-426614174007"));
        String token = shortExpirationJwtUtil.generateAccessToken(user);

        Thread.sleep(10);

        assertFalse(shortExpirationJwtUtil.isTokenValid(token, user));
    }

    @Test
    void generateAccessToken_ShouldSetIssuedAtAndExpiration() {
        AppUser user = createUserWithId(UUID.fromString("123e4567-e89b-12d3-a456-426614174008"));

        String token = jwtUtil.generateAccessToken(user);
        Claims claims = parseClaims(token);

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private AppUser createUserWithId(UUID id) {
        AppUser user = new AppUser();
        user.setId(id);
        return user;
    }
}
