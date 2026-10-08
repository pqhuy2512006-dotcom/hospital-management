package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.DichVuCLS;
import com.nhom12.hospital.service.DichVuCLSService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dichvucls")
@CrossOrigin(origins = "*")
public class DichVuCLSController {

    private final DichVuCLSService dichVuCLSService;

    public DichVuCLSController(DichVuCLSService dichVuCLSService) {
        this.dichVuCLSService = dichVuCLSService;
    }

    @GetMapping
    public List<DichVuCLS> getAll() {
        return dichVuCLSService.getAll();
    }
}
