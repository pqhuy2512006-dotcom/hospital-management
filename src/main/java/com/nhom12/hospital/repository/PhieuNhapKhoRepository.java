package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.PhieuNhapKho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieuNhapKhoRepository extends JpaRepository<PhieuNhapKho, String> {
}
