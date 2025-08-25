package com.example.finder.dto.input;

import java.time.LocalDate;

public class RequestDiscussion {
    private String message;

    public RequestDiscussion() {
    }

    public RequestDiscussion(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}