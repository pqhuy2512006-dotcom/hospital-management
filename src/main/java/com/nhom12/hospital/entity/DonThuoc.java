package com.nhom12.hospital.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class DonThuoc {
    private String maDonThuoc;
    private String maPhieuKham;
    private String maThuoc;
    private String soLo;
    private String donViTinh;
    private Integer soLuong;
    private String lieuDung;
    private String cachDung;
    private String trangThai = "PENDING";
}
