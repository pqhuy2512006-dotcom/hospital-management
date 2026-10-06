USE QuanLyBenhVien;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* ======================================================================
   HỆ THỐNG PHÂN QUYỀN RBAC (ROLE-BASED ACCESS CONTROL) CHUẨN Y TẾ
   Gán các chức năng trên UI của các vai trò:
   1. Thu Ngân (CASHIER / ThuNgan) - Quản lý viện phí, hóa đơn, hoàn ứng
   2. Điều Dưỡng (NURSE / DieuDuong) - Giường bệnh nội trú, EMR, tra cứu viện phí
   3. Quản Lý Nhân Sự (HR / NhanSu) - Quản lý nhân viên y tế, phân công lịch trực
   ====================================================================== */

-- 1. BẢNG VAITRO (Danh mục Vai trò)
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'VaiTro')
BEGIN
    CREATE TABLE dbo.VaiTro (
        MaVaiTro        VARCHAR(20)      PRIMARY KEY,
        TenVaiTro       NVARCHAR(100)    NOT NULL,
        TenTiengAnh     VARCHAR(50)      NULL,
        MoTa            NVARCHAR(255)    NULL,
        TrangThai       BIT              NOT NULL DEFAULT 1
    );
END
GO

-- 2. BẢNG CHUCNANG (Danh mục Chức năng trên UI)
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'ChucNang')
BEGIN
    CREATE TABLE dbo.ChucNang (
        MaChucNang      VARCHAR(50)      PRIMARY KEY,
        TenChucNang     NVARCHAR(150)    NOT NULL,
        MaPhanHe        VARCHAR(30)      NOT NULL,
        TenPhanHe       NVARCHAR(100)    NOT NULL,
        UrlTrang        VARCHAR(100)     NULL,
        PhuongThucApi   VARCHAR(10)      NULL,
        EndpointApi     VARCHAR(150)     NULL,
        MoTa            NVARCHAR(500)    NULL,
        ThuTuHienThi    INT              NOT NULL DEFAULT 0
    );
END
GO

-- 3. BẢNG PHANQUYEN (Gán Chức Năng Cho Vai Trò)
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'PhanQuyen')
BEGIN
    CREATE TABLE dbo.PhanQuyen (
        MaPhanQuyen     BIGINT IDENTITY(1,1) PRIMARY KEY,
        MaVaiTro        VARCHAR(20)      NOT NULL,
        MaChucNang      VARCHAR(50)      NOT NULL,
        QuyenXem        BIT              NOT NULL DEFAULT 1,
        QuyenThem       BIT              NOT NULL DEFAULT 0,
        QuyenSua        BIT              NOT NULL DEFAULT 0,
        QuyenXoa        BIT              NOT NULL DEFAULT 0,
        GhiChu          NVARCHAR(255)    NULL,
        NgayCapNhat     DATETIME         NOT NULL DEFAULT GETDATE(),
        CONSTRAINT UQ_PhanQuyen_VaiTro_ChucNang UNIQUE (MaVaiTro, MaChucNang),
        CONSTRAINT FK_PhanQuyen_VaiTro FOREIGN KEY (MaVaiTro) REFERENCES dbo.VaiTro(MaVaiTro) ON DELETE CASCADE,
        CONSTRAINT FK_PhanQuyen_ChucNang FOREIGN KEY (MaChucNang) REFERENCES dbo.ChucNang(MaChucNang) ON DELETE CASCADE
    );
END
GO

-- 4. TẠO CÁC VIEWS TRA CỨU
CREATE OR ALTER VIEW dbo.vw_VaiTro AS SELECT * FROM dbo.VaiTro;
GO

CREATE OR ALTER VIEW dbo.vw_ChucNang AS SELECT * FROM dbo.ChucNang;
GO

CREATE OR ALTER VIEW dbo.vw_PhanQuyen AS SELECT * FROM dbo.PhanQuyen;
GO

