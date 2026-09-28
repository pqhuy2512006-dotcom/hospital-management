package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.NoiTru;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoiTruRepository extends JpaRepository<NoiTru, String> {
    List<NoiTru> findByTrangThai(String trangThai);
    List<NoiTru> findByMaBenhNhan(String maBenhNhan);
}
