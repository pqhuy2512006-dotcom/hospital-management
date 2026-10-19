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

    public void save(GiuongBenh giuongBenh) {
        if (giuongBenh.getSoGiuong() == null || giuongBenh.getSoGiuong().trim().isEmpty()) {
            throw new IllegalArgumentException("Số giường không được để trống!");
        }
        if ((giuongBenh.getMaGiuong() == null || giuongBenh.getMaGiuong().trim().isEmpty())
                && (giuongBenh.getMaPhong() == null || giuongBenh.getMaPhong().trim().isEmpty())) {
            throw new IllegalArgumentException("Vui lòng chọn phòng cho giường!");
        }
        giuongBenhRepository.save(giuongBenh);
    }

    public void toggleStatus(String maGiuong) {
        giuongBenhRepository.toggleStatus(maGiuong);
    }
}
