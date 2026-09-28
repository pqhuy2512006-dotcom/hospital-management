package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "BenhNhan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BenhNhan {

    @Id
    @Column(name = "MaBenhNhan", length = 15)
    private String maBenhNhan;

    @Column(name = "MaTaiKhoan")
    private Long maTaiKhoan;

    @Column(name = "HoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "NgaySinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "GioiTinh", nullable = false, length = 10)
    private String gioiTinh;

    @Column(name = "SoCCCD", length = 12)
    private String soCCCD;

    @Column(name = "MaBHYT", length = 15)
    private String maBHYT;

    @Column(name = "NhomMau", length = 5)
    private String nhomMau;

    @Column(name = "NgheNghiep", length = 100)
    private String ngheNghiep;

    @Column(name = "DiaChi", length = 200)
    private String diaChi;

    @Column(name = "SoDienThoai", nullable = false, length = 15)
    private String soDienThoai;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "NguoiLienHeKhanCap", length = 100)
    private String nguoiLienHeKhanCap;

    @Column(name = "QuanHeNguoiLienHe", length = 50)
    private String quanHeNguoiLienHe;

    @Column(name = "SdtNguoiLienHe", length = 15)
    private String sdtNguoiLienHe;

    @Column(name = "TienSuBenhNen", length = 500)
    private String tienSuBenhNen;

    @Column(name = "TienSuDiUng", length = 500)
    private String tienSuDiUng;

    @Column(name = "NgayTaoHoSo", nullable = false)
    private LocalDateTime ngayTaoHoSo;

    @PrePersist
    public void prePersist() {
        if (this.ngayTaoHoSo == null) {
            this.ngayTaoHoSo = LocalDateTime.now();
        }
    }
}
