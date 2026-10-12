USE QuanLyBenhVien;
GO

SET XACT_ABORT ON;
BEGIN TRANSACTION;

DECLARE @constraintName sysname;
DECLARE @sql nvarchar(max);

SELECT TOP (1) @constraintName = name
FROM sys.check_constraints
WHERE parent_object_id = OBJECT_ID(N'dbo.TaiKhoan')
  AND definition LIKE N'%VaiTro%'
  AND definition NOT LIKE N'%GiamDoc%';

IF @constraintName IS NOT NULL
BEGIN
    SET @sql = N'ALTER TABLE dbo.TaiKhoan DROP CONSTRAINT ' + QUOTENAME(@constraintName);
    EXEC sys.sp_executesql @sql;
END;

IF NOT EXISTS (
    SELECT 1
    FROM sys.check_constraints
    WHERE parent_object_id = OBJECT_ID(N'dbo.TaiKhoan')
      AND definition LIKE N'%VaiTro%'
      AND definition LIKE N'%GiamDoc%'
)
BEGIN
    ALTER TABLE dbo.TaiKhoan WITH CHECK
        ADD CONSTRAINT CK_TaiKhoan_VaiTro
        CHECK (VaiTro IN ('BacSi','DieuDuong','LeTan','DuocSi','KTV','ThuNgan','QuanTri','QuanLyNhanSu','GiamDoc','BenhNhan'));
END;

SET @constraintName = NULL;

SELECT TOP (1) @constraintName = name
FROM sys.check_constraints
WHERE parent_object_id = OBJECT_ID(N'dbo.NhanVien')
  AND definition LIKE N'%VaiTro%'
  AND definition NOT LIKE N'%GiamDoc%';

IF @constraintName IS NOT NULL
BEGIN
    SET @sql = N'ALTER TABLE dbo.NhanVien DROP CONSTRAINT ' + QUOTENAME(@constraintName);
    EXEC sys.sp_executesql @sql;
END;

IF NOT EXISTS (
    SELECT 1
    FROM sys.check_constraints
    WHERE parent_object_id = OBJECT_ID(N'dbo.NhanVien')
      AND definition LIKE N'%VaiTro%'
      AND definition LIKE N'%GiamDoc%'
)
BEGIN
    ALTER TABLE dbo.NhanVien WITH CHECK
        ADD CONSTRAINT CK_NhanVien_VaiTro
        CHECK (VaiTro IN ('BacSi','DieuDuong','LeTan','DuocSi','KTV','ThuNgan','QuanTri','QuanLyNhanSu','GiamDoc'));
END;

COMMIT TRANSACTION;
