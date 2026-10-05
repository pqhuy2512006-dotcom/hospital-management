/* ======================================================================
   CƠ SỞ DỮ LIỆU: HỆ THỐNG QUẢN LÝ BỆNH VIỆN
   Chạy trực tiếp trên database đã tạo: QuanLyBenhVien
   ====================================================================== */
create database QuanLyBenhVien;
go 

use QuanLyBenhVien
go

/* ---------- Xóa bảng cũ theo thứ tự phụ thuộc khóa ngoại ---------- */
DROP TABLE IF EXISTS ChiTietHoaDon;
DROP TABLE IF EXISTS HoaDon;
DROP TABLE IF EXISTS ChiTietNhapKho;
DROP TABLE IF EXISTS PhieuNhapKho;
DROP TABLE IF EXISTS ChiTietKetQuaCLS;
DROP TABLE IF EXISTS KetQuaCLS;
DROP TABLE IF EXISTS DichVuCLS;
DROP TABLE IF EXISTS DonThuoc;
DROP TABLE IF EXISTS NoiTru;
DROP TABLE IF EXISTS GiuongBenh;
DROP TABLE IF EXISTS PhieuKham;
DROP TABLE IF EXISTS LichHen;
DROP TABLE IF EXISTS LichTruc;
DROP TABLE IF EXISTS BenhNhan;
DROP TABLE IF EXISTS NhanVien;
DROP TABLE IF EXISTS TaiKhoan;
DROP TABLE IF EXISTS Thuoc;
DROP TABLE IF EXISTS Khoa;
GO

/* ======================================================================
   1. BẢNG TAIKHOAN (Xác thực & Phân quyền Spring Security / JWT)
   ====================================================================== */
CREATE TABLE TaiKhoan (
    MaTaiKhoan      BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    TenDangNhap     VARCHAR(50)          NOT NULL UNIQUE,
    MatKhauHash     VARCHAR(255)         NOT NULL,
    Email           VARCHAR(100)         NULL UNIQUE,
    SoDienThoai     VARCHAR(15)          NOT NULL,
    VaiTro          VARCHAR(20)          NOT NULL 
                    CHECK (VaiTro IN ('BacSi','DieuDuong','LeTan','DuocSi','KTV','ThuNgan','QuanTri','BenhNhan')),
    TrangThai       BIT                  NOT NULL DEFAULT 1,
    NgayTao         DATETIME             NOT NULL DEFAULT GETDATE()
);
GO

/* ======================================================================
   2. BẢNG KHOA
   ====================================================================== */
CREATE TABLE Khoa (
    MaKhoa          VARCHAR(10)          NOT NULL PRIMARY KEY,
    TenKhoa         NVARCHAR(100)        NOT NULL,
    MoTa            NVARCHAR(300)        NULL
);
GO

/* ======================================================================
   3. BẢNG THUOC
   ====================================================================== */
CREATE TABLE Thuoc (
    MaThuoc         VARCHAR(15)          NOT NULL PRIMARY KEY,
    TenThuoc        NVARCHAR(150)        NOT NULL,
    DonViTinh       VARCHAR(20)          NOT NULL,
    DonGiaBan       DECIMAL(12,0)        NOT NULL CHECK (DonGiaBan >= 0),
    TonKhoHienTai   INT                  NOT NULL DEFAULT 0 CHECK (TonKhoHienTai >= 0),
    NguongCanhBao   INT                  NOT NULL DEFAULT 10
);
GO

/* ======================================================================
   4. BẢNG NHANVIEN
   ====================================================================== */
