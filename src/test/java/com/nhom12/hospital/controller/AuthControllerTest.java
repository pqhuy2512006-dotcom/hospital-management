package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.PatientRegistrationRequest;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import com.nhom12.hospital.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private final TaiKhoanRepository taiKhoanRepository = mock(TaiKhoanRepository.class);
    private final NhanVienRepository nhanVienRepository = mock(NhanVienRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final AuditLogService auditLogService = mock(AuditLogService.class);
    private final AuthController controller = new AuthController(
            taiKhoanRepository, nhanVienRepository, passwordEncoder, auditLogService);

    @Test
    void registersAnImmediatelyActivePatientAccountWithoutAProfile() {
        PatientRegistrationRequest request = registrationRequest("newpatient", "person@example.com");
        when(taiKhoanRepository.findByTenDangNhap("newpatient")).thenReturn(Optional.empty());
        when(taiKhoanRepository.findByEmail("person@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret1")).thenReturn("encoded-secret");

        ResponseEntity<?> response = controller.registerPatient(request);

        var captor = org.mockito.ArgumentCaptor.forClass(TaiKhoan.class);
        verify(taiKhoanRepository).save(captor.capture());
        TaiKhoan saved = captor.getValue();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("BenhNhan", saved.getVaiTro());
        assertEquals(true, saved.getTrangThai());
        assertEquals("person@example.com", saved.getEmail());
        assertEquals("0912345678", saved.getSoDienThoai());
        assertEquals("encoded-secret", saved.getMatKhauHash());
        verify(nhanVienRepository, never()).save(any());
    }

    @Test
    void rejectsDuplicatePatientUsername() {
        PatientRegistrationRequest request = registrationRequest("existing", null);
        when(taiKhoanRepository.findByTenDangNhap("existing")).thenReturn(Optional.of(new TaiKhoan()));

        ResponseEntity<?> response = controller.registerPatient(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(taiKhoanRepository, never()).save(any());
    }

    @Test
    void registersPatientAccountWithoutEmail() {
        PatientRegistrationRequest request = registrationRequest("noemail", null);
        when(taiKhoanRepository.findByTenDangNhap("noemail")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret1")).thenReturn("encoded-secret");

        ResponseEntity<?> response = controller.registerPatient(request);

        var captor = org.mockito.ArgumentCaptor.forClass(TaiKhoan.class);
        verify(taiKhoanRepository).save(captor.capture());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(null, captor.getValue().getEmail());
    }

    @Test
    void rejectsRegistrationWithoutPhoneNumber() {
        PatientRegistrationRequest request = registrationRequest("nophone", null);
        request.setSoDienThoai(" ");

        ResponseEntity<?> response = controller.registerPatient(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(taiKhoanRepository, never()).save(any());
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