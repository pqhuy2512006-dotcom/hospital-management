package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.BenhNhan;
import com.nhom12.hospital.entity.LichHen;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.repository.BenhNhanRepository;
import com.nhom12.hospital.repository.KhoaRepository;
import com.nhom12.hospital.repository.LichHenRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/lichhen")
@CrossOrigin(origins = "*")
public class LichHenController {

    private final LichHenRepository lichHenRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final KhoaRepository khoaRepository;
    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    public LichHenController(LichHenRepository lichHenRepository,
                             BenhNhanRepository benhNhanRepository,
                             KhoaRepository khoaRepository,
                             NhanVienRepository nhanVienRepository,
                             TaiKhoanRepository taiKhoanRepository) {
        this.lichHenRepository = lichHenRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.khoaRepository = khoaRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @GetMapping
    public List<LichHen> getAll(HttpServletRequest request) {
        if (isDoctor(request)) {
            return findCurrentDoctor(request)
                    .map(doctor -> lichHenRepository.findByMaBacSi(doctor.getMaNhanVien()))
                    .orElseGet(List::of);
        }
        return lichHenRepository.findAll();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyAppointments(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        if (accountId == null) {
            return ResponseEntity.status(403).body(Map.of("message", "Yêu cầu đăng nhập."));
        }
        Optional<NhanVien> doctor = nhanVienRepository.findByMaTaiKhoan(accountId)
                .filter(employee -> "BacSi".equalsIgnoreCase(employee.getVaiTro()));
        if (doctor.isEmpty()) {
            return ResponseEntity.status(403).body(Map.of("message", "Không tìm thấy hồ sơ bác sĩ."));
        }
        return ResponseEntity.ok(lichHenRepository.findByMaBacSi(doctor.get().getMaNhanVien()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LichHen> getById(@PathVariable String id, HttpServletRequest request) {
        Optional<LichHen> appointment = lichHenRepository.findById(id);
        if (appointment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (isDoctor(request) && findCurrentDoctor(request)
                .filter(doctor -> doctor.getMaNhanVien().equals(appointment.get().getMaBacSi())).isEmpty()) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(appointment.get());
    }

    private Optional<NhanVien> findCurrentDoctor(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        return accountId == null ? Optional.empty() : nhanVienRepository.findByMaTaiKhoan(accountId)
                .filter(employee -> "BacSi".equalsIgnoreCase(employee.getVaiTro()));
    }

    private boolean isDoctor(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        return accountId != null && taiKhoanRepository.findById(accountId)
                .map(TaiKhoan::getVaiTro)
                .filter("BacSi"::equalsIgnoreCase)
                .isPresent();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> req) {
        String maLH = "LH2026" + String.format("%06d", lichHenRepository.count() + 1);
        LichHen lh = new LichHen();
        lh.setMaLichHen(maLH);

        // Xử lý bệnh nhân: có thể gửi maBenhNhan hoặc tên bệnh nhân
        String maBN = (String) req.get("maBenhNhan");
        String tenBN = (String) req.get("patientName");
        String cccd = (String) req.get("cccd");

        if (maBN == null || maBN.trim().isEmpty()) {
            // Tìm theo CCCD nếu có
            if (cccd != null && !cccd.trim().isEmpty()) {
                Optional<BenhNhan> bnOpt = benhNhanRepository.findAll().stream()
                        .filter(b -> cccd.equals(b.getSoCCCD()))
                        .findFirst();
                if (bnOpt.isPresent()) {
                    maBN = bnOpt.get().getMaBenhNhan();
                }
            }
            // Nếu vẫn chưa có, tạo nhanh bệnh nhân mới
            if (maBN == null || maBN.trim().isEmpty()) {
                BenhNhan newBn = new BenhNhan();
                newBn.setMaBenhNhan("BN2026" + String.format("%06d", benhNhanRepository.count() + 1));
                newBn.setHoTen(tenBN != null && !tenBN.trim().isEmpty() ? tenBN : "Bệnh nhân mới");
                newBn.setNgaySinh(LocalDate.of(1990, 1, 1));
                newBn.setGioiTinh("Nam");
                newBn.setSoCCCD(cccd != null ? cccd : "");
                newBn.setSoDienThoai("09" + (int)(Math.random() * 90000000 + 10000000));
                newBn.setNgayTaoHoSo(LocalDateTime.now());
                BenhNhan savedBn = benhNhanRepository.save(newBn);
                maBN = savedBn.getMaBenhNhan();
            }
        }
        lh.setMaBenhNhan(maBN);

        // Xử lý khoa
        String maKhoa = (String) req.get("maKhoa");
        if (maKhoa == null || maKhoa.trim().isEmpty()) {
            String deptName = (String) req.get("dept");
            if (deptName != null) {
                if (deptName.contains("Nội")) maKhoa = "KNT";
                else if (deptName.contains("Ngoại")) maKhoa = "KNG";
                else if (deptName.contains("Tai")) maKhoa = "TMH";
                else if (deptName.contains("Cấp Cứu")) maKhoa = "KCC";
                else maKhoa = "KKB";
            } else {
                maKhoa = "KKB";
            }
        }
        lh.setMaKhoa(maKhoa);

        lh.setMaBacSi((String) req.getOrDefault("maBacSi", "NV-DOC01"));
        lh.setNgayKham(req.get("ngayKham") != null ? LocalDate.parse(req.get("ngayKham").toString()) : LocalDate.now());

        String gioKhamStr = (String) req.get("gioKham");
        if (gioKhamStr != null && gioKhamStr.length() >= 5) {
            try {
                lh.setGioKham(LocalTime.parse(gioKhamStr.substring(0, 5) + ":00"));
            } catch (Exception e) {
                lh.setGioKham(LocalTime.of(8, 30));
            }
        } else {
            lh.setGioKham(LocalTime.of(8, 30));
        }

        lh.setLoaiKham((String) req.getOrDefault("loaiKham", "KhamThuong"));
        lh.setHinhThucDat((String) req.getOrDefault("hinhThucDat", "Online"));
        lh.setLyDoKham((String) req.get("lyDoKham"));
        lh.setGhiChu((String) req.get("ghiChu"));
        lh.setTrangThai("ChoXacNhan");
        lh.setNgayDatLich(LocalDateTime.now());

        LichHen saved = lichHenRepository.save(lh);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String id) {
        return lichHenRepository.findById(id).map(lh -> {
            lh.setTrangThai("DaHuy");
            return ResponseEntity.ok(lichHenRepository.save(lh));
        }).orElse(ResponseEntity.notFound().build());
    }
}