CREATE TABLE NhanVien (
    MaNhanVien          VARCHAR(15)      NOT NULL PRIMARY KEY,
    MaTaiKhoan          BIGINT           NULL UNIQUE,
    HoTen               NVARCHAR(100)    NOT NULL,
    NgaySinh            DATE             NULL,
    GioiTinh            VARCHAR(10)      NULL CHECK (GioiTinh IN ('Nam', 'Nu', 'Khac')),
    SoCCCD              VARCHAR(12)      NULL,
    SoDienThoai         VARCHAR(15)      NOT NULL,
    Email               VARCHAR(100)     NULL,
    DiaChi              NVARCHAR(200)    NULL,
    ChuyenKhoa          NVARCHAR(100)    NULL,
    ChungChiHanhNghe    VARCHAR(50)      NULL,
    TrinhDoChuyenMon    NVARCHAR(100)    NULL,
    NgayVaoLam          DATE             NULL,
    VaiTro              VARCHAR(20)      NOT NULL 
                        CHECK (VaiTro IN ('BacSi','DieuDuong','LeTan','DuocSi','KTV','ThuNgan','QuanTri')),
    TrangThai           VARCHAR(20)      NOT NULL DEFAULT 'DangLamViec' 
                        CHECK (TrangThai IN ('DangLamViec','DaNghi')),
    CONSTRAINT FK_NhanVien_TaiKhoan FOREIGN KEY (MaTaiKhoan) REFERENCES TaiKhoan(MaTaiKhoan),
    CONSTRAINT UQ_NhanVien_SDT UNIQUE (SoDienThoai)
);
GO

CREATE UNIQUE INDEX UQ_NhanVien_CCCD ON NhanVien(SoCCCD) WHERE SoCCCD IS NOT NULL;
GO

/* ======================================================================
   5. BẢNG BENHNHAN
   ====================================================================== */
CREATE TABLE BenhNhan (
    MaBenhNhan          VARCHAR(15)      NOT NULL PRIMARY KEY,
    MaTaiKhoan          BIGINT           NULL UNIQUE,
    HoTen               NVARCHAR(100)    NOT NULL,
    NgaySinh            DATE             NOT NULL,
    GioiTinh            VARCHAR(10)      NOT NULL CHECK (GioiTinh IN ('Nam', 'Nu', 'Khac')),
    SoCCCD              VARCHAR(12)      NULL,
    MaBHYT              VARCHAR(15)      NULL,
    NhomMau             VARCHAR(5)       NULL,
    NgheNghiep          NVARCHAR(100)    NULL,
    DiaChi              NVARCHAR(200)    NULL,
    SoDienThoai         VARCHAR(15)      NOT NULL,
    Email               VARCHAR(100)     NULL,
    NguoiLienHeKhanCap  NVARCHAR(100)    NULL,
    QuanHeNguoiLienHe   NVARCHAR(50)     NULL,
    SdtNguoiLienHe      VARCHAR(15)      NULL,
    TienSuBenhNen       NVARCHAR(500)    NULL,
    TienSuDiUng         NVARCHAR(500)    NULL,
    NgayTaoHoSo         DATETIME         NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_BenhNhan_TaiKhoan FOREIGN KEY (MaTaiKhoan) REFERENCES TaiKhoan(MaTaiKhoan)
);
GO

CREATE UNIQUE INDEX UQ_BenhNhan_CCCD ON BenhNhan(SoCCCD) WHERE SoCCCD IS NOT NULL;
CREATE UNIQUE INDEX UQ_BenhNhan_BHYT ON BenhNhan(MaBHYT) WHERE MaBHYT IS NOT NULL;
GO

/* ======================================================================
   6. BẢNG LICHTRUC
   ====================================================================== */
CREATE TABLE LichTruc (
    MaLichTruc      VARCHAR(15)          NOT NULL PRIMARY KEY,
    MaNhanVien      VARCHAR(15)          NOT NULL,
    Ngay            DATE                 NOT NULL,
    Ca              VARCHAR(10)          NOT NULL CHECK (Ca IN ('Sang','Chieu','Toi')),
    MaKhoa          VARCHAR(10)          NULL,
    CONSTRAINT FK_LichTruc_NhanVien FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_LichTruc_Khoa     FOREIGN KEY (MaKhoa)     REFERENCES Khoa(MaKhoa),
    CONSTRAINT UQ_LichTruc_NV_Ngay_Ca UNIQUE (MaNhanVien, Ngay, Ca)
);
GO

