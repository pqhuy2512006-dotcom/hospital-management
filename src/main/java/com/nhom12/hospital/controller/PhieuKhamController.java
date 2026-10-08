package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.PhieuKham;
import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.service.PhieuKhamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/phieukham")
@CrossOrigin(origins = "*")
public class PhieuKhamController {

    private final PhieuKhamService phieuKhamService;

    public PhieuKhamController(PhieuKhamService phieuKhamService) {
        this.phieuKhamService = phieuKhamService;
    }

    @GetMapping
    public List<PhieuKham> getAll() {
        return phieuKhamService.getAll();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyExaminations(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        try {
            return ResponseEntity.ok(phieuKhamService.getMyExaminations(accountId));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable String id) {
        return phieuKhamService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> hoanTatKham(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        try {
            return ResponseEntity.ok(phieuKhamService.hoanTatKham(payload, accountId));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/chi-dinh-cls")
    public ResponseEntity<?> chiDinhCLS(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        try {
            return ResponseEntity.ok(phieuKhamService.chiDinhCLS(payload, accountId));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
