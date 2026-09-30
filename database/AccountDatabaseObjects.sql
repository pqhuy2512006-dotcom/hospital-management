use QuanLyBenhVien
go

CREATE OR ALTER VIEW dbo.vw_TaiKhoan
AS
    SELECT MaTaiKhoan, TenDangNhap, MatKhauHash, Email, SoDienThoai, VaiTro, TrangThai, NgayTao
    FROM dbo.TaiKhoan;
GO

CREATE OR ALTER PROCEDURE dbo.usp_TaiKhoan_Insert
    @TenDangNhap VARCHAR(50),
    @MatKhauHash VARCHAR(255),
    @Email VARCHAR(100) = NULL,
    @SoDienThoai VARCHAR(15),
    @VaiTro VARCHAR(20),
    @TrangThai BIT,
    @NgayTao DATETIME,
    @MaTaiKhoan BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.TaiKhoan (TenDangNhap, MatKhauHash, Email, SoDienThoai, VaiTro, TrangThai, NgayTao)
    VALUES (@TenDangNhap, @MatKhauHash, @Email, @SoDienThoai, @VaiTro, @TrangThai, @NgayTao);

    SET @MaTaiKhoan = CONVERT(BIGINT, SCOPE_IDENTITY());
END;
GO

CREATE OR ALTER PROCEDURE dbo.usp_TaiKhoan_Update
    @MaTaiKhoan BIGINT,
    @MatKhauHash VARCHAR(255),
    @Email VARCHAR(100) = NULL,
    @SoDienThoai VARCHAR(15),
    @VaiTro VARCHAR(20),
    @TrangThai BIT,
    @NgayTao DATETIME
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE dbo.TaiKhoan
    SET MatKhauHash = @MatKhauHash,
        Email = @Email,
        SoDienThoai = @SoDienThoai,
        VaiTro = @VaiTro,
        TrangThai = @TrangThai,
        NgayTao = @NgayTao
    WHERE MaTaiKhoan = @MaTaiKhoan;
END;
GO