/* ======================================================================
   7. BẢNG LICHHEN
   ====================================================================== */
CREATE TABLE LichHen (
    MaLichHen       VARCHAR(15)          NOT NULL PRIMARY KEY,
    MaBenhNhan      VARCHAR(15)          NOT NULL,
    MaKhoa          VARCHAR(10)          NOT NULL,
    MaBacSi         VARCHAR(15)          NULL,
    NgayKham        DATE                 NOT NULL,
    GioKham         TIME                 NOT NULL,
    LoaiKham        VARCHAR(20)          NOT NULL 
                    CHECK (LoaiKham IN ('KhamThuong','KhamDichVu','TaiKham')),
    ThoiGianKhamDuKien INT               NOT NULL,
    HinhThucDat     VARCHAR(20)          NOT NULL 
                    CHECK (HinhThucDat IN ('Online','TrucTiep','DienThoai')),
    LyDoKham        NVARCHAR(300)        NULL,
    TrangThai       VARCHAR(20)          NOT NULL DEFAULT 'DaDatLich'
                    Constraint CK_LichHen_TrangThai CHECK (TrangThai IN ('DaDatLich','DangChoKham','DangKham','DaHuy','DaKham')),
    NgayDatLich     DATETIME             NOT NULL DEFAULT GETDATE(),
    CONSTRAINT CK_LichHen_ThoiGianKhamDuKien CHECK (
        (LoaiKham IN ('KhamThuong','TaiKham') AND ThoiGianKhamDuKien = 30)
        OR (LoaiKham = 'KhamDichVu' AND ThoiGianKhamDuKien = 45)
    ),
    CONSTRAINT FK_LichHen_BenhNhan FOREIGN KEY (MaBenhNhan) REFERENCES BenhNhan(MaBenhNhan),
    CONSTRAINT FK_LichHen_Khoa     FOREIGN KEY (MaKhoa)     REFERENCES Khoa(MaKhoa),
    CONSTRAINT FK_LichHen_BacSi    FOREIGN KEY (MaBacSi)    REFERENCES NhanVien(MaNhanVien)
);
GO

/* ======================================================================
   8. BẢNG PHIEUKHAM
   ====================================================================== */
CREATE TABLE PhieuKham (
    MaPhieuKham     VARCHAR(15)          NOT NULL PRIMARY KEY,
    MaBenhNhan      VARCHAR(15)          NOT NULL,
    MaBacSi         VARCHAR(15)          NOT NULL,
    MaKhoa          VARCHAR(10)          NOT NULL,
    MaLichHen       VARCHAR(15)          NULL,
    NgayKham        DATETIME             NOT NULL DEFAULT GETDATE(),
    Mach            INT                  NULL,
    NhietDo         DECIMAL(4,1)         NULL,
    HuyetAp         VARCHAR(15)          NULL,
    NhipTho         INT                  NULL,
    CanNang         DECIMAL(5,1)         NULL,
    ChieuCao        DECIMAL(5,1)         NULL,
    TrieuChung      NVARCHAR(500)        NULL,
    KetQuaKhamLS    NVARCHAR(1000)       NULL,
    ChanDoan        NVARCHAR(500)        NULL,
    LoiDanBacSi     NVARCHAR(500)        NULL,
    NgayHenTaiKham  DATE                 NULL,
    CONSTRAINT FK_PhieuKham_BenhNhan FOREIGN KEY (MaBenhNhan) REFERENCES BenhNhan(MaBenhNhan),
    CONSTRAINT FK_PhieuKham_BacSi    FOREIGN KEY (MaBacSi)    REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_PhieuKham_Khoa     FOREIGN KEY (MaKhoa)     REFERENCES Khoa(MaKhoa),
    CONSTRAINT FK_PhieuKham_LichHen  FOREIGN KEY (MaLichHen)  REFERENCES LichHen(MaLichHen)
);
GO

