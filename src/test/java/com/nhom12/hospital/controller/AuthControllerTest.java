package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.PatientRegistrationRequest;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.service.AuditLogService;
import com.nhom12.hospital.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private final AuthService authService = mock(AuthService.class);
    private final AuditLogService auditLogService = mock(AuditLogService.class);
    private final AuthController controller = new AuthController(authService, auditLogService);

    @Test
    void registersAnImmediatelyActivePatientAccountWithoutAProfile() {
        PatientRegistrationRequest request = registrationRequest("newpatient", "person@example.com");
        
        TaiKhoan mockAccount = new TaiKhoan();
        mockAccount.setTenDangNhap("newpatient");
        mockAccount.setVaiTro("BenhNhan");
        
        when(authService.registerPatient(request)).thenReturn(mockAccount);

        ResponseEntity<?> response = controller.registerPatient(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void rejectsDuplicatePatientUsername() {
        PatientRegistrationRequest request = registrationRequest("existing", null);
        when(authService.registerPatient(request)).thenThrow(new IllegalStateException("Tên đăng nhập đã được sử dụng."));

        ResponseEntity<?> response = controller.registerPatient(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void registersPatientAccountWithoutEmail() {
        PatientRegistrationRequest request = registrationRequest("noemail", null);
        
        TaiKhoan mockAccount = new TaiKhoan();
        mockAccount.setTenDangNhap("noemail");
        mockAccount.setVaiTro("BenhNhan");
        
        when(authService.registerPatient(request)).thenReturn(mockAccount);

        ResponseEntity<?> response = controller.registerPatient(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void rejectsRegistrationWithoutPhoneNumber() {
        PatientRegistrationRequest request = registrationRequest("nophone", null);
        request.setSoDienThoai(" ");
        
        when(authService.registerPatient(request)).thenThrow(new IllegalArgumentException("Số điện thoại là bắt buộc"));

        ResponseEntity<?> response = controller.registerPatient(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private PatientRegistrationRequest registrationRequest(String username, String email) {
        PatientRegistrationRequest request = new PatientRegistrationRequest();
        request.setUsername(username);
        request.setSoDienThoai("0912345678");
        request.setEmail(email);
        request.setPassword("secret1");
        request.setConfirmPassword("secret1");
        return request;
    }
}