CREATE OR ALTER VIEW dbo.vw_PhanQuyenChiTiet
AS
SELECT 
    pq.MaPhanQuyen,
    vt.MaVaiTro,
    vt.TenVaiTro,
    vt.TenTiengAnh,
    cn.MaChucNang,
    cn.TenChucNang,
    cn.MaPhanHe,
    cn.TenPhanHe,
    cn.UrlTrang,
    cn.PhuongThucApi,
    cn.EndpointApi,
    pq.QuyenXem,
    pq.QuyenThem,
    pq.QuyenSua,
    pq.QuyenXoa,
    pq.GhiChu,
    pq.NgayCapNhat
FROM dbo.PhanQuyen pq
JOIN dbo.VaiTro vt ON pq.MaVaiTro = vt.MaVaiTro
JOIN dbo.ChucNang cn ON pq.MaChucNang = cn.MaChucNang;
GO

-- 5. STORED PROCEDURES
CREATE OR ALTER PROCEDURE dbo.usp_PhanQuyen_GanChucNang
    @MaVaiTro VARCHAR(20),
    @MaChucNang VARCHAR(50),
    @QuyenXem BIT = 1,
    @QuyenThem BIT = 0,
    @QuyenSua BIT = 0,
    @QuyenXoa BIT = 0,
    @GhiChu NVARCHAR(255) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT EXISTS (SELECT 1 FROM dbo.VaiTro WHERE MaVaiTro = @MaVaiTro)
    BEGIN
        RAISERROR(N'Vai trò không tồn tại trong hệ thống.', 16, 1);
        RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = @MaChucNang)
    BEGIN
        RAISERROR(N'Chức năng không tồn tại trong hệ thống.', 16, 1);
        RETURN;
    END

    IF EXISTS (SELECT 1 FROM dbo.PhanQuyen WHERE MaVaiTro = @MaVaiTro AND MaChucNang = @MaChucNang)
    BEGIN
        UPDATE dbo.PhanQuyen
        SET QuyenXem = @QuyenXem,
            QuyenThem = @QuyenThem,
            QuyenSua = @QuyenSua,
            QuyenXoa = @QuyenXoa,
            GhiChu = @GhiChu,
            NgayCapNhat = GETDATE()
        WHERE MaVaiTro = @MaVaiTro AND MaChucNang = @MaChucNang;
    END
    ELSE
    BEGIN
        INSERT INTO dbo.PhanQuyen (MaVaiTro, MaChucNang, QuyenXem, QuyenThem, QuyenSua, QuyenXoa, GhiChu, NgayCapNhat)
        VALUES (@MaVaiTro, @MaChucNang, @QuyenXem, @QuyenThem, @QuyenSua, @QuyenXoa, @GhiChu, GETDATE());
    END
END;
GO

CREATE OR ALTER PROCEDURE dbo.usp_PhanQuyen_LayTheoVaiTro
    @MaVaiTro VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT *
    FROM dbo.vw_PhanQuyenChiTiet
    WHERE MaVaiTro = @MaVaiTro
    ORDER BY MaPhanHe, MaChucNang;
END;
GO

/* ======================================================================
   6. SEED DỮ LIỆU VAITRO
   ====================================================================== */
IF NOT EXISTS (SELECT 1 FROM dbo.VaiTro WHERE MaVaiTro = 'ThuNgan')
INSERT INTO dbo.VaiTro (MaVaiTro, TenVaiTro, TenTiengAnh, MoTa, TrangThai) VALUES
('ThuNgan', N'Thu Ngân / Kế Toán Viện Phí', 'CASHIER', N'Quản lý viện phí, thu ngân, hoàn ứng, hóa đơn điện tử', 1);

IF NOT EXISTS (SELECT 1 FROM dbo.VaiTro WHERE MaVaiTro = 'DieuDuong')
INSERT INTO dbo.VaiTro (MaVaiTro, TenVaiTro, TenTiengAnh, MoTa, TrangThai) VALUES
('DieuDuong', N'Điều Dưỡng Nội Trú', 'NURSE', N'Quản lý buồng giường, tiếp nhận nội trú, chăm sóc người bệnh, tra cứu EMR và viện phí', 1);

