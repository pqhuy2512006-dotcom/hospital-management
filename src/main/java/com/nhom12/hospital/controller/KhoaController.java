package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.Khoa;
import com.nhom12.hospital.service.KhoaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/khoa")
public class KhoaController {

    private final KhoaService khoaService;

    public KhoaController(KhoaService khoaService) {
        this.khoaService = khoaService;
    }

    @GetMapping
    public List<Khoa> getAllKhoa() {
        return khoaService.getAllKhoa();
    }

    // Thêm Khoa Mới
    @PostMapping
    public ResponseEntity<?> saveKhoa(@RequestBody Khoa khoa) {
        try {
            khoaService.save(khoa);
            return ResponseEntity.ok(Map.of("message", "Lưu thông tin khoa thành công!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }

    // Cập Nhật Trạng Thái Của Khoa 
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable String id) {
        try {
            khoaService.toggleStatus(id);
            return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái khoa thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }
}