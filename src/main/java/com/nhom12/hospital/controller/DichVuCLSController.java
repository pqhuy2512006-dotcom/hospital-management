package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.DichVuCLS;
import com.nhom12.hospital.repository.DichVuCLSRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dichvucls")
@CrossOrigin(origins = "*")
public class DichVuCLSController {

    private final DichVuCLSRepository dichVuCLSRepository;

    public DichVuCLSController(DichVuCLSRepository dichVuCLSRepository) {
        this.dichVuCLSRepository = dichVuCLSRepository;
    }

    @GetMapping
    public List<DichVuCLS> getAll() {
        return dichVuCLSRepository.findAll();
    }
}
