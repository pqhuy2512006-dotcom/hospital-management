package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuKham {
    private String maPhieuKham;
    private String maBenhNhan;
    private String maBacSi;
    private String maKhoa;
    private String maLichHen;
    private LocalDateTime ngayKham;
    private Integer mach;
    private BigDecimal nhietDo;
    private String huyetAp;
    private Integer nhipTho;
    private BigDecimal canNang;
    private BigDecimal chieuCao;
    private String trieuChung;
    private String ketQuaKhamLS;
    private String chanDoan;
    private String loiDanBacSi;
    private LocalDate ngayHenTaiKham;
    public void prePersist() {
        if (this.ngayKham == null) {
            this.ngayKham = LocalDateTime.now();
        }
    }
}
