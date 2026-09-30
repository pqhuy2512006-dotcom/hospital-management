package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/taikhoan")
@CrossOrigin(origins = "*")
public class TaiKhoanController {

    private static final Set<String> STAFF_ROLES = Set.of(
            "QuanTri", "BacSi", "DieuDuong", "LeTan", "DuocSi", "KTV", "ThuNgan", "NhanSu");

    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;

    public TaiKhoanController(TaiKhoanRepository taiKhoanRepository,
                              NhanVienRepository nhanVienRepository,
                              PasswordEncoder passwordEncoder) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
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
            map.put("status", Boolean.TRUE.equals(tk.getTrangThai()) ? "Hoạt động" : "Bị khóa");

            // Tìm nhân viên liên kết
            Optional<NhanVien> nvOpt = nhanVienRepository.findAll().stream()
                    .filter(nv -> tk.getMaTaiKhoan().equals(nv.getMaTaiKhoan()))
                    .findFirst();

            map.put("fullName", nvOpt.map(NhanVien::getHoTen).orElse(tk.getTenDangNhap()));
            res.add(map);
        }

        return res;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            String username = (String) payload.get("username");
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Tên đăng nhập không được để trống!"));
            }
            username = username.trim();

            if (taiKhoanRepository.findByTenDangNhap(username).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Tên đăng nhập \"" + username + "\" đã tồn tại!"));
            }

            String password = (String) payload.get("password");
            if (password == null || password.length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu ban đầu phải có ít nhất 6 ký tự."));
            }
            String dbRole = (String) payload.getOrDefault("role", "BacSi");
            if (!STAFF_ROLES.contains(dbRole)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Vai trò nhân viên không hợp lệ."));
            }
            String fullName = (String) payload.getOrDefault("fullName", username);
            String email = payload.get("email") instanceof String value ? value.trim() : "";
            String phone = payload.get("phone") instanceof String value ? value.trim() : "";
            if (email.isEmpty() || email.length() > 100 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email nhân viên không hợp lệ."));
            }
            if (phone.isEmpty() || phone.length() > 15) {
                return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại là bắt buộc và tối đa 15 ký tự."));
            }
            if (taiKhoanRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email đã được sử dụng."));
            }
            if (nhanVienRepository.existsBySoDienThoai(phone)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại đã được nhân viên khác sử dụng."));
            }

            TaiKhoan tk = new TaiKhoan();
            tk.setTenDangNhap(username);
            tk.setMatKhauHash(passwordEncoder.encode(password));
            tk.setEmail(email);
            tk.setSoDienThoai(phone);
            tk.setVaiTro(dbRole);
            tk.setTrangThai(true);
            tk.setNgayTao(LocalDateTime.now());
            TaiKhoan saved = taiKhoanRepository.save(tk);

            // Tạo bản ghi nhân viên tương ứng nếu không phải role BenhNhan
            if (!"BenhNhan".equals(dbRole)) {
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien("NV" + (System.currentTimeMillis() % 10000000));
                nv.setMaTaiKhoan(saved.getMaTaiKhoan());
                nv.setHoTen(fullName != null && !fullName.trim().isEmpty() ? fullName.trim() : username);
                nv.setSoDienThoai(phone);
                nv.setEmail(email);
                nv.setGioiTinh("Nam");
                nv.setVaiTro(dbRole);
                nv.setTrangThai("DangLamViec");
                nhanVienRepository.save(nv);
            }
            request.setAttribute(SessionAttributes.AUDIT_DETAIL, "Cấp tài khoản " + username + "; role=" + dbRole);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cấp tài khoản nhân sự thành công!",
                    "username", username,
                    "role", dbRole
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Lỗi tạo tài khoản: " + (e.getMessage() != null ? e.getMessage() : e.toString())
            ));
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateAccount(@PathVariable Long id,
                                           @RequestBody Map<String, Object> payload,
                                           HttpServletRequest request) {
        Optional<TaiKhoan> accountOpt = taiKhoanRepository.findById(id);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Long currentAccountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        TaiKhoan account = accountOpt.get();
        if ("BenhNhan".equals(account.getVaiTro())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Chỉ được quản lý tài khoản nhân viên."));
        }
        Object roleValue = payload.get("role");
        Object enabledValue = payload.get("enabled");
        String role = roleValue instanceof String value ? value : account.getVaiTro();
        boolean enabled = enabledValue instanceof Boolean value ? value : Boolean.TRUE.equals(account.getTrangThai());

        if (!STAFF_ROLES.contains(role)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vai trò nhân viên không hợp lệ."));
        }
        if (Objects.equals(currentAccountId, id) && (!enabled || !"QuanTri".equals(role))) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không thể tự khóa hoặc tự thu hồi quyền Admin."));
        }

        account.setVaiTro(role);
        account.setTrangThai(enabled);
        taiKhoanRepository.save(account);
        request.setAttribute(SessionAttributes.AUDIT_DETAIL,
            "Cập nhật tài khoản " + account.getTenDangNhap() + "; role=" + role + "; enabled=" + enabled);

        nhanVienRepository.findAll().stream()
                .filter(staff -> id.equals(staff.getMaTaiKhoan()))
                .findFirst()
                .ifPresent(staff -> {
                    staff.setVaiTro(role);
                    nhanVienRepository.save(staff);
                });

        return ResponseEntity.ok(Map.of("success", true, "role", role, "enabled", enabled));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> req, HttpServletRequest request) {
        String username = (String) request.getAttribute(SessionAttributes.USERNAME);
        String oldPassword = req.get("oldPassword");
        String newPassword = req.get("newPassword");

        if (oldPassword == null || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cần nhập mật khẩu hiện tại và mật khẩu mới tối thiểu 6 ký tự."));
        }

        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByTenDangNhap(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy tài khoản!"));
        }

        TaiKhoan user = userOpt.get();
        if (!passwordEncoder.matches(oldPassword, user.getMatKhauHash())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu cũ không chính xác!"));
        }

        user.setMatKhauHash(passwordEncoder.encode(newPassword));
        taiKhoanRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Đổi mật khẩu thành công!"));
    }

}
