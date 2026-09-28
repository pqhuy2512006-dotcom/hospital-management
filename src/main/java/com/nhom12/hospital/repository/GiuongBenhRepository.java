package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.GiuongBenh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiuongBenhRepository extends JpaRepository<GiuongBenh, String> {
    List<GiuongBenh> findByMaKhoa(String maKhoa);
    List<GiuongBenh> findByTrangThai(String trangThai);
}
