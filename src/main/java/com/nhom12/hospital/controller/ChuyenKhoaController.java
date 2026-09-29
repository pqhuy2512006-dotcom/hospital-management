package com.nhom12.hospital.controller;

import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.YeuCauChuyenKhoa;
import com.nhom12.hospital.repository.KhoaRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.PhieuKhamRepository;
import com.nhom12.hospital.repository.YeuCauChuyenKhoaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/chuyenkhoa")
public class ChuyenKhoaController {

    private final YeuCauChuyenKhoaRepository requestRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhoaRepository khoaRepository;
    private final PhieuKhamRepository phieuKhamRepository;

    public ChuyenKhoaController(YeuCauChuyenKhoaRepository requestRepository,
                                NhanVienRepository nhanVienRepository,
                                KhoaRepository khoaRepository,
                                PhieuKhamRepository phieuKhamRepository) {
        this.requestRepository = requestRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khoaRepository = khoaRepository;
        this.phieuKhamRepository = phieuKhamRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyRequests(HttpServletRequest request) {
        Optional<NhanVien> doctor = findCurrentDoctor(request);
        if (doctor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Không tìm thấy hồ sơ bác sĩ."));
        }
        List<YeuCauChuyenKhoa> requests = requestRepository
                .findByMaBacSiGuiOrderByNgayTaoDesc(doctor.get().getMaNhanVien());
        return ResponseEntity.ok(requests);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        Optional<NhanVien> doctorOpt = findCurrentDoctor(request);
        if (doctorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Không tìm thấy hồ sơ bác sĩ."));
        }

        String type = payload.get("loaiYeuCau");
        String examId = payload.get("maPhieuKham");
        String departmentId = payload.get("maKhoaNhan");
        String reason = payload.get("lyDo");
        String invitedDoctorId = normalizeOptional(payload.get("maBacSiDuocMoi"));

        if (!"CHUYEN_KHOA".equals(type) && !"HOI_CHAN".equals(type)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Loại yêu cầu không hợp lệ."));
        }
        if (examId == null || examId.isBlank() || departmentId == null || departmentId.isBlank()
                || reason == null || reason.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cần chọn phiếu khám, khoa nhận và nhập lý do."));
        }
        if (reason.length() > 500) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lý do tối đa 500 ký tự."));
        }
        if ("HOI_CHAN".equals(type) && invitedDoctorId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Hội chẩn cần chọn bác sĩ được mời."));
        }

        var doctor = doctorOpt.get();
        var examOpt = phieuKhamRepository.findById(examId);
        if (examOpt.isEmpty() || !doctor.getMaNhanVien().equals(examOpt.get().getMaBacSi())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ được chuyển khoa/hội chẩn trên phiếu khám của chính bác sĩ."));
        }
        if (!khoaRepository.existsById(departmentId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Khoa nhận không tồn tại."));
        }
        if (invitedDoctorId != null) {
            Optional<NhanVien> invitedDoctor = nhanVienRepository.findById(invitedDoctorId);
            if (invitedDoctor.isEmpty() || !"BacSi".equalsIgnoreCase(invitedDoctor.get().getVaiTro())
                    || "DaNghi".equalsIgnoreCase(invitedDoctor.get().getTrangThai())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Bác sĩ được mời không hợp lệ."));
            }
        }

        YeuCauChuyenKhoa referral = new YeuCauChuyenKhoa();
        referral.setMaYeuCau("YC" + String.format("%013d", System.currentTimeMillis()));
        referral.setLoaiYeuCau(type);
        referral.setMaPhieuKham(examId);
        referral.setMaBenhNhan(examOpt.get().getMaBenhNhan());
        referral.setMaBacSiGui(doctor.getMaNhanVien());
        referral.setMaKhoaNhan(departmentId);
        referral.setMaBacSiDuocMoi(invitedDoctorId);
        referral.setLyDo(reason.trim());
        referral.setTrangThai("CHO_TIEP_NHAN");

        YeuCauChuyenKhoa saved = requestRepository.save(referral);
        request.setAttribute(SessionAttributes.AUDIT_DETAIL,
                "Tạo yêu cầu " + type + "; mã=" + saved.getMaYeuCau() + "; phiếu khám=" + examId);
        return ResponseEntity.ok(saved);
    }

    private Optional<NhanVien> findCurrentDoctor(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        return accountId == null ? Optional.empty() : nhanVienRepository.findByMaTaiKhoan(accountId)
                .filter(employee -> "BacSi".equalsIgnoreCase(employee.getVaiTro()));
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}