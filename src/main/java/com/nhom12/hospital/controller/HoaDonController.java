package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.ChiTietHoaDon;
import com.nhom12.hospital.entity.HoaDon;
import com.nhom12.hospital.repository.BenhNhanRepository;
import com.nhom12.hospital.repository.ChiTietHoaDonRepository;
import com.nhom12.hospital.repository.HoaDonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/hoadon")
@CrossOrigin(origins = "*")
public class HoaDonController {

    private final HoaDonRepository hoaDonRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;
    private final BenhNhanRepository benhNhanRepository;

    public HoaDonController(HoaDonRepository hoaDonRepository,
                             ChiTietHoaDonRepository chiTietHoaDonRepository,
                             BenhNhanRepository benhNhanRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
        this.benhNhanRepository = benhNhanRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        List<HoaDon> list = hoaDonRepository.findAll();
        List<Map<String, Object>> res = new ArrayList<>();

        for (HoaDon hd : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("billId", hd.getMaHoaDon());
            map.put("patientId", hd.getMaBenhNhan());
            map.put("total", hd.getTongTienDichVu());
            map.put("bhyt", hd.getBhytChiTra());
            map.put("pay", hd.getTongTienDichVu().subtract(hd.getBhytChiTra()));
            map.put("status", "DaThanhToan".equalsIgnoreCase(hd.getTrangThaiTT()) ? "PAID" : "UNPAID");
            map.put("method", hd.getHinhThucThanhToan());
            map.put("date", hd.getNgayLap());

            benhNhanRepository.findById(hd.getMaBenhNhan()).ifPresent(bn -> {
                map.put("patientName", bn.getHoTen());
                map.put("bhytCode", bn.getMaBHYT() != null ? bn.getMaBHYT() : "Không có (Khám dịch vụ)");
            });

            List<ChiTietHoaDon> details = chiTietHoaDonRepository.findByMaHoaDon(hd.getMaHoaDon());
            List<Map<String, Object>> items = new ArrayList<>();
            for (ChiTietHoaDon d : details) {
                Map<String, Object> it = new HashMap<>();
                it.put("name", d.getTenDichVu());
                it.put("type", d.getLoaiDichVu());
                it.put("qty", d.getSoLuong());
                it.put("price", d.getDonGia());
                it.put("total", d.getDonGia().multiply(java.math.BigDecimal.valueOf(d.getSoLuong())));
                items.add(it);
            }
            map.put("items", items);
            res.add(map);
        }

        return res;
    }

    @PutMapping("/{id}/thanhtoan")
    public ResponseEntity<?> thanhToan(@PathVariable String id, @RequestBody(required = false) Map<String, Object> req) {
        String method = req != null && req.containsKey("method") ? (String) req.get("method") : "TienMat";
        return hoaDonRepository.findById(id).map(hd -> {
            hd.setTrangThaiTT("DaThanhToan");
            hd.setHinhThucThanhToan(method);
            hd.setNgayThanhToan(LocalDateTime.now());
            hoaDonRepository.save(hd);
            return ResponseEntity.ok(Map.of("success", true, "message", "Xác nhận thanh toán hóa đơn thành công!"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
