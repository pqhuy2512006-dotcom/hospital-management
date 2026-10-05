USE QuanLyBenhVien;
GO
ALTER TABLE dbo.LichHen DROP CONSTRAINT CK_LichHen_TrangThai;
GO
ALTER TABLE dbo.LichHen ADD CONSTRAINT CK_LichHen_TrangThai CHECK (TrangThai IN ('DaDatLich', 'DangChoKham', 'DangKham', 'DaKham', 'DaHuy'));
GO
