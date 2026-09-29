package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.ChiTietNhapKho;
import com.nhom12.hospital.entity.PhieuNhapKho;
import com.nhom12.hospital.entity.Thuoc;
import com.nhom12.hospital.repository.ChiTietNhapKhoRepository;
import com.nhom12.hospital.repository.PhieuNhapKhoRepository;
import com.nhom12.hospital.repository.ThuocRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/thuoc")
@CrossOrigin(origins = "*")
public class ThuocController {

    private final ThuocRepository thuocRepository;
    private final PhieuNhapKhoRepository phieuNhapKhoRepository;
    private final ChiTietNhapKhoRepository chiTietNhapKhoRepository;

    public ThuocController(ThuocRepository thuocRepository,
                           PhieuNhapKhoRepository phieuNhapKhoRepository,
                           ChiTietNhapKhoRepository chiTietNhapKhoRepository) {
        this.thuocRepository = thuocRepository;
        this.phieuNhapKhoRepository = phieuNhapKhoRepository;
        this.chiTietNhapKhoRepository = chiTietNhapKhoRepository;
    }

    @GetMapping
    public List<Thuoc> getAll() {
        return thuocRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Thuoc> getById(@PathVariable String id) {
        return thuocRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Thuoc create(@RequestBody Thuoc thuoc) {
        if (thuoc.getMaThuoc() == null || thuoc.getMaThuoc().trim().isEmpty()) {
            thuoc.setMaThuoc("MED-" + String.format("%03d", thuocRepository.count() + 1));
        }
        return thuocRepository.save(thuoc);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Thuoc> update(@PathVariable String id, @RequestBody Thuoc updated) {
        return thuocRepository.findById(id).map(existing -> {
            existing.setTenThuoc(updated.getTenThuoc());
            existing.setDonViTinh(updated.getDonViTinh());
            existing.setDonGiaBan(updated.getDonGiaBan());
            existing.setTonKhoHienTai(updated.getTonKhoHienTai());
            existing.setNguongCanhBao(updated.getNguongCanhBao());
            return ResponseEntity.ok(thuocRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/nhapkho")
    public ResponseEntity<?> nhapKho(@RequestBody Map<String, Object> req) {
        String maThuoc = (String) req.get("maThuoc");
        Integer soLuong = Integer.parseInt(req.get("soLuong").toString());
        String supplier = (String) req.getOrDefault("nhaCungCap", "DHG Pharma");
        String invoiceNo = (String) req.getOrDefault("soHoaDonNCC", "HD-" + System.currentTimeMillis() % 10000);
        String batch = (String) req.getOrDefault("soLo", "L2026-X");
        String expiryStr = (String) req.getOrDefault("hanSuDung", LocalDate.now().plusYears(2).toString());
        BigDecimal price = req.get("donGia") != null ? new BigDecimal(req.get("donGia").toString()) : BigDecimal.valueOf(1000);

        Optional<Thuoc> drugOpt = thuocRepository.findById(maThuoc);
        if (drugOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Thuốc không tồn tại!");
        }
        Thuoc drug = drugOpt.get();
        drug.setTonKhoHienTai(drug.getTonKhoHienTai() + soLuong);
        thuocRepository.save(drug);

        // Tạo phiếu nhập kho
        String maPN = "PN" + (System.currentTimeMillis() % 100000);
        PhieuNhapKho pnk = new PhieuNhapKho();
        pnk.setMaPhieuNhap(maPN);
        pnk.setNgayNhap(LocalDate.now());
        pnk.setNhaCungCap(supplier);
        pnk.setSoHoaDonNCC(invoiceNo);
        pnk.setNguoiNhap("NV-PHA01");
        pnk.setTongTien(price.multiply(BigDecimal.valueOf(soLuong)));
        phieuNhapKhoRepository.save(pnk);

        // Chi tiết nhập kho
        ChiTietNhapKho ctnk = new ChiTietNhapKho();
        ctnk.setMaChiTietNhap("CTN" + (System.currentTimeMillis() % 100000));
        ctnk.setMaPhieuNhap(maPN);
        ctnk.setMaThuoc(maThuoc);
        ctnk.setSoLo(batch);
        ctnk.setSoLuong(soLuong);
        ctnk.setDonGia(price);
        ctnk.setHanSuDung(LocalDate.parse(expiryStr));
        chiTietNhapKhoRepository.save(ctnk);

        return ResponseEntity.ok(drug);
    }
}