/* ======================================================================
   9. BẢNG GIUONGBENH & NOITRU
   ====================================================================== */
CREATE TABLE GiuongBenh (
    MaGiuong        VARCHAR(15)          NOT NULL PRIMARY KEY,
    MaKhoa          VARCHAR(10)          NOT NULL,
    SoGiuong        VARCHAR(20)          NOT NULL,
    TrangThai       VARCHAR(20)          NOT NULL DEFAULT 'Trong' 
                    CHECK (TrangThai IN ('Trong', 'DangSuDung', 'BaoTri')),
    DonGiaNgay      DECIMAL(12,0)        NOT NULL DEFAULT 0 CHECK (DonGiaNgay >= 0),
    CONSTRAINT FK_GiuongBenh_Khoa FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa)
);
GO

CREATE TABLE NoiTru (
    MaNoiTru        VARCHAR(15)          NOT NULL PRIMARY KEY,
    MaBenhNhan      VARCHAR(15)          NOT NULL,
    MaGiuong        VARCHAR(15)          NOT NULL,
    MaPhieuKham     VARCHAR(15)          NOT NULL,
    NgayNhapVien    DATETIME             NOT NULL DEFAULT GETDATE(),
    NgayXuatVien    DATETIME             NULL,
    TrangThai       VARCHAR(20)          NOT NULL DEFAULT 'DangNam' 
                    CHECK (TrangThai IN ('DangNam', 'DaXuatVien', 'ChuyenKhoa')),
    CONSTRAINT FK_NoiTru_BenhNhan  FOREIGN KEY (MaBenhNhan)  REFERENCES BenhNhan(MaBenhNhan),
    CONSTRAINT FK_NoiTru_Giuong    FOREIGN KEY (MaGiuong)    REFERENCES GiuongBenh(MaGiuong),
    CONSTRAINT FK_NoiTru_PhieuKham FOREIGN KEY (MaPhieuKham) REFERENCES PhieuKham(MaPhieuKham),
    CONSTRAINT CK_NoiTru_NgayXuat  CHECK (NgayXuatVien IS NULL OR NgayXuatVien >= NgayNhapVien)
);
GO

/* ======================================================================
   10. BẢNG DONTHUOC
   ====================================================================== */
CREATE TABLE DonThuoc (
    MaDonThuoc      VARCHAR(15)          NOT NULL PRIMARY KEY,
    MaPhieuKham     VARCHAR(15)          NOT NULL,
    MaThuoc         VARCHAR(15)          NOT NULL,
    SoLo            VARCHAR(30)          NULL,
    DonViTinh       VARCHAR(20)          NOT NULL,
    SoLuong         INT                  NOT NULL CHECK (SoLuong > 0),
    LieuDung        NVARCHAR(100)        NULL,
    CachDung        NVARCHAR(200)        NULL,
    CONSTRAINT FK_DonThuoc_PhieuKham FOREIGN KEY (MaPhieuKham) REFERENCES PhieuKham(MaPhieuKham),
    CONSTRAINT FK_DonThuoc_Thuoc     FOREIGN KEY (MaThuoc)     REFERENCES Thuoc(MaThuoc)
);
GO

/* ======================================================================
   11. PHÂN HỆ CẬN LÂM SÀNG
   ====================================================================== */
CREATE TABLE DichVuCLS (
    MaDichVu        VARCHAR(15)          NOT NULL PRIMARY KEY,
    TenDichVu       NVARCHAR(150)        NOT NULL,
    DonGia          DECIMAL(12,0)        NOT NULL CHECK (DonGia >= 0),
    DonViTinh       NVARCHAR(20)         NULL,
    KhoangThamChieu NVARCHAR(100)        NULL
);
GO

