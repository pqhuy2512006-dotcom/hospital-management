package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.LoginRequest;
import com.nhom12.hospital.dto.LoginResponse;
import com.nhom12.hospital.dto.RegisterRequest;
import com.nhom12.hospital.entity.NguoiDung;
import com.nhom12.hospital.repository.NguoiDungRepository;
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

        if (!passwordEncoder.matches(request.getPassword(), user.getMatKhau())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Mật khẩu không chính xác!", null, null));
        }

        return ResponseEntity.ok(
                new LoginResponse(true, "Đăng nhập thành công!", user.getHoTen(), user.getVaiTro())
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // 1. Kiểm tra trường bắt buộc
        if (request.getTenDangNhap() == null || request.getTenDangNhap().trim().isEmpty() ||
            request.getMatKhau() == null || request.getMatKhau().trim().isEmpty() ||
            request.getHoTen() == null || request.getHoTen().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Vui lòng nhập đầy đủ tên đăng nhập, mật khẩu và họ tên!"));
        }

        // 2. Kiểm tra tài khoản trùng lặp
        Optional<NguoiDung> existing = nguoiDungRepository.findByTenDangNhap(request.getTenDangNhap().trim());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Tên đăng nhập đã tồn tại!"));
        }

        // 3. Tạo tài khoản với đầy đủ thông tin
        NguoiDung user = new NguoiDung();
        user.setTenDangNhap(request.getTenDangNhap().trim());
        user.setMatKhau(passwordEncoder.encode(request.getMatKhau().trim()));
        user.setHoTen(request.getHoTen().trim());
        user.setSoDienThoai(request.getSoDienThoai());
        user.setEmail(request.getEmail());
        user.setVaiTro("PATIENT");

        nguoiDungRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay."
        ));
    }
}