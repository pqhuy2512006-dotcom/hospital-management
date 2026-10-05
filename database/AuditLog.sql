use QuanLyBenhVien
go

IF OBJECT_ID(N'dbo.AuditLog', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.AuditLog (
        AuditLogId BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        EventType VARCHAR(40) NOT NULL,
        Username VARCHAR(50) NULL,
        UserRole VARCHAR(20) NULL,
        HttpMethod VARCHAR(10) NOT NULL,
        RequestPath VARCHAR(255) NOT NULL,
        HttpStatus INT NOT NULL,
        RemoteAddress VARCHAR(64) NULL,
        Details VARCHAR(500) NULL,
        OccurredAt DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );

END;

IF COL_LENGTH(N'dbo.AuditLog', N'Details') IS NULL
    ALTER TABLE dbo.AuditLog ADD Details VARCHAR(500) NULL;

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_AuditLog_OccurredAt' AND object_id = OBJECT_ID(N'dbo.AuditLog'))
    CREATE INDEX IX_AuditLog_OccurredAt ON dbo.AuditLog(OccurredAt DESC);