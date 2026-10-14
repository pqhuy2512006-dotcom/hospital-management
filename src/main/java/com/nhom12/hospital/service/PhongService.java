package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.Phong;
import com.nhom12.hospital.repository.PhongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhongService {
    private final PhongRepository phongRepository;

    public PhongService(PhongRepository phongRepository) {
        this.phongRepository = phongRepository;
    }

    public List<Phong> getPhongByKhoaAndLoaiPhong(String maKhoa, String loaiPhong) {
        return phongRepository.findByKhoaAndLoaiPhong(maKhoa, loaiPhong);
    }

    public void save(Phong phong) {
        if (phong.getMaPhong() == null || phong.getMaPhong().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng không được để trống!");
        }
        if (phong.getTenPhong() == null || phong.getTenPhong().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phòng không được để trống!");
        }
        if (phong.getMaKhoa() == null || phong.getMaKhoa().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn Khoa cho phòng!");
        }
        if (phong.getLoaiPhong() == null || phong.getLoaiPhong().trim().isEmpty()) {
            phong.setLoaiPhong("KHAM_BENH");
        }
        phongRepository.save(phong);
    }

    public void toggleStatus(String maPhong) {
        phongRepository.toggleStatus(maPhong);
    }
}