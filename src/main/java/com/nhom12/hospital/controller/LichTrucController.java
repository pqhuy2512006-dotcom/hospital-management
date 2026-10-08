package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.LichTruc;
import com.nhom12.hospital.service.LichTrucService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/lichtruc")
@CrossOrigin(origins = "*")
public class LichTrucController {

    private final LichTrucService lichTrucService;

    public LichTrucController(LichTrucService lichTrucService) {
        this.lichTrucService = lichTrucService;
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return lichTrucService.getAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LichTruc lt) {
        return ResponseEntity.ok(lichTrucService.create(lt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            return ResponseEntity.ok(lichTrucService.delete(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
