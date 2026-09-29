package com.nhom12.hospital.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "YeuCauChuyenKhoa")
@Getter
@Setter
@NoArgsConstructor
public class YeuCauChuyenKhoa {

    @Id
    @Column(name = "MaYeuCau", length = 15)
    private String maYeuCau;

    @Column(name = "LoaiYeuCau", nullable = false, length = 20)
    private String loaiYeuCau;

    @Column(name = "MaPhieuKham", nullable = false, length = 15)
    private String maPhieuKham;

    @Column(name = "MaBenhNhan", nullable = false, length = 15)
    private String maBenhNhan;

    @Column(name = "MaBacSiGui", nullable = false, length = 15)
    private String maBacSiGui;

    @Column(name = "MaKhoaNhan", nullable = false, length = 10)
    private String maKhoaNhan;

    @Column(name = "MaBacSiDuocMoi", length = 15)
    private String maBacSiDuocMoi;

    @Column(name = "LyDo", nullable = false, length = 500)
    private String lyDo;

    @Column(name = "TrangThai", nullable = false, length = 30)
    private String trangThai;

    @Column(name = "NgayTao", nullable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
    }
}