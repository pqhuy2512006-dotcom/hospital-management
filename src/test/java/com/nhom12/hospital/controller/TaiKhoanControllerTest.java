package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaiKhoanControllerTest {

    private final TaiKhoanRepository taiKhoanRepository = mock(TaiKhoanRepository.class);
    private final NhanVienRepository nhanVienRepository = mock(NhanVienRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final TaiKhoanController controller = new TaiKhoanController(
            taiKhoanRepository, nhanVienRepository, passwordEncoder);

    @Test
    void createsStaffAccountWithAdminEnteredPhoneAndEmail() {
        when(taiKhoanRepository.findByTenDangNhap("doctor.one")).thenReturn(Optional.empty());
        when(taiKhoanRepository.findByEmail("doctor@example.com")).thenReturn(Optional.empty());
        when(nhanVienRepository.existsBySoDienThoai("0912345678")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("encoded-secret");
        TaiKhoan savedAccount = new TaiKhoan();
        savedAccount.setMaTaiKhoan(12L);
        when(taiKhoanRepository.save(any(TaiKhoan.class))).thenReturn(savedAccount);

        ResponseEntity<?> response = controller.create(Map.of(
                "username", "doctor.one",
                "password", "secret1",
                "role", "BacSi",
                "fullName", "Doctor One",
                "phone", "0912345678",
                "email", "doctor@example.com"
        ), mock(HttpServletRequest.class));

        var accountCaptor = org.mockito.ArgumentCaptor.forClass(TaiKhoan.class);
        var employeeCaptor = org.mockito.ArgumentCaptor.forClass(NhanVien.class);
        verify(taiKhoanRepository).save(accountCaptor.capture());
        verify(nhanVienRepository).save(employeeCaptor.capture());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("0912345678", accountCaptor.getValue().getSoDienThoai());
        assertEquals("doctor@example.com", accountCaptor.getValue().getEmail());
        assertEquals("0912345678", employeeCaptor.getValue().getSoDienThoai());
        assertEquals("doctor@example.com", employeeCaptor.getValue().getEmail());
    }

    @Test
    void rejectsStaffAccountWithoutPhone() {
        when(taiKhoanRepository.findByTenDangNhap("doctor.one")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.create(Map.of(
                "username", "doctor.one",
                "password", "secret1",
                "role", "BacSi",
                "email", "doctor@example.com"
        ), mock(HttpServletRequest.class));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(taiKhoanRepository, never()).save(any());
        verify(nhanVienRepository, never()).save(any());
    }
}