use QuanLyBenhVien
go 

IF COL_LENGTH(N'dbo.LichHen', N'ThoiGianKhamDuKien') IS NULL
BEGIN
    ALTER TABLE dbo.LichHen ADD ThoiGianKhamDuKien INT NULL;
END;
GO

UPDATE dbo.LichHen
SET ThoiGianKhamDuKien = CASE
    WHEN LoaiKham = 'KhamDichVu' THEN 45
    ELSE 30
END
WHERE ThoiGianKhamDuKien IS NULL
   OR ThoiGianKhamDuKien <> CASE WHEN LoaiKham = 'KhamDichVu' THEN 45 ELSE 30 END;
GO

ALTER TABLE dbo.LichHen ALTER COLUMN ThoiGianKhamDuKien INT NOT NULL;
GO

IF OBJECT_ID(N'dbo.vw_LichHen', N'V') IS NOT NULL
BEGIN
    EXEC sys.sp_refreshview N'dbo.vw_LichHen';
END;
GO

IF OBJECT_ID(N'dbo.CK_LichHen_ThoiGianKhamDuKien', N'C') IS NULL
BEGIN
    ALTER TABLE dbo.LichHen ADD CONSTRAINT CK_LichHen_ThoiGianKhamDuKien CHECK (
        (LoaiKham IN ('KhamThuong', 'TaiKham') AND ThoiGianKhamDuKien = 30)
        OR (LoaiKham = 'KhamDichVu' AND ThoiGianKhamDuKien = 45)
    );
END;
GO
