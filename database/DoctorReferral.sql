IF OBJECT_ID(N'dbo.YeuCauChuyenKhoa', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.YeuCauChuyenKhoa (
        MaYeuCau VARCHAR(15) NOT NULL PRIMARY KEY,
        LoaiYeuCau VARCHAR(20) NOT NULL CHECK (LoaiYeuCau IN ('CHUYEN_KHOA', 'HOI_CHAN')),
        MaPhieuKham VARCHAR(15) NOT NULL,
        MaBenhNhan VARCHAR(15) NOT NULL,
        MaBacSiGui VARCHAR(15) NOT NULL,
        MaKhoaNhan VARCHAR(10) NOT NULL,
        MaBacSiDuocMoi VARCHAR(15) NULL,
        LyDo NVARCHAR(500) NOT NULL,
        TrangThai VARCHAR(30) NOT NULL DEFAULT 'CHO_TIEP_NHAN',
        NgayTao DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
        CONSTRAINT FK_YeuCauChuyenKhoa_PhieuKham FOREIGN KEY (MaPhieuKham) REFERENCES dbo.PhieuKham(MaPhieuKham),
        CONSTRAINT FK_YeuCauChuyenKhoa_BenhNhan FOREIGN KEY (MaBenhNhan) REFERENCES dbo.BenhNhan(MaBenhNhan),
        CONSTRAINT FK_YeuCauChuyenKhoa_BacSiGui FOREIGN KEY (MaBacSiGui) REFERENCES dbo.NhanVien(MaNhanVien),
        CONSTRAINT FK_YeuCauChuyenKhoa_KhoaNhan FOREIGN KEY (MaKhoaNhan) REFERENCES dbo.Khoa(MaKhoa),
        CONSTRAINT FK_YeuCauChuyenKhoa_BacSiMoi FOREIGN KEY (MaBacSiDuocMoi) REFERENCES dbo.NhanVien(MaNhanVien)
    );
    CREATE INDEX IX_YeuCauChuyenKhoa_BacSiNgay ON dbo.YeuCauChuyenKhoa(MaBacSiGui, NgayTao DESC);
END;