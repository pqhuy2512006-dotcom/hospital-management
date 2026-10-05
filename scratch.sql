USE QuanLyBenhVien;
GO
DECLARE @StatusCheck SYSNAME;
SELECT TOP (1) @StatusCheck = name FROM sys.check_constraints WHERE parent_object_id = OBJECT_ID(N'dbo.LichHen') AND definition LIKE N'%TrangThai%';
IF @StatusCheck IS NOT NULL EXEC(N'ALTER TABLE dbo.LichHen DROP CONSTRAINT ' + QUOTENAME(@StatusCheck));
GO
ALTER TABLE dbo.LichHen ADD CONSTRAINT CK_LichHen_TrangThai CHECK (TrangThai IN ('DaDatLich', 'DangChoKham', 'DangKham', 'DaKham', 'DaHuy'));
GO
