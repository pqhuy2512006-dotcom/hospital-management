package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.ChiTietNhapKho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietNhapKhoRepository extends JpaRepository<ChiTietNhapKho, String> {
    List<ChiTietNhapKho> findByMaPhieuNhap(String maPhieuNhap);
}
