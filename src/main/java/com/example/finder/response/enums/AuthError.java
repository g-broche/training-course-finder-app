package com.example.finder.response.enums;

public enum AuthError {
    EMAIL_ALREADY_USED("The email is already in use"),
    DISPLAY_NAME_ALREADY_USED("The display name is already in use"),
    INVALID_REGISTER_DATA("The information sent to register a new user is invalid"),
    INVALID_CREDENTIALS("The credentials provided are invalid"),
    GUEST_FORBIDDEN("Guest user cannot access this resource"),
    USER_BANNED("Your account has been banned."),
    INVALID_VERIFICATION_TOKEN("The verification token is invalid");

    private final String errorMessage;

    AuthError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
