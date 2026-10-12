package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.GiuongBenh;
import com.nhom12.hospital.repository.GiuongBenhRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GiuongBenhService {
    private final GiuongBenhRepository giuongBenhRepository;

    public GiuongBenhService(GiuongBenhRepository giuongBenhRepository) {
        this.giuongBenhRepository = giuongBenhRepository;
    }

    public List<GiuongBenh> getAll(String khoa) {
        if (khoa != null && !khoa.trim().isEmpty() && !khoa.equalsIgnoreCase("ALL")) {
            return giuongBenhRepository.findByMaKhoa(khoa);
        }
        return giuongBenhRepository.findAll();
    }

    public Optional<GiuongBenh> getById(String id) {
        return giuongBenhRepository.findById(id);
    }

    public GiuongBenh create(GiuongBenh giuongBenh) {
        return giuongBenhRepository.save(giuongBenh);
    }

    public Optional<GiuongBenh> update(String id, GiuongBenh updated) {
        return giuongBenhRepository.findById(id).map(g -> {
            g.setSoGiuong(updated.getSoGiuong());
            g.setTrangThai(updated.getTrangThai());
            g.setDonGiaNgay(updated.getDonGiaNgay());
            g.setMaKhoa(updated.getMaKhoa());
            return giuongBenhRepository.save(g);
        });
    }
}

