package com.example.finder.response.enums;

import java.util.Map;

public enum AuthError {
    EMAIL_ALREADY_USED("The email is already in use"),
    INVALID_REGISTER_DATA("The information sent to register a new user is invalid"),
    INVALID_CREDENTIALS("The credentials provided are invalid"),
    INVALID_VERIFICATION_TOKEN("The verification token is invalid");

    private final String errorMessage;

    AuthError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage(){return errorMessage;}
}
