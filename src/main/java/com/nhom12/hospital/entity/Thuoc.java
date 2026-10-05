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
public class Thuoc {
    private String maThuoc;
    private String tenThuoc;
    private String donViTinh;
    private BigDecimal donGiaBan;
    private Integer tonKhoHienTai = 0;
    private Integer nguongCanhBao = 10;
}
