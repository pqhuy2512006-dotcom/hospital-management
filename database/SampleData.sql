USE QuanLyBenhVien;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

-- 1. KHOA
IF NOT EXISTS (SELECT 1 FROM Khoa WHERE MaKhoa = 'KKB')
INSERT INTO Khoa (MaKhoa, TenKhoa, MoTa) VALUES
('KKB', N'Khoa Khám Bệnh', N'Tiếp nhận và khám bệnh ban đầu cho người bệnh'),
('KNT', N'Khoa Nội Tổng Hợp', N'Chẩn đoán và điều trị các bệnh nội khoa'),
('KNG', N'Khoa Ngoại Tiêu Hóa', N'Phẫu thuật và điều trị các bệnh lý đường tiêu hóa'),
('KCC', N'Khoa Cấp Cứu', N'Cấp cứu và hồi sức tích cực 24/7'),
('TMH', N'Khoa Tai Mũi Họng', N'Khám và điều trị các bệnh lý tai mũi họng'),
('CLS', N'Khoa Cận Lâm Sàng', N'Xét nghiệm sinh hóa, huyết học, chẩn đoán hình ảnh');
GO

-- 2. TAI KHOAN
DECLARE @Hash VARCHAR(255) = '$2a$10$MhcIw3ha3SWQrvmNnDzurO2NOEzBnB2BiKG43ZfsUoPhK21olvCDq';

IF NOT EXISTS (SELECT 1 FROM TaiKhoan WHERE TenDangNhap = 'bs_hung')
INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, Email, SoDienThoai, VaiTro, TrangThai) VALUES
('bs_hung', @Hash, 'hung.tran@hospital.com', '0912345001', 'BacSi', 1),
('bs_quan', @Hash, 'quan.do@hospital.com', '0912345002', 'BacSi', 1),
('bs_mai', @Hash, 'mai.nguyen@hospital.com', '0912345003', 'BacSi', 1),
('ds_lan', @Hash, 'lan.duoc@hospital.com', '0912345004', 'DuocSi', 1),
('tn_nga', @Hash, 'nga.thungan@hospital.com', '0912345005', 'ThuNgan', 1),
('ktv_huy', @Hash, 'huy.ktv@hospital.com', '0912345006', 'KTV', 1),
('lt_minh', @Hash, 'minh.letan@hospital.com', '0912345007', 'LeTan', 1),
('dd_hoa', @Hash, 'hoa.dieuduong@hospital.com', '0912345008', 'DieuDuong', 1);
GO

