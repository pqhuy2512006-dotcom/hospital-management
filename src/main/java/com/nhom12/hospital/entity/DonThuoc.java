package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DonThuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonThuoc {

    @Id
    @Column(name = "MaDonThuoc", length = 15)
    private String maDonThuoc;

    @Column(name = "MaPhieuKham", nullable = false, length = 15)
    private String maPhieuKham;

    @Column(name = "MaThuoc", nullable = false, length = 15)
    private String maThuoc;

    @Column(name = "SoLo", length = 30)
    private String soLo;

    @Column(name = "DonViTinh", nullable = false, length = 20)
    private String donViTinh;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "LieuDung", length = 100)
    private String lieuDung;

    @Column(name = "CachDung", length = 200)
    private String cachDung;
}
