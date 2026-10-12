DECLARE @Hash VARCHAR(255) = '\\\';

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'ns_ngoc')
BEGIN
    INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, Email, SoDienThoai, VaiTro, TrangThai) 
    VALUES ('ns_ngoc', @Hash, 'ngoc.nhansu@hospital.com', '0912345009', 'QuanLyNhanSu', 1);
END
GO
