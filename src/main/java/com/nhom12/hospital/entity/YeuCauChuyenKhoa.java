package com.nhom12.hospital.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class YeuCauChuyenKhoa {
    private String maYeuCau;
    private String loaiYeuCau;
    private String maPhieuKham;
    private String maBenhNhan;
    private String maBacSiGui;
    private String maKhoaNhan;
    private String maBacSiDuocMoi;
    private String lyDo;
    private String trangThai;
    private LocalDateTime ngayTao;
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
    }
}