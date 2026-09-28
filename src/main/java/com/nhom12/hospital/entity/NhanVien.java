package com.nhom12.hospital.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NhanVien", schema = "dbo")
@Getter
@Setter
@NoArgsConstructor
public class NhanVien {

    @Id
    @Column(name = "MaNhanVien", nullable = false, length = 15)
    private String maNhanVien;

    @Column(name = "MaTaiKhoan", unique = true)
    private Long maTaiKhoan;

    @Column(name = "HoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "SoDienThoai", nullable = false, unique = true, length = 15)
    private String soDienThoai;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "VaiTro", nullable = false, length = 20)
    private String vaiTro;

    @Column(name = "TrangThai", nullable = false, length = 20)
    private String trangThai;
}
