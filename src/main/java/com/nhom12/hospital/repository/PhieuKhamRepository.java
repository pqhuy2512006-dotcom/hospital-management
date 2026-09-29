package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.PhieuKham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface PhieuKhamRepository extends JpaRepository<PhieuKham, String> {
    List<PhieuKham> findByMaBenhNhan(String maBenhNhan);
    List<PhieuKham> findByMaBacSi(String maBacSi);
    List<PhieuKham> findByNgayKhamGreaterThanEqualAndNgayKhamLessThan(LocalDateTime from, LocalDateTime toExclusive);
}
