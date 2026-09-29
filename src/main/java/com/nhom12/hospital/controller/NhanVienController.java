package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/nhanvien")
@CrossOrigin(origins = "*")
public class NhanVienController {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    public NhanVienController(NhanVienRepository nhanVienRepository, TaiKhoanRepository taiKhoanRepository) {
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @GetMapping
    public List<NhanVien> getAll() {
        return nhanVienRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NhanVien> getById(@PathVariable String id) {
        return nhanVienRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(HttpServletRequest request) {
        return findCurrentEmployee(request)
                .map(employee -> ResponseEntity.ok(profileView(employee)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doctors")
    public List<Map<String, String>> getDoctorsForConsultation() {
        return nhanVienRepository.findAll().stream()
                .filter(employee -> "BacSi".equalsIgnoreCase(employee.getVaiTro()))
                .filter(employee -> !"DaNghi".equalsIgnoreCase(employee.getTrangThai()))
                .map(employee -> {
                    Map<String, String> doctor = new LinkedHashMap<>();
                    doctor.put("id", employee.getMaNhanVien());
                    doctor.put("name", employee.getHoTen());
                    doctor.put("specialty", employee.getChuyenKhoa());
                    return doctor;
                })
                .collect(Collectors.toList());
    }

    @PutMapping("/me")
    @Transactional
    public ResponseEntity<?> updateMyProfile(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        Optional<NhanVien> employeeOpt = findCurrentEmployee(request);
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        NhanVien employee = employeeOpt.get();
        String phone = payload.get("soDienThoai");
        if (phone == null || phone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại không được để trống."));
        }
        phone = phone.trim();
        if (nhanVienRepository.existsBySoDienThoaiAndMaNhanVienNot(phone, employee.getMaNhanVien())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại đã được nhân viên khác sử dụng."));
        }

        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        Optional<TaiKhoan> accountOpt = taiKhoanRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String email = normalizeOptional(payload.get("email"));
        if (email != null && taiKhoanRepository.findByEmail(email)
                .filter(account -> !account.getMaTaiKhoan().equals(accountId)).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email đã được tài khoản khác sử dụng."));
        }

        employee.setSoDienThoai(phone);
        employee.setEmail(email);
        employee.setDiaChi(normalizeOptional(payload.get("diaChi")));
        nhanVienRepository.save(employee);

        TaiKhoan account = accountOpt.get();
        account.setEmail(email);
        taiKhoanRepository.save(account);

        return ResponseEntity.ok(profileView(employee));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NhanVien nhanVien) {
        if (nhanVien.getMaNhanVien() == null || nhanVien.getMaNhanVien().trim().isEmpty()) {
            nhanVien.setMaNhanVien("NV-DOC" + String.format("%02d", nhanVienRepository.count() + 1));
        }
        if (nhanVien.getSoDienThoai() != null) {
            boolean sdtExists = nhanVienRepository.findAll().stream()
                    .anyMatch(nv -> nhanVien.getSoDienThoai().equals(nv.getSoDienThoai()));
            if (sdtExists) {
                return ResponseEntity.badRequest().body("Số điện thoại " + nhanVien.getSoDienThoai() + " đã được đăng ký cho nhân viên khác!");
            }
        }
        if (nhanVien.getVaiTro() == null) {
            nhanVien.setVaiTro("BacSi");
        }
        if (nhanVien.getTrangThai() == null) {
            nhanVien.setTrangThai("DangLamViec");
        }
        return ResponseEntity.ok(nhanVienRepository.save(nhanVien));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NhanVien> update(@PathVariable String id, @RequestBody NhanVien updated) {
        return nhanVienRepository.findById(id).map(nv -> {
            nv.setHoTen(updated.getHoTen());
            nv.setTrinhDoChuyenMon(updated.getTrinhDoChuyenMon());
            nv.setChuyenKhoa(updated.getChuyenKhoa());
            nv.setDiaChi(updated.getDiaChi());
            nv.setSoDienThoai(updated.getSoDienThoai());
            nv.setChungChiHanhNghe(updated.getChungChiHanhNghe());
            nv.setTrangThai(updated.getTrangThai());
            return ResponseEntity.ok(nhanVienRepository.save(nv));
        }).orElse(ResponseEntity.notFound().build());
    }

    private Optional<NhanVien> findCurrentEmployee(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        return accountId == null ? Optional.empty() : nhanVienRepository.findByMaTaiKhoan(accountId);
    }

    private Map<String, Object> profileView(NhanVien employee) {
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("maNhanVien", employee.getMaNhanVien());
        profile.put("hoTen", employee.getHoTen());
        profile.put("vaiTro", employee.getVaiTro());
        profile.put("chuyenKhoa", employee.getChuyenKhoa());
        profile.put("chungChiHanhNghe", employee.getChungChiHanhNghe());
        profile.put("soDienThoai", employee.getSoDienThoai());
        profile.put("email", employee.getEmail());
        profile.put("diaChi", employee.getDiaChi());
        return profile;
    }

    private String normalizeOptional(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
