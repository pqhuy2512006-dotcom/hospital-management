package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.BenhNhan;
import com.nhom12.hospital.entity.ChiTietKetQuaCLS;
import com.nhom12.hospital.entity.DonThuoc;
import com.nhom12.hospital.entity.KetQuaCLS;
import com.nhom12.hospital.entity.PhieuKham;
import com.nhom12.hospital.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/benhnhan")
@CrossOrigin(origins = "*")
public class BenhNhanController {

    private final BenhNhanRepository benhNhanRepository;
    private final PhieuKhamRepository phieuKhamRepository;
    private final DonThuocRepository donThuocRepository;
    private final ThuocRepository thuocRepository;
    private final KetQuaCLSRepository ketQuaCLSRepository;
    private final KhoaRepository khoaRepository;
    private final NhanVienRepository nhanVienRepository;

    public BenhNhanController(BenhNhanRepository benhNhanRepository,
                              PhieuKhamRepository phieuKhamRepository,
                              DonThuocRepository donThuocRepository,
                              ThuocRepository thuocRepository,
                              KetQuaCLSRepository ketQuaCLSRepository,
                              KhoaRepository khoaRepository,
                              NhanVienRepository nhanVienRepository) {
        this.benhNhanRepository = benhNhanRepository;
        this.phieuKhamRepository = phieuKhamRepository;
        this.donThuocRepository = donThuocRepository;
        this.thuocRepository = thuocRepository;
        this.ketQuaCLSRepository = ketQuaCLSRepository;
        this.khoaRepository = khoaRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    @GetMapping
    public List<BenhNhan> getAll() {
        return benhNhanRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BenhNhan> getById(@PathVariable String id) {
        return benhNhanRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BenhNhan benhNhan) {
        if (benhNhan.getMaBenhNhan() == null || benhNhan.getMaBenhNhan().trim().isEmpty()) {
            benhNhan.setMaBenhNhan("BN2026" + String.format("%06d", benhNhanRepository.count() + 1));
        }
        if (benhNhan.getNgayTaoHoSo() == null) {
            benhNhan.setNgayTaoHoSo(LocalDateTime.now());
        }
        if (benhNhan.getSoCCCD() != null && !benhNhan.getSoCCCD().trim().isEmpty()) {
            boolean exists = benhNhanRepository.findAll().stream()
                    .anyMatch(b -> benhNhan.getSoCCCD().equals(b.getSoCCCD()));
            if (exists) {
                return ResponseEntity.badRequest().body("Số CCCD (" + benhNhan.getSoCCCD() + ") đã tồn tại trong hệ thống!");
            }
        }
        return ResponseEntity.ok(benhNhanRepository.save(benhNhan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody BenhNhan updated) {
        return benhNhanRepository.findById(id).map(bn -> {
            bn.setHoTen(updated.getHoTen());
            bn.setNgaySinh(updated.getNgaySinh());
            bn.setGioiTinh(updated.getGioiTinh());
            bn.setSoDienThoai(updated.getSoDienThoai());
            bn.setNhomMau(updated.getNhomMau());
            bn.setDiaChi(updated.getDiaChi());
            bn.setEmail(updated.getEmail());
            bn.setNgheNghiep(updated.getNgheNghiep());
            bn.setNguoiLienHeKhanCap(updated.getNguoiLienHeKhanCap());
            bn.setSdtNguoiLienHe(updated.getSdtNguoiLienHe());
            bn.setQuanHeNguoiLienHe(updated.getQuanHeNguoiLienHe());
            bn.setTienSuBenhNen(updated.getTienSuBenhNen());
            bn.setTienSuDiUng(updated.getTienSuDiUng());
            return ResponseEntity.ok(benhNhanRepository.save(bn));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/history")
    public List<Map<String, Object>> getHistory(@PathVariable String id) {
        List<PhieuKham> list = phieuKhamRepository.findByMaBenhNhan(id);
        List<Map<String, Object>> res = new ArrayList<>();

        for (PhieuKham pk : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("ngayKham", pk.getNgayKham());
            map.put("chanDoan", pk.getChanDoan());

            nhanVienRepository.findById(pk.getMaBacSi()).ifPresent(nv -> {
                map.put("bacSi", nv.getHoTen());
            });

            khoaRepository.findById(pk.getMaKhoa()).ifPresent(k -> {
                map.put("khoa", k.getTenKhoa());
            });

            // Lấy danh sách thuốc đã kê
            List<DonThuoc> dts = donThuocRepository.findByMaPhieuKham(pk.getMaPhieuKham());
            List<String> medNames = new ArrayList<>();
            for (DonThuoc dt : dts) {
                thuocRepository.findById(dt.getMaThuoc()).ifPresent(d -> {
                    medNames.add(d.getTenThuoc() + " (" + dt.getSoLuong() + " " + dt.getDonViTinh() + ")");
                });
            }
            map.put("donThuoc", String.join(", ", medNames));

            // Lấy danh sách kết quả CLS
            List<KetQuaCLS> kqs = ketQuaCLSRepository.findByMaPhieuKham(pk.getMaPhieuKham());
            List<String> clsNames = new ArrayList<>();
            for (KetQuaCLS kq : kqs) {
                clsNames.add(kq.getLoaiXetNghiem() + (kq.getKetLuan() != null ? ": " + kq.getKetLuan() : ""));
            }
            map.put("cls", String.join("; ", clsNames));

            res.add(map);
        }

        return res;
    }
}
