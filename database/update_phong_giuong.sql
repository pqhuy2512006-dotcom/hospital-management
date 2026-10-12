-- 1. Create table Phong
CREATE TABLE Phong (
    MaPhong        VARCHAR(15)          NOT NULL PRIMARY KEY,
    TenPhong       NVARCHAR(100)        NOT NULL,
    LoaiPhong      VARCHAR(20)          NOT NULL CHECK (LoaiPhong IN ('KHAM_BENH', 'LUU_BENH', 'XET_NGHIEM', 'PHAU_THUAT')),
    MaKhoa         VARCHAR(10)          NOT NULL,
    TrangThai      BIT                  NOT NULL DEFAULT 1,
    CONSTRAINT FK_Phong_Khoa FOREIGN KEY (MaKhoa) REFERENCES Khoa(MaKhoa)
);
GO

-- 2. Insert initial rooms based on existing beds
INSERT INTO Phong (MaPhong, TenPhong, LoaiPhong, MaKhoa, TrangThai) VALUES
('P301', N'Phòng Luu B?nh 301', 'LUU_BENH', 'KNT', 1),
('P302', N'Phòng Luu B?nh 302', 'LUU_BENH', 'KNT', 1),
('P401', N'Phòng Luu B?nh 401', 'LUU_BENH', 'KNG', 1),
('P101', N'Phòng C?p C?u 101', 'LUU_BENH', 'KCC', 1),
('PK01', N'Phòng Khám N?i 1', 'KHAM_BENH', 'KKB', 1),
('PK02', N'Phòng Khám Ngo?i 1', 'KHAM_BENH', 'KNG', 1),
('PCLS1', N'Phòng Xét Nghi?m', 'XET_NGHIEM', 'CLS', 1);
GO

-- 3. Modify GiuongBenh table
ALTER TABLE GiuongBenh ADD MaPhong VARCHAR(15);
GO

-- Update data
UPDATE GiuongBenh 
SET MaPhong = 'P' + SUBSTRING(SoGiuong, 2, CHARINDEX('-', SoGiuong) - 2),
    SoGiuong = SUBSTRING(SoGiuong, CHARINDEX('-', SoGiuong) + 1, LEN(SoGiuong))
WHERE SoGiuong LIKE 'P%-%';
GO

-- Fallback for any unmapped
UPDATE GiuongBenh SET MaPhong = 'P301' WHERE MaPhong IS NULL;
GO

-- Add FK and NOT NULL
ALTER TABLE GiuongBenh ALTER COLUMN MaPhong VARCHAR(15) NOT NULL;
ALTER TABLE GiuongBenh ADD CONSTRAINT FK_GiuongBenh_Phong FOREIGN KEY (MaPhong) REFERENCES Phong(MaPhong);
GO

-- Drop old MaKhoa column
ALTER TABLE GiuongBenh DROP CONSTRAINT FK_GiuongBenh_Khoa;
ALTER TABLE GiuongBenh DROP COLUMN MaKhoa;
GO
