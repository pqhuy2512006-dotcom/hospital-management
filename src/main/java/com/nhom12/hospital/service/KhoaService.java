package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.Khoa;
import com.nhom12.hospital.repository.KhoaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

@Service
public class KhoaService {
    private final KhoaRepository khoaRepository;

    public KhoaService(KhoaRepository khoaRepository) {
        this.khoaRepository = khoaRepository;
    }

    public List<Khoa> getAllKhoa(boolean all) {
    if (all) {
        return khoaRepository.findAllForAdmin();
    }
    return khoaRepository.findAll();
}

    public Optional<Khoa> getKhoaById(String maKhoa) {
        return khoaRepository.findById(maKhoa);
    }

    @Transactional
    public Khoa createKhoa(Khoa khoa) {
        if (khoa.getMaKhoa() == null || khoa.getMaKhoa().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khoa không được để trống.");
        }
        if (khoa.getTenKhoa() == null || khoa.getTenKhoa().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khoa không được để trống.");
        }
        if (khoaRepository.existsById(khoa.getMaKhoa())) {
            throw new IllegalArgumentException("Mã khoa đã tồn tại trong hệ thống.");
        }
        
        return khoaRepository.save(khoa);
    }

    @Transactional
    public Khoa updateKhoa(String maKhoa, Khoa khoaUpdate) {
        Optional<Khoa> existingKhoaOpt = khoaRepository.findById(maKhoa);
        if (existingKhoaOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy khoa cần sửa.");
        }
        
        if (khoaUpdate.getTenKhoa() == null || khoaUpdate.getTenKhoa().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khoa không được để trống.");
        }

        Khoa existingKhoa = existingKhoaOpt.get();
        existingKhoa.setTenKhoa(khoaUpdate.getTenKhoa());
        existingKhoa.setMoTa(khoaUpdate.getMoTa());
        existingKhoa.setMaTruongKhoa(khoaUpdate.getMaTruongKhoa());

        return khoaRepository.save(existingKhoa);
    }

    @Transactional
    public void deleteKhoa(String maKhoa) {
        if (!khoaRepository.existsById(maKhoa)) {
            throw new IllegalArgumentException("Không tìm thấy khoa cần xóa.");
        }
        khoaRepository.deleteById(maKhoa);
    }
}