IF NOT EXISTS (SELECT 1 FROM dbo.VaiTro WHERE MaVaiTro = 'NhanSu')
INSERT INTO dbo.VaiTro (MaVaiTro, TenVaiTro, TenTiengAnh, MoTa, TrangThai) VALUES
('NhanSu', N'Quản Lý Nhân Sự', 'HR MANAGER', N'Quản lý hồ sơ nhân viên y tế, bác sĩ, điều dưỡng, phân công lịch trực tuần', 1);

IF NOT EXISTS (SELECT 1 FROM dbo.VaiTro WHERE MaVaiTro = 'QuanTri')
INSERT INTO dbo.VaiTro (MaVaiTro, TenVaiTro, TenTiengAnh, MoTa, TrangThai) VALUES
('QuanTri', N'Quản Trị Viên Hệ Thống', 'ADMIN', N'Toàn quyền cấu hình, cấp tài khoản, giám sát audit log', 1);

IF NOT EXISTS (SELECT 1 FROM dbo.VaiTro WHERE MaVaiTro = 'GiamDoc')
INSERT INTO dbo.VaiTro (MaVaiTro, TenVaiTro, TenTiengAnh, MoTa, TrangThai) VALUES
('GiamDoc', N'Ban Giám Đốc', 'DIRECTOR', N'Xem báo cáo điều hành, thống kê tài chính, hiệu suất bệnh viện', 1);

IF NOT EXISTS (SELECT 1 FROM dbo.VaiTro WHERE MaVaiTro = 'BacSi')
INSERT INTO dbo.VaiTro (MaVaiTro, TenVaiTro, TenTiengAnh, MoTa, TrangThai) VALUES
('BacSi', N'Bác Sĩ Điều Trị', 'DOCTOR', N'Khám bệnh, chỉ định CLS, kê đơn thuốc, hội chẩn', 1);

IF NOT EXISTS (SELECT 1 FROM dbo.VaiTro WHERE MaVaiTro = 'DuocSi')
INSERT INTO dbo.VaiTro (MaVaiTro, TenVaiTro, TenTiengAnh, MoTa, TrangThai) VALUES
('DuocSi', N'Dược Sĩ Kho Dược', 'PHARMACIST', N'Quản lý kho dược, nhập kho thuốc, xuất phát thuốc FEFO', 1);

IF NOT EXISTS (SELECT 1 FROM dbo.VaiTro WHERE MaVaiTro = 'KTV')
INSERT INTO dbo.VaiTro (MaVaiTro, TenVaiTro, TenTiengAnh, MoTa, TrangThai) VALUES
('KTV', N'Kỹ Thuật Viên Cận Lâm Sàng', 'TECHNICIAN', N'Thực hiện xét nghiệm, CĐHA, nhập kết quả CLS', 1);

IF NOT EXISTS (SELECT 1 FROM dbo.VaiTro WHERE MaVaiTro = 'LeTan')
INSERT INTO dbo.VaiTro (MaVaiTro, TenVaiTro, TenTiengAnh, MoTa, TrangThai) VALUES
('LeTan', N'Tiếp Đón / Lễ Tân', 'RECEPTIONIST', N'Tiếp đón bệnh nhân, đặt lịch khám, phân luồng hàng đợi', 1);

IF NOT EXISTS (SELECT 1 FROM dbo.VaiTro WHERE MaVaiTro = 'BenhNhan')
INSERT INTO dbo.VaiTro (MaVaiTro, TenVaiTro, TenTiengAnh, MoTa, TrangThai) VALUES
('BenhNhan', N'Bệnh Nhân', 'PATIENT', N'Đặt lịch trực tuyến, theo dõi hồ sơ cá nhân và hóa đơn', 1);
GO

