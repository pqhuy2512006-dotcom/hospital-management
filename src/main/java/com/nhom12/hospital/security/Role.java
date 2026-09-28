package com.nhom12.hospital.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public enum Role {
    QUAN_TRI("QuanTri", Set.of("*")),
    BAC_SI("BacSi", Set.of("dashboard.html", "examination.html", "laboratory.html", "patients.html")),
    DIEU_DUONG("DieuDuong", Set.of("dashboard.html", "inpatient.html", "patients.html")),
    LE_TAN("LeTan", Set.of("dashboard.html", "appointments.html", "patients.html", "doctors.html")),
    DUOC_SI("DuocSi", Set.of("dashboard.html", "pharmacy.html")),
    KTV("KTV", Set.of("dashboard.html", "laboratory.html")),
    THU_NGAN("ThuNgan", Set.of("dashboard.html", "billing.html", "patients.html")),
    BENH_NHAN("BenhNhan", Set.of("appointments.html", "patients.html", "billing.html"));

    private static final Map<String, Role> ALIAS_MAP;

    static {
        Map<String, Role> aliasMap = new HashMap<>();
        for (Role role : values()) {
            aliasMap.put(normalizeKey(role.value), role);
            for (String alias : role.getAliases()) {
                aliasMap.put(normalizeKey(alias), role);
            }
        }
        ALIAS_MAP = Collections.unmodifiableMap(aliasMap);
    }

    private final String value;
    private final Set<String> allowedPages;

    Role(String value, Set<String> allowedPages) {
        this.value = value;
        this.allowedPages = allowedPages;
    }

    public String getValue() {
        return value;
    }

    public Set<String> getAllowedPages() {
        return allowedPages;
    }

    public boolean canAccess(String page) {
        if (page == null || page.isBlank()) {
            return false;
        }
        String normalized = page.trim();
        return allowedPages.contains("*") || allowedPages.contains(normalized);
            || allowedPages.contains(normalized.replace("/", ""));
    }

    public String[] getAliases() {
        return switch (this) {
            case QUAN_TRI -> new String[] {"ADMIN", "QUANTRI", "QUAN_TRI"};
            case BAC_SI -> new String[] {"DOCTOR", "BACSI", "BAC_SI"};
            case DIEU_DUONG -> new String[] {"NURSE", "DIEUDUONG", "DIEU_DUONG"};
            case LE_TAN -> new String[] {"RECEPTIONIST", "LETAN", "LE_TAN"};
            case DUOC_SI -> new String[] {"PHARMACIST", "DUOCSI", "DUOC_SI"};
            case KTV -> new String[] {"TECHNICIAN", "KYTHUATVIEN", "KY_THUAT_VIEN"};
            case THU_NGAN -> new String[] {"CASHIER", "THUNGAN", "THU_NGAN"};
            case BENH_NHAN -> new String[] {"PATIENT", "BENHNHAN", "BENH_NHAN"};
        };
    }

    public static Role fromValue(String value) {
        if (value == null || value.isBlank()) {
            return BENH_NHAN;
        }

        String key = normalizeKey(value);
        Role matched = ALIAS_MAP.get(key);
        if (matched != null) {
            return matched;
        }

        return Arrays.stream(values())
                .filter(role -> role.value.equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(BENH_NHAN);
    }

    private static String normalizeKey(String text) {
        return text.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
    }
}
