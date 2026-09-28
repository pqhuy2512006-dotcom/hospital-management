package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.ChiTietKetQuaCLS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietKetQuaCLSRepository extends JpaRepository<ChiTietKetQuaCLS, String> {
    List<ChiTietKetQuaCLS> findByMaKetQua(String maKetQua);
}