/* ======================================================================
   7. SEED DỮ LIỆU CHUCNANG (CÁC CHỨC NĂNG TRÊN UI)
   ====================================================================== */

-- PHÂN HỆ VIỆN PHÍ & HÓA ĐƠN (billing.html)
IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'VP_XEM_DS_HOA_DON')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('VP_XEM_DS_HOA_DON', N'Xem danh sách hóa đơn viện phí', 'VIEN_PHI', N'Viện Phí & Hóa Đơn', '/billing.html', 'GET', '/api/v1/hoadon', N'Tra cứu và hiển thị danh sách hóa đơn của người bệnh', 1);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'VP_XEM_CHI_TIET_HOA_DON')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('VP_XEM_CHI_TIET_HOA_DON', N'Xem chi tiết dịch vụ viện phí', 'VIEN_PHI', N'Viện Phí & Hóa Đơn', '/billing.html', 'GET', '/api/v1/hoadon/{id}', N'Xem bảng kê chi tiết các dịch vụ khám, CLS, thuốc, giường của hóa đơn', 2);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'VP_TINH_TIEN_TU_DONG')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('VP_TINH_TIEN_TU_DONG', N'Tính viện phí tự động', 'VIEN_PHI', N'Viện Phí & Hóa Đơn', '/billing.html', 'POST', '/api/v1/hoadon/{id}/tinh-tu-dong', N'Tự động tổng hợp chi phí từ tất cả các phiếu chỉ định và áp dụng chính sách viện phí', 3);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'VP_AP_DUNG_BHYT')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('VP_AP_DUNG_BHYT', N'Áp dụng quyền lợi BHYT', 'VIEN_PHI', N'Viện Phí & Hóa Đơn', '/billing.html', 'POST', '/api/v1/hoadon/{id}/ap-dung-bhyt', N'Áp dụng mức hưởng bảo hiểm y tế (80%, 100%) và khấu trừ BHYT chi trả', 4);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'VP_THU_TIEN_THANH_TOAN')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('VP_THU_TIEN_THANH_TOAN', N'Thu tiền viện phí & Xác nhận thanh toán', 'VIEN_PHI', N'Viện Phí & Hóa Đơn', '/billing.html', 'PUT', '/api/v1/hoadon/{id}/thanhtoan', N'Thực hiện thu tiền (Tiền mặt, Chuyển khoản, Thẻ) và cập nhật trạng thái DaThanhToan', 5);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'VP_LAP_PHIEU_HOAN_UNG')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('VP_LAP_PHIEU_HOAN_UNG', N'Lập phiếu hoàn ứng viện phí', 'VIEN_PHI', N'Viện Phí & Hóa Đơn', '/billing.html', 'POST', '/api/v1/hoadon/{id}/hoan-ung', N'Lập phiếu hoàn trả phần tiền chênh lệch sau quyết toán viện phí', 6);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'VP_XUAT_IN_HOA_DON')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('VP_XUAT_IN_HOA_DON', N'Xuất và in biên lai / Hóa đơn viện phí', 'VIEN_PHI', N'Viện Phí & Hóa Đơn', '/billing.html', 'GET', NULL, N'In phiếu thu/hóa đơn điện tử viện phí theo Mẫu QLVP_BM1', 7);

