package com.example.finder.model.enums;

public enum AvailableUserStatus {
    ALLOWED("Allowed"),
    BANNED("Banned");

    private final String displayName;

    AvailableUserStatus(String displayName) {
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
