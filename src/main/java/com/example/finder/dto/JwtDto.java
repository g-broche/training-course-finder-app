package com.example.finder.dto;

import com.example.finder.dto.output.DetailedUserDto;

public class JwtDto {
    private String accessToken;
    private String refreshToken;
    private DetailedUserDto user;

    public JwtDto() {
    }

    public JwtDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public JwtDto(String accessToken, String refreshToken, DetailedUserDto user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public DetailedUserDto getUser() {
        return user;
    }

    public void setUser(DetailedUserDto user) {
        this.user = user;
    }
}
