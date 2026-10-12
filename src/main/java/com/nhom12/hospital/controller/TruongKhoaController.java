package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.KhoaTruongKhoaDTO;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.service.TruongKhoaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/truongkhoa")
public class TruongKhoaController {
    private final TruongKhoaService service;

    public TruongKhoaController(TruongKhoaService service) {
        this.service = service;
    }

    @GetMapping("/khoa-list")
    public List<KhoaTruongKhoaDTO> getAllKhoaWithTruongKhoa() {
        return service.getAllKhoaWithTruongKhoa();
    }

    @GetMapping("/khoa/{maKhoa}/ung-vien")
    public List<NhanVien> getUngVienByKhoa(@PathVariable String maKhoa) {
        return service.getUngVienByKhoa(maKhoa);
    }

    @PostMapping("/khoa/{maKhoa}/bo-nhiem")
    public ResponseEntity<?> boNhiemTruongKhoa(@PathVariable String maKhoa, @RequestBody Map<String, String> payload) {
        String maNhanVien = payload.get("maNhanVien");
        if (maNhanVien == null || maNhanVien.isEmpty()) {
            return ResponseEntity.badRequest().body("Thiếu mã nhân viên");
        }
        service.boNhiemTruongKhoa(maKhoa, maNhanVien);
        return ResponseEntity.ok(Map.of("message", "Bổ nhiệm trưởng khoa thành công"));
    }

    @PostMapping("/khoa/{maKhoa}/mien-nhiem")
    public ResponseEntity<?> mienNhiemTruongKhoa(@PathVariable String maKhoa) {
        service.mienNhiemTruongKhoa(maKhoa);
        return ResponseEntity.ok(Map.of("message", "Miễn nhiệm trưởng khoa thành công"));
    }
}

