package com.nhom12.hospital.controller;

import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.entity.ThongBao;
import com.nhom12.hospital.service.ThongBaoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import com.nhom12.hospital.entity.TaiKhoan;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/v1/thongbao")
@CrossOrigin(origins = "*")
public class ThongBaoController {

        private final ThongBaoService thongBaoService;
    private final TaiKhoanRepository taiKhoanRepository;

    public ThongBaoController(ThongBaoService thongBaoService, TaiKhoanRepository taiKhoanRepository) {
        this.thongBaoService = thongBaoService;
        this.taiKhoanRepository = taiKhoanRepository;
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
        @PostMapping("/broadcast")
    public ResponseEntity<?> broadcast(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        if (accountId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Yêu cầu đăng nhập."));
        }

        // Kiểm tra quyền: Chỉ Admin (QuanTri) 
        Optional<TaiKhoan> accountOpt = taiKhoanRepository.findById(accountId);
        if (accountOpt.isEmpty() || (!"QuanTri".equalsIgnoreCase(accountOpt.get().getVaiTro()) 
                && !"ADMIN".equalsIgnoreCase(accountOpt.get().getVaiTro()) )) {
            return ResponseEntity.status(403).body(Map.of("message", "Chỉ Quản trị viên mới có quyền phát thông báo toàn viện!"));
        }

        String tieuDe = payload.get("tieuDe");
        String noiDung = payload.get("noiDung");
        String loaiThongBao = payload.getOrDefault("loaiThongBao", "THONG_BAO_CHUNG");
        String targetRole = payload.getOrDefault("targetRole", "ALL");

        if (tieuDe == null || tieuDe.trim().isEmpty() || noiDung == null || noiDung.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tiêu đề và nội dung thông báo không được để trống!"));
        }

        int count = thongBaoService.broadcast(tieuDe.trim(), noiDung.trim(), loaiThongBao, targetRole);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã phát thông báo thành công đến " + count + " tài khoản!",
                "count", count
        ));
    }

    @GetMapping("/broadcast-history")
    public ResponseEntity<?> getBroadcastHistory() {
        return ResponseEntity.ok(thongBaoService.getBroadcastHistory());
    }
}
