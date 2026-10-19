use QuanLyBenhVien
go

CREATE VIEW dbo.vw_TaiKhoan  
AS  
    SELECT MaTaiKhoan, TenDangNhap, MatKhauHash, Email, SoDienThoai, VaiTro, TrangThai, NgayTao  
    FROM dbo.TaiKhoan;  

EXEC sp_helptext 'dbo.vw_TaiKhoan';