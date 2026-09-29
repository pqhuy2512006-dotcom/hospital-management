package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.Thuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThuocRepository extends JpaRepository<Thuoc, String> {
    List<Thuoc> findByTenThuocContainingIgnoreCase(String tenThuoc);
}
