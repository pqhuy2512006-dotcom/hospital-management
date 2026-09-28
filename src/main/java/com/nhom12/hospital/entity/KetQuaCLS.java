package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "KetQuaCLS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KetQuaCLS {

    @Id
    @Column(name = "MaKetQua", length = 15)
    private String maKetQua;

    @Column(name = "MaPhieuKham", nullable = false, length = 15)
    private String maPhieuKham;

    @Column(name = "MaBenhNhan", nullable = false, length = 15)
    private String maBenhNhan;

    @Column(name = "MaDichVu", length = 15)
    private String maDichVu;

    @Column(name = "MaKTV", nullable = false, length = 15)
    private String maKTV;

    @Column(name = "MaBacSiDoc", length = 15)
    private String maBacSiDoc;

    @Column(name = "LoaiXetNghiem", nullable = false, length = 100)
    private String loaiXetNghiem;

    @Column(name = "KetLuan", length = 500)
    private String ketLuan;

    @Column(name = "NgayThucHien", nullable = false)
    private LocalDateTime ngayThucHien;

    @PrePersist
    public void prePersist() {
        if (this.ngayThucHien == null) {
            this.ngayThucHien = LocalDateTime.now();
        }
    }
}
