package com.example.finder.dto;

public class JwtDto {
    private String jwt;

    public JwtDto(){
    }

    public JwtDto(String token){
        this.jwt = token;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
