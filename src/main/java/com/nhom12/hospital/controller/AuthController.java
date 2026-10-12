package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.LoginRequest;
import com.nhom12.hospital.dto.LoginResponse;
import com.nhom12.hospital.dto.PatientRegistrationRequest;
import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.service.AuditLogService;
import com.nhom12.hospital.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final AuditLogService auditLogService;

    public AuthController(AuthService authService, AuditLogService auditLogService) {
        this.authService = authService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        LoginResponse response = authService.authenticate(request);

        TaiKhoan user = authService.getTaiKhoanByUsername(request.getUsername());
        String role = user != null ? user.getVaiTro() : null;

        if (!response.isSuccess()) {
            auditLogService.record("LOGIN_FAILED", request.getUsername(), role, "POST",
                    "/api/v1/auth/login", HttpStatus.UNAUTHORIZED.value(), httpRequest.getRemoteAddr(), null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        HttpSession session = httpRequest.getSession(true);
        httpRequest.changeSessionId();
        session.setAttribute(SessionAttributes.ACCOUNT_ID, user.getMaTaiKhoan());
        auditLogService.record("LOGIN", user.getTenDangNhap(), user.getVaiTro(), "POST",
                "/api/v1/auth/login", HttpStatus.OK.value(), httpRequest.getRemoteAddr(), null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {
        try {
            TaiKhoan account = authService.registerPatient(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Đăng ký thành công. Bạn có thể đăng nhập ngay.",
                    "username", account.getTenDangNhap(),
                    "vaiTro", account.getVaiTro()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "Đăng xuất thành công."));
    }

}