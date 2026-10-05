package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietNhapKho {
    private String maChiTietNhap;
    private String maPhieuNhap;
    private String maThuoc;
    private String soLo;
    private Integer soLuong;
    private Integer soLuongTon;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private LocalDate hanSuDung;
}
