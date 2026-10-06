package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.BenhNhan;
import com.nhom12.hospital.entity.GiuongBenh;
import com.nhom12.hospital.entity.NoiTru;
import com.nhom12.hospital.repository.BenhNhanRepository;
import com.nhom12.hospital.repository.GiuongBenhRepository;
import com.nhom12.hospital.repository.NoiTruRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/noitru")
@CrossOrigin(origins = "*")
public class NoiTruController {

    private final NoiTruRepository noiTruRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final GiuongBenhRepository giuongBenhRepository;

    public NoiTruController(NoiTruRepository noiTruRepository,
                            BenhNhanRepository benhNhanRepository,
                            GiuongBenhRepository giuongBenhRepository) {
        this.noiTruRepository = noiTruRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.giuongBenhRepository = giuongBenhRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        List<NoiTru> list = noiTruRepository.findAll();
        List<Map<String, Object>> res = new ArrayList<>();
        for (NoiTru nt : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("maNoiTru", nt.getMaNoiTru());
            map.put("maBenhNhan", nt.getMaBenhNhan());
            map.put("maGiuong", nt.getMaGiuong());
            map.put("maPhieuKham", nt.getMaPhieuKham());
            map.put("ngayNhapVien", nt.getNgayNhapVien());
            map.put("ngayXuatVien", nt.getNgayXuatVien());
            map.put("trangThai", nt.getTrangThai());

            benhNhanRepository.findById(nt.getMaBenhNhan()).ifPresent(bn -> {
                map.put("tenBenhNhan", bn.getHoTen());
                map.put("gioiTinh", bn.getGioiTinh());
                map.put("ngaySinh", bn.getNgaySinh());
                map.put("maBHYT", bn.getMaBHYT());
                map.put("soDienThoai", bn.getSoDienThoai());
            });

            giuongBenhRepository.findById(nt.getMaGiuong()).ifPresent(g -> {
                map.put("soGiuong", g.getSoGiuong());
                map.put("maKhoa", g.getMaKhoa());
                map.put("donGiaNgay", g.getDonGiaNgay());
            });

            res.add(map);
        }
        return res;
    }

    @PostMapping
    public ResponseEntity<?> nhapVien(@RequestBody NoiTru noiTru) {
        if (noiTru.getMaNoiTru() == null || noiTru.getMaNoiTru().trim().isEmpty()) {
            noiTru.setMaNoiTru("NT" + System.currentTimeMillis() % 10000000);
        }
        if (noiTru.getNgayNhapVien() == null) {
            noiTru.setNgayNhapVien(LocalDateTime.now());
        }
        if (noiTru.getTrangThai() == null) {
            noiTru.setTrangThai("DangNam");
        }
        if (noiTru.getMaPhieuKham() == null) {
            noiTru.setMaPhieuKham("PK2026000001");
        }

        // Cập nhật trạng thái giường
        giuongBenhRepository.findById(noiTru.getMaGiuong()).ifPresent(g -> {
            g.setTrangThai("DangSuDung");
            giuongBenhRepository.save(g);
        });

        NoiTru saved = noiTruRepository.save(noiTru);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/xuatvien")
    public ResponseEntity<?> xuatVien(@PathVariable String id) {
        return noiTruRepository.findById(id).map(nt -> {
            nt.setTrangThai("DaXuatVien");
            nt.setNgayXuatVien(LocalDateTime.now());
            noiTruRepository.save(nt);

            // Trả giường về 'Trong'
            giuongBenhRepository.findById(nt.getMaGiuong()).ifPresent(g -> {
                g.setTrangThai("Trong");
                giuongBenhRepository.save(g);
            });

            return ResponseEntity.ok(nt);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/chuyenkhoa")
    public ResponseEntity<?> chuyenKhoa(@PathVariable String id, @RequestBody Map<String, String> payload) {
        String maGiuongMoi = payload.get("maGiuongMoi");
        if (maGiuongMoi == null || maGiuongMoi.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cần chọn giường mới để chuyển đến."));
        }
        return noiTruRepository.findById(id).map(nt -> {
            String giuongCu = nt.getMaGiuong();
            // Trả giường cũ
            giuongBenhRepository.findById(giuongCu).ifPresent(g -> {
                g.setTrangThai("Trong");
                giuongBenhRepository.save(g);
            });
            // Nhận giường mới
            giuongBenhRepository.findById(maGiuongMoi).ifPresent(g -> {
                g.setTrangThai("DangSuDung");
                giuongBenhRepository.save(g);
            });
            nt.setMaGiuong(maGiuongMoi);
            noiTruRepository.save(nt);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Chuyển bệnh nhân sang giường " + maGiuongMoi + " thành công!",
                    "data", nt
            ));
        }).orElse(ResponseEntity.notFound().build());
    }
}
