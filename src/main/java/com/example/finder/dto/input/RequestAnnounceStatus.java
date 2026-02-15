package com.example.finder.dto.input;

public class RequestAnnounceStatus {
    private String announceStatus;

    public RequestAnnounceStatus() {
    }

    public RequestAnnounceStatus(String announceStatus) {
        this.announceStatus = announceStatus;
    }

    public String getAnnounceStatus() {
        return announceStatus;
    }

    public void setAnnounceStatus(String announceStatus) {
        this.announceStatus = announceStatus;
    }
}