-- PHÂN HỆ QUẢN LÝ GIƯỜNG BỆNH & NỘI TRÚ (inpatient.html)
IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'NT_XEM_BUONG_GIUONG')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('NT_XEM_BUONG_GIUONG', N'Xem sơ đồ buồng phòng & Giường bệnh', 'NOI_TRU', N'Quản Lý Nội Trú', '/inpatient.html', 'GET', '/api/v1/giuongbenh', N'Theo dõi trực quan trạng thái trống, đang nằm của từng buồng giường theo khoa', 10);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'NT_TIEP_NHAN_XEP_GIUONG')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('NT_TIEP_NHAN_XEP_GIUONG', N'Tiếp nhận & Xếp giường bệnh nhân', 'NOI_TRU', N'Quản Lý Nội Trú', '/inpatient.html', 'POST', '/api/v1/noitru', N'Tiếp nhận người bệnh vào điều trị nội trú và gán vào giường bệnh chỉ định', 11);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'NT_XUAT_VIEN_TRA_GIUONG')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('NT_XUAT_VIEN_TRA_GIUONG', N'Xuất viện & Giải phóng giường bệnh', 'NOI_TRU', N'Quản Lý Nội Trú', '/inpatient.html', 'PUT', '/api/v1/noitru/{id}/xuat-vien', N'Xác nhận bệnh nhân ra viện, cập nhật trạng thái DaXuatVien và giải phóng giường', 12);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'NT_THEO_DOI_CHAM_SOC')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('NT_THEO_DOI_CHAM_SOC', N'Theo dõi điều trị & Chăm sóc nội trú', 'NOI_TRU', N'Quản Lý Nội Trú', '/inpatient.html', 'GET', '/api/v1/noitru', N'Theo dõi người bệnh đang nằm viện, thời gian lưu trú và hỗ trợ điều dưỡng', 13);

-- PHÂN HỆ HỒ SƠ BỆNH NHÂN EMR (patients.html)
IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'EMR_TRA_CUU_BENH_NHAN')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('EMR_TRA_CUU_BENH_NHAN', N'Tra cứu hồ sơ bệnh nhân', 'HO_SO', N'Hồ Sơ Bệnh Nhân EMR', '/patients.html', 'GET', '/api/v1/benhnhan', N'Tìm kiếm hồ sơ theo CCCD, BHYT, tên hoặc số điện thoại', 20);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'EMR_XEM_CHI_TIET_HO_SO')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('EMR_XEM_CHI_TIET_HO_SO', N'Xem chi tiết tiền sử & Dị ứng bệnh nhân', 'HO_SO', N'Hồ Sơ Bệnh Nhân EMR', '/patients.html', 'GET', '/api/v1/benhnhan/{id}', N'Xem thông tin nhóm máu, tiền sử bệnh nền, tiền sử dị ứng thuốc phục vụ điều dưỡng/bác sĩ', 21);

-- PHÂN HỆ QUẢN LÝ NHÂN SỰ & BÁC SĨ (doctors.html)
IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'NS_XEM_DS_NHAN_VIEN')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('NS_XEM_DS_NHAN_VIEN', N'Xem danh sách nhân viên y tế & Bác sĩ', 'NHAN_SU', N'Quản Lý Nhân Sự', '/doctors.html', 'GET', '/api/v1/nhanvien', N'Xem danh sách cán bộ, bác sĩ, điều dưỡng, kỹ thuật viên theo khoa phòng', 30);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'NS_THEM_NHAN_VIEN')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('NS_THEM_NHAN_VIEN', N'Thêm mới hồ sơ nhân viên y tế (UC64)', 'NHAN_SU', N'Quản Lý Nhân Sự', '/doctors.html', 'POST', '/api/v1/nhanvien', N'Thêm mới nhân viên y tế theo biểu mẫu chuẩn QLNV_BM1', 31);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'NS_SUA_NHAN_VIEN')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('NS_SUA_NHAN_VIEN', N'Cập nhật hồ sơ nhân sự & Bác sĩ', 'NHAN_SU', N'Quản Lý Nhân Sự', '/doctors.html', 'PUT', '/api/v1/nhanvien/{id}', N'Chỉnh sửa học vị, chức danh, chứng chỉ hành nghề CCHN, số điện thoại, khoa phòng', 32);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'NS_XEM_LICH_TRUC')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('NS_XEM_LICH_TRUC', N'Xem bảng phân công lịch trực tuần', 'NHAN_SU', N'Quản Lý Nhân Sự', '/doctors.html', 'GET', '/api/v1/lichtruc', N'Xem lịch trực bác sĩ và nhân viên các khoa trong tuần theo Mẫu QLNV_BM2', 33);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'NS_PHAN_CONG_LICH_TRUC')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('NS_PHAN_CONG_LICH_TRUC', N'Phân công xếp ca trực tuần (QLNV_BM2)', 'NHAN_SU', N'Quản Lý Nhân Sự', '/doctors.html', 'POST', '/api/v1/lichtruc', N'Xếp ca sáng, chiều, tối cho nhân viên y tế các khoa', 34);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'NS_DIEU_CHINH_LICH_TRUC')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('NS_DIEU_CHINH_LICH_TRUC', N'Xóa & Điều chỉnh phân công ca trực', 'NHAN_SU', N'Quản Lý Nhân Sự', '/doctors.html', 'DELETE', '/api/v1/lichtruc/{id}', N'Hủy ca trực hoặc hoán đổi lịch trực nhân sự', 35);

