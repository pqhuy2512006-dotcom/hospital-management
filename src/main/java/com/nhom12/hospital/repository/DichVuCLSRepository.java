package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.DichVuCLS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DichVuCLSRepository extends JpaRepository<DichVuCLS, String> {
}
