package com.example.finder.utils.jwt;

import com.example.finder.config.JwtProperties;
import com.example.finder.model.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(AppUser user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpirationMs()))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID extractUserId(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return subject != null ? UUID.fromString(subject) : null;
    }

    public boolean isTokenValid(String token, AppUser user) {
        try {
            UUID tokenUserId = extractUserId(token);
            return tokenUserId != null && tokenUserId.equals(user.getId()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

}
