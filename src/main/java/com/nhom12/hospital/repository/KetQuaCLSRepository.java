package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.KetQuaCLS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KetQuaCLSRepository extends JpaRepository<KetQuaCLS, String> {
    List<KetQuaCLS> findByMaBenhNhan(String maBenhNhan);
    List<KetQuaCLS> findByMaPhieuKham(String maPhieuKham);
}
