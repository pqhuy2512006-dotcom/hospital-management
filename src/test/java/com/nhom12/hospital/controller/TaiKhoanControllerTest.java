package com.nhom12.hospital.controller;

import com.nhom12.hospital.service.TaiKhoanService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaiKhoanControllerTest {

    private final TaiKhoanService taiKhoanService = mock(TaiKhoanService.class);
    private final TaiKhoanController controller = new TaiKhoanController(taiKhoanService);

    @Test
    void createsStaffAccountWithAdminEnteredPhoneAndEmail() {
        Map<String, Object> payload = Map.of(
                "username", "doctor.one",
                "password", "secret1",
                "role", "BacSi",
                "fullName", "Doctor One",
                "phone", "0912345678",
                "email", "doctor@example.com"
        );

        when(taiKhoanService.create(any())).thenReturn(Map.of(
                "success", true,
                "message", "Cấp tài khoản nhân sự thành công!",
                "username", "doctor.one",
                "role", "BacSi"
        ));

        ResponseEntity<?> response = controller.create(payload, mock(HttpServletRequest.class));

        verify(taiKhoanService).create(payload);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void rejectsStaffAccountWithoutPhone() {
        Map<String, Object> payload = Map.of(
                "username", "doctor.one",
                "password", "secret1",
                "role", "BacSi",
                "email", "doctor@example.com"
        );

        when(taiKhoanService.create(any())).thenThrow(new IllegalArgumentException("Số điện thoại là bắt buộc"));

        ResponseEntity<?> response = controller.create(payload, mock(HttpServletRequest.class));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}