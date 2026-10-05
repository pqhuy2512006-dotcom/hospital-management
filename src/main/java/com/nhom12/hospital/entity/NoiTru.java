package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoiTru {
    private String maNoiTru;
    private String maBenhNhan;
    private String maGiuong;
    private String maPhieuKham;
    private LocalDateTime ngayNhapVien;
    private LocalDateTime ngayXuatVien;
    private String trangThai = "DangNam";
    public void prePersist() {
        if (this.ngayNhapVien == null) {
            this.ngayNhapVien = LocalDateTime.now();
        }
    }
}
