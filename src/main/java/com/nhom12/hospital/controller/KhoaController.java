package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.Khoa;
import com.nhom12.hospital.service.KhoaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/khoa")
public class KhoaController {

    private final KhoaService khoaService;

    public KhoaController(KhoaService khoaService) {
        this.khoaService = khoaService;
    }

    @GetMapping
    public List<Khoa> getAllKhoa() {
        return khoaService.getAllKhoa();
    }
}