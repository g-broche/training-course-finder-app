package com.example.finder.response.enums;

public enum DiscussionError {
    INVALID_MESSAGE("Invalid input given to send a new message"),
    AUTHOR_CANT_INITIATE_DISCUSSION("An announce author can't start a discussion with himself"),
    DISCUSSION_ALREADY_OPEN("A discussion is already open for this announce");

    private final String errorMessage;

    DiscussionError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
