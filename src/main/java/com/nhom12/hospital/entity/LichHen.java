package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LichHen {
    private String maLichHen;
    private String maBenhNhan;
    private String maKhoa;
    private String maBacSi;
    private LocalDate ngayKham;
    private LocalTime gioKham;
    private String loaiKham;
    private Integer thoiGianKhamDuKien;
    private String hinhThucDat;
    private String lyDoKham;
    private String ghiChu;
    private String trangThai;
    private LocalDateTime ngayDatLich;
    private Integer viTriHangDoi;
}