-- 3. NHAN VIEN
IF NOT EXISTS (SELECT 1 FROM NhanVien WHERE MaNhanVien = 'NV-DOC01')
BEGIN
    DECLARE @tkHung BIGINT = (SELECT MaTaiKhoan FROM TaiKhoan WHERE TenDangNhap = 'bs_hung');
    DECLARE @tkQuan BIGINT = (SELECT MaTaiKhoan FROM TaiKhoan WHERE TenDangNhap = 'bs_quan');
    DECLARE @tkMai BIGINT = (SELECT MaTaiKhoan FROM TaiKhoan WHERE TenDangNhap = 'bs_mai');
    DECLARE @tkLan BIGINT = (SELECT MaTaiKhoan FROM TaiKhoan WHERE TenDangNhap = 'ds_lan');
    DECLARE @tkNga BIGINT = (SELECT MaTaiKhoan FROM TaiKhoan WHERE TenDangNhap = 'tn_nga');
    DECLARE @tkHuy BIGINT = (SELECT MaTaiKhoan FROM TaiKhoan WHERE TenDangNhap = 'ktv_huy');
    DECLARE @tkMinh BIGINT = (SELECT MaTaiKhoan FROM TaiKhoan WHERE TenDangNhap = 'lt_minh');
    DECLARE @tkHoa BIGINT = (SELECT MaTaiKhoan FROM TaiKhoan WHERE TenDangNhap = 'dd_hoa');

    INSERT INTO NhanVien (MaNhanVien, MaTaiKhoan, HoTen, NgaySinh, GioiTinh, SoCCCD, SoDienThoai, Email, DiaChi, ChuyenKhoa, ChungChiHanhNghe, TrinhDoChuyenMon, NgayVaoLam, VaiTro, TrangThai) VALUES
    ('NV-DOC01', @tkHung, N'BS. CKI. Trần Văn Hùng', '1980-05-15', 'Nam', '079080001111', '0912345001', 'hung.tran@hospital.com', N'Phòng 201 - Nhà A', N'Nội Tổng Hợp', '012345/BYT-CCHN', N'Bác sĩ CKI', '2015-01-10', 'BacSi', 'DangLamViec'),
    ('NV-DOC02', @tkQuan, N'BS. Đỗ Minh Quân', '1985-08-20', 'Nam', '079085002222', '0912345002', 'quan.do@hospital.com', N'Phòng 305 - Nhà B', N'Ngoại Tiêu Hóa', '012346/BYT-CCHN', N'Bác sĩ CKII', '2018-03-15', 'BacSi', 'DangLamViec'),
    ('NV-DOC03', @tkMai, N'ThS. BS. Nguyễn Thị Mai', '1988-11-12', 'Nu', '079088003333', '0912345003', 'mai.nguyen@hospital.com', N'Phòng 108 - Nhà A', N'Tai Mũi Họng', '012347/BYT-CCHN', N'Thạc sĩ Bác sĩ', '2019-06-01', 'BacSi', 'DangLamViec'),
    ('NV-PHA01', @tkLan, N'DS. Lê Thị Phương Lan', '1990-04-18', 'Nu', '079090004444', '0912345004', 'lan.duoc@hospital.com', N'Kho Dược Trung Tâm', N'Dược Lâm Sàng', '012348/BYT-CCHN', N'Dược sĩ Đại học', '2020-02-01', 'DuocSi', 'DangLamViec'),
    ('NV-CAS01', @tkNga, N'Nguyễn Thu Nga', '1992-09-25', 'Nu', '079092005555', '0912345005', 'nga.thungan@hospital.com', N'Quầy Thu Ngân 01', N'Viện Phí', NULL, N'Cử nhân Kế toán', '2021-05-10', 'ThuNgan', 'DangLamViec'),
    ('NV-LAB01', @tkHuy, N'KTV. Phạm Quốc Huy', '1993-12-05', 'Nam', '079093006666', '0912345006', 'huy.ktv@hospital.com', N'Phòng Xét Nghiệm Tầng 2', N'Kỹ Thuật Y Học', '012349/BYT-CCHN', N'Cử nhân Xét nghiệm', '2021-08-15', 'KTV', 'DangLamViec'),
    ('NV-REC01', @tkMinh, N'Lê Quang Minh', '1995-07-30', 'Nam', '079095007777', '0912345007', 'minh.letan@hospital.com', N'Quầy Tiếp Đón Trung Tâm', N'Tiếp Đón', NULL, N'Cao đẳng Y tế', '2022-01-10', 'LeTan', 'DangLamViec'),
    ('NV-NUR01', @tkHoa, N'ĐD. Vũ Quỳnh Hoa', '1994-03-22', 'Nu', '079094008888', '0912345008', 'hoa.dieuduong@hospital.com', N'Khoa Nội Trú Tầng 3', N'Điều Dưỡng', '012350/BYT-CCHN', N'Cử nhân Điều dưỡng', '2021-09-01', 'DieuDuong', 'DangLamViec');
END
GO

-- 4. BENH NHAN
IF NOT EXISTS (SELECT 1 FROM BenhNhan WHERE MaBenhNhan = 'BN2026000001')
INSERT INTO BenhNhan (MaBenhNhan, HoTen, NgaySinh, GioiTinh, SoCCCD, MaBHYT, NhomMau, NgheNghiep, DiaChi, SoDienThoai, Email, NguoiLienHeKhanCap, QuanHeNguoiLienHe, SdtNguoiLienHe, TienSuBenhNen, TienSuDiUng, NgayTaoHoSo) VALUES
('BN2026000001', N'Nguyễn Văn An', '1988-04-12', 'Nam', '079088001234', 'DN4790880012345', 'O+', N'Kỹ sư', N'Quận 1, TP. Hồ Chí Minh', '0912345678', 'an.nguyen@email.com', N'Nguyễn Văn Ba', N'Bố', '0903112233', N'Đau dạ dày mạn', N'Dị ứng kháng sinh Penicillin', GETDATE()),
('BN2026000002', N'Trần Thị Mai', '1995-10-25', 'Nu', '079095005678', 'GD4790950056789', 'A+', N'Giáo viên', N'Quận 3, TP. Hồ Chí Minh', '0987654321', 'mai.tran@email.com', N'Lê Văn Tự', N'Chồng', '0988776655', N'Không có', N'Không có', GETDATE()),
('BN2026000003', N'Lê Hoàng Long', '2001-01-15', 'Nam', '079201009988', NULL, 'B+', N'Sinh viên', N'Bình Thạnh, TP. Hồ Chí Minh', '0903112233', 'long.le@email.com', N'Hoàng Thị Cúc', N'Mẹ', '0909112233', N'Viêm xoang mũi', N'Dị ứng Aspirin', GETDATE()),
('BN2026000004', N'Phạm Minh Tuấn', '1975-06-30', 'Nam', '079075001234', 'DN4790750012345', 'O+', N'Kinh doanh', N'TP. Thủ Đức, TP. Hồ Chí Minh', '0933221100', 'tuan.pham@email.com', N'Vũ Kim Chi', N'Vợ', '0933221101', N'Tăng huyết áp vô căn', N'Không có', GETDATE()),
('BN2026000005', N'Hoàng Thu Thảo', '1998-08-19', 'Nu', '079098007788', 'GD4790980077889', 'AB+', N'Nhân viên văn phòng', N'Gò Vấp, TP. Hồ Chí Minh', '0977889900', 'thao.hoang@email.com', N'Hoàng Văn Nam', N'Bố', '0977889901', N'Trào ngược dạ dày', N'Dị ứng hải sản', GETDATE());
GO

