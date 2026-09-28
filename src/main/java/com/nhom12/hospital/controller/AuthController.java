package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.LoginRequest;
import com.nhom12.hospital.dto.LoginResponse;
import com.nhom12.hospital.entity.NguoiDung;
import com.nhom12.hospital.repository.NguoiDungRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(NguoiDungRepository nguoiDungRepository, PasswordEncoder passwordEncoder) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {


        Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(request.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Tên đăng nhập không tồn tại!", null, null));
        }

        NguoiDung user = userOpt.get();

        // IN LOG ĐỂ SOI LỖI THỰC TẾ
        System.out.println("=== KIEM TRA LOGIN ===");
        System.out.println("Password gui len: [" + request.getPassword() + "]");
        System.out.println("Mat khau trong DB: [" + user.getMatKhau() + "]");
        System.out.println("Do dai trong DB : " + (user.getMatKhau() != null ? user.getMatKhau().length() : 0));
        System.out.println("Ket qua so khop : " + passwordEncoder.matches(request.getPassword(), user.getMatKhau()));
        System.out.println("======================");

        if (!passwordEncoder.matches(request.getPassword(), user.getMatKhau())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Mật khẩu không chính xác!", null, null));
        }

        return ResponseEntity.ok(
                new LoginResponse(true, "Đăng nhập thành công!", user.getHoTen(), user.getVaiTro())
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        Optional<NguoiDung> existing = nguoiDungRepository.findByTenDangNhap(request.getUsername());
        NguoiDung user;

        if (existing.isPresent()) {
            user = existing.get();
        } else {
            user = new NguoiDung();
            user.setTenDangNhap(request.getUsername());
            user.setHoTen("Quản Trị Viên");
            user.setVaiTro("ADMIN");
        }

        // Tự động mã hóa mật khẩu bằng BCrypt của Spring Security
        user.setMatKhau(passwordEncoder.encode(request.getPassword()));
        nguoiDungRepository.save(user);

        return ResponseEntity.ok("Cập nhật mật khẩu thành công!");
    }
}