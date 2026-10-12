package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.DichVuCLS;
import com.nhom12.hospital.repository.DichVuCLSRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DichVuCLSService {
    private final DichVuCLSRepository dichVuCLSRepository;

    public DichVuCLSService(DichVuCLSRepository dichVuCLSRepository) {
        this.dichVuCLSRepository = dichVuCLSRepository;
    }

    public List<DichVuCLS> getAll() {
        return dichVuCLSRepository.findAll();
    }
}

