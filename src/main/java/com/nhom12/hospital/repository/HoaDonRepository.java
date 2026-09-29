package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    List<HoaDon> findByMaBenhNhan(String maBenhNhan);
    List<HoaDon> findByTrangThaiTT(String trangThaiTT);
    List<HoaDon> findByTrangThaiTTAndNgayThanhToanGreaterThanEqualAndNgayThanhToanLessThan(
            String trangThaiTT, LocalDateTime from, LocalDateTime toExclusive);
}