-- 5. THUOC
IF NOT EXISTS (SELECT 1 FROM Thuoc WHERE MaThuoc = 'MED-001')
INSERT INTO Thuoc (MaThuoc, TenThuoc, DonViTinh, DonGiaBan, TonKhoHienTai, NguongCanhBao) VALUES
('MED-001', N'Paracetamol 500mg (Viên nén)', 'Vien', 2000, 500, 50),
('MED-002', N'Amoxicillin 500mg (Kháng sinh)', 'Vien', 5000, 200, 30),
('MED-003', N'Panadol Extra (Đỏ)', 'Vien', 3500, 15, 20),
('MED-004', N'Berberin 100mg (Tiêu hóa)', 'Vien', 1500, 350, 40),
('MED-005', N'Vitamin C 500mg (Viên sủi)', 'Vien', 4000, 30, 20),
('MED-006', N'Esomeprazole 40mg (Dạ dày)', 'Vien', 12000, 180, 25),
('MED-007', N'Phosphalugel (Chữ P dạ dày)', 'Goi', 8000, 120, 20),
('MED-008', N'Oresol 245 (Bù nước điện giải)', 'Goi', 5000, 250, 50),
('MED-009', N'Amlodipine 5mg (Huyết áp)', 'Vien', 4500, 150, 30);
GO

-- 6. DICH VU CAN LAM SANG
IF NOT EXISTS (SELECT 1 FROM DichVuCLS WHERE MaDichVu = 'CLS01')
INSERT INTO DichVuCLS (MaDichVu, TenDichVu, DonGia, DonViTinh, KhoangThamChieu) VALUES
('CLS01', N'Tổng phân tích tế bào máu ngoại vi (Bằng máy laser)', 85000, N'Lần', N'WBC: 4.0-10.0, RBC: 4.2-5.4, PLT: 150-450'),
('CLS02', N'Sinh hóa máu: Định lượng Glucose', 45000, N'Lần', N'3.9 - 6.4 mmol/L'),
('CLS03', N'Nội soi thực quản - dạ dày - tá tràng', 450000, N'Lần', N'Niêm mạc nhẵn, không loét'),
('CLS04', N'X-Quang ngực thẳng', 95000, N'Lần', N'Phế trường sáng, bóng tim bình thường'),
('CLS05', N'Siêu âm ổ bụng tổng quát', 150000, N'Lần', N'Các tạng kích thước bình thường');
GO

-- 7. GIUONG BENH (SoGiuong <= 20 chars)
IF NOT EXISTS (SELECT 1 FROM GiuongBenh WHERE MaGiuong = 'G-301-A')
INSERT INTO GiuongBenh (MaGiuong, MaKhoa, SoGiuong, TrangThai, DonGiaNgay) VALUES
('G-301-A', 'KNT', 'P301-G1', 'Trong', 250000),
('G-301-B', 'KNT', 'P301-G2', 'Trong', 250000),
('G-302-A', 'KNT', 'P302-G1', 'Trong', 250000),
('G-302-B', 'KNT', 'P302-G2', 'Trong', 250000),
('G-401-A', 'KNG', 'P401-G1', 'Trong', 300000),
('G-401-B', 'KNG', 'P401-G2', 'Trong', 300000),
('G-101-A', 'KCC', 'P101-G1', 'Trong', 400000),
('G-101-B', 'KCC', 'P101-G2', 'Trong', 400000);
GO

