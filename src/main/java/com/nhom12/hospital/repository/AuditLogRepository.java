package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.AuditLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AuditLogRepository {

    private static final RowMapper<AuditLog> ROW_MAPPER = (resultSet, rowNum) -> {
        AuditLog auditLog = new AuditLog();
        auditLog.setId(resultSet.getLong("AuditLogId"));
        auditLog.setEventType(resultSet.getString("EventType"));
        auditLog.setUsername(resultSet.getString("Username"));
        auditLog.setUserRole(resultSet.getString("UserRole"));
        auditLog.setHttpMethod(resultSet.getString("HttpMethod"));
        auditLog.setRequestPath(resultSet.getString("RequestPath"));
        auditLog.setHttpStatus(resultSet.getInt("HttpStatus"));
        auditLog.setRemoteAddress(resultSet.getString("RemoteAddress"));
        auditLog.setDetails(resultSet.getString("Details"));
        auditLog.setOccurredAt(resultSet.getTimestamp("OccurredAt").toLocalDateTime());
        return auditLog;
    };

    private final JdbcTemplate jdbcTemplate;

    public AuditLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AuditLog> findTop200ByOrderByOccurredAtDesc() {
        return jdbcTemplate.query(
                "SELECT TOP (200) * FROM dbo.vw_AuditLog ORDER BY OccurredAt DESC",
                ROW_MAPPER
        );
    }

    public AuditLog save(AuditLog auditLog) {
        if (auditLog.getOccurredAt() == null) {
            auditLog.setOccurredAt(LocalDateTime.now());
        }
        Map<String, Object> columns = new LinkedHashMap<>();
        columns.put("EventType", auditLog.getEventType());
        columns.put("Username", auditLog.getUsername());
        columns.put("UserRole", auditLog.getUserRole());
        columns.put("HttpMethod", auditLog.getHttpMethod());
        columns.put("RequestPath", auditLog.getRequestPath());
        columns.put("HttpStatus", auditLog.getHttpStatus());
        columns.put("RemoteAddress", auditLog.getRemoteAddress());
        columns.put("Details", auditLog.getDetails());
        columns.put("OccurredAt", auditLog.getOccurredAt());
        EntityProcedureSupport.save(jdbcTemplate, "AuditLog", columns);
        return auditLog;
    }
}