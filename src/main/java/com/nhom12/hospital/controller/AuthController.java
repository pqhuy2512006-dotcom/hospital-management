package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.LoginRequest;
import com.nhom12.hospital.dto.LoginResponse;
import com.nhom12.hospital.dto.PatientRegistrationRequest;
import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import com.nhom12.hospital.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final TaiKhoanRepository taiKhoanRepository;
        private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

        public AuthController(TaiKhoanRepository taiKhoanRepository, NhanVienRepository nhanVienRepository,
                                                  PasswordEncoder passwordEncoder,
                          AuditLogService auditLogService) {
        this.taiKhoanRepository = taiKhoanRepository;
                this.nhanVienRepository = nhanVienRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByTenDangNhap(request.getUsername());

        if (userOpt.isEmpty()) {
            auditLogService.record("LOGIN_FAILED", request.getUsername(), null, "POST",
                    "/api/v1/auth/login", HttpStatus.UNAUTHORIZED.value(), httpRequest.getRemoteAddr(), null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Tên đăng nhập không tồn tại!", null, null));
        }

        TaiKhoan user = userOpt.get();

        if (!user.getTrangThai()) {
            auditLogService.record("LOGIN_FAILED", user.getTenDangNhap(), user.getVaiTro(), "POST",
                    "/api/v1/auth/login", HttpStatus.UNAUTHORIZED.value(), httpRequest.getRemoteAddr(), null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Tài khoản đã bị khóa!", null, null));
        }
        if (request.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getMatKhauHash())) {
            auditLogService.record("LOGIN_FAILED", user.getTenDangNhap(), user.getVaiTro(), "POST",
                    "/api/v1/auth/login", HttpStatus.UNAUTHORIZED.value(), httpRequest.getRemoteAddr(), null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Mật khẩu không chính xác!", null, null));
        }

        HttpSession session = httpRequest.getSession(true);
        httpRequest.changeSessionId();
        session.setAttribute(SessionAttributes.ACCOUNT_ID, user.getMaTaiKhoan());
        auditLogService.record("LOGIN", user.getTenDangNhap(), user.getVaiTro(), "POST",
                "/api/v1/auth/login", HttpStatus.OK.value(), httpRequest.getRemoteAddr(), null);

        String displayName = nhanVienRepository.findByMaTaiKhoan(user.getMaTaiKhoan())
                .map(employee -> employee.getHoTen())
                .orElse(user.getTenDangNhap());
        return ResponseEntity.ok(new LoginResponse(true, "Đăng nhập thành công!", displayName, user.getVaiTro()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {
        String username = request.getUsername().trim();
        String phone = request.getSoDienThoai() == null ? "" : request.getSoDienThoai().trim();
        String password = request.getPassword().trim();
        String confirmPassword = request.getConfirmPassword().trim();
        String email = request.getEmail() == null || request.getEmail().trim().isEmpty()
                ? null : request.getEmail().trim();

        if (phone.isEmpty() || phone.length() > 15) {
            return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại là bắt buộc và tối đa 15 ký tự."));
        }
        if (password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu phải có ít nhất 6 ký tự."));
        }
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu xác nhận không khớp."));
        }
        if (taiKhoanRepository.findByTenDangNhap(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Tên đăng nhập đã được sử dụng."));
        }
        if (email != null && taiKhoanRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email đã được sử dụng."));
        }

        TaiKhoan account = new TaiKhoan();
        account.setTenDangNhap(username);
        account.setMatKhauHash(passwordEncoder.encode(password));
        account.setEmail(email);
        account.setSoDienThoai(phone);
        account.setVaiTro("BenhNhan");
        account.setTrangThai(true);
        account.setNgayTao(java.time.LocalDateTime.now());
        taiKhoanRepository.save(account);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Đăng ký thành công. Bạn có thể đăng nhập ngay.",
                "username", username,
                "vaiTro", "BenhNhan"
        ));
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