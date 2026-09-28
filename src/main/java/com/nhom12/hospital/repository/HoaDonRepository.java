package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    List<HoaDon> findByMaBenhNhan(String maBenhNhan);
    List<HoaDon> findByTrangThaiTT(String trangThaiTT);
}
