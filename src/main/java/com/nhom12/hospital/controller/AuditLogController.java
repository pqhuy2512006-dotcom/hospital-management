package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.AuditLog;
import com.nhom12.hospital.service.AuditLogService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public List<AuditLog> getRecentLogs() {
        return auditLogService.getRecentLogs();
    }
}