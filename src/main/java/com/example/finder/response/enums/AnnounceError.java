package com.example.finder.response.enums;

public enum AnnounceError {
    INVALID_CREATION_DATA("The information sent to create a new announce is invalid");

    private final String errorMessage;

    AnnounceError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage(){return errorMessage;}
}
