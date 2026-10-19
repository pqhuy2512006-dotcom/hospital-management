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
    public void save(Khoa khoa) {
        if (khoa.getMaKhoa() == null || khoa.getMaKhoa().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khoa không được để trống!");
        }
        if (khoa.getTenKhoa() == null || khoa.getTenKhoa().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khoa không được để trống!");
        }
        khoaRepository.save(khoa);
    }

    public void toggleStatus(String maKhoa) {
        khoaRepository.toggleStatus(maKhoa);
    }
}

