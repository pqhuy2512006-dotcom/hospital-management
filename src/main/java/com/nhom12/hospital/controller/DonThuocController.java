package com.nhom12.hospital.controller;

import com.nhom12.hospital.service.DonThuocService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/donthuoc")
@CrossOrigin(origins = "*")
public class DonThuocController {

    private final DonThuocService donThuocService;

    public DonThuocController(DonThuocService donThuocService) {
        this.donThuocService = donThuocService;
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return donThuocService.getAll();
    }

    @PostMapping("/{id}/xuat")
    public ResponseEntity<?> xuatThuoc(@PathVariable String id) {
        try {
            return ResponseEntity.ok(donThuocService.xuatThuoc(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
