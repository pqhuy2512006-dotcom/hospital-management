CREATE OR ALTER VIEW dbo.vw_BenhNhan AS SELECT * FROM dbo.BenhNhan;
GO
CREATE OR ALTER VIEW dbo.vw_AuditLog AS SELECT * FROM dbo.AuditLog;
GO
CREATE OR ALTER VIEW dbo.vw_ChiTietHoaDon AS SELECT * FROM dbo.ChiTietHoaDon;
GO
CREATE OR ALTER VIEW dbo.vw_ChiTietKetQuaCLS AS SELECT * FROM dbo.ChiTietKetQuaCLS;
GO
CREATE OR ALTER VIEW dbo.vw_ChiTietNhapKho AS SELECT * FROM dbo.ChiTietNhapKho;
GO
CREATE OR ALTER VIEW dbo.vw_DichVuCLS AS SELECT * FROM dbo.DichVuCLS;
GO
CREATE OR ALTER VIEW dbo.vw_DonThuoc AS SELECT * FROM dbo.DonThuoc;
GO
CREATE OR ALTER VIEW dbo.vw_GiuongBenh AS SELECT * FROM dbo.GiuongBenh;
GO
CREATE OR ALTER VIEW dbo.vw_HoaDon AS SELECT * FROM dbo.HoaDon;
GO
CREATE OR ALTER VIEW dbo.vw_KetQuaCLS AS SELECT * FROM dbo.KetQuaCLS;
GO
CREATE OR ALTER VIEW dbo.vw_Khoa AS SELECT * FROM dbo.Khoa;
GO
CREATE OR ALTER VIEW dbo.vw_LichHen AS SELECT * FROM dbo.LichHen;
GO
CREATE OR ALTER VIEW dbo.vw_LichTruc AS SELECT * FROM dbo.LichTruc;
GO
CREATE OR ALTER VIEW dbo.vw_NhanVien AS SELECT * FROM dbo.NhanVien;
GO
CREATE OR ALTER VIEW dbo.vw_NoiTru AS SELECT * FROM dbo.NoiTru;
GO
CREATE OR ALTER VIEW dbo.vw_PhieuKham AS SELECT * FROM dbo.PhieuKham;
GO
CREATE OR ALTER VIEW dbo.vw_PhieuNhapKho AS SELECT * FROM dbo.PhieuNhapKho;
GO
CREATE OR ALTER VIEW dbo.vw_Thuoc AS SELECT * FROM dbo.Thuoc;
GO
CREATE OR ALTER VIEW dbo.vw_YeuCauChuyenKhoa AS SELECT * FROM dbo.YeuCauChuyenKhoa;
GO
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
