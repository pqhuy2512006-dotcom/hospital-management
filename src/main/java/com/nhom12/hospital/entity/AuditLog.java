package com.nhom12.hospital.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {
    private Long id;
    private String eventType;
    private String username;
    private String userRole;
    private String httpMethod;
    private String requestPath;
    private Integer httpStatus;
    private String remoteAddress;
    private String details;
    private LocalDateTime occurredAt;
    public void prePersist() {
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }
}