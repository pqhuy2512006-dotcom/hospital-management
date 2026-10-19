package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TaiKhoanService {

    private static final Set<String> STAFF_ROLES = Set.of(
            "QuanTri", "BacSi", "DieuDuong", "LeTan",
            "DuocSi", "KTV", "ThuNgan", "QuanLyNhanSu");

    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;

    public TaiKhoanService(TaiKhoanRepository taiKhoanRepository,
            NhanVienRepository nhanVienRepository,
            PasswordEncoder passwordEncoder) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Map<String, Object>> getAll() {

        List<TaiKhoan> list = taiKhoanRepository.findAll();
        List<Map<String, Object>> res = new ArrayList<>();

        for (TaiKhoan tk : list) {

            if ("BenhNhan".equals(tk.getVaiTro())) {
                continue;
            }

            Map<String, Object> map = new HashMap<>();

            map.put("id", tk.getMaTaiKhoan());
            map.put("username", tk.getTenDangNhap());
            map.put("email", tk.getEmail());
            map.put("role", tk.getVaiTro());
            map.put("enabled", Boolean.TRUE.equals(tk.getTrangThai()));
            map.put(
                    "status",
                    Boolean.TRUE.equals(tk.getTrangThai())
                            ? "Hoạt động"
                            : "Bị khóa");

            Optional<NhanVien> nvOpt = nhanVienRepository.findByMaTaiKhoan(tk.getMaTaiKhoan());

            map.put(
                    "fullName",
                    nvOpt.map(NhanVien::getHoTen)
                            .orElse(tk.getTenDangNhap()));

            res.add(map);
        }

        return res;
    }

    @Transactional
    public Map<String, Object> create(Map<String, Object> payload) {

        String username = (String) payload.get("username");

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập không được để trống!");
        }

        username = username.trim();

        if (taiKhoanRepository.findByTenDangNhap(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập \"" + username + "\" đã tồn tại!");
        }

        String password = (String) payload.get("password");

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Mật khẩu ban đầu phải có ít nhất 6 ký tự.");
        }

        String dbRole = (String) payload.getOrDefault("role", "BacSi");

        if (!STAFF_ROLES.contains(dbRole)) {
            throw new IllegalArgumentException(
                    "Vai trò nhân viên không hợp lệ.");
        }

        String email = payload.get("email") instanceof String value
                ? value.trim()
                : "";

        String phone = payload.get("phone") instanceof String value
                ? value.trim()
                : "";

        if (email.isEmpty()
                || email.length() > 100
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new IllegalArgumentException(
                    "Email nhân viên không hợp lệ.");
        }

        if (phone.isEmpty() || phone.length() > 15) {
            throw new IllegalArgumentException(
                    "Số điện thoại là bắt buộc và tối đa 15 ký tự.");
        }

        if (taiKhoanRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "Email đã được sử dụng.");
        }

        if (nhanVienRepository.existsBySoDienThoai(phone)) {
            throw new IllegalArgumentException(
                    "Số điện thoại đã được nhân viên khác sử dụng.");
        }

        // Tạo tài khoản
        TaiKhoan tk = new TaiKhoan();

        tk.setTenDangNhap(username);
        tk.setMatKhauHash(passwordEncoder.encode(password));
        tk.setEmail(email);
        tk.setSoDienThoai(phone);
        tk.setVaiTro(dbRole);
        tk.setTrangThai(true);
        tk.setNgayTao(LocalDateTime.now());

        taiKhoanRepository.save(tk);

        // Theo Hướng 2: Admin chỉ cấp tài khoản đăng nhập (TaiKhoan), HR sẽ tạo hồ sơ
        // NhanVien và liên kết với tài khoản này
        return Map.of(
                "success", true,
                "message", "Cấp tài khoản nhân sự thành công! Hồ sơ nhân viên sẽ do phòng Nhân sự (HR) hoàn thiện.",
                "username", username,
                "role", dbRole);
    }

    public List<Map<String, Object>> getUnlinkedAccounts() {
        List<TaiKhoan> list = taiKhoanRepository.findUnlinkedAccounts();
        List<Map<String, Object>> res = new ArrayList<>();
        for (TaiKhoan tk : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("maTaiKhoan", tk.getMaTaiKhoan());
            map.put("tenDangNhap", tk.getTenDangNhap());
            map.put("soDienThoai", tk.getSoDienThoai());
            map.put("email", tk.getEmail());
            map.put("vaiTro", tk.getVaiTro());
            res.add(map);
        }
        return res;
    }

    public void updateAccount(
            Long id,
            boolean enabled,
            Long currentAccountId) {

        Optional<TaiKhoan> accountOpt = taiKhoanRepository.findById(id);

        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException(
                    "Không tìm thấy tài khoản!");
        }

        TaiKhoan account = accountOpt.get();

        if ("BenhNhan".equals(account.getVaiTro())) {
            throw new IllegalArgumentException(
                    "Chỉ được quản lý tài khoản nhân viên.");
        }

        if (Objects.equals(currentAccountId, id) && !enabled) {
            throw new IllegalArgumentException(
                    "Không thể tự khóa tài khoản của mình.");
        }

        account.setTrangThai(enabled);

        taiKhoanRepository.save(account);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {

        if (oldPassword == null || newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Cần nhập mật khẩu hiện tại và mật khẩu mới tối thiểu 6 ký tự.");
        }

        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByTenDangNhap(username);

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản!");
        }

        TaiKhoan user = userOpt.get();

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, user.getMatKhauHash())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác!");
        }

        // Mã hóa mật khẩu mới
        user.setMatKhauHash(passwordEncoder.encode(newPassword));

        // Cập nhật database
        taiKhoanRepository.save(user);
    }

    @Transactional
    public String resetPassword(Long id, String newPassword, Long currentAccountId) {
        Optional<TaiKhoan> accountOpt = taiKhoanRepository.findById(id);

        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản!");
        }

        TaiKhoan account = accountOpt.get();

        if ("BenhNhan".equals(account.getVaiTro())) {
            throw new IllegalArgumentException("Chỉ được quản lý tài khoản nhân viên.");
        }

        if (Objects.equals(currentAccountId, id)) {
            throw new IllegalArgumentException("Vui lòng dùng chức năng Đổi Mật Khẩu cá nhân thay vì Reset.");
        }

        // Mật khẩu mặc định là 123456
        String passToSet = "123456";

        account.setMatKhauHash(passwordEncoder.encode(passToSet));
        taiKhoanRepository.save(account);

        return passToSet;
    }
}