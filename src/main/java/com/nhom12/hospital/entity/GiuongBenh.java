package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GiuongBenh {
    private String maGiuong;
    private String maKhoa;
    private String soGiuong;
    private String trangThai = "Trong";
    private BigDecimal donGiaNgay = BigDecimal.ZERO;
}
