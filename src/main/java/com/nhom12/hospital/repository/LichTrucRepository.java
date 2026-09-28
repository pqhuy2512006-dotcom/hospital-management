package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.LichTruc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LichTrucRepository extends JpaRepository<LichTruc, String> {
    List<LichTruc> findByNgayBetween(LocalDate from, LocalDate to);
    List<LichTruc> findByMaNhanVien(String maNhanVien);
}
