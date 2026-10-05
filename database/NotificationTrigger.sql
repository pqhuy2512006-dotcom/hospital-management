CREATE OR ALTER TRIGGER trg_KetQuaCLS_Notification ON dbo.KetQuaCLS
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
