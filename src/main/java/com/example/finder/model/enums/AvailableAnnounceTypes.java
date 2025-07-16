package com.example.finder.model.enums;

public enum AvailableAnnounceTypes {
    FOUND("found"),
    LOST("lost");

    private final String displayName;

    AvailableAnnounceTypes(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
