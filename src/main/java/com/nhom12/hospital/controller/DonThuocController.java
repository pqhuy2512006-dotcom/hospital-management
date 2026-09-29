package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.DonThuoc;
import com.nhom12.hospital.entity.PhieuKham;
import com.nhom12.hospital.entity.Thuoc;
import com.nhom12.hospital.repository.BenhNhanRepository;
import com.nhom12.hospital.repository.DonThuocRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.PhieuKhamRepository;
import com.nhom12.hospital.repository.ThuocRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/donthuoc")
@CrossOrigin(origins = "*")
public class DonThuocController {

    private final DonThuocRepository donThuocRepository;
    private final PhieuKhamRepository phieuKhamRepository;
    private final ThuocRepository thuocRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final NhanVienRepository nhanVienRepository;

    public DonThuocController(DonThuocRepository donThuocRepository,
                              PhieuKhamRepository phieuKhamRepository,
                              ThuocRepository thuocRepository,
                              BenhNhanRepository benhNhanRepository,
                              NhanVienRepository nhanVienRepository) {
        this.donThuocRepository = donThuocRepository;
        this.phieuKhamRepository = phieuKhamRepository;
        this.thuocRepository = thuocRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        List<DonThuoc> list = donThuocRepository.findAll();
        List<Map<String, Object>> res = new ArrayList<>();

        for (DonThuoc dt : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dt.getMaDonThuoc());
            map.put("maPhieuKham", dt.getMaPhieuKham());
            map.put("drugId", dt.getMaThuoc());
            map.put("qty", dt.getSoLuong());
            map.put("lieuDung", dt.getLieuDung());
            map.put("cachDung", dt.getCachDung());
            map.put("status", "PENDING");

            thuocRepository.findById(dt.getMaThuoc()).ifPresent(drug -> {
                map.put("drugName", drug.getTenThuoc());
                map.put("unit", drug.getDonViTinh());
                map.put("stock", drug.getTonKhoHienTai());
            });

            phieuKhamRepository.findById(dt.getMaPhieuKham()).ifPresent(pk -> {
                map.put("patientId", pk.getMaBenhNhan());
                benhNhanRepository.findById(pk.getMaBenhNhan()).ifPresent(bn -> {
                    map.put("patientName", bn.getHoTen());
                });
                nhanVienRepository.findById(pk.getMaBacSi()).ifPresent(nv -> {
                    map.put("doctor", nv.getHoTen());
                });
            });

            res.add(map);
        }
        return res;
    }

    @PostMapping("/{id}/xuat")
    public ResponseEntity<?> xuatThuoc(@PathVariable String id) {
        return donThuocRepository.findById(id).map(dt -> {
            Optional<Thuoc> drugOpt = thuocRepository.findById(dt.getMaThuoc());
            if (drugOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Thuốc không tồn tại!");
            }
            Thuoc drug = drugOpt.get();
            if (drug.getTonKhoHienTai() < dt.getSoLuong()) {
                return ResponseEntity.badRequest().body("Số lượng tồn kho không đủ để xuất!");
            }
            drug.setTonKhoHienTai(drug.getTonKhoHienTai() - dt.getSoLuong());
            thuocRepository.save(drug);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã xuất thuốc thành công!", "tonKhoMoi", drug.getTonKhoHienTai()));
        }).orElse(ResponseEntity.notFound().build());
    }
}
