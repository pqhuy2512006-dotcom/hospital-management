package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/taikhoan")
@CrossOrigin(origins = "*")
public class TaiKhoanController {

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

    private String normalizeRole(String rawRole) {
        if (rawRole == null || rawRole.trim().isEmpty()) return "BacSi";
        String r = rawRole.trim().toUpperCase();
        if (r.contains("DOC") || r.contains("BACSI")) return "BacSi";
        if (r.contains("CASHIER") || r.contains("THUNGAN")) return "ThuNgan";
        if (r.contains("PHARM") || r.contains("DUOC")) return "DuocSi";
        if (r.contains("RECEPT") || r.contains("LETAN") || r.contains("TIEPDON")) return "LeTan";
        if (r.contains("TECH") || r.contains("KTV") || r.contains("KYTHUAT")) return "KTV";
        if (r.contains("NURSE") || r.contains("DIEUDUONG")) return "DieuDuong";
        if (r.contains("ADMIN") || r.contains("QUANTRI")) return "QuanTri";
        if (r.contains("BENHNHAN") || r.contains("PATIENT")) return "BenhNhan";
        return "BacSi";
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        List<TaiKhoan> list = taiKhoanRepository.findAll();
        List<Map<String, Object>> res = new ArrayList<>();

        for (TaiKhoan tk : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", tk.getMaTaiKhoan());
            map.put("username", tk.getTenDangNhap());
            map.put("email", tk.getEmail());
            map.put("role", tk.getVaiTro());
            map.put("status", tk.getTrangThai() ? "Hoạt động" : "Bị khóa");

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
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        try {
            String username = (String) payload.get("username");
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Tên đăng nhập không được để trống!"));
            }
            username = username.trim();

            if (taiKhoanRepository.findByTenDangNhap(username).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Tên đăng nhập \"" + username + "\" đã tồn tại!"));
            }

            String password = (String) payload.getOrDefault("password", "123456");
            String rawRole = (String) payload.getOrDefault("role", "BacSi");
            String dbRole = normalizeRole(rawRole);
            String fullName = (String) payload.getOrDefault("fullName", username);
            String email = (String) payload.get("email");

            TaiKhoan tk = new TaiKhoan();
            tk.setTenDangNhap(username);
            tk.setMatKhauHash(passwordEncoder.encode(password));
            tk.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : username.toLowerCase() + "@hospital.com");
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
                nv.setSoDienThoai("09" + (int)(Math.random() * 90000000 + 10000000));
                nv.setEmail(tk.getEmail());
                nv.setVaiTro(dbRole);
                nv.setTrangThai("DangLamViec");
                nhanVienRepository.save(nv);
            }

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

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String oldPassword = req.get("oldPassword");
        String newPassword = req.get("newPassword");

        Optional<TaiKhoan> userOpt = taiKhoanRepository.findByTenDangNhap(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy tài khoản!"));
        }

        TaiKhoan user = userOpt.get();
        if (oldPassword != null && !passwordEncoder.matches(oldPassword, user.getMatKhauHash())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu cũ không chính xác!"));
        }

        user.setMatKhauHash(passwordEncoder.encode(newPassword));
        taiKhoanRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Đổi mật khẩu thành công!"));
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        return taiKhoanRepository.findById(id).map(user -> {
            user.setMatKhauHash(passwordEncoder.encode("123456"));
            taiKhoanRepository.save(user);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã đặt lại mật khẩu về mặc định (123456)!"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
