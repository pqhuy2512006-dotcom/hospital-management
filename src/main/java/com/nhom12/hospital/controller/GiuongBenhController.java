package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.GiuongBenh;
import com.nhom12.hospital.service.GiuongBenhService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/giuongbenh")
@CrossOrigin(origins = "*")
public class GiuongBenhController {

    private final GiuongBenhService giuongBenhService;

    public GiuongBenhController(GiuongBenhService giuongBenhService) {
        this.giuongBenhService = giuongBenhService;
    }

    @GetMapping
    public List<GiuongBenh> getAll(@RequestParam(required = false) String khoa) {
        return giuongBenhService.getAll(khoa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiuongBenh> getById(@PathVariable String id) {
        return giuongBenhService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> saveGiuong(@RequestBody GiuongBenh giuongBenh) {
        try {
            giuongBenhService.save(giuongBenh);
            return ResponseEntity.ok(java.util.Map.of("message", "Lưu thông tin giường thành công!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable String id) {
        try {
            giuongBenhService.toggleStatus(id);
            return ResponseEntity.ok(java.util.Map.of("message", "Cập nhật trạng thái giường thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }
}
