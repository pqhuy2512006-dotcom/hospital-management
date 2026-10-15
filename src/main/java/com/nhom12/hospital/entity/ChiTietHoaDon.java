package com.nhom12.hospital.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
public class ChiTietHoaDon {
    private String maChiTietHD;
    private String maHoaDon;
    private String tenDichVu;
    private String loaiDichVu;
    private BigDecimal donGia;
    private Integer soLuong = 1;
    private BigDecimal thanhTien;
}
