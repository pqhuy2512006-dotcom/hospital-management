package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.Phong;
import com.nhom12.hospital.service.PhongService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/phong")
public class PhongController {
    private final PhongService phongService;

    public PhongController(PhongService phongService) {
        this.phongService = phongService;
    }

    @GetMapping
    public List<Phong> getPhong(
            @RequestParam(required = false) String maKhoa,
            @RequestParam(required = false) String loaiPhong) {
        return phongService.getPhongByKhoaAndLoaiPhong(maKhoa, loaiPhong);
    }
}

