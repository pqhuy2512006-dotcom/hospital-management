package com.nhom12.hospital.config;

import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import com.nhom12.hospital.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RoleAuthorizationFilterTest {

    private final TaiKhoanRepository taiKhoanRepository = mock(TaiKhoanRepository.class);
    private final AuditLogService auditLogService = mock(AuditLogService.class);
    private final RoleAuthorizationFilter filter = new RoleAuthorizationFilter(taiKhoanRepository, auditLogService);

    @Test
    void rejectsForgedAdminHeaderWithoutServerSession() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/benhnhan");
        request.addHeader("X-User-Role", "QuanTri");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> { });

        assertEquals(403, response.getStatus());
    }

    @Test
    void adminCannotReadOperationalPatientApi() throws Exception {
        TaiKhoan admin = account(1L, "admin", "QuanTri", true);
        when(taiKhoanRepository.findById(1L)).thenReturn(Optional.of(admin));
        MockHttpServletRequest request = authenticatedRequest("GET", "/api/v1/benhnhan", 1L);
        AtomicBoolean chainInvoked = new AtomicBoolean();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> chainInvoked.set(true));

        assertEquals(403, response.getStatus());
        assertTrue(!chainInvoked.get());
    }

    @Test
    void adminCanReadAggregateDashboardStats() throws Exception {
        TaiKhoan admin = account(1L, "admin", "QuanTri", true);
        when(taiKhoanRepository.findById(1L)).thenReturn(Optional.of(admin));
        MockHttpServletRequest request = authenticatedRequest("GET", "/api/v1/dashboard/stats", 1L);
        AtomicBoolean chainInvoked = new AtomicBoolean();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> chainInvoked.set(true));

        assertTrue(chainInvoked.get());
        assertEquals(200, response.getStatus());
    }

        @Test
        void doctorCanReadPatientsAndCreateExamination() throws Exception {
        TaiKhoan doctor = account(2L, "doctor", "BacSi", true);
        when(taiKhoanRepository.findById(2L)).thenReturn(Optional.of(doctor));

        MockHttpServletResponse patientResponse = new MockHttpServletResponse();
        AtomicBoolean patientRead = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("GET", "/api/v1/benhnhan/BN001/history", 2L), patientResponse,
            (req, res) -> patientRead.set(true));

        MockHttpServletResponse examinationResponse = new MockHttpServletResponse();
        AtomicBoolean examinationCreated = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("POST", "/api/v1/phieukham", 2L), examinationResponse,
            (req, res) -> examinationCreated.set(true));

        assertTrue(patientRead.get());
        assertTrue(examinationCreated.get());
        assertEquals(200, patientResponse.getStatus());
        assertEquals(200, examinationResponse.getStatus());
        }

        @Test
        void doctorCannotModifyPatientsOrWriteLabResults() throws Exception {
        TaiKhoan doctor = account(2L, "doctor", "BacSi", true);
        when(taiKhoanRepository.findById(2L)).thenReturn(Optional.of(doctor));

        MockHttpServletResponse patientResponse = new MockHttpServletResponse();
        AtomicBoolean patientUpdated = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("PUT", "/api/v1/benhnhan/BN001", 2L), patientResponse,
            (req, res) -> patientUpdated.set(true));

        MockHttpServletResponse labResponse = new MockHttpServletResponse();
        AtomicBoolean resultWritten = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("PUT", "/api/v1/cls/CLS001/ketqua", 2L), labResponse,
            (req, res) -> resultWritten.set(true));

        assertEquals(403, patientResponse.getStatus());
        assertEquals(403, labResponse.getStatus());
        assertTrue(!patientUpdated.get());
        assertTrue(!resultWritten.get());
        }

        @Test
        void doctorCannotCreatePatientRecord() throws Exception {
        TaiKhoan doctor = account(2L, "doctor", "BacSi", true);
        when(taiKhoanRepository.findById(2L)).thenReturn(Optional.of(doctor));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainInvoked = new AtomicBoolean();

        filter.doFilter(authenticatedRequest("POST", "/api/v1/benhnhan", 2L), response,
            (req, res) -> chainInvoked.set(true));

        assertEquals(403, response.getStatus());
        assertTrue(!chainInvoked.get());
        }

        @Test
        void doctorCanReadOwnProfileAndResultsButNotStaffOrGlobalResultLists() throws Exception {
        TaiKhoan doctor = account(2L, "doctor", "BacSi", true);
        when(taiKhoanRepository.findById(2L)).thenReturn(Optional.of(doctor));

        MockHttpServletResponse profileResponse = new MockHttpServletResponse();
        AtomicBoolean profileRead = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("GET", "/api/v1/nhanvien/me", 2L), profileResponse,
            (req, res) -> profileRead.set(true));

        MockHttpServletResponse resultResponse = new MockHttpServletResponse();
        AtomicBoolean ownResultsRead = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("GET", "/api/v1/cls/me", 2L), resultResponse,
            (req, res) -> ownResultsRead.set(true));

        MockHttpServletResponse staffResponse = new MockHttpServletResponse();
        AtomicBoolean staffRead = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("GET", "/api/v1/nhanvien", 2L), staffResponse,
            (req, res) -> staffRead.set(true));

        MockHttpServletResponse allResultsResponse = new MockHttpServletResponse();
        AtomicBoolean allResultsRead = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("GET", "/api/v1/cls", 2L), allResultsResponse,
            (req, res) -> allResultsRead.set(true));

        assertTrue(profileRead.get());
        assertTrue(ownResultsRead.get());
        assertEquals(403, staffResponse.getStatus());
        assertEquals(403, allResultsResponse.getStatus());
        assertTrue(!staffRead.get());
        assertTrue(!allResultsRead.get());
        }

    private MockHttpServletRequest authenticatedRequest(String method, String path, Long accountId) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.getSession(true).setAttribute(SessionAttributes.ACCOUNT_ID, accountId);
        return request;
    }

    private TaiKhoan account(Long id, String username, String role, boolean enabled) {
        TaiKhoan account = new TaiKhoan();
        account.setMaTaiKhoan(id);
        account.setTenDangNhap(username);
        account.setVaiTro(role);
        account.setTrangThai(enabled);
        return account;
    }
}
