package com.example.finder.model.enums;

public enum AvailableInteractivityState {
    OPEN("open"),
    CLOSE("close");

    private final String displayName;

    AvailableInteractivityState(String displayName) {
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