-- PHÂN HỆ THÔNG TIN CÁ NHÂN & TÀI KHOẢN (settings.html)
IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'PROFILE_XEM_THONG_TIN')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('PROFILE_XEM_THONG_TIN', N'Xem thông tin hồ sơ cá nhân', 'HE_THONG', N'Thông Tin Cá Nhân', '/settings.html', 'GET', '/api/v1/nhanvien/me', N'Xem thông tin tài khoản, họ tên, email, chức vụ của bản thân', 40);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'PROFILE_CAP_NHAT')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('PROFILE_CAP_NHAT', N'Cập nhật thông tin liên hệ cá nhân', 'HE_THONG', N'Thông Tin Cá Nhân', '/settings.html', 'PUT', '/api/v1/nhanvien/me', N'Cập nhật số điện thoại, địa chỉ, email cá nhân', 41);

IF NOT EXISTS (SELECT 1 FROM dbo.ChucNang WHERE MaChucNang = 'PROFILE_DOI_MAT_KHAU')
INSERT INTO dbo.ChucNang (MaChucNang, TenChucNang, MaPhanHe, TenPhanHe, UrlTrang, PhuongThucApi, EndpointApi, MoTa, ThuTuHienThi) VALUES
('PROFILE_DOI_MAT_KHAU', N'Đổi mật khẩu tài khoản cá nhân', 'HE_THONG', N'Thông Tin Cá Nhân', '/settings.html', 'POST', '/api/v1/taikhoan/change-password', N'Đổi mật khẩu bảo mật tài khoản cá nhân', 42);
GO

/* ======================================================================
   8. GÁN CÁC CHỨC NĂNG TRÊN UI VÀO DATABASE CHO:
      A. THU NGÂN (ThuNgan / CASHIER)
      B. ĐIỀU DƯỠNG (DieuDuong / NURSE)
      C. QUẢN LÝ NHÂN SỰ (NhanSu / HR)
   ====================================================================== */

