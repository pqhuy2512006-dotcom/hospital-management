package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.Khoa;
import com.nhom12.hospital.repository.KhoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/khoa")
public class KhoaController {

    @Autowired
    private KhoaRepository khoaRepository;

    @GetMapping
    public List<Khoa> getAllKhoa() {
        return khoaRepository.findAll();
    }
}