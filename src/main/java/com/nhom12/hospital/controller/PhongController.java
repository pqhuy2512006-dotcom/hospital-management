package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.Phong;
import com.nhom12.hospital.service.PhongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/phong")
public class PhongController {
    private final PhongService phongService;

    public PhongController(PhongService phongService) {
        this.phongService = phongService;
    }

    @GetMapping
    public List<Phong> getPhong(
            @RequestParam(required = false) String maKhoa,
            @RequestParam(required = false) String loaiPhong) {
        return phongService.getPhongByKhoaAndLoaiPhong(maKhoa, loaiPhong);
    }

    // Tạo và cập nhật phòng
    @PostMapping
    public ResponseEntity<?> savePhong(@RequestBody Phong phong) {
        try {
            phongService.save(phong);
            return ResponseEntity.ok(Map.of("message", "Lưu thông tin phòng thành công!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable String id) {
        try {
            phongService.toggleStatus(id);
            return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái phòng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }
}