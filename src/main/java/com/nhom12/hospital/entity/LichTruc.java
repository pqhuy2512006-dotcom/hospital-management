package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "LichTruc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LichTruc {

    @Id
    @Column(name = "MaLichTruc", length = 15)
    private String maLichTruc;

    @Column(name = "MaNhanVien", nullable = false, length = 15)
    private String maNhanVien;

    @Column(name = "Ngay", nullable = false)
    private LocalDate ngay;

    @Column(name = "Ca", nullable = false, length = 10)
    private String ca;

    @Column(name = "MaKhoa", length = 10)
    private String maKhoa;
}
