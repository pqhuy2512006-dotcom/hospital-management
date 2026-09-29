package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@org.hibernate.annotations.Immutable
@Table(name = "vw_PhieuKham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuKham {

    @Id
    @Column(name = "MaPhieuKham", length = 15)
    private String maPhieuKham;

    @Column(name = "MaBenhNhan", nullable = false, length = 15)
    private String maBenhNhan;

    @Column(name = "MaBacSi", nullable = false, length = 15)
    private String maBacSi;

    @Column(name = "MaKhoa", nullable = false, length = 10)
    private String maKhoa;

    @Column(name = "MaLichHen", length = 15)
    private String maLichHen;

    @Column(name = "NgayKham", nullable = false)
    private LocalDateTime ngayKham;

    @Column(name = "Mach")
    private Integer mach;

    @Column(name = "NhietDo")
    private BigDecimal nhietDo;

    @Column(name = "HuyetAp", length = 15)
    private String huyetAp;

    @Column(name = "NhipTho")
    private Integer nhipTho;

    @Column(name = "CanNang")
    private BigDecimal canNang;

    @Column(name = "ChieuCao")
    private BigDecimal chieuCao;

    @Column(name = "TrieuChung", length = 500)
    private String trieuChung;

    @Column(name = "KetQuaKhamLS", length = 1000)
    private String ketQuaKhamLS;

    @Column(name = "ChanDoan", length = 500)
    private String chanDoan;

    @Column(name = "LoiDanBacSi", length = 500)
    private String loiDanBacSi;

    @Column(name = "NgayHenTaiKham")
    private LocalDate ngayHenTaiKham;

    @PrePersist
    public void prePersist() {
        if (this.ngayKham == null) {
            this.ngayKham = LocalDateTime.now();
        }
    }
}