-- A. VAI TRÒ THU NGÂN (ThuNgan)
-- Phân hệ Viện Phí & Hóa Đơn: Toàn quyền quản lý viện phí, thanh toán, hoàn ứng, in hóa đơn
EXEC dbo.usp_PhanQuyen_GanChucNang 'ThuNgan', 'VP_XEM_DS_HOA_DON',        1, 0, 0, 0, N'Xem toàn bộ danh sách hóa đơn viện phí';
EXEC dbo.usp_PhanQuyen_GanChucNang 'ThuNgan', 'VP_XEM_CHI_TIET_HOA_DON', 1, 0, 0, 0, N'Xem chi tiết các dịch vụ viện phí cần thu';
EXEC dbo.usp_PhanQuyen_GanChucNang 'ThuNgan', 'VP_TINH_TIEN_TU_DONG',     1, 1, 1, 0, N'Tính viện phí tự động từ dịch vụ khám/CLS/thuốc/giường';
EXEC dbo.usp_PhanQuyen_GanChucNang 'ThuNgan', 'VP_AP_DUNG_BHYT',         1, 1, 1, 0, N'Áp dụng quyền lợi và tỷ lệ miễn giảm BHYT';
EXEC dbo.usp_PhanQuyen_GanChucNang 'ThuNgan', 'VP_THU_TIEN_THANH_TOAN',   1, 1, 1, 0, N'Xác nhận thu tiền viện phí (Tiền mặt, Chuyển khoản, Thẻ)';
EXEC dbo.usp_PhanQuyen_GanChucNang 'ThuNgan', 'VP_LAP_PHIEU_HOAN_UNG',    1, 1, 1, 0, N'Lập phiếu hoàn ứng viện phí sau điều trị';
EXEC dbo.usp_PhanQuyen_GanChucNang 'ThuNgan', 'VP_XUAT_IN_HOA_DON',       1, 0, 0, 0, N'Xuất và in biên lai viện phí điện tử Mẫu QLVP_BM1';
-- Phân hệ Cá nhân
EXEC dbo.usp_PhanQuyen_GanChucNang 'ThuNgan', 'PROFILE_XEM_THONG_TIN',    1, 0, 0, 0, N'Xem thông tin tài khoản thu ngân';
EXEC dbo.usp_PhanQuyen_GanChucNang 'ThuNgan', 'PROFILE_CAP_NHAT',         1, 0, 1, 0, N'Cập nhật thông tin cá nhân';
EXEC dbo.usp_PhanQuyen_GanChucNang 'ThuNgan', 'PROFILE_DOI_MAT_KHAU',     1, 0, 1, 0, N'Đổi mật khẩu tài khoản thu ngân';

-- B. VAI TRÒ ĐIỀU DƯỠNG (DieuDuong)
-- Phân hệ Quản Lý Giường & Nội Trú: Tiếp nhận, xếp giường, trả giường xuất viện
EXEC dbo.usp_PhanQuyen_GanChucNang 'DieuDuong', 'NT_XEM_BUONG_GIUONG',      1, 0, 0, 0, N'Xem sơ đồ buồng giường theo khoa nội trú';
EXEC dbo.usp_PhanQuyen_GanChucNang 'DieuDuong', 'NT_TIEP_NHAN_XEP_GIUONG',   1, 1, 1, 0, N'Tiếp nhận bệnh nhân và xếp giường nội trú';
EXEC dbo.usp_PhanQuyen_GanChucNang 'DieuDuong', 'NT_XUAT_VIEN_TRA_GIUONG',   1, 0, 1, 0, N'Làm thủ tục xuất viện, trả giường nội trú';
EXEC dbo.usp_PhanQuyen_GanChucNang 'DieuDuong', 'NT_THEO_DOI_CHAM_SOC',     1, 1, 1, 0, N'Theo dõi chăm sóc người bệnh nội trú';
-- Phân hệ Hồ Sơ Bệnh Nhân EMR: Tra cứu và xem chi tiết hồ sơ bệnh nhân
EXEC dbo.usp_PhanQuyen_GanChucNang 'DieuDuong', 'EMR_TRA_CUU_BENH_NHAN',    1, 0, 0, 0, N'Tra cứu danh sách bệnh nhân EMR';
EXEC dbo.usp_PhanQuyen_GanChucNang 'DieuDuong', 'EMR_XEM_CHI_TIET_HO_SO',   1, 0, 0, 0, N'Xem tiền sử bệnh nền và dị ứng phục vụ chăm sóc';
-- Phân hệ Viện Phí: Chỉ tra cứu/xem viện phí, không có quyền thu tiền hay hoàn ứng
EXEC dbo.usp_PhanQuyen_GanChucNang 'DieuDuong', 'VP_XEM_DS_HOA_DON',        1, 0, 0, 0, N'Tra cứu viện phí bệnh nhân (chế độ chỉ xem)';
EXEC dbo.usp_PhanQuyen_GanChucNang 'DieuDuong', 'VP_XEM_CHI_TIET_HOA_DON', 1, 0, 0, 0, N'Xem chi tiết viện phí (chế độ chỉ xem)';
-- Phân hệ Cá nhân
EXEC dbo.usp_PhanQuyen_GanChucNang 'DieuDuong', 'PROFILE_XEM_THONG_TIN',    1, 0, 0, 0, N'Xem thông tin tài khoản điều dưỡng';
EXEC dbo.usp_PhanQuyen_GanChucNang 'DieuDuong', 'PROFILE_CAP_NHAT',         1, 0, 1, 0, N'Cập nhật thông tin cá nhân';
EXEC dbo.usp_PhanQuyen_GanChucNang 'DieuDuong', 'PROFILE_DOI_MAT_KHAU',     1, 0, 1, 0, N'Đổi mật khẩu tài khoản điều dưỡng';

