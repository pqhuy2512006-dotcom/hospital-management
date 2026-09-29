package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.YeuCauChuyenKhoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YeuCauChuyenKhoaRepository extends JpaRepository<YeuCauChuyenKhoa, String> {
    List<YeuCauChuyenKhoa> findByMaBacSiGuiOrderByNgayTaoDesc(String maBacSiGui);
}