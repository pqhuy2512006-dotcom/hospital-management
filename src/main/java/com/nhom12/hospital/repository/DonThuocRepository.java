package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.DonThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonThuocRepository extends JpaRepository<DonThuoc, String> {
    List<DonThuoc> findByMaPhieuKham(String maPhieuKham);
}