CREATE TABLE KetQuaCLS (
    MaKetQua        VARCHAR(15)          NOT NULL PRIMARY KEY,
    MaPhieuKham     VARCHAR(15)          NOT NULL,
    MaBenhNhan      VARCHAR(15)          NOT NULL,
    MaDichVu        VARCHAR(15)          NULL,
    MaKTV           VARCHAR(15)          NOT NULL,
    MaBacSiDoc      VARCHAR(15)          NULL,
    LoaiXetNghiem   NVARCHAR(100)        NOT NULL,
    KetLuan         NVARCHAR(500)        NULL,
    NgayThucHien    DATETIME             NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_KetQuaCLS_PhieuKham FOREIGN KEY (MaPhieuKham) REFERENCES PhieuKham(MaPhieuKham),
    CONSTRAINT FK_KetQuaCLS_BenhNhan  FOREIGN KEY (MaBenhNhan)  REFERENCES BenhNhan(MaBenhNhan),
    CONSTRAINT FK_KetQuaCLS_DichVu    FOREIGN KEY (MaDichVu)    REFERENCES DichVuCLS(MaDichVu),
    CONSTRAINT FK_KetQuaCLS_KTV       FOREIGN KEY (MaKTV)       REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_KetQuaCLS_BacSiDoc  FOREIGN KEY (MaBacSiDoc)  REFERENCES NhanVien(MaNhanVien)
);
GO

CREATE TABLE ChiTietKetQuaCLS (
    MaChiTietXN     VARCHAR(15)          NOT NULL PRIMARY KEY,
    MaKetQua        VARCHAR(15)          NOT NULL,
    TenChiSo        NVARCHAR(100)        NOT NULL,
    GiaTri          VARCHAR(30)          NULL,
    DonVi           VARCHAR(20)          NULL,
    KhoangThamChieu VARCHAR(50)          NULL,
    DanhGia         VARCHAR(20)          NULL CHECK (DanhGia IN ('BinhThuong','Cao','Thap')),
    CONSTRAINT FK_ChiTietKQCLS_KetQuaCLS FOREIGN KEY (MaKetQua) REFERENCES KetQuaCLS(MaKetQua)
);
GO

/* ======================================================================
   12. BẢNG PHIEUNHAPKHO & CHITIETNHAPKHO
   ====================================================================== */
CREATE TABLE PhieuNhapKho (
    MaPhieuNhap     VARCHAR(15)          NOT NULL PRIMARY KEY,
    NgayNhap        DATE                 NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    NhaCungCap      NVARCHAR(150)        NOT NULL,
    SoHoaDonNCC     VARCHAR(30)          NULL,
    NguoiNhap       VARCHAR(15)          NOT NULL,
    NguoiDuyet      VARCHAR(15)          NULL,
    TongTien        DECIMAL(14,0)        NOT NULL DEFAULT 0,
    CONSTRAINT FK_PhieuNhap_NguoiNhap  FOREIGN KEY (NguoiNhap)  REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_PhieuNhap_NguoiDuyet FOREIGN KEY (NguoiDuyet) REFERENCES NhanVien(MaNhanVien)
);
GO

CREATE TABLE ChiTietNhapKho (
    MaChiTietNhap   VARCHAR(15)          NOT NULL PRIMARY KEY,
    MaPhieuNhap     VARCHAR(15)          NOT NULL,
    MaThuoc         VARCHAR(15)          NOT NULL,
    SoLo            VARCHAR(30)          NOT NULL,
    SoLuong         INT                  NOT NULL CHECK (SoLuong > 0),
    DonGia          DECIMAL(12,0)        NOT NULL CHECK (DonGia >= 0),
    ThanhTien       AS (SoLuong * DonGia) PERSISTED,
    HanSuDung       DATE                 NOT NULL,
    CONSTRAINT FK_ChiTietNhap_PhieuNhap FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhapKho(MaPhieuNhap),
    CONSTRAINT FK_ChiTietNhap_Thuoc     FOREIGN KEY (MaThuoc)     REFERENCES Thuoc(MaThuoc)
);
GO

/* ======================================================================
   13. BẢNG HOADON & CHITIETHOADON
   ====================================================================== */
