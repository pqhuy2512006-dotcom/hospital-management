package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.NoiTru;
import com.nhom12.hospital.service.NoiTruService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/noitru")
@CrossOrigin(origins = "*")
public class NoiTruController {

    private final NoiTruService noiTruService;

    public NoiTruController(NoiTruService noiTruService) {
        this.noiTruService = noiTruService;
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return noiTruService.getAll();
    }

    @PostMapping
    public ResponseEntity<?> nhapVien(@RequestBody NoiTru noiTru) {
        NoiTru saved = noiTruService.nhapVien(noiTru);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/xuatvien")
    public ResponseEntity<?> xuatVien(@PathVariable String id) {
        return noiTruService.xuatVien(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
