package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChucNang {
    private String maChucNang;
    private String tenChucNang;
    private String maPhanHe;
    private String tenPhanHe;
    private String urlTrang;
    private String phuongThucApi;
    private String endpointApi;
    private String moTa;
    private Integer thuTuHienThi;
}
