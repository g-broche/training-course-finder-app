package com.example.finder.model.enums;

public enum AvailableRecordStatus {
    SHOWN("shown"),
    HIDDEN("hidden"),
    TO_DELETE("to delete");

    private final String displayName;

    AvailableRecordStatus(String displayName) {
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
