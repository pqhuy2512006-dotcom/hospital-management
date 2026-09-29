package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.ChiTietHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietHoaDonRepository extends JpaRepository<ChiTietHoaDon, String> {
    List<ChiTietHoaDon> findByMaHoaDon(String maHoaDon);
}
