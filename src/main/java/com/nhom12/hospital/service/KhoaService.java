package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.Khoa;
import com.nhom12.hospital.repository.KhoaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhoaService {
    private final KhoaRepository khoaRepository;

    public KhoaService(KhoaRepository khoaRepository) {
        this.khoaRepository = khoaRepository;
    }

    public List<Khoa> getAllKhoa() {
        return khoaRepository.findAll();
    }
}

