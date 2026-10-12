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
}

