package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaiTro {
    private String maVaiTro;
    private String tenVaiTro;
    private String tenTiengAnh;
    private String moTa;
    private Boolean trangThai;
}
