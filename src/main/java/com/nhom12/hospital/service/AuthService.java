package com.nhom12.hospital.service;

import com.nhom12.hospital.dto.LoginRequest;
import com.nhom12.hospital.dto.LoginResponse;
import com.nhom12.hospital.dto.PatientRegistrationRequest;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(TaiKhoanRepository taiKhoanRepository, NhanVienRepository nhanVienRepository, PasswordEncoder passwordEncoder) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticate(LoginRequest request) {
        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByTenDangNhap(request.getUsername());

        if (userOpt.isEmpty()) {
            return new LoginResponse(false, "Tên đăng nhập không tồn tại!", null, null);
        }

        TaiKhoan user = userOpt.get();

        if (!user.getTrangThai()) {
            return new LoginResponse(false, "Tài khoản đã bị khóa!", null, null);
        }
        if (request.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getMatKhauHash())) {
            return new LoginResponse(false, "Mật khẩu không chính xác!", null, null);
        }

        String displayName = nhanVienRepository.findByMaTaiKhoan(user.getMaTaiKhoan())
                .map(employee -> employee.getHoTen())
                .orElse(user.getTenDangNhap());

        return new LoginResponse(true, "Đăng nhập thành công!", displayName, user.getVaiTro());
    }
    
    public TaiKhoan getTaiKhoanByUsername(String username) {
        return taiKhoanRepository.findByTenDangNhap(username).orElse(null);
    }

    public TaiKhoan registerPatient(PatientRegistrationRequest request) {
        String username = request.getUsername().trim();
        String phone = request.getSoDienThoai() == null ? "" : request.getSoDienThoai().trim();
        String password = request.getPassword().trim();
        String confirmPassword = request.getConfirmPassword().trim();
        String email = request.getEmail() == null || request.getEmail().trim().isEmpty()
                ? null : request.getEmail().trim();

        if (phone.isEmpty() || phone.length() > 15) {
            throw new IllegalArgumentException("Số điện thoại là bắt buộc và tối đa 15 ký tự.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
        }
        if (taiKhoanRepository.findByTenDangNhap(username).isPresent()) {
            throw new IllegalStateException("Tên đăng nhập đã được sử dụng.");
        }
        if (email != null && taiKhoanRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email đã được sử dụng.");
        }

        TaiKhoan account = new TaiKhoan();
        account.setTenDangNhap(username);
        account.setMatKhauHash(passwordEncoder.encode(password));
        account.setEmail(email);
        account.setSoDienThoai(phone);
        account.setVaiTro("BenhNhan");
        account.setTrangThai(true);
        account.setNgayTao(java.time.LocalDateTime.now());
        return taiKhoanRepository.save(account);
    }
}

