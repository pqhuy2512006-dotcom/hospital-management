package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.BenhNhan;
import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.service.BenhNhanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/benhnhan")
@CrossOrigin(origins = "*")
public class BenhNhanController {

    private final BenhNhanService benhNhanService;

    public BenhNhanController(BenhNhanService benhNhanService) {
        this.benhNhanService = benhNhanService;
    }

    @GetMapping
    public List<BenhNhan> getAll() {
        return benhNhanService.getAll();
    }

    @GetMapping("/me")
    public ResponseEntity<BenhNhan> getMyProfile(HttpServletRequest request) {
        Long accountId = getAccountId(request);
        if (accountId == null) {
            return ResponseEntity.status(403).build();
        }
        return benhNhanService.getMyProfile(accountId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/me")
    public ResponseEntity<?> createMyProfile(@RequestBody BenhNhan profile, HttpServletRequest request) {
        Long accountId = getAccountId(request);
        if (accountId == null) {
            return ResponseEntity.status(403).body(Map.of("message", "Yêu cầu đăng nhập."));
        }
        try {
            return ResponseEntity.ok(benhNhanService.createMyProfile(accountId, profile));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@RequestBody BenhNhan updated, HttpServletRequest request) {
        Long accountId = getAccountId(request);
        if (accountId == null) {
            return ResponseEntity.status(403).body(Map.of("message", "Yêu cầu đăng nhập."));
        }
        try {
            BenhNhan profile = benhNhanService.updateMyProfile(accountId, updated);
            if (profile == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BenhNhan> getById(@PathVariable String id) {
        return benhNhanService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BenhNhan benhNhan) {
        try {
            return ResponseEntity.ok(benhNhanService.create(benhNhan));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody BenhNhan updated) {
        return benhNhanService.update(id, updated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/history")
    public List<Map<String, Object>> getHistory(@PathVariable String id) {
        return benhNhanService.buildHistory(id);
    }

    @GetMapping("/me/history")
    public ResponseEntity<?> getMyHistory(HttpServletRequest request) {
        Long accountId = getAccountId(request);
        if (accountId == null) {
            return ResponseEntity.status(403).body(Map.of("message", "Yêu cầu đăng nhập."));
        }
        Optional<BenhNhan> profile = benhNhanService.getMyProfile(accountId);
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(benhNhanService.buildHistory(profile.get().getMaBenhNhan()));
    }

    private Long getAccountId(HttpServletRequest request) {
        return (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
    }
}
