package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@org.hibernate.annotations.Immutable
@Table(name = "vw_ChiTietHoaDon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDon {

    @Id
    @Column(name = "MaChiTietHD", length = 15)
    private String maChiTietHD;

    @Column(name = "MaHoaDon", nullable = false, length = 15)
    private String maHoaDon;

    @Column(name = "TenDichVu", nullable = false, length = 150)
    private String tenDichVu;

    @Column(name = "LoaiDichVu", nullable = false, length = 20)
    private String loaiDichVu;

    @Column(name = "DonGia", nullable = false)
    private BigDecimal donGia;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong = 1;

    @Column(name = "ThanhTien", insertable = false, updatable = false)
    private BigDecimal thanhTien;
}
