package com.nhom12.hospital.controller;


import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.service.TaiKhoanService;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

@RestController
@RequestMapping("/api/v1/taikhoan")
@CrossOrigin(origins = "*")
public class TaiKhoanController {

    private final TaiKhoanService taiKhoanService;

    public TaiKhoanController(TaiKhoanService taiKhoanService) {
    this.taiKhoanService = taiKhoanService;
}

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return taiKhoanService.getAll();
    } 

    @PostMapping
public ResponseEntity<?> create(@RequestBody Map<String, Object> payload,
                                HttpServletRequest request) {
    try {
        Map<String, Object> result = taiKhoanService.create(payload);

        request.setAttribute(
                SessionAttributes.AUDIT_DETAIL,
                "Cấp tài khoản " + result.get("username")
                        + "; role=" + result.get("role")
        );

        return ResponseEntity.ok(result);

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));

    } catch (Exception e) {
        return ResponseEntity.status(500)
                .body(Map.of(
                        "message",
                        "Lỗi tạo tài khoản: "
                                + (e.getMessage() != null
                                ? e.getMessage()
                                : e.toString())
                ));
    }
}
@PutMapping("/{id}")
public ResponseEntity<?> updateAccount(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload,
        HttpServletRequest request) {

    try {
        Long currentAccountId =
                (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);

        boolean enabled = payload.get("enabled") instanceof Boolean value
                ? value
                : true;

        taiKhoanService.updateAccount(
        id,
        enabled,
        currentAccountId
);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "enabled", enabled
                )
        );

    } catch (IllegalArgumentException e) {

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "message", e.getMessage()
                ));

    } catch (Exception e) {

        return ResponseEntity.status(500)
                .body(Map.of(
                        "message",
                        "Lỗi cập nhật tài khoản: "
                                + (e.getMessage() != null
                                ? e.getMessage()
                                : e.toString())
                ));
    }
}
    @PostMapping("/change-password")
public ResponseEntity<?> changePassword(
        @RequestBody Map<String, String> req,
        HttpServletRequest request) {

    try {
        String username =
                (String) request.getAttribute(
                        SessionAttributes.USERNAME);

        taiKhoanService.changePassword(
                username,
                req.get("oldPassword"),
                req.get("newPassword")
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Đổi mật khẩu thành công!"
                )
        );

    } catch (IllegalArgumentException e) {

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "message", e.getMessage()
                ));

    } catch (Exception e) {

        return ResponseEntity.status(500)
                .body(Map.of(
                        "message",
                        "Lỗi đổi mật khẩu: "
                                + (e.getMessage() != null
                                ? e.getMessage()
                                : e.toString())
                ));
    }
}

}
