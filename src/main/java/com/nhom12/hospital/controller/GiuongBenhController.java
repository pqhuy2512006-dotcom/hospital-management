package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.GiuongBenh;
import com.nhom12.hospital.service.GiuongBenhService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/giuongbenh")
@CrossOrigin(origins = "*")
public class GiuongBenhController {

    private final GiuongBenhService giuongBenhService;

    public GiuongBenhController(GiuongBenhService giuongBenhService) {
        this.giuongBenhService = giuongBenhService;
    }

    @GetMapping
    public List<GiuongBenh> getAll(@RequestParam(required = false) String khoa) {
        return giuongBenhService.getAll(khoa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiuongBenh> getById(@PathVariable String id) {
        return giuongBenhService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public GiuongBenh create(@RequestBody GiuongBenh giuongBenh) {
        return giuongBenhService.create(giuongBenh);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GiuongBenh> update(@PathVariable String id, @RequestBody GiuongBenh updated) {
        return giuongBenhService.update(id, updated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
