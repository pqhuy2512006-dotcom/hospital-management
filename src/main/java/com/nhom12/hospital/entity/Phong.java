package com.nhom12.hospital.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Phong {
    private String maPhong;
    private String tenPhong;
    private String loaiPhong;
    private String maKhoa;
    @Builder.Default
    private Boolean trangThai = true;
    private Khoa khoa;
}
