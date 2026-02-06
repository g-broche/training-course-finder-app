package com.example.finder.dto.input;

public class RequestAnnounceType {
    private String announceType;

    public RequestAnnounceType() {
    }

    public RequestAnnounceType(String announceType) {
        this.announceType = announceType;
    }

    public String getAnnounceType() {
        return announceType;
    }

    public void setAnnounceType(String announceType) {
        this.announceType = announceType;
    }
}
