package com.example.finder.dto.input;

public class RequestRefreshToken {
    private String refreshToken;

    public RequestRefreshToken() {
    }

    public RequestRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
