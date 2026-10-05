use QuanLyBenhVien
go 

/* ---------- Xóa bảng cũ theo thứ tự phụ thuộc khóa ngoại ---------- */
DROP TABLE IF EXISTS ChiTietHoaDon;
DROP TABLE IF EXISTS HoaDon;
DROP TABLE IF EXISTS DonThuoc;
DROP TABLE IF EXISTS ChiTietKetQuaCLS;
DROP TABLE IF EXISTS KetQuaCLS;
DROP TABLE IF EXISTS NoiTru;
DROP TABLE IF EXISTS PhieuKham;
DROP TABLE IF EXISTS LichHen;
DROP TABLE IF EXISTS LichTruc;
DROP TABLE IF EXISTS ChiTietNhapKho;
DROP TABLE IF EXISTS PhieuNhapKho;
DROP TABLE IF EXISTS Thuoc;
DROP TABLE IF EXISTS DichVuCLS;
DROP TABLE IF EXISTS GiuongBenh;
DROP TABLE IF EXISTS Khoa;
DROP TABLE IF EXISTS BenhNhan;
DROP TABLE IF EXISTS NhanVien;
DROP TABLE IF EXISTS TaiKhoan;
GO

/* ======================================================================
   1. BẢNG TAIKHOAN
   ====================================================================== */
CREATE TABLE TaiKhoan (
    MaTaiKhoan      BIGINT IDENTITY(1,1) PRIMARY KEY,
    TenDangNhap     VARCHAR(50)          NOT NULL UNIQUE,
    MatKhauHash     VARCHAR(255)         NOT NULL,
    Email           VARCHAR(100)         NULL UNIQUE,
    SoDienThoai     VARCHAR(15)          NOT NULL,
    VaiTro          VARCHAR(20)          NOT NULL 
                    CHECK (VaiTro IN ('BacSi','DieuDuong','LeTan','DuocSi','KTV','ThuNgan','QuanTri','NhanSu','BenhNhan')),
    TrangThai       BIT                  NOT NULL DEFAULT 1,
    NgayTao         DATETIME             NOT NULL DEFAULT GETDATE()
);
GO

/* ======================================================================
   2. BẢNG BENHNHAN
   ====================================================================== */