CREATE TABLE HoaDon (
    MaHoaDon            VARCHAR(15)      NOT NULL PRIMARY KEY,
    MaBenhNhan          VARCHAR(15)      NOT NULL,
    MaPhieuKham         VARCHAR(15)      NOT NULL,
    NgayLap             DATETIME         NOT NULL DEFAULT GETDATE(),
    TongTienDichVu      DECIMAL(12,0)    NOT NULL DEFAULT 0,
    BHYTChiTra          DECIMAL(12,0)    NOT NULL DEFAULT 0,
    TongThanhToan       AS (TongTienDichVu - BHYTChiTra) PERSISTED,
    HinhThucThanhToan   VARCHAR(30)      NOT NULL 
                        CHECK (HinhThucThanhToan IN ('TienMat','ChuyenKhoan','ViDienTu')),
    TrangThaiTT         VARCHAR(20)      NOT NULL DEFAULT 'ChuaThanhToan' 
                        CHECK (TrangThaiTT IN ('ChuaThanhToan','DaThanhToan','HoanTien')),
    NhanVienThu         VARCHAR(15)      NOT NULL,
    NgayThanhToan       DATETIME         NULL,
    CONSTRAINT FK_HoaDon_BenhNhan  FOREIGN KEY (MaBenhNhan)  REFERENCES BenhNhan(MaBenhNhan),
    CONSTRAINT FK_HoaDon_PhieuKham FOREIGN KEY (MaPhieuKham) REFERENCES PhieuKham(MaPhieuKham),
    CONSTRAINT FK_HoaDon_NhanVien  FOREIGN KEY (NhanVienThu) REFERENCES NhanVien(MaNhanVien)
);
GO

CREATE TABLE ChiTietHoaDon (
    MaChiTietHD     VARCHAR(15)          NOT NULL PRIMARY KEY,
    MaHoaDon        VARCHAR(15)          NOT NULL,
    TenDichVu       NVARCHAR(150)        NOT NULL,
    LoaiDichVu      VARCHAR(20)          NOT NULL 
                    CHECK (LoaiDichVu IN ('Kham','CanLamSang','Thuoc','Giuong')),
    DonGia          DECIMAL(12,0)        NOT NULL CHECK (DonGia >= 0),
    SoLuong         INT                  NOT NULL DEFAULT 1 CHECK (SoLuong > 0),
    ThanhTien       AS (DonGia * SoLuong) PERSISTED,
    CONSTRAINT FK_ChiTietHD_HoaDon FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon)
);
GO

/* ======================================================================
   14. TRIGGER BẢO TOÀN TRẠNG THÁI GIƯỜNG NỘI TRÚ (ĐÃ FIX CHUẨN)
   ====================================================================== */
CREATE OR ALTER TRIGGER TRG_CapNhatTrangThaiGiuong
ON NoiTru
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra nếu giường đã có người khác đang nằm
    IF EXISTS (
        SELECT 1 
        FROM inserted i
        JOIN NoiTru nt ON i.MaGiuong = nt.MaGiuong
        WHERE i.TrangThai = 'DangNam' 
          AND nt.TrangThai = 'DangNam'
          AND i.MaNoiTru <> nt.MaNoiTru
    )
    BEGIN
        RAISERROR (N'Lỗi: Giường này hiện đang có bệnh nhân khác sử dụng!', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    -- Cập nhật giường sang 'DangSuDung' khi nhập viện
    UPDATE g
    SET g.TrangThai = 'DangSuDung'
    FROM GiuongBenh g
    JOIN inserted i ON g.MaGiuong = i.MaGiuong
    WHERE i.TrangThai = 'DangNam';

    -- Trả giường về 'Trong' khi xuất viện hoặc chuyển khoa
    UPDATE g
    SET g.TrangThai = 'Trong'
    FROM GiuongBenh g
    JOIN inserted i ON g.MaGiuong = i.MaGiuong
    WHERE i.TrangThai IN ('DaXuatVien', 'ChuyenKhoa');
END;
GO



