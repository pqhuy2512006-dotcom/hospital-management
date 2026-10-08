package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.PhieuKham;
import com.nhom12.hospital.entity.YeuCauChuyenKhoa;
import com.nhom12.hospital.repository.KhoaRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.PhieuKhamRepository;
import com.nhom12.hospital.repository.YeuCauChuyenKhoaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChuyenKhoaService {

    private final YeuCauChuyenKhoaRepository requestRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhoaRepository khoaRepository;
    private final PhieuKhamRepository phieuKhamRepository;

    public ChuyenKhoaService(YeuCauChuyenKhoaRepository requestRepository,
                             NhanVienRepository nhanVienRepository,
                             KhoaRepository khoaRepository,
                             PhieuKhamRepository phieuKhamRepository) {
        this.requestRepository = requestRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khoaRepository = khoaRepository;
        this.phieuKhamRepository = phieuKhamRepository;
    }

    public List<YeuCauChuyenKhoa> getMyRequests(Long accountId) {
        Optional<NhanVien> doctor = findCurrentDoctor(accountId);
        if (doctor.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy hồ sơ bác sĩ.");
        }
        return requestRepository.findByMaBacSiGuiOrderByNgayTaoDesc(doctor.get().getMaNhanVien());
    }

    public YeuCauChuyenKhoa createRequest(Map<String, String> payload, Long accountId) {
        Optional<NhanVien> doctorOpt = findCurrentDoctor(accountId);
        if (doctorOpt.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy hồ sơ bác sĩ.");
        }

        String type = payload.get("loaiYeuCau");
        String examId = payload.get("maPhieuKham");
        String departmentId = payload.get("maKhoaNhan");
        String reason = payload.get("lyDo");
        String invitedDoctorId = normalizeOptional(payload.get("maBacSiDuocMoi"));

        if (!"CHUYEN_KHOA".equals(type) && !"HOI_CHAN".equals(type)) {
            throw new IllegalArgumentException("Loại yêu cầu không hợp lệ.");
        }
        if (examId == null || examId.isBlank() || departmentId == null || departmentId.isBlank()
                || reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Cần chọn phiếu khám, khoa nhận và nhập lý do.");
        }
        if (reason.length() > 500) {
            throw new IllegalArgumentException("Lý do tối đa 500 ký tự.");
        }
        if ("HOI_CHAN".equals(type) && invitedDoctorId == null) {
            throw new IllegalArgumentException("Hội chẩn cần chọn bác sĩ được mời.");
        }

        var doctor = doctorOpt.get();
        var examOpt = phieuKhamRepository.findById(examId);
        if (examOpt.isEmpty() || !doctor.getMaNhanVien().equals(examOpt.get().getMaBacSi())) {
            throw new IllegalStateException("Chỉ được chuyển khoa/hội chẩn trên phiếu khám của chính bác sĩ.");
        }
        if (!khoaRepository.existsById(departmentId)) {
            throw new IllegalArgumentException("Khoa nhận không tồn tại.");
        }
        if (invitedDoctorId != null) {
            Optional<NhanVien> invitedDoctor = nhanVienRepository.findById(invitedDoctorId);
            if (invitedDoctor.isEmpty() || !"BacSi".equalsIgnoreCase(invitedDoctor.get().getVaiTro())
                    || "DaNghi".equalsIgnoreCase(invitedDoctor.get().getTrangThai())) {
                throw new IllegalArgumentException("Bác sĩ được mời không hợp lệ.");
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

        return requestRepository.save(referral);
    }

    private Optional<NhanVien> findCurrentDoctor(Long accountId) {
        return accountId == null ? Optional.empty() : nhanVienRepository.findByMaTaiKhoan(accountId)
                .filter(employee -> "BacSi".equalsIgnoreCase(employee.getVaiTro()));
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

