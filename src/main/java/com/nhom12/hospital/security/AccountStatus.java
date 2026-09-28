package com.nhom12.hospital.security;

public enum AccountStatus {
    HOAT_DONG,
    BI_KHOA;

    public static AccountStatus fromActive(boolean active) {
        return active ? HOAT_DONG : BI_KHOA;
    }

    public boolean isActive() {
        return this == HOAT_DONG;
    }
}
