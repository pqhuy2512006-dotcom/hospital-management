package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
    Optional<NhanVien> findByMaTaiKhoan(Long maTaiKhoan);
    List<NhanVien> findAllByMaTaiKhoanIn(Collection<Long> maTaiKhoanIds);
    boolean existsBySoDienThoai(String soDienThoai);
}