CREATE TABLE BenhNhan (
    MaBenhNhan          VARCHAR(15)      PRIMARY KEY,
    MaTaiKhoan          BIGINT           NULL UNIQUE,
    HoTen               NVARCHAR(100)    NOT NULL,
    NgaySinh            DATE             NOT NULL,
    GioiTinh            NVARCHAR(10)     NOT NULL 
                        CHECK (GioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    SoCCCD              VARCHAR(20)      NULL UNIQUE,
    MaBHYT              VARCHAR(20)      NULL UNIQUE,
    NhomMau             VARCHAR(5)       NULL 
                        CHECK (NhomMau IN ('A','B','AB','O',NULL)),
    NgheNghiep          NVARCHAR(100)    NULL,
    DiaChi              NVARCHAR(255)    NULL,
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

/* ======================================================================
   3. BẢNG KHOA
   ====================================================================== */
CREATE TABLE Khoa (
    MaKhoa      VARCHAR(10)      PRIMARY KEY,
    TenKhoa     NVARCHAR(100)    NOT NULL,
    MoTa        NVARCHAR(255)    NULL
);
GO

/* ======================================================================
   4. BẢNG NHANVIEN
   ====================================================================== */
CREATE TABLE NhanVien (
    MaNhanVien          VARCHAR(15)      PRIMARY KEY,
    MaTaiKhoan          BIGINT           NOT NULL UNIQUE,
    HoTen               NVARCHAR(100)    NOT NULL,
    NgaySinh            DATE             NULL,
    GioiTinh            NVARCHAR(10)     NULL 
                        CHECK (GioiTinh IN (N'Nam', N'Nữ', N'Khác', NULL)),
    SoCCCD              VARCHAR(20)      NULL UNIQUE,
    SoDienThoai         VARCHAR(15)      NOT NULL UNIQUE,
    Email               VARCHAR(100)     NULL UNIQUE,
    DiaChi              NVARCHAR(255)    NULL,
    ChuyenKhoa          NVARCHAR(100)    NULL,
    ChungChiHanhNghe    VARCHAR(50)      NULL UNIQUE,
    TrinhDoChuyenMon    NVARCHAR(100)    NULL,
    NgayVaoLam          DATE             NULL,
    VaiTro              VARCHAR(20)      NOT NULL 
                        CHECK (VaiTro IN ('BacSi','DieuDuong','LeTan','DuocSi','KTV','ThuNgan','QuanTri','NhanSu')),
    TrangThai           VARCHAR(20)      NOT NULL DEFAULT 'DangLamViec' 
                        CHECK (TrangThai IN ('DangLamViec','DaNghi')),
    CONSTRAINT FK_NhanVien_TaiKhoan FOREIGN KEY (MaTaiKhoan) REFERENCES TaiKhoan(MaTaiKhoan),
    CONSTRAINT CK_NhanVien_ChungChiHanhNghe CHECK (
        (VaiTro = 'BacSi' AND ChungChiHanhNghe IS NOT NULL)
        OR (VaiTro <> 'BacSi')
    )
);
GO

/* ======================================================================
   5. BẢNG DICHVUCLS
   ====================================================================== */
CREATE TABLE DichVuCLS (
    MaDichVu            VARCHAR(15)      PRIMARY KEY,
    TenDichVu           NVARCHAR(150)    NOT NULL,
    DonGia              DECIMAL(12,2)    NOT NULL CHECK (DonGia >= 0),
    DonViTinh           NVARCHAR(30)     NULL,
    KhoangThamChieu     NVARCHAR(100)    NULL
);
GO

/* ======================================================================
   6. BẢNG THUOC
   ====================================================================== */
CREATE TABLE Thuoc (
    MaThuoc             VARCHAR(15)      PRIMARY KEY,
    TenThuoc            NVARCHAR(150)    NOT NULL,
    DonViTinh           NVARCHAR(30)     NOT NULL,
    DonGiaBan           DECIMAL(12,2)    NOT NULL CHECK (DonGiaBan >= 0),
    TonKhoHienTai       INT              NOT NULL DEFAULT 0 CHECK (TonKhoHienTai >= 0),
    NguongCanhBao       INT              NOT NULL DEFAULT 10 CHECK (NguongCanhBao >= 0)
);
GO

/* ======================================================================
   7. BẢNG LICHHEN
   ====================================================================== */
CREATE TABLE LichHen (
    MaLichHen       VARCHAR(15)          PRIMARY KEY,
    MaBenhNhan      VARCHAR(15)          NOT NULL,
    MaKhoa          VARCHAR(10)          NOT NULL,
    MaBacSi         VARCHAR(15)          NULL,
    NgayKham        DATE                 NOT NULL,
    GioKham         TIME(0)              NOT NULL,
    LoaiKham        VARCHAR(20)          NOT NULL 
                    CHECK (LoaiKham IN ('KhamThuong','KhamDichVu','TaiKham')),
    ThoiGianKhamDuKien INT               NOT NULL DEFAULT 30,
    HinhThucDat     VARCHAR(20)          NOT NULL 
                    CHECK (HinhThucDat IN ('Online','TrucTiep','DienThoai')),
    LyDoKham        NVARCHAR(300)        NULL,
    GhiChu          NVARCHAR(300)        NULL,
    TrangThai       VARCHAR(20)          NOT NULL DEFAULT 'DaDatLich'
                    CONSTRAINT CK_LichHen_TrangThai CHECK (TrangThai IN ('DaDatLich','DangChoKham','DangKham','DaHuy','DaKham')),
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
    MaPhieuKham         VARCHAR(15)      PRIMARY KEY,
    MaBenhNhan          VARCHAR(15)      NOT NULL,
    MaBacSi             VARCHAR(15)      NOT NULL,
    MaKhoa              VARCHAR(10)      NOT NULL,
    MaLichHen           VARCHAR(15)      NULL UNIQUE,
    NgayKham            DATETIME         NOT NULL DEFAULT GETDATE(),
    Mach                INT              NULL CHECK (Mach BETWEEN 30 AND 250),
    NhietDo             DECIMAL(4,1)     NULL CHECK (NhietDo BETWEEN 34.0 AND 43.0),
    HuyetAp             VARCHAR(15)      NULL,
    NhipTho             INT              NULL CHECK (NhipTho BETWEEN 8 AND 60),
    CanNang             DECIMAL(5,1)     NULL CHECK (CanNang > 0),
    ChieuCao            DECIMAL(5,1)     NULL CHECK (ChieuCao > 0),
    TrieuChung          NVARCHAR(500)    NULL,
    KetQuaKhamLS        NVARCHAR(1000)   NULL,
    ChanDoan            NVARCHAR(500)    NOT NULL,
    LoiDanBacSi         NVARCHAR(500)    NULL,
    NgayHenTaiKham      DATE             NULL,
    CONSTRAINT FK_PhieuKham_BenhNhan FOREIGN KEY (MaBenhNhan) REFERENCES BenhNhan(MaBenhNhan),
    CONSTRAINT FK_PhieuKham_BacSi    FOREIGN KEY (MaBacSi)    REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_PhieuKham_Khoa     FOREIGN KEY (MaKhoa)     REFERENCES Khoa(MaKhoa),
    CONSTRAINT FK_PhieuKham_LichHen  FOREIGN KEY (MaLichHen)  REFERENCES LichHen(MaLichHen)
);
GO

/* ======================================================================
   9. BẢNG KETQUACLS
   ====================================================================== */
CREATE TABLE KetQuaCLS (
    MaKetQua            VARCHAR(15)      PRIMARY KEY,
    MaPhieuKham         VARCHAR(15)      NOT NULL,
    MaBenhNhan          VARCHAR(15)      NOT NULL,
    MaDichVu            VARCHAR(15)      NULL,
    MaKTV               VARCHAR(15)      NOT NULL,
    MaBacSiDoc          VARCHAR(15)      NULL,
    LoaiXetNghiem       NVARCHAR(100)    NOT NULL,
    KetLuan             NVARCHAR(500)    NULL,
    HinhAnhFile         NVARCHAR(MAX)    NULL,
    NgayThucHien        DATETIME         NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_KetQuaCLS_PhieuKham FOREIGN KEY (MaPhieuKham) REFERENCES PhieuKham(MaPhieuKham),
    CONSTRAINT FK_KetQuaCLS_BenhNhan  FOREIGN KEY (MaBenhNhan)  REFERENCES BenhNhan(MaBenhNhan),
    CONSTRAINT FK_KetQuaCLS_DichVu    FOREIGN KEY (MaDichVu)    REFERENCES DichVuCLS(MaDichVu),
    CONSTRAINT FK_KetQuaCLS_KTV       FOREIGN KEY (MaKTV)       REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_KetQuaCLS_BacSiDoc  FOREIGN KEY (MaBacSiDoc)  REFERENCES NhanVien(MaNhanVien)
);
GO

/* ======================================================================
   10. BẢNG CHITIETKETQUACLS
   ====================================================================== */
CREATE TABLE ChiTietKetQuaCLS (
    MaChiTietXN         VARCHAR(15)      PRIMARY KEY,
    MaKetQua            VARCHAR(15)      NOT NULL,
    TenChiSo            NVARCHAR(100)    NOT NULL,
    GiaTri              NVARCHAR(50)     NOT NULL,
    DonVi               NVARCHAR(30)     NULL,
    KhoangThamChieu     NVARCHAR(100)    NULL,
    DanhGia             NVARCHAR(50)     NULL,
    CONSTRAINT FK_ChiTietCLS_KetQua FOREIGN KEY (MaKetQua) REFERENCES KetQuaCLS(MaKetQua)
);
GO

/* ======================================================================
   11. BẢNG DONTHUOC
   ====================================================================== */
CREATE TABLE DonThuoc (
    MaDonThuoc          VARCHAR(15)      PRIMARY KEY,
    MaPhieuKham         VARCHAR(15)      NOT NULL,
    MaThuoc             VARCHAR(15)      NOT NULL,
    SoLo                VARCHAR(30)      NULL,
    DonViTinh           NVARCHAR(30)     NOT NULL,
    SoLuong             INT              NOT NULL CHECK (SoLuong > 0),
    LieuDung            NVARCHAR(100)    NOT NULL,
    CachDung            NVARCHAR(200)    NULL,
    CONSTRAINT FK_DonThuoc_PhieuKham FOREIGN KEY (MaPhieuKham) REFERENCES PhieuKham(MaPhieuKham),
    CONSTRAINT FK_DonThuoc_Thuoc     FOREIGN KEY (MaThuoc)     REFERENCES Thuoc(MaThuoc)
);
GO

/* ======================================================================
   12. BẢNG GIUONGBENH
   ====================================================================== */
CREATE TABLE GiuongBenh (
    MaGiuong            VARCHAR(15)      PRIMARY KEY,
    MaKhoa              VARCHAR(10)      NOT NULL,
    SoGiuong            VARCHAR(10)      NOT NULL,
    TrangThai           VARCHAR(20)      NOT NULL DEFAULT 'Trong'
                        CHECK (TrangThai IN ('Trong','DangSuDung','BaoTri')),
    DonGiaNgay          DECIMAL(12,2)    NOT NULL DEFAULT 0 CHECK (DonGiaNgay >= 0),
    CONSTRAINT UQ_GiuongBenh_Khoa_SoGiuong UNIQUE (MaKhoa, SoGiuong),
    CONSTRAINT FK_GiuongBenh_Khoa FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa)
);
GO

/* ======================================================================
   13. BẢNG NOITRU
   ====================================================================== */
CREATE TABLE NoiTru (
    MaNoiTru            VARCHAR(15)      PRIMARY KEY,
    MaBenhNhan          VARCHAR(15)      NOT NULL,
    MaGiuong            VARCHAR(15)      NOT NULL,
    MaPhieuKham         VARCHAR(15)      NULL,
    NgayNhapVien        DATETIME         NOT NULL DEFAULT GETDATE(),
    NgayXuatVien        DATETIME         NULL,
    TrangThai           VARCHAR(20)      NOT NULL DEFAULT 'DangNam'
                        CHECK (TrangThai IN ('DangNam','DaXuatVien')),
    CONSTRAINT FK_NoiTru_BenhNhan  FOREIGN KEY (MaBenhNhan)  REFERENCES BenhNhan(MaBenhNhan),
    CONSTRAINT FK_NoiTru_Giuong    FOREIGN KEY (MaGiuong)    REFERENCES GiuongBenh(MaGiuong),
    CONSTRAINT FK_NoiTru_PhieuKham FOREIGN KEY (MaPhieuKham) REFERENCES PhieuKham(MaPhieuKham)
);
GO

/* ======================================================================
   14. BẢNG HOADON
   ====================================================================== */
CREATE TABLE HoaDon (
    MaHoaDon            VARCHAR(15)      PRIMARY KEY,
    MaBenhNhan          VARCHAR(15)      NOT NULL,
    MaPhieuKham         VARCHAR(15)      NULL,
    NgayLap             DATETIME         NOT NULL DEFAULT GETDATE(),
    TongTienDichVu      DECIMAL(12,2)    NOT NULL DEFAULT 0 CHECK (TongTienDichVu >= 0),
    BhytChiTra          DECIMAL(12,2)    NOT NULL DEFAULT 0 CHECK (BhytChiTra >= 0),
    TongThanhToan       AS (TongTienDichVu - BhytChiTra) PERSISTED,
    HinhThucThanhToan   VARCHAR(20)      NOT NULL DEFAULT 'TienMat'
                        CHECK (HinhThucThanhToan IN ('TienMat','ChuyenKhoan','ViDienTu','The')),
    TrangThaiTT         VARCHAR(20)      NOT NULL DEFAULT 'ChuaThanhToan'
                        CHECK (TrangThaiTT IN ('ChuaThanhToan','DaThanhToan','HoanTien')),
    NhanVienThu         VARCHAR(15)      NULL,
    NgayThanhToan       DATETIME         NULL,
    CONSTRAINT CK_HoaDon_BhytChiTra CHECK (BhytChiTra <= TongTienDichVu),
    CONSTRAINT FK_HoaDon_BenhNhan  FOREIGN KEY (MaBenhNhan)  REFERENCES BenhNhan(MaBenhNhan),
    CONSTRAINT FK_HoaDon_PhieuKham FOREIGN KEY (MaPhieuKham) REFERENCES PhieuKham(MaPhieuKham),
    CONSTRAINT FK_HoaDon_NhanVien  FOREIGN KEY (NhanVienThu) REFERENCES NhanVien(MaNhanVien)
);
GO

/* ======================================================================
   15. BẢNG CHITIETHOADON
   ====================================================================== */
CREATE TABLE ChiTietHoaDon (
    MaChiTietHD         VARCHAR(15)      PRIMARY KEY,
    MaHoaDon            VARCHAR(15)      NOT NULL,
    TenDichVu           NVARCHAR(150)    NOT NULL,
    LoaiDichVu          VARCHAR(30)      NOT NULL
                        CHECK (LoaiDichVu IN ('KhamBenh','CanLamSang','Thuoc','GiuongBenh')),
    DonGia              DECIMAL(12,2)    NOT NULL CHECK (DonGia >= 0),
    SoLuong             INT              NOT NULL DEFAULT 1 CHECK (SoLuong > 0),
    ThanhTien           AS (DonGia * SoLuong) PERSISTED,
    CONSTRAINT FK_ChiTietHoaDon_HoaDon FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon)
);
GO

