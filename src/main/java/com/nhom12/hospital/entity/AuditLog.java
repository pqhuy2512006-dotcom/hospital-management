package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@org.hibernate.annotations.Immutable
@Table(name = "vw_AuditLog")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AuditLogId")
    private Long id;

    @Column(name = "EventType", nullable = false, length = 40)
    private String eventType;

    @Column(name = "Username", length = 50)
    private String username;

    @Column(name = "UserRole", length = 20)
    private String userRole;

    @Column(name = "HttpMethod", nullable = false, length = 10)
    private String httpMethod;

    @Column(name = "RequestPath", nullable = false, length = 255)
    private String requestPath;

    @Column(name = "HttpStatus", nullable = false)
    private Integer httpStatus;

    @Column(name = "RemoteAddress", length = 64)
    private String remoteAddress;

    @Column(name = "Details", length = 500)
    private String details;

    @Column(name = "OccurredAt", nullable = false)
    private LocalDateTime occurredAt;

    @PrePersist
    public void prePersist() {
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }
}