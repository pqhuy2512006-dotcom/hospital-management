
IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'admin')
BEGIN
    INSERT INTO TaiKhoan
    (
        TenDangNhap,
        MatKhauHash,
        Email,
        SoDienThoai,
        VaiTro,
        TrangThai
    )
    VALUES
    (
        'admin',
        '$2a$10$MhcIw3ha3SWQrvmNnDzurO2NOEzBnB2BiKG43ZfsUoPhK21olvCDq',
        'admin@hospital.com',
        '0900000000',
        'QuanTri',
        1
    );
END
GO