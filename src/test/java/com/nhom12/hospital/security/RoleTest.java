package com.nhom12.hospital.security;

import com.nhom12.hospital.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void shouldNormalizeRoleAliases() {
        assertEquals(Role.BAC_SI, Role.fromValue("BacSi"));
        assertEquals(Role.BAC_SI, Role.fromValue("DOCTOR"));
        assertEquals(Role.BENH_NHAN, Role.fromValue("benh_nhan"));
        assertEquals(Role.QUAN_TRI, Role.fromValue("ADMIN"));
        assertEquals(Role.QUAN_LY_NHAN_SU, Role.fromValue("HR"));
        assertEquals(Role.BAN_GIAM_DOC, Role.fromValue("DIRECTOR"));
    }

    @Test
    void shouldAllowOnlyAuthorizedPagesForEachRole() {
        assertTrue(Role.BAC_SI.canAccess("examination.html"));
        assertTrue(Role.BENH_NHAN.canAccess("appointments.html"));
        assertFalse(Role.BENH_NHAN.canAccess("laboratory.html"));
        assertTrue(Role.QUAN_TRI.canAccess("dashboard.html"));
        assertTrue(Role.QUAN_TRI.canAccess("settings.html"));
        assertFalse(Role.QUAN_TRI.canAccess("appointments.html"));
        assertFalse(Role.QUAN_TRI.canAccess("patients.html"));
        assertFalse(Role.QUAN_TRI.canAccess("pharmacy.html"));
        assertFalse(Role.QUAN_TRI.canAccess("inpatient.html"));
        assertFalse(Role.QUAN_TRI.canAccess("billing.html"));
    }

    @Test
    void shouldGrantSharedAndRoleSpecificPermissions() {
        for (Role role : Role.values()) {
            assertTrue(role.hasPermission(Permission.LOGIN));
            assertTrue(role.hasPermission(Permission.LOGOUT));
            if (role != Role.BENH_NHAN && role != Role.QUAN_TRI) {
                assertTrue(role.hasPermission(Permission.PASSWORD_CHANGE));
                assertTrue(role.hasPermission(Permission.PROFILE_UPDATE));
            }
        }
        assertTrue(Role.BENH_NHAN.hasPermission(Permission.PASSWORD_CHANGE));
        assertTrue(Role.QUAN_TRI.hasPermission(Permission.PASSWORD_CHANGE));

        assertTrue(Role.BENH_NHAN.hasPermission(Permission.APPOINTMENT_BOOK));
        assertTrue(Role.LE_TAN.hasPermission(Permission.WALK_IN_REGISTER));
        assertTrue(Role.BAC_SI.hasPermission(Permission.ICD10_DIAGNOSIS_RECORD));
        assertTrue(Role.KTV.hasPermission(Permission.CLS_RESULT_ENTER));
        assertTrue(Role.DUOC_SI.hasPermission(Permission.MEDICINE_DISPENSE_FEFO));
        assertTrue(Role.THU_NGAN.hasPermission(Permission.INSURANCE_APPLY));
        assertTrue(Role.DIEU_DUONG.hasPermission(Permission.BED_ASSIGN));
        assertTrue(Role.QUAN_LY_NHAN_SU.hasPermission(Permission.DUTY_ROSTER_ASSIGN));
        assertTrue(Role.QUAN_TRI.hasPermission(Permission.RBAC_MANAGE));
        assertTrue(Role.QUAN_TRI.hasPermission(Permission.STAFF_PASSWORD_RESET));
        assertTrue(Role.BAN_GIAM_DOC.hasPermission(Permission.REPORT_EXPORT));
        assertFalse(Role.QUAN_TRI.hasPermission(Permission.PROFILE_UPDATE));
        assertFalse(Role.QUAN_LY_NHAN_SU.hasPermission(Permission.RBAC_MANAGE));
    }

    @Test
    void shouldEnforcePageAccessOnTheServer() {
        var manager = new SecurityConfig().pageAuthorizationManager();
        var doctor = new TestingAuthenticationToken("doctor", null, "BacSi");

        var patientsPage = new RequestAuthorizationContext(
                new MockHttpServletRequest("GET", "/patients.html"));
        var pharmacyPage = new RequestAuthorizationContext(
                new MockHttpServletRequest("GET", "/pharmacy.html"));

        assertTrue(manager.authorize((Supplier<org.springframework.security.core.Authentication>) () -> doctor,
                patientsPage).isGranted());
        assertFalse(manager.authorize(() -> doctor, pharmacyPage).isGranted());

        var administrator = new TestingAuthenticationToken("admin", null, "QuanTri");
        assertFalse(manager.authorize(() -> administrator, pharmacyPage).isGranted());
    }
}
