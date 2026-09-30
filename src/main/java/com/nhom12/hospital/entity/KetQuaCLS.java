package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KetQuaCLS {
    private String maKetQua;
    private String maPhieuKham;
    private String maBenhNhan;
    private String maDichVu;
    private String maKTV;
    private String maBacSiDoc;
    private String loaiXetNghiem;
    private String ketLuan;
    private LocalDateTime ngayThucHien;
    public void prePersist() {
        if (this.ngayThucHien == null) {
            this.ngayThucHien = LocalDateTime.now();
        }
    }
}
