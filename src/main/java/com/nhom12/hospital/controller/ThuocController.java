package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.Thuoc;
import com.nhom12.hospital.service.ThuocService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/thuoc")
@CrossOrigin(origins = "*")
public class ThuocController {

    private final ThuocService thuocService;

    public ThuocController(ThuocService thuocService) {
        this.thuocService = thuocService;
    }

    @GetMapping
    public List<Thuoc> getAll() {
        return thuocService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Thuoc> getById(@PathVariable String id) {
        return thuocService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Thuoc create(@RequestBody Thuoc thuoc) {
        return thuocService.create(thuoc);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Thuoc> update(@PathVariable String id, @RequestBody Thuoc updated) {
        return thuocService.update(id, updated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/nhapkho")
    public ResponseEntity<?> nhapKho(@RequestBody Map<String, Object> req) {
        try {
            Thuoc drug = thuocService.nhapKho(req);
            return ResponseEntity.ok(drug);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
