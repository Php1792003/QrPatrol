package com.example.demo.Enum;

public enum PenaltyStatus {
    NHAC_NHO("Nhắc nhở"),
    CANH_CAO("Cảnh cáo"),
    CHE_TAI("Chế tài");

    private final String displayName;

    PenaltyStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}