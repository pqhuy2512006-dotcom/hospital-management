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
public class DichVuCLS {
    private String maDichVu;
    private String tenDichVu;
    private BigDecimal donGia;
    private String donViTinh;
    private String khoangThamChieu;
}
