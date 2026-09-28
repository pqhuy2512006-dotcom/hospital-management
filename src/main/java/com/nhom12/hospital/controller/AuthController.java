package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.LoginRequest;
import com.nhom12.hospital.dto.LoginResponse;
import com.nhom12.hospital.dto.RegisterRequest;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(TaiKhoanRepository taiKhoanRepository, PasswordEncoder passwordEncoder) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByTenDangNhap(request.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Tên đăng nhập không tồn tại!", null, null));
        }

        TaiKhoan user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getMatKhauHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Mật khẩu không chính xác!", null, null));
        }

        return ResponseEntity.ok(
                new LoginResponse(true, "Đăng nhập thành công!", null, user.getVaiTro())
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // 1. Kiểm tra trường bắt buộc
        if (request.getTenDangNhap() == null || request.getTenDangNhap().trim().isEmpty() ||
            request.getMatKhau() == null || request.getMatKhau().trim().isEmpty() ||
            request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Vui lòng nhập đầy đủ tên đăng nhập, email và mật khẩu!"));
        }

        // 2. Kiểm tra tài khoản trùng lặp
        Optional<TaiKhoan> existing = taiKhoanRepository.findByTenDangNhap(
                request.getTenDangNhap().trim()
        );

        if (existing.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Tên đăng nhập đã tồn tại!"));
        }

        // 3. Tạo tài khoản
        TaiKhoan user = new TaiKhoan();
        user.setTenDangNhap(request.getTenDangNhap().trim());
        user.setMatKhauHash(passwordEncoder.encode(request.getMatKhau().trim()));
        user.setEmail(request.getEmail().trim());
        user.setVaiTro("BenhNhan");
        user.setTrangThai(true);
        user.setNgayTao(LocalDateTime.now());

        taiKhoanRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay."
        ));
    }
}