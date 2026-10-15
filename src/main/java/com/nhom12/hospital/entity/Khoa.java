package com.nhom12.hospital.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Khoa {
    private String maKhoa;
    private String tenKhoa;
    private String moTa;
    private String maTruongKhoa;
    private Boolean trangThai = true;
    private String tenTruongKhoa;
}