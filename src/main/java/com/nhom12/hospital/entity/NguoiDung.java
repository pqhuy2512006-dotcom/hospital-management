package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NguoiDung", schema = "dbo")
@Getter
@Setter
@NoArgsConstructor
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNguoiDung")
    private Integer maNguoiDung;

    @Column(name = "TenDangNhap", nullable = false, unique = true, length = 50)
    private String tenDangNhap;

    @Column(name = "MatKhau", nullable = false, length = 255)
    private String matKhau;

    @Column(name = "HoTen", length = 100)
    private String hoTen;

    @Column(name = "VaiTro", length = 20)
    private String vaiTro;

    @Column(name = "SoDienThoai", length = 20)
    private String soDienThoai;

    @Column(name = "Email", length = 100)
    private String email;
}