/* ======================================================================
   16. BẢNG LICHTRUC
   ====================================================================== */
CREATE TABLE LichTruc (
    MaLichTruc          VARCHAR(15)      PRIMARY KEY,
    MaNhanVien          VARCHAR(15)      NOT NULL,
    Ngay                DATE             NOT NULL,
    Ca                  VARCHAR(10)      NOT NULL 
                        CHECK (Ca IN ('Sang','Chieu','Toi')),
    MaKhoa              VARCHAR(10)      NOT NULL,
    CONSTRAINT UQ_LichTruc UNIQUE (MaNhanVien, Ngay, Ca),
    CONSTRAINT FK_LichTruc_NhanVien FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_LichTruc_Khoa     FOREIGN KEY (MaKhoa)     REFERENCES Khoa(MaKhoa)
);
GO

/* ======================================================================
   17. BẢNG PHIEUNHAPKHO
   ====================================================================== */
CREATE TABLE PhieuNhapKho (
    MaPhieuNhap         VARCHAR(15)      PRIMARY KEY,
    NgayNhap            DATE             NOT NULL,
    NhaCungCap          NVARCHAR(150)    NOT NULL,
    SoHoaDonNCC         VARCHAR(50)      NULL,
    NguoiNhap           VARCHAR(15)      NOT NULL,
    NguoiDuyet          VARCHAR(15)      NULL,
    TongTien            DECIMAL(14,2)    NOT NULL DEFAULT 0 CHECK (TongTien >= 0),
    CONSTRAINT FK_PhieuNhap_NguoiNhap  FOREIGN KEY (NguoiNhap)  REFERENCES NhanVien(MaNhanVien),
    CONSTRAINT FK_PhieuNhap_NguoiDuyet FOREIGN KEY (NguoiDuyet) REFERENCES NhanVien(MaNhanVien)
);
GO

/* ======================================================================
   18. BẢNG CHITIETNHAPKHO
   ====================================================================== */
CREATE TABLE ChiTietNhapKho (
    MaChiTietNhap       VARCHAR(15)      PRIMARY KEY,
    MaPhieuNhap         VARCHAR(15)      NOT NULL,
    MaThuoc             VARCHAR(15)      NOT NULL,
    SoLo                VARCHAR(30)      NOT NULL,
    SoLuong             INT              NOT NULL CHECK (SoLuong > 0),
    SoLuongTon          INT              NOT NULL DEFAULT 0,
    DonGia              DECIMAL(12,2)    NOT NULL CHECK (DonGia >= 0),
    ThanhTien           AS (DonGia * SoLuong) PERSISTED,
    HanSuDung           DATE             NOT NULL,
    CONSTRAINT FK_ChiTietNhap_PhieuNhap FOREIGN KEY (MaPhieuNhap) REFERENCES PhieuNhapKho(MaPhieuNhap),
    CONSTRAINT FK_ChiTietNhap_Thuoc     FOREIGN KEY (MaThuoc)     REFERENCES Thuoc(MaThuoc)
);
GO
