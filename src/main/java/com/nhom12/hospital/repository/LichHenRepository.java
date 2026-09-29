package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.LichHen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichHenRepository extends JpaRepository<LichHen, String> {
	List<LichHen> findByMaBacSi(String maBacSi);
}
