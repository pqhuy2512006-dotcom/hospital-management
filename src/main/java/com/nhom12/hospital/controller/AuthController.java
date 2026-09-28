package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.ChangePasswordRequest;
import com.nhom12.hospital.dto.LoginRequest;
import com.nhom12.hospital.dto.LoginResponse;
import com.nhom12.hospital.dto.RegisterRequest;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.security.JwtCookieBearerTokenResolver;
import com.nhom12.hospital.security.JwtService;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.security.Role;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final TaiKhoanRepository taiKhoanRepository;
        private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final boolean secureCookie;

    public AuthController(
            TaiKhoanRepository taiKhoanRepository,
            NhanVienRepository nhanVienRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${app.security.cookie.secure:false}") boolean secureCookie) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.secureCookie = secureCookie;
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        return Map.of("token", csrfToken.getToken());
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> Arrays.stream(Role.values())
                        .anyMatch(candidate -> candidate.getValue().equals(authority)))
                .findFirst()
                .orElse(Role.BENH_NHAN.getValue());
        return ResponseEntity.ok(Map.of("username", authentication.getName(), "role", role));
    }

        @PostMapping("/password/change")
        public ResponseEntity<?> changePassword(
                        Authentication authentication,
                        @Valid @RequestBody ChangePasswordRequest request) {
                TaiKhoan account = taiKhoanRepository.findByTenDangNhap(authentication.getName())
                                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED, "Không tìm thấy tài khoản đang đăng nhập."));
                if (!passwordEncoder.matches(request.currentPassword(), account.getMatKhauHash())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(Map.of("message", "Mật khẩu hiện tại không chính xác."));
                }
                if (passwordEncoder.matches(request.newPassword(), account.getMatKhauHash())) {
                        return ResponseEntity.badRequest()
                                        .body(Map.of("message", "Mật khẩu mới phải khác mật khẩu hiện tại."));
                }

                account.setMatKhauHash(passwordEncoder.encode(request.newPassword()));
                taiKhoanRepository.save(account);
                return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công."));
        }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse httpResponse) {
        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByTenDangNhap(request.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Tên đăng nhập không tồn tại!", null, null));
        }

        TaiKhoan user = userOpt.get();

        if (!Boolean.TRUE.equals(user.getTrangThai())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Tài khoản đã bị khóa!", null, null));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getMatKhauHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Mật khẩu không chính xác!", null, null));
        }

        if (request.getRole() != null && !request.getRole().isBlank()) {
            Role requestedRole = Role.fromValue(request.getRole());
            Role storedRole = Role.fromValue(user.getVaiTro());

            if (requestedRole != storedRole) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false,
                                "Vai trò không khớp. Tài khoản này thuộc vai trò " + storedRole.getValue() + ".",
                                null,
                                storedRole.getValue()));
            }
        }

        Role role = Role.fromValue(user.getVaiTro());
        if (role != Role.BENH_NHAN && role != Role.QUAN_TRI
                && nhanVienRepository.findByMaTaiKhoan(user.getMaTaiKhoan()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false,
                            "Tài khoản đang bị khóa vì chưa có hồ sơ nhân viên được liên kết.",
                            null,
                            role.getValue()));
        }
        String token = jwtService.createToken(user.getTenDangNhap(), role);
        ResponseCookie accessTokenCookie = ResponseCookie.from(JwtCookieBearerTokenResolver.COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofSeconds(jwtService.getExpirationSeconds()))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .body(new LoginResponse(true, "Đăng nhập thành công!", null, role.getValue()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie expiredCookie = ResponseCookie.from(JwtCookieBearerTokenResolver.COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .body(Map.of("success", true, "message", "Đăng xuất thành công."));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.getTenDangNhap() == null || request.getTenDangNhap().trim().isEmpty() ||
            request.getMatKhau() == null || request.getMatKhau().trim().isEmpty() ||
            request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Vui lòng nhập đầy đủ tên đăng nhập, email và mật khẩu!"));
        }

        Optional<TaiKhoan> existing = taiKhoanRepository.findByTenDangNhap(request.getTenDangNhap().trim());

        if (existing.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Tên đăng nhập đã tồn tại!"));
        }

        TaiKhoan user = new TaiKhoan();
        user.setTenDangNhap(request.getTenDangNhap().trim());
        user.setMatKhauHash(passwordEncoder.encode(request.getMatKhau().trim()));
        user.setEmail(request.getEmail().trim());
        user.setVaiTro(Role.BENH_NHAN.getValue());
        user.setTrangThai(true);
        user.setNgayTao(LocalDateTime.now());

        taiKhoanRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay."
        ));
    }
}