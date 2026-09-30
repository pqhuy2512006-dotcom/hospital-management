package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@org.hibernate.annotations.Immutable
@Table(name = "vw_ChiTietNhapKho")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietNhapKho {

    @Id
    @Column(name = "MaChiTietNhap", length = 15)
    private String maChiTietNhap;

    @Column(name = "MaPhieuNhap", nullable = false, length = 15)
    private String maPhieuNhap;

    @Column(name = "MaThuoc", nullable = false, length = 15)
    private String maThuoc;

    @Column(name = "SoLo", nullable = false, length = 30)
    private String soLo;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "DonGia", nullable = false)
    private BigDecimal donGia;

    @Column(name = "ThanhTien", insertable = false, updatable = false)
    private BigDecimal thanhTien;

    @Column(name = "HanSuDung", nullable = false)
    private LocalDate hanSuDung;
}
