package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PhieuNhapKho")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuNhapKho {

    @Id
    @Column(name = "MaPhieuNhap", length = 15)
    private String maPhieuNhap;

    @Column(name = "NgayNhap", nullable = false)
    private LocalDate ngayNhap;

    @Column(name = "NhaCungCap", nullable = false, length = 150)
    private String nhaCungCap;

    @Column(name = "SoHoaDonNCC", length = 30)
    private String soHoaDonNCC;

    @Column(name = "NguoiNhap", nullable = false, length = 15)
    private String nguoiNhap;

    @Column(name = "NguoiDuyet", length = 15)
    private String nguoiDuyet;

    @Column(name = "TongTien", nullable = false)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @PrePersist
    public void prePersist() {
        if (this.ngayNhap == null) {
            this.ngayNhap = LocalDate.now();
        }
    }
}
