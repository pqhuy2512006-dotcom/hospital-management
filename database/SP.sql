use QuanLyBenhVien 
go


CREATE PROCEDURE dbo.sp_ThongBao_Broadcast  
    @TieuDe NVARCHAR(200),  
    @NoiDung NVARCHAR(1000),  
    @LoaiThongBao VARCHAR(50),  
    @TargetRole VARCHAR(20) = 'ALL'  
AS  
BEGIN  
    SET NOCOUNT ON;  
    -- Lấy danh sách tài khoản từ VIEW dbo.vw_TaiKhoan theo nhóm chỉ định  
    IF @TargetRole IS NULL OR @TargetRole = '' OR UPPER(@TargetRole) = 'ALL'  
    BEGIN  
        INSERT INTO dbo.ThongBao (MaTaiKhoan, TieuDe, NoiDung, LoaiThongBao, DaDoc, NgayTao)  
        SELECT MaTaiKhoan, @TieuDe, @NoiDung, @LoaiThongBao, 0, GETDATE()  
        FROM dbo.vw_TaiKhoan  
        WHERE TrangThai = 1;  
    END  
    ELSE IF UPPER(@TargetRole) = 'STAFF'  
    BEGIN  
        INSERT INTO dbo.ThongBao (MaTaiKhoan, TieuDe, NoiDung, LoaiThongBao, DaDoc, NgayTao)  
        SELECT MaTaiKhoan, @TieuDe, @NoiDung, @LoaiThongBao, 0, GETDATE()  
        FROM dbo.vw_TaiKhoan  
        WHERE TrangThai = 1 AND VaiTro <> 'BenhNhan';  
    END  
    ELSE  
    BEGIN  
        INSERT INTO dbo.ThongBao (MaTaiKhoan, TieuDe, NoiDung, LoaiThongBao, DaDoc, NgayTao)  
        SELECT MaTaiKhoan, @TieuDe, @NoiDung, @LoaiThongBao, 0, GETDATE()  
        FROM dbo.vw_TaiKhoan  
        WHERE TrangThai = 1 AND VaiTro = @TargetRole;  
    END  
    -- Trả về số lượng người nhận thực tế  
    SELECT @@ROWCOUNT AS SoNguoiNhan;  
END;  
go

-- Tạo Cập nhật Khoa
CREATE PROCEDURE dbo.sp_Khoa_Save  
    @MaKhoa VARCHAR(10),  
    @TenKhoa NVARCHAR(100),  
    @MoTa NVARCHAR(300) = NULL  
AS  
BEGIN  
    SET NOCOUNT ON;  
    IF EXISTS (SELECT 1 FROM Khoa WHERE MaKhoa = @MaKhoa)  
    BEGIN  
        UPDATE Khoa   
        SET TenKhoa = @TenKhoa, MoTa = @MoTa  
        WHERE MaKhoa = @MaKhoa;  
    END  
    ELSE  
    BEGIN  
        INSERT INTO Khoa (MaKhoa, TenKhoa, MoTa, TrangThai)  
        VALUES (@MaKhoa, @TenKhoa, @MoTa, 1);  
    END  
END;
go

-- 3. SP Bật/Tắt KHOA: Khóa toàn bộ Phòng và Giường thuộc Khoa (JOIN qua bảng Phong)  
create PROCEDURE dbo.sp_Khoa_ToggleStatus  
    @MaKhoa VARCHAR(10)  
AS  
BEGIN  
    SET NOCOUNT ON;  
    DECLARE @CurrentStatus BIT;  
    SELECT @CurrentStatus = ISNULL(TrangThai, 1) FROM Khoa WHERE MaKhoa = @MaKhoa;  
  
    IF @CurrentStatus = 1  
    BEGIN  
        -- Đổi Khoa sang Tạm ngưng (0)  
        UPDATE Khoa SET TrangThai = 0 WHERE MaKhoa = @MaKhoa;  
  
        -- Khóa tất cả Phòng thuộc khoa  
        UPDATE Phong SET TrangThai = 0 WHERE MaKhoa = @MaKhoa;  
  
        -- Khóa tất cả Giường trống thuộc các phòng của khoa này (JOIN qua Phong)  
        UPDATE g  
        SET g.TrangThai = 'BaoTri'  
        FROM GiuongBenh g  
        INNER JOIN Phong p ON g.MaPhong = p.MaPhong  
        WHERE p.MaKhoa = @MaKhoa AND g.TrangThai = 'Trong';  
  
        -- Hủy các lịch hẹn tương lai chưa khám  
        DECLARE @AffectedAppointments TABLE (MaLichHen VARCHAR(15), MaTaiKhoan BIGINT, NgayKham DATE);  
          
        INSERT INTO @AffectedAppointments (MaLichHen, MaTaiKhoan, NgayKham)  
        SELECT lh.MaLichHen, bn.MaTaiKhoan, lh.NgayKham  
        FROM LichHen lh  
        inner JOIN BenhNhan bn ON lh.MaBenhNhan = bn.MaBenhNhan  
        WHERE lh.MaKhoa = @MaKhoa   
          AND lh.NgayKham >= CAST(GETDATE() AS DATE)  
          AND lh.TrangThai = 'DaDatLich';  
  
        UPDATE LichHen   
        SET TrangThai = 'DaHuy'   
        WHERE MaLichHen IN (SELECT MaLichHen FROM @AffectedAppointments);  
  
        -- Bắn thông báo cho bệnh nhân  
        DECLARE @TenKhoa NVARCHAR(100);  
        SELECT @TenKhoa = TenKhoa FROM Khoa WHERE MaKhoa = @MaKhoa;  
  
        INSERT INTO ThongBao (MaTaiKhoan, TieuDe, NoiDung, LoaiThongBao, DaDoc, NgayTao)  
        SELECT   
            aa.MaTaiKhoan,  
            N'Thông báo hủy lịch hẹn do Khoa tạm ngưng tiếp nhận',  
            N'Lịch hẹn ngày ' + CONVERT(VARCHAR, aa.NgayKham, 103) + N' tại ' + @TenKhoa + N' đã được tự động hủy do Khoa tạm ngưng hoạt động. Quý khách vui lòng đặt lại lịch tại khoa khác hoặc liên hệ bộ phận Tiếp đón.',  
            N'LICH_HEN',  
            0,  
            GETDATE()  
        FROM @AffectedAppointments aa  
        WHERE aa.MaTaiKhoan IS NOT NULL;  
    END  
    ELSE  
    BEGIN  
        -- Mở lại hoạt động Khoa (1)  
        UPDATE Khoa SET TrangThai = 1 WHERE MaKhoa = @MaKhoa;  
  
        -- Mở lại tất cả Phòng thuộc khoa  
        UPDATE Phong SET TrangThai = 1 WHERE MaKhoa = @MaKhoa;  
  
        -- Mở lại các giường 'BaoTri' về lại 'Trong' (JOIN qua Phong)  
        UPDATE g  
        SET g.TrangThai = 'Trong'  
        FROM GiuongBenh g  
        INNER JOIN Phong p ON g.MaPhong = p.MaPhong  
        WHERE p.MaKhoa = @MaKhoa AND g.TrangThai = 'BaoTri';  
    END  
END;  
EXEC sp_helptext 'dbo.sp_Khoa_ToggleStatus'