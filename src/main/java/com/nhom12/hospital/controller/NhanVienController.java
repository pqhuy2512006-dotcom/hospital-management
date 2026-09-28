package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.repository.NhanVienRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nhanvien")
@CrossOrigin(origins = "*")
public class NhanVienController {

    private final NhanVienRepository nhanVienRepository;

    public NhanVienController(NhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }

    @GetMapping
    public List<NhanVien> getAll() {
        return nhanVienRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NhanVien> getById(@PathVariable String id) {
        return nhanVienRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NhanVien nhanVien) {
        if (nhanVien.getMaNhanVien() == null || nhanVien.getMaNhanVien().trim().isEmpty()) {
            nhanVien.setMaNhanVien("NV-DOC" + String.format("%02d", nhanVienRepository.count() + 1));
        }
        if (nhanVien.getSoDienThoai() != null) {
            boolean sdtExists = nhanVienRepository.findAll().stream()
                    .anyMatch(nv -> nhanVien.getSoDienThoai().equals(nv.getSoDienThoai()));
            if (sdtExists) {
                return ResponseEntity.badRequest().body("Số điện thoại " + nhanVien.getSoDienThoai() + " đã được đăng ký cho nhân viên khác!");
            }
        }
        if (nhanVien.getVaiTro() == null) {
            nhanVien.setVaiTro("BacSi");
        }
        if (nhanVien.getTrangThai() == null) {
            nhanVien.setTrangThai("DangLamViec");
        }
        return ResponseEntity.ok(nhanVienRepository.save(nhanVien));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NhanVien> update(@PathVariable String id, @RequestBody NhanVien updated) {
        return nhanVienRepository.findById(id).map(nv -> {
            nv.setHoTen(updated.getHoTen());
            nv.setTrinhDoChuyenMon(updated.getTrinhDoChuyenMon());
            nv.setChuyenKhoa(updated.getChuyenKhoa());
            nv.setDiaChi(updated.getDiaChi());
            nv.setSoDienThoai(updated.getSoDienThoai());
            nv.setChungChiHanhNghe(updated.getChungChiHanhNghe());
            nv.setTrangThai(updated.getTrangThai());
            return ResponseEntity.ok(nhanVienRepository.save(nv));
        }).orElse(ResponseEntity.notFound().build());
    }
}
