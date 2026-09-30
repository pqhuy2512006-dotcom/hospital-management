package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BenhNhan {
    private String maBenhNhan;
    private Long maTaiKhoan;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String soCCCD;
    private String maBHYT;
    private String nhomMau;
    private String ngheNghiep;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private String nguoiLienHeKhanCap;
    private String quanHeNguoiLienHe;
    private String sdtNguoiLienHe;
    private String tienSuBenhNen;
    private String tienSuDiUng;
    private LocalDateTime ngayTaoHoSo;
    public void prePersist() {
        if (this.ngayTaoHoSo == null) {
            this.ngayTaoHoSo = LocalDateTime.now();
        }
    }
}
