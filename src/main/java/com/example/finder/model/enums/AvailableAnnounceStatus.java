package com.example.finder.model.enums;

public enum AvailableAnnounceStatus {
    UNSOLVED("unsolved"),
    SOLVED("solved");

    private final String displayName;

    AvailableAnnounceStatus(String displayName) {
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
