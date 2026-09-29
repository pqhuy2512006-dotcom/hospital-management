package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.GiuongBenh;
import com.nhom12.hospital.repository.GiuongBenhRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/giuongbenh")
@CrossOrigin(origins = "*")
public class GiuongBenhController {

    private final GiuongBenhRepository giuongBenhRepository;

    public GiuongBenhController(GiuongBenhRepository giuongBenhRepository) {
        this.giuongBenhRepository = giuongBenhRepository;
    }

    @GetMapping
    public List<GiuongBenh> getAll(@RequestParam(required = false) String khoa) {
        if (khoa != null && !khoa.trim().isEmpty() && !khoa.equalsIgnoreCase("ALL")) {
            return giuongBenhRepository.findByMaKhoa(khoa);
        }
        return giuongBenhRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiuongBenh> getById(@PathVariable String id) {
        return giuongBenhRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public GiuongBenh create(@RequestBody GiuongBenh giuongBenh) {
        return giuongBenhRepository.save(giuongBenh);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GiuongBenh> update(@PathVariable String id, @RequestBody GiuongBenh updated) {
        return giuongBenhRepository.findById(id).map(g -> {
            g.setSoGiuong(updated.getSoGiuong());
            g.setTrangThai(updated.getTrangThai());
            g.setDonGiaNgay(updated.getDonGiaNgay());
            g.setMaKhoa(updated.getMaKhoa());
            return ResponseEntity.ok(giuongBenhRepository.save(g));
        }).orElse(ResponseEntity.notFound().build());
    }
}