-- C. VAI TRÒ QUẢN LÝ NHÂN SỰ (NhanSu)
-- Phân hệ Quản Lý Nhân Sự & Bác Sĩ: Toàn quyền quản lý hồ sơ nhân sự, phân công ca trực
EXEC dbo.usp_PhanQuyen_GanChucNang 'NhanSu', 'NS_XEM_DS_NHAN_VIEN',       1, 0, 0, 0, N'Xem danh sách cán bộ, nhân viên, bác sĩ';
EXEC dbo.usp_PhanQuyen_GanChucNang 'NhanSu', 'NS_THEM_NHAN_VIEN',          1, 1, 0, 0, N'Thêm mới nhân viên y tế UC64 / Mẫu QLNV_BM1';
EXEC dbo.usp_PhanQuyen_GanChucNang 'NhanSu', 'NS_SUA_NHAN_VIEN',           1, 0, 1, 0, N'Chỉnh sửa thông tin hồ sơ nhân sự, chức danh, CCHN';
EXEC dbo.usp_PhanQuyen_GanChucNang 'NhanSu', 'NS_XEM_LICH_TRUC',           1, 0, 0, 0, N'Xem bảng phân công ca trực tuần các khoa';
EXEC dbo.usp_PhanQuyen_GanChucNang 'NhanSu', 'NS_PHAN_CONG_LICH_TRUC',      1, 1, 1, 0, N'Phân công xếp ca trực tuần theo Mẫu QLNV_BM2';
EXEC dbo.usp_PhanQuyen_GanChucNang 'NhanSu', 'NS_DIEU_CHINH_LICH_TRUC',    1, 0, 0, 1, N'Xóa và điều chỉnh phân công ca trực';
-- Phân hệ Cá nhân
EXEC dbo.usp_PhanQuyen_GanChucNang 'NhanSu', 'PROFILE_XEM_THONG_TIN',      1, 0, 0, 0, N'Xem thông tin tài khoản nhân sự';
EXEC dbo.usp_PhanQuyen_GanChucNang 'NhanSu', 'PROFILE_CAP_NHAT',           1, 0, 1, 0, N'Cập nhật thông tin cá nhân';
EXEC dbo.usp_PhanQuyen_GanChucNang 'NhanSu', 'PROFILE_DOI_MAT_KHAU',       1, 0, 1, 0, N'Đổi mật khẩu tài khoản nhân sự';
GO

-- 9. KIỂM TRA KẾT QUẢ GÁN QUYỀN
SELECT 
    vt.TenVaiTro,
    cn.TenPhanHe,
    cn.TenChucNang,
    cn.UrlTrang,
    pq.QuyenXem,
    pq.QuyenThem,
    pq.QuyenSua,
    pq.QuyenXoa,
    pq.GhiChu
FROM dbo.PhanQuyen pq
JOIN dbo.VaiTro vt ON pq.MaVaiTro = vt.MaVaiTro
JOIN dbo.ChucNang cn ON pq.MaChucNang = cn.MaChucNang
WHERE vt.MaVaiTro IN ('ThuNgan', 'DieuDuong', 'NhanSu')
ORDER BY vt.MaVaiTro, cn.MaPhanHe, cn.ThuTuHienThi;
GO
