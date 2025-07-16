package com.example.finder.model.enums;

public enum AvailableRoles {
    ADMIN("admin"),
    USER("user");

    private final String displayName;

    AvailableRoles(String displayName) {
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
