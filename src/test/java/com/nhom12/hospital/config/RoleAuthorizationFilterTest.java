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
    void allowsPatientRegistrationWithoutAnExistingSession() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/register");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainInvoked = new AtomicBoolean();

        filter.doFilter(request, response, (req, res) -> chainInvoked.set(true));

        assertTrue(chainInvoked.get());
        assertEquals(200, response.getStatus());
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

    @Test
    void cashierCanManageInvoicesAndRefundAndProfile() throws Exception {
        TaiKhoan cashier = account(3L, "cashier", "ThuNgan", true);
        when(taiKhoanRepository.findById(3L)).thenReturn(Optional.of(cashier));

        MockHttpServletResponse getBillsResp = new MockHttpServletResponse();
        AtomicBoolean getBills = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("GET", "/api/v1/hoadon", 3L), getBillsResp,
                (req, res) -> getBills.set(true));

        MockHttpServletResponse payResp = new MockHttpServletResponse();
        AtomicBoolean paid = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("PUT", "/api/v1/hoadon/HD001/thanhtoan", 3L), payResp,
                (req, res) -> paid.set(true));

        MockHttpServletResponse autoCalcResp = new MockHttpServletResponse();
        AtomicBoolean autoCalc = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("POST", "/api/v1/hoadon/HD001/tinh-tu-dong", 3L), autoCalcResp,
                (req, res) -> autoCalc.set(true));

        MockHttpServletResponse bhytResp = new MockHttpServletResponse();
        AtomicBoolean bhyt = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("POST", "/api/v1/hoadon/HD001/ap-dung-bhyt", 3L), bhytResp,
                (req, res) -> bhyt.set(true));

        MockHttpServletResponse refundResp = new MockHttpServletResponse();
        AtomicBoolean refund = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("POST", "/api/v1/hoadon/HD001/hoan-ung", 3L), refundResp,
                (req, res) -> refund.set(true));

        MockHttpServletResponse profileResp = new MockHttpServletResponse();
        AtomicBoolean profile = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("PUT", "/api/v1/nhanvien/me", 3L), profileResp,
                (req, res) -> profile.set(true));

        MockHttpServletResponse pwResp = new MockHttpServletResponse();
        AtomicBoolean pw = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("POST", "/api/v1/taikhoan/change-password", 3L), pwResp,
                (req, res) -> pw.set(true));

        MockHttpServletResponse clinicalResp = new MockHttpServletResponse();
        AtomicBoolean clinical = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("POST", "/api/v1/phieukham", 3L), clinicalResp,
                (req, res) -> clinical.set(true));

        assertTrue(getBills.get());
        assertTrue(paid.get());
        assertTrue(autoCalc.get());
        assertTrue(bhyt.get());
        assertTrue(refund.get());
        assertTrue(profile.get());
        assertTrue(pw.get());
        assertTrue(!clinical.get());
        assertEquals(200, getBillsResp.getStatus());
        assertEquals(200, payResp.getStatus());
        assertEquals(200, autoCalcResp.getStatus());
        assertEquals(200, bhytResp.getStatus());
        assertEquals(200, refundResp.getStatus());
        assertEquals(200, profileResp.getStatus());
        assertEquals(200, pwResp.getStatus());
        assertEquals(403, clinicalResp.getStatus());
    }

    @Test
    void nurseCanReadInvoicesAndProfileButCannotPayOrRefund() throws Exception {
        TaiKhoan nurse = account(4L, "nurse", "DieuDuong", true);
        when(taiKhoanRepository.findById(4L)).thenReturn(Optional.of(nurse));

        MockHttpServletResponse getBillsResp = new MockHttpServletResponse();
        AtomicBoolean getBills = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("GET", "/api/v1/hoadon", 4L), getBillsResp,
                (req, res) -> getBills.set(true));

        MockHttpServletResponse payResp = new MockHttpServletResponse();
        AtomicBoolean paid = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("PUT", "/api/v1/hoadon/HD001/thanhtoan", 4L), payResp,
                (req, res) -> paid.set(true));

        MockHttpServletResponse refundResp = new MockHttpServletResponse();
        AtomicBoolean refund = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("POST", "/api/v1/hoadon/HD001/hoan-ung", 4L), refundResp,
                (req, res) -> refund.set(true));

        MockHttpServletResponse profileResp = new MockHttpServletResponse();
        AtomicBoolean profile = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("PUT", "/api/v1/nhanvien/me", 4L), profileResp,
                (req, res) -> profile.set(true));

        assertTrue(getBills.get());
        assertTrue(profile.get());
        assertTrue(!paid.get());
        assertTrue(!refund.get());
        assertEquals(200, getBillsResp.getStatus());
        assertEquals(200, profileResp.getStatus());
        assertEquals(403, payResp.getStatus());
        assertEquals(403, refundResp.getStatus());
    }

    @Test
    void hrManagerCannotAccessInvoicesButCanManageStaff() throws Exception {
        TaiKhoan hr = account(5L, "hr", "NhanSu", true);
        when(taiKhoanRepository.findById(5L)).thenReturn(Optional.of(hr));

        MockHttpServletResponse getBillsResp = new MockHttpServletResponse();
        AtomicBoolean getBills = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("GET", "/api/v1/hoadon", 5L), getBillsResp,
                (req, res) -> getBills.set(true));

        MockHttpServletResponse payResp = new MockHttpServletResponse();
        AtomicBoolean paid = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("PUT", "/api/v1/hoadon/HD001/thanhtoan", 5L), payResp,
                (req, res) -> paid.set(true));

        MockHttpServletResponse staffManageResp = new MockHttpServletResponse();
        AtomicBoolean staffManage = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("POST", "/api/v1/nhanvien", 5L), staffManageResp,
                (req, res) -> staffManage.set(true));

        MockHttpServletResponse profileResp = new MockHttpServletResponse();
        AtomicBoolean profile = new AtomicBoolean();
        filter.doFilter(authenticatedRequest("PUT", "/api/v1/nhanvien/me", 5L), profileResp,
                (req, res) -> profile.set(true));

        assertTrue(!getBills.get());
        assertTrue(!paid.get());
        assertTrue(staffManage.get());
        assertTrue(profile.get());
        assertEquals(403, getBillsResp.getStatus());
        assertEquals(403, payResp.getStatus());
        assertEquals(200, staffManageResp.getStatus());
        assertEquals(200, profileResp.getStatus());
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