-- 8. LICH HEN
IF NOT EXISTS (SELECT 1 FROM LichHen WHERE MaLichHen = 'LH2026000001')
INSERT INTO LichHen (MaLichHen, MaBenhNhan, MaKhoa, MaBacSi, NgayKham, GioKham, LoaiKham, ThoiGianKhamDuKien, HinhThucDat, LyDoKham, GhiChu, TrangThai, NgayDatLich) VALUES
('LH2026000001', 'BN2026000001', 'KNT', 'NV-DOC01', CAST(GETDATE() AS DATE), '08:30:00', 'KhamThuong', 30, 'Online', N'Đau thượng vị âm ỉ, ợ chua kéo dài', N'Khám buổi sáng', 'ChoXacNhan', GETDATE()),
('LH2026000002', 'BN2026000002', 'KNT', 'NV-DOC01', CAST(GETDATE() AS DATE), '09:00:00', 'KhamDichVu', 45, 'TrucTiep', N'Sốt nhẹ 38 độ, mệt mỏi, đau nhức cơ', N'Tiếp đón tại quầy', 'ChoXacNhan', GETDATE()),
('LH2026000003', 'BN2026000003', 'TMH', 'NV-DOC03', CAST(GETDATE() AS DATE), '10:00:00', 'KhamThuong', 30, 'Online', N'Nghẹt mũi, chảy nước mũi trong, đau đầu', N'Đã đặt qua app', 'DaXacNhan', GETDATE()),
('LH2026000004', 'BN2026000004', 'KNT', 'NV-DOC01', CAST(GETDATE() AS DATE), '10:30:00', 'TaiKham', 30, 'DienThoai', N'Tái khám định kỳ tăng huyết áp', N'Bệnh nhân quen', 'DaXacNhan', GETDATE());
GO

-- 9. PHIEU KHAM
IF NOT EXISTS (SELECT 1 FROM PhieuKham WHERE MaPhieuKham = 'PK2026000001')
INSERT INTO PhieuKham (MaPhieuKham, MaBenhNhan, MaBacSi, MaKhoa, MaLichHen, NgayKham, Mach, NhietDo, HuyetAp, NhipTho, CanNang, ChieuCao, TrieuChung, KetQuaKhamLS, ChanDoan, LoiDanBacSi, NgayHenTaiKham) VALUES
('PK2026000001', 'BN2026000001', 'NV-DOC01', 'KNT', 'LH2026000001', GETDATE(), 78, 37.0, '120/80', 18, 65.5, 170.0, N'Đau âm ỉ vùng thượng vị sau ăn', N'Bụng mềm, ấn tức nhẹ thượng vị', N'K29.7 - Viêm dạ dày mạn tính, trào ngược dạ dày thực quản', N'Ăn uống đúng giờ, tránh cay nóng, uống thuốc theo đơn', DATEADD(DAY, 7, CAST(GETDATE() AS DATE))),
('PK2026000002', 'BN2026000004', 'NV-DOC01', 'KNT', 'LH2026000004', GETDATE(), 82, 36.8, '140/90', 20, 72.0, 168.0, N'Chóng mặt nhẹ khi thay đổi tư thế', N'Tim đều, phổi trong, mạch rõ', N'I10 - Tăng huyết áp nguyên phát', N'Ăn giảm muối, vận động nhẹ nhàng', DATEADD(DAY, 30, CAST(GETDATE() AS DATE)));
GO

-- 10. DON THUOC
IF NOT EXISTS (SELECT 1 FROM DonThuoc WHERE MaDonThuoc = 'DT2026000001')
INSERT INTO DonThuoc (MaDonThuoc, MaPhieuKham, MaThuoc, SoLo, DonViTinh, SoLuong, LieuDung, CachDung) VALUES
('DT2026000001', 'PK2026000001', 'MED-006', 'L2026-08', 'Vien', 28, N'Uống ngày 2 lần, mỗi lần 1 viên', N'Uống trước ăn sáng và ăn tối 30 phút'),
('DT2026000002', 'PK2026000001', 'MED-007', 'L2026-09', 'Goi', 20, N'Uống ngày 2 lần, mỗi lần 1 gói', N'Uống khi đau hoặc sau ăn 2 giờ'),
('DT2026000003', 'PK2026000002', 'MED-009', 'L2026-07', 'Vien', 30, N'Uống ngày 1 lần, mỗi lần 1 viên', N'Uống vào buổi sáng cố định giờ');
GO

