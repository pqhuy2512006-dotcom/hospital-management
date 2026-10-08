
ALTER TABLE NhanVien 
ADD MaKhoa VARCHAR(10) NULL;
GO

EXEC sp_refreshview 'dbo.vw_NhanVien';
GO


UPDATE NhanVien SET MaKhoa = 'KNT' WHERE ChuyenKhoa = N'Nội Tổng Hợp';
UPDATE NhanVien SET MaKhoa = 'KNG' WHERE ChuyenKhoa = N'Ngoại Tiêu Hóa';
UPDATE NhanVien SET MaKhoa = 'TMH' WHERE ChuyenKhoa = N'Tai Mũi Họng';
UPDATE NhanVien SET MaKhoa = 'CLS' WHERE VaiTro = 'KTV';
UPDATE NhanVien SET MaKhoa = 'KKB' WHERE VaiTro IN ('ThuNgan', 'DuocSi', 'LeTan');
UPDATE NhanVien SET MaKhoa = 'KNT' WHERE VaiTro = 'DieuDuong' AND MaKhoa IS NULL;
UPDATE NhanVien SET MaKhoa = 'KKB' WHERE MaKhoa IS NULL;
GO

ALTER TABLE NhanVien
ADD CONSTRAINT FK_NhanVien_Khoa FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa);
GO

