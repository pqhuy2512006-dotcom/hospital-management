package com.nhom12.hospital.controller;

import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.service.HoaDonService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/hoadon")
@CrossOrigin(origins = "*")
public class HoaDonController {

    private final HoaDonService hoaDonService;

    public HoaDonController(HoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    @GetMapping
    public List<Map<String, Object>> getAll(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        return hoaDonService.getAll(accountId);
    }

    @PutMapping("/{id}/thanhtoan")
    public ResponseEntity<?> thanhToan(@PathVariable String id,
                                       @RequestBody(required = false) Map<String, Object> req,
                                       HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        try {
            return ResponseEntity.ok(hoaDonService.thanhToan(id, req, accountId));
        } catch (NoSuchElementException | IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
