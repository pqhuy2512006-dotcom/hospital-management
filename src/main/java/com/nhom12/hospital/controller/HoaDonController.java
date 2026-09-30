package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.ChiTietHoaDon;
import com.nhom12.hospital.entity.HoaDon;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.repository.BenhNhanRepository;
import com.nhom12.hospital.repository.ChiTietHoaDonRepository;
import com.nhom12.hospital.repository.HoaDonRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/hoadon")
@CrossOrigin(origins = "*")
public class HoaDonController {

    private final HoaDonRepository hoaDonRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;

    public HoaDonController(HoaDonRepository hoaDonRepository,
                             ChiTietHoaDonRepository chiTietHoaDonRepository,
                             BenhNhanRepository benhNhanRepository,
                             TaiKhoanRepository taiKhoanRepository,
                             NhanVienRepository nhanVienRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getAll(HttpServletRequest request) {
        List<HoaDon> list;
        if (isPatientAccount(request)) {
            list = findCurrentPatient(request)
                    .map(patient -> hoaDonRepository.findByMaBenhNhan(patient.getMaBenhNhan()))
                    .orElseGet(List::of);
        } else {
            list = hoaDonRepository.findAll();
        }
        List<Map<String, Object>> res = new ArrayList<>();

        for (HoaDon hd : list) {
            res.add(formatInvoiceMap(hd));
        }

        return res;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id, HttpServletRequest request) {
        Optional<HoaDon> invoice = hoaDonRepository.findById(id);
        if (invoice.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        HoaDon hd = invoice.get();
        if (isPatientAccount(request)) {
            Optional<com.nhom12.hospital.entity.BenhNhan> patient = findCurrentPatient(request);
            if (patient.isEmpty() || !patient.get().getMaBenhNhan().equals(hd.getMaBenhNhan())) {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(formatInvoiceMap(hd));
    }

    /**
     * Chức năng: Tính viện phí tự động từ chi tiết dịch vụ
     */
    @PostMapping("/{id}/tinh-tu-dong")
    public ResponseEntity<?> tinhVienPhiTuDong(@PathVariable String id) {
        Optional<HoaDon> invoiceOpt = hoaDonRepository.findById(id);
        if (invoiceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        HoaDon bill = invoiceOpt.get();
        List<ChiTietHoaDon> details = chiTietHoaDonRepository.findByMaHoaDon(id);

        BigDecimal sumTotal = BigDecimal.ZERO;
        for (ChiTietHoaDon d : details) {
            BigDecimal lineTotal = d.getDonGia().multiply(BigDecimal.valueOf(d.getSoLuong()));
            sumTotal = sumTotal.add(lineTotal);
        }

        bill.setTongTienDichVu(sumTotal);

        // Kiểm tra và áp dụng BHYT tự động nếu bệnh nhân có BHYT
        boolean hasBHYT = benhNhanRepository.findById(bill.getMaBenhNhan())
                .map(bn -> bn.getMaBHYT() != null && !bn.getMaBHYT().trim().isEmpty())
                .orElse(false);

        if (hasBHYT) {
            bill.setBhytChiTra(sumTotal.multiply(BigDecimal.valueOf(0.8)));
        } else {
            bill.setBhytChiTra(BigDecimal.ZERO);
        }

        hoaDonRepository.save(bill);

        Map<String, Object> res = formatInvoiceMap(bill);
        res.put("message", "Đã tự động tính toán lại viện phí và áp dụng quyền lợi BHYT!");
        return ResponseEntity.ok(res);
    }

    /**
     * Chức năng: Áp dụng bảo hiểm y tế (BHYT) theo mức hưởng
     */
    @PostMapping("/{id}/ap-dung-bhyt")
    public ResponseEntity<?> apDungBHYT(@PathVariable String id,
                                        @RequestBody(required = false) Map<String, Object> req) {
        Optional<HoaDon> invoiceOpt = hoaDonRepository.findById(id);
        if (invoiceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        HoaDon bill = invoiceOpt.get();

        double rate = 0.8; // Mặc định 80%
        if (req != null && req.containsKey("tiLe")) {
            try {
                double rawRate = Double.parseDouble(String.valueOf(req.get("tiLe")));
                rate = rawRate > 1.0 ? rawRate / 100.0 : rawRate;
            } catch (Exception ignored) {
            }
        } else {
            boolean hasBHYT = benhNhanRepository.findById(bill.getMaBenhNhan())
                    .map(bn -> bn.getMaBHYT() != null && !bn.getMaBHYT().trim().isEmpty())
                    .orElse(false);
            if (!hasBHYT) {
                rate = 0.0;
            }
        }

        BigDecimal bhytTien = bill.getTongTienDichVu().multiply(BigDecimal.valueOf(rate));
        bill.setBhytChiTra(bhytTien);
        hoaDonRepository.save(bill);

        Map<String, Object> res = formatInvoiceMap(bill);
        res.put("message", "Đã áp dụng mức hưởng BHYT " + (int)(rate * 100) + "% thành công!");
        return ResponseEntity.ok(res);
    }

    /**
     * Chức năng: Thu tiền / thanh toán (Tiền mặt, Chuyển khoản, Ví điện tử)
     */
    @PutMapping("/{id}/thanhtoan")
    public ResponseEntity<?> thanhToan(@PathVariable String id,
                                       @RequestBody(required = false) Map<String, Object> req,
                                       HttpServletRequest request) {
        String rawMethod = req != null && req.containsKey("method") ? String.valueOf(req.get("method")) : "TienMat";
        String normalizedMethod = normalizePaymentMethod(rawMethod);

        Optional<HoaDon> invoice = hoaDonRepository.findById(id);
        if (invoice.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Optional<com.nhom12.hospital.entity.BenhNhan> patient = findCurrentPatient(request);
        if (isPatientAccount(request) && (patient.isEmpty()
                || !patient.get().getMaBenhNhan().equals(invoice.get().getMaBenhNhan()))) {
            return ResponseEntity.notFound().build();
        }

        HoaDon bill = invoice.get();
        bill.setTrangThaiTT("DaThanhToan");
        bill.setHinhThucThanhToan(normalizedMethod);
        bill.setNgayThanhToan(LocalDateTime.now());

        // Ghi nhận nhân viên thu ngân thực tế
        findCurrentEmployee(request).ifPresent(staff -> bill.setNhanVienThu(staff.getMaNhanVien()));

        hoaDonRepository.save(bill);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Xác nhận thu tiền và đóng hóa đơn thành công (" + normalizedMethod + ")!",
                "data", formatInvoiceMap(bill)
        ));
    }

    /**
     * Chức năng: Lập phiếu hoàn ứng khi cần điều chỉnh sau thanh toán
     */
    @PostMapping("/{id}/hoan-ung")
    public ResponseEntity<?> hoanUng(@PathVariable String id,
                                     @RequestBody(required = false) Map<String, Object> req,
                                     HttpServletRequest request) {
        Optional<HoaDon> invoiceOpt = hoaDonRepository.findById(id);
        if (invoiceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        HoaDon bill = invoiceOpt.get();

        BigDecimal soTienHoan = BigDecimal.ZERO;
        if (req != null && req.containsKey("soTienHoan")) {
            try {
                soTienHoan = new BigDecimal(String.valueOf(req.get("soTienHoan")));
            } catch (Exception ignored) {
            }
        }
        if (soTienHoan.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal benhNhanTra = bill.getTongTienDichVu().subtract(bill.getBhytChiTra());
            soTienHoan = benhNhanTra.compareTo(BigDecimal.ZERO) > 0 ? benhNhanTra : BigDecimal.ZERO;
        }

        String lyDo = req != null && req.containsKey("lyDo")
                ? String.valueOf(req.get("lyDo"))
                : "Hoàn tiền điều chỉnh sau thanh toán";
        String hinhThuc = req != null && req.containsKey("hinhThuc")
                ? normalizePaymentMethod(String.valueOf(req.get("hinhThuc")))
                : "TienMat";

        // Cập nhật trạng thái hóa đơn sang Hoàn tiền
        bill.setTrangThaiTT("HoanTien");
        hoaDonRepository.save(bill);

        String cashierName = findCurrentEmployee(request).map(NhanVien::getHoTen).orElse("Thu ngân viên");
        String cashierId = findCurrentEmployee(request).map(NhanVien::getMaNhanVien).orElse("NV-CAS01");

        Map<String, Object> phieuHoan = new LinkedHashMap<>();
        phieuHoan.put("maPhieuHoan", "PHU" + (System.currentTimeMillis() % 10000000));
        phieuHoan.put("maHoaDon", bill.getMaHoaDon());
        phieuHoan.put("maBenhNhan", bill.getMaBenhNhan());
        phieuHoan.put("soTienHoan", soTienHoan);
        phieuHoan.put("lyDo", lyDo);
        phieuHoan.put("hinhThuc", hinhThuc);
        phieuHoan.put("ngayHoan", LocalDateTime.now());
        phieuHoan.put("nhanVienThu", cashierName);
        phieuHoan.put("maNhanVienThu", cashierId);

        benhNhanRepository.findById(bill.getMaBenhNhan()).ifPresent(bn -> {
            phieuHoan.put("tenBenhNhan", bn.getHoTen());
            phieuHoan.put("soDienThoai", bn.getSoDienThoai());
            phieuHoan.put("maBHYT", bn.getMaBHYT());
        });

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Lập phiếu hoàn ứng thành công!",
                "refundSlip", phieuHoan,
                "invoice", formatInvoiceMap(bill)
        ));
    }

    private String normalizePaymentMethod(String raw) {
        if (raw == null) return "TienMat";
        String upper = raw.trim().toUpperCase();
        if (upper.contains("QR") || upper.contains("CHUYEN") || upper.contains("BANK")) {
            return "ChuyenKhoan";
        }
        if (upper.contains("VI") || upper.contains("MOMO") || upper.contains("ZALO")
                || upper.contains("VNPAY") || upper.contains("WALLET") || upper.contains("POS") || upper.contains("THE")) {
            return "ViDienTu";
        }
        return "TienMat";
    }

    private Map<String, Object> formatInvoiceMap(HoaDon hd) {
        Map<String, Object> map = new HashMap<>();
        map.put("billId", hd.getMaHoaDon());
        map.put("patientId", hd.getMaBenhNhan());
        map.put("total", hd.getTongTienDichVu());
        map.put("bhyt", hd.getBhytChiTra());
        map.put("pay", hd.getTongTienDichVu().subtract(hd.getBhytChiTra()));
        
        String stt = "UNPAID";
        if ("DaThanhToan".equalsIgnoreCase(hd.getTrangThaiTT())) {
            stt = "PAID";
        } else if ("HoanTien".equalsIgnoreCase(hd.getTrangThaiTT())) {
            stt = "REFUNDED";
        }
        map.put("status", stt);
        map.put("rawStatus", hd.getTrangThaiTT());
        map.put("method", hd.getHinhThucThanhToan());
        map.put("date", hd.getNgayLap());
        map.put("paymentDate", hd.getNgayThanhToan());
        map.put("cashierId", hd.getNhanVienThu());

        benhNhanRepository.findById(hd.getMaBenhNhan()).ifPresent(bn -> {
            map.put("patientName", bn.getHoTen());
            map.put("bhytCode", bn.getMaBHYT() != null && !bn.getMaBHYT().isBlank()
                    ? bn.getMaBHYT() : "Không có (Khám dịch vụ)");
        });

        if (hd.getNhanVienThu() != null) {
            nhanVienRepository.findById(hd.getNhanVienThu()).ifPresent(nv -> {
                map.put("cashierName", nv.getHoTen());
            });
        }

        List<ChiTietHoaDon> details = chiTietHoaDonRepository.findByMaHoaDon(hd.getMaHoaDon());
        List<Map<String, Object>> items = new ArrayList<>();
        for (ChiTietHoaDon d : details) {
            Map<String, Object> it = new HashMap<>();
            it.put("id", d.getMaChiTietHD());
            it.put("name", d.getTenDichVu());
            it.put("type", d.getLoaiDichVu());
            it.put("qty", d.getSoLuong());
            it.put("price", d.getDonGia());
            it.put("total", d.getDonGia().multiply(BigDecimal.valueOf(d.getSoLuong())));
            items.add(it);
        }
        map.put("items", items);
        return map;
    }

    private Optional<com.nhom12.hospital.entity.BenhNhan> findCurrentPatient(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        if (accountId == null || !isPatientAccount(request)) {
            return Optional.empty();
        }
        return benhNhanRepository.findByMaTaiKhoan(accountId);
    }

    private Optional<NhanVien> findCurrentEmployee(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        return accountId == null ? Optional.empty() : nhanVienRepository.findByMaTaiKhoan(accountId);
    }

    private boolean isPatientAccount(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        return accountId != null && taiKhoanRepository.findById(accountId)
                .map(TaiKhoan::getVaiTro)
                .filter("BenhNhan"::equalsIgnoreCase)
                .isPresent();
    }
}
