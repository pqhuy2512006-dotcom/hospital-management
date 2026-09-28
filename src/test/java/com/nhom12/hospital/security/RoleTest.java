package com.nhom12.hospital.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void shouldNormalizeRoleAliases() {
        assertEquals(Role.BAC_SI, Role.fromValue("BacSi"));
        assertEquals(Role.BAC_SI, Role.fromValue("DOCTOR"));
        assertEquals(Role.BENH_NHAN, Role.fromValue("benh_nhan"));
        assertEquals(Role.QUAN_TRI, Role.fromValue("ADMIN"));
    }

    @Test
    void shouldAllowOnlyAuthorizedPagesForEachRole() {
        assertTrue(Role.BAC_SI.canAccess("examination.html"));
        assertTrue(Role.BENH_NHAN.canAccess("appointments.html"));
        assertFalse(Role.BENH_NHAN.canAccess("laboratory.html"));
        assertTrue(Role.QUAN_TRI.canAccess("settings.html"));
    }
}
