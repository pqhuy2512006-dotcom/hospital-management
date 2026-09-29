package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
	Optional<NhanVien> findByMaTaiKhoan(Long maTaiKhoan);
	boolean existsBySoDienThoai(String soDienThoai);
	boolean existsBySoDienThoaiAndMaNhanVienNot(String soDienThoai, String maNhanVien);
}
