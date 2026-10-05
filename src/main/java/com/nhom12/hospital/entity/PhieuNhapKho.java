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
public class PhieuNhapKho {
    private String maPhieuNhap;
    private LocalDate ngayNhap;
    private String nhaCungCap;
    private String soHoaDonNCC;
    private String nguoiNhap;
    private String nguoiDuyet;
    private BigDecimal tongTien = BigDecimal.ZERO;
    public void prePersist() {
        if (this.ngayNhap == null) {
            this.ngayNhap = LocalDate.now();
        }
    }
}
