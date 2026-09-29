package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop200ByOrderByOccurredAtDesc();
}