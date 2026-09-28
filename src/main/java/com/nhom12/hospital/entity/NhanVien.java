package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "NhanVien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {

    @Id
    @Column(name = "MaNhanVien", length = 15)
    private String maNhanVien;

    @Column(name = "MaTaiKhoan")
    private Long maTaiKhoan;

    @Column(name = "HoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "GioiTinh", length = 10)
    private String gioiTinh;

    @Column(name = "SoCCCD", length = 12)
    private String soCCCD;

    @Column(name = "SoDienThoai", nullable = false, length = 15)
    private String soDienThoai;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "DiaChi", length = 200)
    private String diaChi;

    @Column(name = "ChuyenKhoa", length = 100)
    private String chuyenKhoa;

    @Column(name = "ChungChiHanhNghe", length = 50)
    private String chungChiHanhNghe;

    @Column(name = "TrinhDoChuyenMon", length = 100)
    private String trinhDoChuyenMon;

    @Column(name = "NgayVaoLam")
    private LocalDate ngayVaoLam;

    @Column(name = "VaiTro", nullable = false, length = 20)
    private String vaiTro;

    @Column(name = "TrangThai", nullable = false, length = 20)
    private String trangThai;
}
