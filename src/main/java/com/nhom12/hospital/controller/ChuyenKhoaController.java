package com.nhom12.hospital.controller;

import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.entity.YeuCauChuyenKhoa;
import com.nhom12.hospital.service.ChuyenKhoaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chuyenkhoa")
public class ChuyenKhoaController {

    private final ChuyenKhoaService chuyenKhoaService;

    public ChuyenKhoaController(ChuyenKhoaService chuyenKhoaService) {
        this.chuyenKhoaService = chuyenKhoaService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyRequests(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        try {
            return ResponseEntity.ok(chuyenKhoaService.getMyRequests(accountId));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        try {
            YeuCauChuyenKhoa saved = chuyenKhoaService.createRequest(payload, accountId);
            request.setAttribute(SessionAttributes.AUDIT_DETAIL,
                    "Tạo yêu cầu " + saved.getLoaiYeuCau() + "; mã=" + saved.getMaYeuCau() + "; phiếu khám=" + saved.getMaPhieuKham());
            return ResponseEntity.ok(saved);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}