package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaiKhoanServiceTest {

    @Test
    void createsDirectorAccount() {
        TaiKhoanRepository taiKhoanRepository = mock(TaiKhoanRepository.class);
        NhanVienRepository nhanVienRepository = mock(NhanVienRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        TaiKhoanService service = new TaiKhoanService(
                taiKhoanRepository,
                nhanVienRepository,
                passwordEncoder
        );

        when(taiKhoanRepository.findByTenDangNhap("director.one")).thenReturn(Optional.empty());
        when(taiKhoanRepository.findByEmail("director@example.com")).thenReturn(Optional.empty());
        when(nhanVienRepository.existsBySoDienThoai("0912345678")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("encoded-secret");
        when(taiKhoanRepository.save(any(TaiKhoan.class))).thenAnswer(invocation -> {
            TaiKhoan account = invocation.getArgument(0);
            account.setMaTaiKhoan(1L);
            return account;
        });

        Map<String, Object> result = service.create(Map.of(
                "username", "director.one",
                "password", "secret1",
                "role", "GiamDoc",
                "fullName", "Director One",
                "phone", "0912345678",
                "email", "director@example.com"
        ));

        assertEquals("GiamDoc", result.get("role"));
        verify(nhanVienRepository).save(any(NhanVien.class));
    }
}
