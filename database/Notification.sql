CREATE TABLE dbo.ThongBao (
    MaThongBao INT IDENTITY(1,1) PRIMARY KEY,
    MaTaiKhoan BIGINT NOT NULL,
    TieuDe NVARCHAR(200) NOT NULL,
    NoiDung NVARCHAR(1000) NOT NULL,
    LoaiThongBao VARCHAR(50) NOT NULL,
    DaDoc BIT NOT NULL DEFAULT 0,
    NgayTao DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_ThongBao_TaiKhoan FOREIGN KEY (MaTaiKhoan) REFERENCES dbo.TaiKhoan(MaTaiKhoan)
);
GO

CREATE VIEW dbo.vw_ThongBao AS
SELECT * FROM dbo.ThongBao;
GO

-- Stored Procedure đánh dấu đã đọc
CREATE PROCEDURE dbo.sp_ThongBao_MarkRead
    @MaThongBao INT,
    @MaTaiKhoan BIGINT
AS
BEGIN
    UPDATE dbo.ThongBao
    SET DaDoc = 1
    WHERE MaThongBao = @MaThongBao AND MaTaiKhoan = @MaTaiKhoan;
END;
GO

-- Trigger: Tự động báo khi có kết quả Cận Lâm Sàng
CREATE TRIGGER trg_KetQuaCLS_Notification ON dbo.KetQuaCLS
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Nếu cột KetLuan được cập nhật (nghĩa là đã có kết luận)
    IF UPDATE(KetLuan)
    BEGIN
        INSERT INTO dbo.ThongBao (MaTaiKhoan, TieuDe, NoiDung, LoaiThongBao)
        SELECT 
            tk.MaTaiKhoan,
            N'Kết quả xét nghiệm/CĐHA mới',
            N'Kết quả dịch vụ "' + d.TenDichVu + N'" của bạn đã có. Vui lòng kiểm tra mục Hồ sơ bệnh án.',
            'KET_QUA_XN'
        FROM inserted i
        JOIN deleted d_old ON i.MaKetQua = d_old.MaKetQua
        JOIN dbo.PhieuKham pk ON i.MaPhieuKham = pk.MaPhieuKham
        JOIN dbo.BenhNhan bn ON pk.MaBenhNhan = bn.MaBenhNhan
        JOIN dbo.TaiKhoan tk ON bn.MaTaiKhoan = tk.MaTaiKhoan
        JOIN dbo.DichVuCLS d ON i.MaDichVu = d.MaDichVu
        WHERE i.KetLuan IS NOT NULL AND (d_old.KetLuan IS NULL OR d_old.KetLuan = '');
    END
END;
GO

-- Stored Procedure: Tự động nhắc lịch hẹn ngày mai
CREATE PROCEDURE dbo.sp_TaoThongBaoNhacLich
AS
BEGIN
    SET NOCOUNT ON;
    
    INSERT INTO dbo.ThongBao (MaTaiKhoan, TieuDe, NoiDung, LoaiThongBao)
    SELECT 
        tk.MaTaiKhoan,
        N'Nhắc nhở lịch hẹn khám bệnh',
        N'Bạn có lịch khám vào ngày mai lúc ' + CONVERT(VARCHAR(5), lh.GioKham, 108) + N'. Vui lòng có mặt đúng giờ để được phục vụ tốt nhất.',
        'NHAC_LICH'
    FROM dbo.LichHen lh
    JOIN dbo.BenhNhan bn ON lh.MaBenhNhan = bn.MaBenhNhan
    JOIN dbo.TaiKhoan tk ON bn.MaTaiKhoan = tk.MaTaiKhoan
    WHERE lh.TrangThai = 'DaDatLich' 
      AND CAST(lh.NgayKham AS DATE) = CAST(DATEADD(DAY, 1, GETDATE()) AS DATE)
      AND NOT EXISTS (
          SELECT 1 FROM dbo.ThongBao tb 
          WHERE tb.MaTaiKhoan = tk.MaTaiKhoan 
            AND tb.LoaiThongBao = 'NHAC_LICH' 
            AND CAST(tb.NgayTao AS DATE) = CAST(GETDATE() AS DATE)
      );
END;
GO
