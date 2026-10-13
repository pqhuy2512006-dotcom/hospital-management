package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.Khoa;
import com.nhom12.hospital.service.KhoaService;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/khoa")
// public class KhoaController {

    // private final KhoaService khoaService;

    // public KhoaController(KhoaService khoaService) {
    //     this.khoaService = khoaService;
    // }

    // @GetMapping
    // public List<Khoa> getAllKhoa() {
    //     return khoaService.getAllKhoa();
    // }
public class KhoaController {

    private final KhoaService khoaService;

    public KhoaController(KhoaService khoaService) {
        this.khoaService = khoaService;
    }

    @GetMapping
    public List<Khoa> getAllKhoa() {
        return khoaService.getAllKhoa();
    }

    @GetMapping("/{maKhoa}")
    public ResponseEntity<?> getKhoaById(@PathVariable String maKhoa) {
        return khoaService.getKhoaById(maKhoa)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy khoa")));
    }

    @PostMapping
    public ResponseEntity<?> createKhoa(@RequestBody Khoa khoa) {
        try {
            Khoa savedKhoa = khoaService.createKhoa(khoa);
            return ResponseEntity.ok(Map.of("success", true, "message", "Thêm khoa thành công", "data", savedKhoa));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{maKhoa}")
    public ResponseEntity<?> updateKhoa(@PathVariable String maKhoa, @RequestBody Khoa khoa) {
        try {
            Khoa updatedKhoa = khoaService.updateKhoa(maKhoa, khoa);
            return ResponseEntity.ok(Map.of("success", true, "message", "Cập nhật khoa thành công", "data", updatedKhoa));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{maKhoa}")
    public ResponseEntity<?> deleteKhoa(@PathVariable String maKhoa) {
        try {
            khoaService.deleteKhoa(maKhoa);
            return ResponseEntity.ok(Map.of("success", true, "message", "Xóa khoa thành công"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
// }