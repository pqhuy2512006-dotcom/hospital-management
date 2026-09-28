package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {

    @Id
    @Column(name = "MaHoaDon", length = 15)
    private String maHoaDon;

    @Column(name = "MaBenhNhan", nullable = false, length = 15)
    private String maBenhNhan;

    @Column(name = "MaPhieuKham", nullable = false, length = 15)
    private String maPhieuKham;

    @Column(name = "NgayLap", nullable = false)
    private LocalDateTime ngayLap;

    @Column(name = "TongTienDichVu", nullable = false)
    private BigDecimal tongTienDichVu = BigDecimal.ZERO;

    @Column(name = "BHYTChiTra", nullable = false)
    private BigDecimal bhytChiTra = BigDecimal.ZERO;

    @Column(name = "TongThanhToan", insertable = false, updatable = false)
    private BigDecimal tongThanhToan;

    @Column(name = "HinhThucThanhToan", nullable = false, length = 30)
    private String hinhThucThanhToan = "TienMat";

    @Column(name = "TrangThaiTT", nullable = false, length = 20)
    private String trangThaiTT = "ChuaThanhToan";

    @Column(name = "NhanVienThu", nullable = false, length = 15)
    private String nhanVienThu;

    @Column(name = "NgayThanhToan")
    private LocalDateTime ngayThanhToan;

    @PrePersist
    public void prePersist() {
        if (this.ngayLap == null) {
            this.ngayLap = LocalDateTime.now();
        }
    }
}
