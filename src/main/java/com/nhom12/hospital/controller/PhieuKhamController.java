package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.*;
import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/phieukham")
@CrossOrigin(origins = "*")
public class PhieuKhamController {

    private final PhieuKhamRepository phieuKhamRepository;
    private final DonThuocRepository donThuocRepository;
    private final ThuocRepository thuocRepository;
    private final LichHenRepository lichHenRepository;
    private final KetQuaCLSRepository ketQuaCLSRepository;
    private final DichVuCLSRepository dichVuCLSRepository;
    private final HoaDonRepository hoaDonRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhoaRepository khoaRepository;

    public PhieuKhamController(PhieuKhamRepository phieuKhamRepository,
                               DonThuocRepository donThuocRepository,
                               ThuocRepository thuocRepository,
                               LichHenRepository lichHenRepository,
                               KetQuaCLSRepository ketQuaCLSRepository,
                               DichVuCLSRepository dichVuCLSRepository,
                               HoaDonRepository hoaDonRepository,
                               ChiTietHoaDonRepository chiTietHoaDonRepository,
                               BenhNhanRepository benhNhanRepository,
                               NhanVienRepository nhanVienRepository,
                               KhoaRepository khoaRepository) {
        this.phieuKhamRepository = phieuKhamRepository;
        this.donThuocRepository = donThuocRepository;
        this.thuocRepository = thuocRepository;
        this.lichHenRepository = lichHenRepository;
        this.ketQuaCLSRepository = ketQuaCLSRepository;
        this.dichVuCLSRepository = dichVuCLSRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khoaRepository = khoaRepository;
    }

