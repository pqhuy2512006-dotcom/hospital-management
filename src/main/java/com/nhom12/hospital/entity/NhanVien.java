package com.nhom12.hospital.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
public class NhanVien {
    private String maNhanVien;
    private Long maTaiKhoan;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String soCCCD;
    private String soDienThoai;
    private String email;
    private String maPhong;
    private String chungChiHanhNghe;
    private String trinhDoChuyenMon;
    private LocalDate ngayVaoLam;
    private String vaiTro;
    private String trangThai;
    private String maKhoa;
}
