USE QuanLyBenhVien;
GO

DECLARE @StatusCheck SYSNAME;
SELECT TOP (1) @StatusCheck = name
FROM sys.check_constraints
WHERE parent_object_id = OBJECT_ID(N'dbo.LichHen')
  AND definition LIKE N'%TrangThai%';

IF @StatusCheck IS NOT NULL
BEGIN
    EXEC(N'ALTER TABLE dbo.LichHen DROP CONSTRAINT ' + QUOTENAME(@StatusCheck));
END;
GO

DECLARE @StatusDefault SYSNAME;
SELECT @StatusDefault = dc.name
FROM sys.default_constraints dc
JOIN sys.columns c
  ON c.object_id = dc.parent_object_id
 AND c.column_id = dc.parent_column_id
WHERE dc.parent_object_id = OBJECT_ID(N'dbo.LichHen')
  AND c.name = N'TrangThai';

IF @StatusDefault IS NOT NULL
BEGIN
    EXEC(N'ALTER TABLE dbo.LichHen DROP CONSTRAINT ' + QUOTENAME(@StatusDefault));
END;
GO

UPDATE dbo.LichHen
SET TrangThai = 'DaDatLich'
WHERE TrangThai IN ('ChoXacNhan', 'DaXacNhan');
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.check_constraints
    WHERE parent_object_id = OBJECT_ID(N'dbo.LichHen')
      AND name = N'CK_LichHen_TrangThai'
)
BEGIN
    ALTER TABLE dbo.LichHen
        ADD CONSTRAINT CK_LichHen_TrangThai
        CHECK (TrangThai IN ('DaDatLich', 'DangKham', 'DaKham', 'DaHuy'));
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.default_constraints
    WHERE parent_object_id = OBJECT_ID(N'dbo.LichHen')
      AND name = N'DF_LichHen_TrangThai'
)
BEGIN
    ALTER TABLE dbo.LichHen
        ADD CONSTRAINT DF_LichHen_TrangThai DEFAULT 'DaDatLich' FOR TrangThai;
END;
GO