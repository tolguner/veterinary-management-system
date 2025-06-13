package com.example.vms_project.enums;

public enum Gender {
    MALE("Erkek"),
    FEMALE("Dişi");

    private final String displayName;

    Gender(String displayName) {
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
