package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@org.hibernate.annotations.Immutable
@Table(name = "vw_LichHen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LichHen {

    @Id
    @Column(name = "MaLichHen", length = 15)
    private String maLichHen;

    @Column(name = "MaBenhNhan", nullable = false, length = 15)
    private String maBenhNhan;

    @Column(name = "MaKhoa", nullable = false, length = 10)
    private String maKhoa;

    @Column(name = "MaBacSi", length = 15)
    private String maBacSi;

    @Column(name = "NgayKham", nullable = false)
    private LocalDate ngayKham;

    @Column(name = "GioKham", nullable = false)
    private LocalTime gioKham;

    @Column(name = "LoaiKham", nullable = false, length = 20)
    private String loaiKham;

    @Column(name = "HinhThucDat", nullable = false, length = 20)
    private String hinhThucDat;

    @Column(name = "LyDoKham", length = 300)
    private String lyDoKham;

    @Column(name = "GhiChu", length = 300)
    private String ghiChu;

    @Column(name = "TrangThai", nullable = false, length = 20)
    private String trangThai;

    @Column(name = "NgayDatLich", nullable = false)
    private LocalDateTime ngayDatLich;
}
