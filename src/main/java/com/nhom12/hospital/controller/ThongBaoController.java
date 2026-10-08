package com.nhom12.hospital.controller;

import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.entity.ThongBao;
import com.nhom12.hospital.service.ThongBaoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/thongbao")
@CrossOrigin(origins = "*")
public class ThongBaoController {

    private final ThongBaoService thongBaoService;

    public ThongBaoController(ThongBaoService thongBaoService) {
        this.thongBaoService = thongBaoService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyNotifications(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        if (accountId == null) {
            return ResponseEntity.status(401).body("Yêu cầu đăng nhập.");
        }
        List<ThongBao> list = thongBaoService.getByMaTaiKhoan(accountId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Integer id, HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        if (accountId == null) {
            return ResponseEntity.status(401).body("Yêu cầu đăng nhập.");
        }
        thongBaoService.markAsRead(id, accountId);
        return ResponseEntity.ok().build();
    }
}
