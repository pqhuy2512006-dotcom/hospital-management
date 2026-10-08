package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.service.NhanVienService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/nhanvien")
@CrossOrigin(origins = "*")
public class NhanVienController {

    private final NhanVienService nhanVienService;

    public NhanVienController(NhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    @GetMapping
    public List<NhanVien> getAll() {
        return nhanVienService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NhanVien> getById(@PathVariable String id) {
        return nhanVienService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        return nhanVienService.findByAccountId(accountId)
                .map(employee -> ResponseEntity.ok(nhanVienService.profileView(employee)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doctors")
    public List<Map<String, String>> getDoctorsForConsultation() {
        return nhanVienService.getDoctorsForConsultation();
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        try {
            NhanVien employee = nhanVienService.updateMyProfile(accountId, payload);
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(nhanVienService.profileView(employee));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NhanVien nhanVien) {
        try {
            return ResponseEntity.ok(nhanVienService.create(nhanVien));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<NhanVien> update(@PathVariable String id, @RequestBody NhanVien updated) {
        return nhanVienService.update(id, updated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
