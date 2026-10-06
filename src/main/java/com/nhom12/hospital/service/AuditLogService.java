package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.AuditLog;
import com.nhom12.hospital.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(String eventType, String username, String role, String method,
                       String path, int status, String remoteAddress, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEventType(eventType);
        auditLog.setUsername(truncate(username, 50));
        auditLog.setUserRole(truncate(role, 20));
        auditLog.setHttpMethod(truncate(method, 10));
        auditLog.setRequestPath(truncate(path, 255));
        auditLog.setHttpStatus(status);
        auditLog.setRemoteAddress(truncate(remoteAddress, 64));
        auditLog.setDetails(truncate(details, 500));
        auditLog.setOccurredAt(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}