package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.Khoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhoaRepository extends JpaRepository<Khoa, String> {
}