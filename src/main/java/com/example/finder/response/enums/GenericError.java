package com.example.finder.response.enums;

public enum GenericError {
    INTERNAL_ERROR("Request failed due to unexpected server error");

    private final String errorMessage;

    GenericError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage(){return errorMessage;}
}
