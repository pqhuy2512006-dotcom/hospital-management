package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LichTruc {
    private String maLichTruc;
    private String maNhanVien;
    private LocalDate ngay;
    private String ca;
    private String maKhoa;
}
