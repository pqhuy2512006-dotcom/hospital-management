package com.nhom12.hospital.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Phong {
    private String maPhong;
    private String tenPhong;
    private String loaiPhong;
    private String maKhoa;
    private String tenKhoa;
    private Boolean trangThai = true;
    private Khoa khoa;
}