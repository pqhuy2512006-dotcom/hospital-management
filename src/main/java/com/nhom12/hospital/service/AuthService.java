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
    private final org.springframework.mail.javamail.JavaMailSender mailSender;

    public AuthService(TaiKhoanRepository taiKhoanRepository, 
                       NhanVienRepository nhanVienRepository, 
                       PasswordEncoder passwordEncoder,
                       @org.springframework.beans.factory.annotation.Autowired(required = false) org.springframework.mail.javamail.JavaMailSender mailSender) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }
    // xác thực tài khoản và mật khẩu
    public LoginResponse authenticate(LoginRequest request) {
        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByTenDangNhap(request.getUsername());

        if (userOpt.isEmpty() || request.getPassword() == null || !passwordEncoder.matches(request.getPassword(), userOpt.get().getMatKhauHash())) {
           return new LoginResponse(false, "Tên đăng nhập hoặc mật khẩu không chính xác!", null, null);
        }
        
        TaiKhoan user = userOpt.get();

        if (Boolean.FALSE.equals(user.getTrangThai())) {
            return new LoginResponse(false, "Tài khoản của bạn đã bị khóa hoặc tạm ngưng!", null, null);
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
        String email = request.getEmail() == null ? "" : request.getEmail().trim();

        // 1. Kiểm tra Email bắt buộc và chống trùng lặp
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email là bắt buộc để dùng cho việc khôi phục mật khẩu.");
        }
        if (taiKhoanRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email này đã được sử dụng bởi một tài khoản khác.");
        }

        // 2. Kiểm tra Số điện thoại và Mật khẩu
        if (phone.isEmpty() || phone.length() > 15) {
            throw new IllegalArgumentException("Số điện thoại là bắt buộc và tối đa 15 ký tự.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
        }

        // 3. Kiểm tra chống trùng Tên đăng nhập
        if (taiKhoanRepository.findByTenDangNhap(username).isPresent()) {
            throw new IllegalStateException("Tên đăng nhập đã được sử dụng.");
        }

        // 4. Khởi tạo tài khoản Bệnh nhân và băm mật khẩu BCrypt
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
        // ========================================================
    // LOGIC KHÔI PHỤC MẬT KHẨU DÀNH RIÊNG CHO BỆNH NHÂN
    // ========================================================
    private static class OtpEntry {
        String code;
        java.time.LocalDateTime expiryTime;
        OtpEntry(String code, java.time.LocalDateTime expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }
    }

    private final java.util.Map<String, OtpEntry> otpStorage = new java.util.concurrent.ConcurrentHashMap<>();

    public String generateAndSendOtp(String email) {
        String cleanEmail = email.trim().toLowerCase();
        Optional<TaiKhoan> accountOpt = taiKhoanRepository.findByEmail(cleanEmail);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản nào liên kết với email này.");
        }

        TaiKhoan account = accountOpt.get();

        // 🛡️ CHẶN NHÂN VIÊN: Tính năng này chỉ dành cho Bệnh nhân!
        if (!"BenhNhan".equalsIgnoreCase(account.getVaiTro())) {
            throw new IllegalArgumentException("Tính năng này chỉ dành cho Bệnh nhân. Nhân viên y tế vui lòng liên hệ Phòng CNTT để cấp lại mật khẩu nội bộ!");
        }

        // Sinh OTP 6 số ngẫu nhiên & lưu hạn 5 phút
        String otp = String.format("%06d", new java.util.Random().nextInt(900000) + 100000);
        otpStorage.put(cleanEmail, new OtpEntry(otp, java.time.LocalDateTime.now().plusMinutes(5)));

        // Gửi email nếu có cấu hình, nếu không thì ghi nhận lên hệ thống
        boolean mailSent = false;
        if (mailSender != null) {
            try {
                org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
                message.setTo(cleanEmail);
                message.setSubject("Mã xác nhận khôi phục mật khẩu - Bệnh viện MEDICARE");
                message.setText("Xin chào,\n\nMã xác thực OTP của bạn là: " + otp + "\nMã có hiệu lực trong 5 phút.\nNếu bạn không yêu cầu đổi mật khẩu, vui lòng bỏ qua thư này.\n\nTrân trọng,\nBệnh viện MEDICARE.");
                mailSender.send(message);
                mailSent = true;
            } catch (Exception e) {
                System.err.println("[MAIL ERROR] Lỗi gửi email: " + e.getMessage());
            }
        }

        System.out.println("=================================================");
        System.out.println("🔑 [MÃ OTP KHÔI PHỤC MẬT KHẨU] Email: " + cleanEmail + " | OTP: " + otp);
        System.out.println("=================================================");

        if (!mailSent) {
            throw new IllegalStateException("Không thể gửi email xác thực. Vui lòng kiểm tra lại kết nối mạng hoặc thử lại sau!");
        }
        return "Mã OTP đã được gửi về email của bạn. Vui lòng kiểm tra hộp thư!";
    }

    public void resetPasswordWithOtp(String email, String otp, String newPassword) {
        String cleanEmail = email.trim().toLowerCase();
        if (otp == null || otp.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập mã OTP.");
        }
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự.");
        }

        OtpEntry entry = otpStorage.get(cleanEmail);
        if (entry == null || !entry.code.equals(otp.trim())) {
            throw new IllegalArgumentException("Mã OTP không chính xác.");
        }
        if (java.time.LocalDateTime.now().isAfter(entry.expiryTime)) {
            otpStorage.remove(cleanEmail);
            throw new IllegalArgumentException("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
        }

        Optional<TaiKhoan> accountOpt = taiKhoanRepository.findByEmail(cleanEmail);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản.");
        }

        TaiKhoan account = accountOpt.get();
        account.setMatKhauHash(passwordEncoder.encode(newPassword.trim()));
        taiKhoanRepository.save(account);

        // Xóa OTP sau khi sử dụng thành công
        otpStorage.remove(cleanEmail);
    }
}