-- 11. NOI TRU
IF NOT EXISTS (SELECT 1 FROM NoiTru WHERE MaNoiTru = 'NT2026000001')
INSERT INTO NoiTru (MaNoiTru, MaBenhNhan, MaGiuong, MaPhieuKham, NgayNhapVien, NgayXuatVien, TrangThai) VALUES
('NT2026000001', 'BN2026000001', 'G-301-A', 'PK2026000001', DATEADD(DAY, -2, GETDATE()), NULL, 'DangNam'),
('NT2026000002', 'BN2026000004', 'G-401-A', 'PK2026000002', DATEADD(DAY, -1, GETDATE()), NULL, 'DangNam');
GO

-- 12. KET QUA CLS & CHI TIET
IF NOT EXISTS (SELECT 1 FROM KetQuaCLS WHERE MaKetQua = 'CLS2026-001')
INSERT INTO KetQuaCLS (MaKetQua, MaPhieuKham, MaBenhNhan, MaDichVu, MaKTV, MaBacSiDoc, LoaiXetNghiem, KetLuan, NgayThucHien) VALUES
('CLS2026-001', 'PK2026000001', 'BN2026000001', 'CLS01', 'NV-LAB01', 'NV-DOC01', N'Tổng phân tích tế bào máu', N'Hồng cầu, bạch cầu trong giới hạn bình thường, không có dấu hiệu nhiễm trùng cấp', GETDATE()),
('CLS2026-002', 'PK2026000001', 'BN2026000001', 'CLS02', 'NV-LAB01', 'NV-DOC01', N'Sinh hóa máu - Glucose', N'Đường huyết lúc đói bình thường', GETDATE());
GO

IF NOT EXISTS (SELECT 1 FROM ChiTietKetQuaCLS WHERE MaChiTietXN = 'CT001')
INSERT INTO ChiTietKetQuaCLS (MaChiTietXN, MaKetQua, TenChiSo, GiaTri, DonVi, KhoangThamChieu, DanhGia) VALUES
('CT001', 'CLS2026-001', N'Số lượng hồng cầu (RBC)', '4.6', 'T/L', '4.2 - 5.4', 'BinhThuong'),
('CT002', 'CLS2026-001', N'Huyết sắc tố (Hb)', '142', 'g/L', '130 - 160', 'BinhThuong'),
('CT003', 'CLS2026-001', N'Số lượng bạch cầu (WBC)', '7.2', 'G/L', '4.0 - 10.0', 'BinhThuong'),
('CT004', 'CLS2026-001', N'Số lượng tiểu cầu (PLT)', '245', 'G/L', '150 - 450', 'BinhThuong'),
('CT005', 'CLS2026-002', N'Định lượng Glucose', '5.4', 'mmol/L', '3.9 - 6.4', 'BinhThuong');
GO

-- 13. HOA DON & CHI TIET
IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE MaHoaDon = 'HD2026-001')
INSERT INTO HoaDon (MaHoaDon, MaBenhNhan, MaPhieuKham, NgayLap, TongTienDichVu, BHYTChiTra, HinhThucThanhToan, TrangThaiTT, NhanVienThu, NgayThanhToan) VALUES
('HD2026-001', 'BN2026000001', 'PK2026000001', GETDATE(), 581000, 464800, 'TienMat', 'ChuaThanhToan', 'NV-CAS01', NULL),
('HD2026-002', 'BN2026000004', 'PK2026000002', DATEADD(HOUR, -3, GETDATE()), 285000, 228000, 'ChuyenKhoan', 'DaThanhToan', 'NV-CAS01', GETDATE());
GO

IF NOT EXISTS (SELECT 1 FROM ChiTietHoaDon WHERE MaChiTietHD = 'CTHD001')
INSERT INTO ChiTietHoaDon (MaChiTietHD, MaHoaDon, TenDichVu, LoaiDichVu, DonGia, SoLuong) VALUES
('CTHD001', 'HD2026-001', N'Công khám Nội Tổng Hợp', 'Kham', 150000, 1),
('CTHD002', 'HD2026-001', N'Tổng phân tích tế bào máu ngoại vi', 'CanLamSang', 85000, 1),
('CTHD003', 'HD2026-001', N'Esomeprazole 40mg', 'Thuoc', 12000, 28),
('CTHD004', 'HD2026-001', N'Phosphalugel', 'Thuoc', 8000, 20),
('CTHD005', 'HD2026-002', N'Công khám Nội Tổng Hợp', 'Kham', 150000, 1),
('CTHD006', 'HD2026-002', N'Amlodipine 5mg', 'Thuoc', 4500, 30);
GO
