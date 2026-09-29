package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "NoiTru")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoiTru {

    @Id
    @Column(name = "MaNoiTru", length = 15)
    private String maNoiTru;

    @Column(name = "MaBenhNhan", nullable = false, length = 15)
    private String maBenhNhan;

    @Column(name = "MaGiuong", nullable = false, length = 15)
    private String maGiuong;

    @Column(name = "MaPhieuKham", nullable = false, length = 15)
    private String maPhieuKham;

    @Column(name = "NgayNhapVien", nullable = false)
    private LocalDateTime ngayNhapVien;

    @Column(name = "NgayXuatVien")
    private LocalDateTime ngayXuatVien;

    @Column(name = "TrangThai", nullable = false, length = 20)
    private String trangThai = "DangNam";

    @PrePersist
    public void prePersist() {
        if (this.ngayNhapVien == null) {
            this.ngayNhapVien = LocalDateTime.now();
        }
    }
}
