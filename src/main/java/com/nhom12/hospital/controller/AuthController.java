package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.LoginRequest;
import com.nhom12.hospital.dto.LoginResponse;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

        if (!user.getTrangThai()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Tài khoản đã bị khóa!", null, null));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getMatKhauHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Mật khẩu không chính xác!", null, null));
        }

        return ResponseEntity.ok(
                new LoginResponse(true, "Đăng nhập thành công!", user.getTenDangNhap(), user.getVaiTro())
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        Optional<TaiKhoan> existing = taiKhoanRepository.findByTenDangNhap(request.getUsername());
        TaiKhoan user;

        if (existing.isPresent()) {
            user = existing.get();
        } else {
            user = new TaiKhoan();
            user.setTenDangNhap(request.getUsername());
            user.setVaiTro("QuanTri");
            user.setTrangThai(true);
            user.setNgayTao(LocalDateTime.now());
        }

        user.setMatKhauHash(passwordEncoder.encode(request.getPassword()));
        taiKhoanRepository.save(user);

        return ResponseEntity.ok("Cập nhật mật khẩu thành công!");
    }

    @PostMapping("/reset-admin")
    public ResponseEntity<?> resetAdmin(@RequestBody LoginRequest request) {
        String username = request.getUsername() == null ? "admin" : request.getUsername().trim();
        String password = request.getPassword() == null ? "admin123" : request.getPassword().trim();

        if (password.isEmpty()) {
            password = "admin123";
        }

        TaiKhoan user = taiKhoanRepository.findByTenDangNhap(username)
                .orElseGet(() -> {
                    TaiKhoan newUser = new TaiKhoan();
                    newUser.setTenDangNhap(username);
                    newUser.setVaiTro("QuanTri");
                    newUser.setTrangThai(true);
                    newUser.setNgayTao(LocalDateTime.now());
                    return newUser;
                });

        user.setMatKhauHash(passwordEncoder.encode(password));
        user.setVaiTro("QuanTri");
        user.setTrangThai(true);
        user.setNgayTao(user.getNgayTao() == null ? LocalDateTime.now() : user.getNgayTao());
        taiKhoanRepository.save(user);

        return ResponseEntity.ok("Đã reset tài khoản admin thành công. Tài khoản: admin / Mật khẩu: " + password);
    }
}