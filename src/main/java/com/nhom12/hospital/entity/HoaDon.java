package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {
    private String maHoaDon;
    private String maBenhNhan;
    private String maPhieuKham;
    private LocalDateTime ngayLap;
    private BigDecimal tongTienDichVu = BigDecimal.ZERO;
    private BigDecimal bhytChiTra = BigDecimal.ZERO;
    private BigDecimal tongThanhToan;
    private String hinhThucThanhToan = "TienMat";
    private String trangThaiTT = "ChuaThanhToan";
    private String nhanVienThu;
    private LocalDateTime ngayThanhToan;
    public void prePersist() {
        if (this.ngayLap == null) {
            this.ngayLap = LocalDateTime.now();
        }
    }
}
