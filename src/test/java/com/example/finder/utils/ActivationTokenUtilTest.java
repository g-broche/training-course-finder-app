package com.example.finder.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActivationTokenUtilTest {

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = ActivationTokenUtil.generateToken();
        assertNotNull(token, "Generated token should not be null");
    }

    @Test
    void generateToken_ShouldReturnNonEmptyToken() {
        String token = ActivationTokenUtil.generateToken();
        assertFalse(token.isEmpty(), "Generated token should not be empty");
    }

    @Test
    void generateToken_ShouldGenerateDifferentTokensOnConsecutiveCalls() {
        String token1 = ActivationTokenUtil.generateToken();
        String token2 = ActivationTokenUtil.generateToken();

        assertNotEquals(token1, token2,
                "Consecutive calls should generate different tokens");
    }
}
