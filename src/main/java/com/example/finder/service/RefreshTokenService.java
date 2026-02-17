package com.example.finder.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.finder.model.AppUser;
import com.example.finder.model.RefreshToken;
import com.example.finder.repository.RefreshTokenRepository;
import com.example.finder.repository.specification.RefreshTokenSpecifications;

@Service
public class RefreshTokenService {
    private final int revokedTokenRetentionLimit = 2;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(AppUser user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
        return refreshTokenRepository.save(token);
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
    }

    public void verifyNotRevoked(RefreshToken token) {
        if (token.getIsRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }
    }

    /**
     * Delete all revoked tokens for a user except for a defined amount.
     * 
     * @param user The user whose old revoked tokens should be cleaned up
     */
    public void deleteOldRevokedTokens(AppUser user) {
        Specification<RefreshToken> spec = Specification.allOf(
                RefreshTokenSpecifications.belongsToUser(user),
                RefreshTokenSpecifications.isRevoked());
        Sort sort = Sort.by("createdAt").descending();
        List<RefreshToken> revokedTokens = refreshTokenRepository.findAll(spec, sort);

        if (revokedTokens.size() > revokedTokenRetentionLimit) {
            List<RefreshToken> tokensToDelete = revokedTokens.subList(revokedTokenRetentionLimit, revokedTokens.size());
            refreshTokenRepository.deleteAll(tokensToDelete);
        }
    }

    public void revokeTokenByString(String refreshTokenString) {
        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(refreshTokenString);
        if (tokenOptional.isPresent()) {
            RefreshToken token = tokenOptional.get();
            token.setIsRevoked(true);
            refreshTokenRepository.save(token);
        }
    }
}