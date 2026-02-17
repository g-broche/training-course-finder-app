package com.example.finder.repository;

import com.example.finder.model.RefreshToken;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long>, JpaSpecificationExecutor<RefreshToken> {
    Optional<RefreshToken> findByToken(String token);
}