    @GetMapping
    public List<PhieuKham> getAll() {
        return phieuKhamRepository.findAll();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyExaminations(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        if (accountId == null) {
            return ResponseEntity.status(403).body(Map.of("message", "Yêu cầu đăng nhập."));
        }
        Optional<NhanVien> doctor = nhanVienRepository.findByMaTaiKhoan(accountId)
                .filter(employee -> "BacSi".equalsIgnoreCase(employee.getVaiTro()));
        if (doctor.isEmpty()) {
            return ResponseEntity.status(403).body(Map.of("message", "Không tìm thấy hồ sơ bác sĩ."));
        }
        return ResponseEntity.ok(phieuKhamRepository.findByMaBacSi(doctor.get().getMaNhanVien()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable String id) {
        return phieuKhamRepository.findById(id).map(pk -> {
            Map<String, Object> map = new HashMap<>();
            map.put("phieuKham", pk);
            map.put("donThuoc", donThuocRepository.findByMaPhieuKham(id));
            map.put("ketQuaCLS", ketQuaCLSRepository.findByMaPhieuKham(id));
            benhNhanRepository.findById(pk.getMaBenhNhan()).ifPresent(bn -> map.put("benhNhan", bn));
            return ResponseEntity.ok(map);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> hoanTatKham(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        Optional<NhanVien> doctorOpt = accountId == null ? Optional.empty()
                : nhanVienRepository.findByMaTaiKhoan(accountId)
                        .filter(employee -> "BacSi".equalsIgnoreCase(employee.getVaiTro()));
        if (doctorOpt.isEmpty()) {
            return ResponseEntity.status(403).body(Map.of("message", "Không tìm thấy hồ sơ bác sĩ."));
        }

        String rawLichHen = (String) payload.get("maLichHen");
        Optional<LichHen> appointmentOpt = rawLichHen == null ? Optional.empty() : lichHenRepository.findById(rawLichHen);
        if (appointmentOpt.isEmpty()
                || !doctorOpt.get().getMaNhanVien().equals(appointmentOpt.get().getMaBacSi())
                || "DaKham".equalsIgnoreCase(appointmentOpt.get().getTrangThai())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lịch khám không hợp lệ, không thuộc bác sĩ hoặc đã hoàn tất."));
        }
        
        LichHen appointment = appointmentOpt.get();
        
        // Bắt buộc phải có Phiếu Khám đã được tạo từ bước "Bắt đầu khám"
        Optional<PhieuKham> existingPk = phieuKhamRepository.findByMaLichHen(appointment.getMaLichHen());
        if (existingPk.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy Phiếu khám. Vui lòng bấm Bắt đầu khám trước."));
        }
        PhieuKham pk = existingPk.get();
        String maPK = pk.getMaPhieuKham();

        pk.setNgayKham(LocalDateTime.now());
        pk.setTrieuChung((String) payload.get("trieuChung"));
        pk.setChanDoan((String) payload.getOrDefault("chanDoan", "Viêm họng cấp"));
        pk.setLoiDanBacSi((String) payload.get("loiDanBacSi"));

        if (payload.get("mach") != null) pk.setMach(Integer.parseInt(payload.get("mach").toString()));
        if (payload.get("nhietDo") != null) pk.setNhietDo(new BigDecimal(payload.get("nhietDo").toString()));
        if (payload.get("huyetAp") != null) pk.setHuyetAp((String) payload.get("huyetAp"));
        if (payload.get("nhipTho") != null) pk.setNhipTho(Integer.parseInt(payload.get("nhipTho").toString()));

        phieuKhamRepository.save(pk);

        // Cập nhật trạng thái Lịch hẹn thành 'DaKham' nếu có
        if (pk.getMaLichHen() != null) {
            lichHenRepository.findById(pk.getMaLichHen()).ifPresent(lh -> {
                lh.setTrangThai("DaKham");
                lichHenRepository.save(lh);
            });
        }

        String maHD = "HD" + (System.currentTimeMillis() % 10000000);
        List<ChiTietHoaDon> billItems = new ArrayList<>();
        BigDecimal totalSum = BigDecimal.ZERO;

        // 1. Mục công khám
        ChiTietHoaDon cthdKham = new ChiTietHoaDon();
        cthdKham.setMaChiTietHD("CTHD" + (System.currentTimeMillis() % 1000000) + "_1");
        cthdKham.setMaHoaDon(maHD);
        cthdKham.setTenDichVu("Công khám chuyên khoa");
        cthdKham.setLoaiDichVu("Kham");
        cthdKham.setDonGia(BigDecimal.valueOf(150000));
        cthdKham.setSoLuong(1);
        billItems.add(cthdKham);
        totalSum = totalSum.add(cthdKham.getDonGia());

        // 2. Chuẩn bị Đơn thuốc
        List<DonThuoc> donThuocList = new ArrayList<>();
        List<Map<String, Object>> meds = (List<Map<String, Object>>) payload.get("donThuoc");
        if (meds != null && !meds.isEmpty()) {
            int seq = 1;
            for (Map<String, Object> m : meds) {
                String maThuoc = (String) m.get("maThuoc");
                int qty = Integer.parseInt(m.getOrDefault("soLuong", 1).toString());
                String cachDung = (String) m.getOrDefault("cachDung", "Uống sau ăn");
                String lieuDung = (String) m.getOrDefault("lieuDung", "Theo chỉ định");

                DonThuoc dt = new DonThuoc();
                dt.setMaDonThuoc("DT" + (System.currentTimeMillis() % 1000000) + "_" + seq++);
                dt.setMaPhieuKham(maPK);
                dt.setMaThuoc(maThuoc);
                dt.setSoLuong(qty);
                dt.setDonViTinh((String) m.getOrDefault("donViTinh", "Vien"));
                dt.setCachDung(cachDung);
                dt.setLieuDung(lieuDung);
                donThuocList.add(dt);

                Optional<Thuoc> drugOpt = thuocRepository.findById(maThuoc);
                if (drugOpt.isPresent()) {
                    Thuoc drug = drugOpt.get();
                    int ton = drug.getTonKhoHienTai() != null ? drug.getTonKhoHienTai() : 0;
                    drug.setTonKhoHienTai(Math.max(0, ton - qty));
                    thuocRepository.save(drug);

                    ChiTietHoaDon cthdMed = new ChiTietHoaDon();
                    cthdMed.setMaChiTietHD("CTHD" + (System.currentTimeMillis() % 1000000) + "_" + seq);
                    cthdMed.setMaHoaDon(maHD);
                    cthdMed.setTenDichVu(drug.getTenThuoc());
                    cthdMed.setLoaiDichVu("Thuoc");
                    cthdMed.setDonGia(drug.getDonGiaBan());
                    cthdMed.setSoLuong(qty);
                    billItems.add(cthdMed);

                    totalSum = totalSum.add(drug.getDonGiaBan().multiply(BigDecimal.valueOf(qty)));
                }
            }
        }

        // 3. Chuẩn bị Chỉ định Cận lâm sàng
        List<KetQuaCLS> clsOrders = new ArrayList<>();
        List<?> clsList = (List<?>) payload.get("clsList");
        if (clsList != null && !clsList.isEmpty()) {
            int clsSeq = 1;
            for (Object item : clsList) {
                String maDV = null;
                if (item instanceof String s) {
                    maDV = s;
                } else if (item instanceof Map<?, ?> m) {
                    maDV = (String) m.get("maDichVu");
                }
                if (maDV == null || maDV.trim().isEmpty()) continue;

                final int currentSeq = clsSeq++;
                final String finalMaDV = maDV;
                Optional<DichVuCLS> dvOpt = dichVuCLSRepository.findById(finalMaDV);
                if (dvOpt.isPresent()) {
                    DichVuCLS dv = dvOpt.get();
                    String maKQ = "CLS" + (System.currentTimeMillis() % 1000000) + "_" + currentSeq;
                    KetQuaCLS kq = new KetQuaCLS();
                    kq.setMaKetQua(maKQ);
                    kq.setMaPhieuKham(maPK);
                    kq.setMaBenhNhan(pk.getMaBenhNhan());
                    kq.setMaDichVu(finalMaDV);
                    kq.setMaKTV("NV-LAB01");
                    kq.setMaBacSiDoc(pk.getMaBacSi());
                    kq.setLoaiXetNghiem(dv.getTenDichVu());
                    kq.setNgayThucHien(LocalDateTime.now());
                    clsOrders.add(kq);

                    ChiTietHoaDon cthdCLS = new ChiTietHoaDon();
                    cthdCLS.setMaChiTietHD("CTHD" + (System.currentTimeMillis() % 1000000) + "_CLS" + currentSeq);
                    cthdCLS.setMaHoaDon(maHD);
                    cthdCLS.setTenDichVu(dv.getTenDichVu());
                    cthdCLS.setLoaiDichVu("CanLamSang");
                    cthdCLS.setDonGia(dv.getDonGia());
                    cthdCLS.setSoLuong(1);
                    billItems.add(cthdCLS);

                    totalSum = totalSum.add(dv.getDonGia());
                }
            }
        }

        // 4. Tính toán BHYT chi trả
        boolean hasBHYT = benhNhanRepository.findById(pk.getMaBenhNhan())
                .map(b -> b.getMaBHYT() != null && !b.getMaBHYT().trim().isEmpty())
                .orElse(false);
        BigDecimal bhytTien = hasBHYT ? totalSum.multiply(BigDecimal.valueOf(0.8)) : BigDecimal.ZERO;

        // 5. Lưu HÓA ĐƠN TRƯỚC (để thỏa mãn khóa ngoại FK_ChiTietHD_HoaDon)
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(maHD);
        hd.setMaBenhNhan(pk.getMaBenhNhan());
        hd.setMaPhieuKham(maPK);
        hd.setNgayLap(LocalDateTime.now());
        hd.setTongTienDichVu(totalSum);
        hd.setBhytChiTra(bhytTien);
        hd.setHinhThucThanhToan("TienMat");
        hd.setTrangThaiTT("ChuaThanhToan");
        hd.setNhanVienThu("NV-CAS01");
        hoaDonRepository.save(hd);

        // 6. Lưu CHI TIẾT HÓA ĐƠN sau khi hóa đơn đã được lưu
        for (ChiTietHoaDon item : billItems) {
            chiTietHoaDonRepository.save(item);
        }

        // 7. Lưu Đơn thuốc & CLS
        donThuocRepository.saveAll(donThuocList);
        ketQuaCLSRepository.saveAll(clsOrders);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "maPhieuKham", maPK,
                "maHoaDon", maHD,
                "message", "Khám bệnh thành công! Đã tạo đơn thuốc và chuyển hóa đơn sang viện phí."
        ));
    }
}
