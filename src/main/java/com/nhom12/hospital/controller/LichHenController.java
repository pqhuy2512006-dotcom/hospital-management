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

    
    @GetMapping("/available-slots")
    public ResponseEntity<List<String>> getAvailableSlots(
            @RequestParam String doctorId,
            @RequestParam String date,
            @RequestParam(defaultValue = "KhamThuong") String loaiKham) {
        
        LocalDate kDate;
        try {
            kDate = LocalDate.parse(date);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        
        if (kDate.isBefore(LocalDate.now())) {
            return ResponseEntity.ok(List.of()); // No slots in the past
        }

        Integer duration = expectedDuration(loaiKham);
        if (duration == null) duration = 30;

        List<LichHen> existing = lichHenRepository.findByMaBacSiAndNgayKham(doctorId, kDate).stream()
                .filter(lh -> !"DaHuy".equalsIgnoreCase(lh.getTrangThai()))
                .toList();

        List<String> availableSlots = new ArrayList<>();
        // Khung giờ sáng: 08:00 - 11:30
        generateSlots(availableSlots, existing, kDate, LocalTime.of(8, 0), LocalTime.of(11, 30), duration);
        // Khung giờ chiều: 13:00 - 16:30
        generateSlots(availableSlots, existing, kDate, LocalTime.of(13, 0), LocalTime.of(16, 30), duration);

        return ResponseEntity.ok(availableSlots);
    }

    private void generateSlots(List<String> availableSlots, List<LichHen> existing, LocalDate kDate, LocalTime start, LocalTime end, int duration) {
        LocalTime current = start;
        LocalDateTime now = LocalDateTime.now();
        while (!current.plusMinutes(duration).isAfter(end)) {
            LocalDateTime slotStart = LocalDateTime.of(kDate, current);
            if (slotStart.isAfter(now)) { // Only future slots
                LocalDateTime slotEnd = slotStart.plusMinutes(duration);
                boolean conflict = existing.stream().anyMatch(lh -> {
                    Integer exDur = lh.getThoiGianKhamDuKien() != null ? lh.getThoiGianKhamDuKien() : expectedDuration(lh.getLoaiKham());
                    if (exDur == null || lh.getGioKham() == null) return false;
                    LocalDateTime exStart = LocalDateTime.of(lh.getNgayKham(), lh.getGioKham());
                    LocalDateTime exEnd = exStart.plusMinutes(exDur);
                    return exStart.isBefore(slotEnd) && slotStart.isBefore(exEnd);
                });
                if (!conflict) {
                    availableSlots.add(current.toString());
                }
            }
            current = current.plusMinutes(30); // Giả sử mỗi slot cách nhau 30p để dễ nhìn
        }
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
        if (isPatient(request)) {
            return benhNhanRepository.findByMaTaiKhoan(accountId)
                    .<ResponseEntity<?>>map(patient -> ResponseEntity.ok(
                            lichHenRepository.findByMaBenhNhan(patient.getMaBenhNhan())))
                    .orElseGet(() -> ResponseEntity.notFound().build());
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
    public ResponseEntity<?> create(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        if (isPatient(request)) {
            return createPatientAppointment(req, request);
        }
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
        Integer duration = expectedDuration(lh.getLoaiKham());
        if (duration == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Loại khám không hợp lệ."));
        }
        lh.setThoiGianKhamDuKien(duration);
        lh.setHinhThucDat((String) req.getOrDefault("hinhThucDat", "Online"));
        lh.setLyDoKham((String) req.get("lyDoKham"));
        lh.setTrangThai("DaDatLich");
        lh.setNgayDatLich(LocalDateTime.now());

        if (hasScheduleConflict(lh, null)) {
            return ResponseEntity.status(409).body(Map.of("message", "Bác sĩ đã có lịch khám giao với khung giờ này."));
        }
        LichHen saved = lichHenRepository.save(lh);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String id, HttpServletRequest request) {
        Optional<LichHen> appointment = lichHenRepository.findById(id);
        if (appointment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (isPatient(request)) {
            if (!ownsAppointment(appointment.get(), request)) {
                return ResponseEntity.status(403).body(Map.of("message", "Bạn chỉ được hủy lịch hẹn của mình."));
            }
            if (!canPatientChange(appointment.get())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Chỉ được hủy lịch trước giờ khám hơn 24 tiếng."));
            }
        }
        LichHen current = appointment.get();
        current.setTrangThai("DaHuy");
        return ResponseEntity.ok(lichHenRepository.save(current));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<?> reschedule(@PathVariable String id,
                                        @RequestBody Map<String, String> requestBody,
                                        HttpServletRequest request) {
        if (!isPatient(request)) {
            return ResponseEntity.status(403).body(Map.of("message", "Chức năng này chỉ dành cho bệnh nhân."));
        }
        Optional<LichHen> appointment = lichHenRepository.findById(id);
        if (appointment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        LichHen current = appointment.get();
        if (!ownsAppointment(current, request)) {
            return ResponseEntity.status(403).body(Map.of("message", "Bạn chỉ được đổi lịch hẹn của mình."));
        }
        if (!canPatientChange(current)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Chỉ được đổi lịch trước giờ khám hơn 24 tiếng."));
        }
        try {
            LocalDate newDate = LocalDate.parse(requestBody.get("ngayKham"));
            LocalTime newTime = LocalTime.parse(requestBody.get("gioKham"));
            if (!LocalDateTime.of(newDate, newTime).isAfter(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Thời gian hẹn mới phải ở trong tương lai."));
            }
            Integer duration = expectedDuration(current.getLoaiKham());
            if (duration == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Loại khám của lịch hẹn không hợp lệ."));
            }
            current.setNgayKham(newDate);
            current.setGioKham(newTime);
            current.setThoiGianKhamDuKien(duration);
            if (hasScheduleConflict(current, id)) {
                return ResponseEntity.status(409).body(Map.of("message", "Bác sĩ đã có lịch khám giao với khung giờ này."));
            }
            return ResponseEntity.ok(lichHenRepository.save(current));
        } catch (RuntimeException error) {
            return ResponseEntity.badRequest().body(Map.of("message", "Ngày hoặc giờ khám không hợp lệ."));
        }
    }

    @GetMapping("/queue")
    public ResponseEntity<?> getDoctorQueue(HttpServletRequest request) {
        if (!isDoctor(request)) {
            return ResponseEntity.status(403).body(Map.of("message", "Chức năng này chỉ dành cho bác sĩ."));
        }
        Optional<NhanVien> doctor = findCurrentDoctor(request);
        if (doctor.isEmpty()) {
            return ResponseEntity.status(403).body(Map.of("message", "Không tìm thấy hồ sơ bác sĩ."));
        }
        List<Map<String, Object>> queue = lichHenRepository.getQueueForDoctor(doctor.get().getMaNhanVien(), LocalDate.now());
        return ResponseEntity.ok(queue);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startExamination(@PathVariable String id, HttpServletRequest request) {
        if (!isDoctor(request)) {
            return ResponseEntity.status(403).body(Map.of("message", "Chỉ bác sĩ mới có quyền tiếp nhận."));
        }
        Optional<LichHen> appointment = lichHenRepository.findById(id);
        if (appointment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        LichHen current = appointment.get();
        Optional<NhanVien> doctor = findCurrentDoctor(request);
        if (doctor.isEmpty() || !doctor.get().getMaNhanVien().equals(current.getMaBacSi())) {
            return ResponseEntity.status(403).body(Map.of("message", "Bạn không được phép khám ca này."));
        }
        
        try {
            // Sinh mã phiếu khám ngẫu nhiên theo định dạng PK + time
            String maPhieuKham = "PK" + id.substring(id.length() > 6 ? id.length() - 6 : 0) + (System.currentTimeMillis() % 10000);
            lichHenRepository.startExamination(id, maPhieuKham, current.getMaBacSi(), current.getMaKhoa());
            return ResponseEntity.ok(Map.of("message", "Bắt đầu khám thành công.", "maPhieuKham", maPhieuKham));
        } catch (org.springframework.dao.DataAccessException e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định khi tiếp nhận bệnh nhân.";
            // Trích xuất câu thông báo lỗi thực tế từ SQL Server (RAISERROR)
            if (errorMsg.contains("Bệnh nhân đã đến trễ quá 10 phút")) {
                return ResponseEntity.status(400).body(Map.of("message", "Bệnh nhân đã đến trễ quá 10 phút. Lịch hẹn đã bị tự động hủy!"));
            }
            return ResponseEntity.status(400).body(Map.of("message", "Không thể bắt đầu khám: " + errorMsg));
        }
    }

    private ResponseEntity<?> createPatientAppointment(Map<String, Object> requestBody, HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        Optional<BenhNhan> patient = accountId == null
                ? Optional.empty()
                : benhNhanRepository.findByMaTaiKhoan(accountId);
        if (patient.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng tạo hồ sơ bệnh nhân trước khi đặt lịch."));
        }
        try {
            LocalDate date = LocalDate.parse(String.valueOf(requestBody.get("ngayKham")));
            LocalTime time = LocalTime.parse(String.valueOf(requestBody.get("gioKham")));
            if (!LocalDateTime.of(date, time).isAfter(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Thời gian khám phải ở trong tương lai."));
            }
            String departmentId = String.valueOf(requestBody.get("maKhoa"));
            String doctorId = String.valueOf(requestBody.get("maBacSi"));
            if (khoaRepository.findById(departmentId).isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Khoa khám không hợp lệ."));
            }
            Optional<NhanVien> doctor = nhanVienRepository.findById(doctorId)
                    .filter(employee -> "BacSi".equalsIgnoreCase(employee.getVaiTro()))
                    .filter(employee -> !"DaNghi".equalsIgnoreCase(employee.getTrangThai()));
            if (doctor.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Bác sĩ được chọn không hợp lệ."));
            }

            LichHen appointment = new LichHen();
            appointment.setMaLichHen("LH2026" + String.format("%06d", lichHenRepository.count() + 1));
            appointment.setMaBenhNhan(patient.get().getMaBenhNhan());
            appointment.setMaKhoa(departmentId);
            appointment.setMaBacSi(doctorId);
            appointment.setNgayKham(date);
            appointment.setGioKham(time);
            String visitType = String.valueOf(requestBody.getOrDefault("loaiKham", "KhamThuong"));
            Integer duration = expectedDuration(visitType);
            if (duration == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Loại khám không hợp lệ."));
            }
            appointment.setLoaiKham(visitType);
            appointment.setThoiGianKhamDuKien(duration);
            appointment.setHinhThucDat("Online");
            appointment.setLyDoKham((String) requestBody.get("lyDoKham"));
            appointment.setTrangThai("DaDatLich");
            appointment.setNgayDatLich(LocalDateTime.now());
            if (hasScheduleConflict(appointment, null)) {
                return ResponseEntity.status(409).body(Map.of("message", "Bác sĩ đã có lịch khám giao với khung giờ này."));
            }
            return ResponseEntity.ok(lichHenRepository.save(appointment));
        } catch (RuntimeException error) {
            return ResponseEntity.badRequest().body(Map.of("message", "Ngày, giờ, khoa hoặc bác sĩ không hợp lệ."));
        }
    }

    private boolean isPatient(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        return accountId != null && taiKhoanRepository.findById(accountId)
                .map(TaiKhoan::getVaiTro)
                .filter("BenhNhan"::equalsIgnoreCase)
                .isPresent();
    }

    private boolean ownsAppointment(LichHen appointment, HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        return accountId != null && benhNhanRepository.findByMaTaiKhoan(accountId)
                .map(patient -> patient.getMaBenhNhan().equals(appointment.getMaBenhNhan()))
                .orElse(false);
    }

    private boolean canPatientChange(LichHen appointment) {
        if (appointment.getNgayKham() == null || appointment.getGioKham() == null
                || "DaHuy".equalsIgnoreCase(appointment.getTrangThai())
                || "DaKham".equalsIgnoreCase(appointment.getTrangThai())) {
            return false;
        }
        return !LocalDateTime.of(appointment.getNgayKham(), appointment.getGioKham())
            .isBefore(LocalDateTime.now().plusHours(24));
    }

    private Integer expectedDuration(String visitType) {
        if ("KhamThuong".equals(visitType) || "TaiKham".equals(visitType)) {
            return 30;
        }
        if ("KhamDichVu".equals(visitType)) {
            return 45;
        }
        return null;
    }

    private boolean hasScheduleConflict(LichHen candidate, String excludedAppointmentId) {
        if (candidate.getMaBacSi() == null || candidate.getNgayKham() == null
                || candidate.getGioKham() == null || candidate.getThoiGianKhamDuKien() == null) {
            return false;
        }
        LocalDateTime candidateStart = LocalDateTime.of(candidate.getNgayKham(), candidate.getGioKham());
        LocalDateTime candidateEnd = candidateStart.plusMinutes(candidate.getThoiGianKhamDuKien());
        return lichHenRepository.findByMaBacSiAndNgayKham(candidate.getMaBacSi(), candidate.getNgayKham()).stream()
                .filter(existing -> !"DaHuy".equalsIgnoreCase(existing.getTrangThai()))
                .filter(existing -> excludedAppointmentId == null
                        || !excludedAppointmentId.equals(existing.getMaLichHen()))
                .anyMatch(existing -> {
                    Integer existingDuration = existing.getThoiGianKhamDuKien() != null
                            ? existing.getThoiGianKhamDuKien()
                            : expectedDuration(existing.getLoaiKham());
                    if (existingDuration == null || existing.getGioKham() == null) {
                        return false;
                    }
                    LocalDateTime existingStart = LocalDateTime.of(existing.getNgayKham(), existing.getGioKham());
                    LocalDateTime existingEnd = existingStart.plusMinutes(existingDuration);
                    return existingStart.isBefore(candidateEnd) && candidateStart.isBefore(existingEnd);
                });
    }
}
