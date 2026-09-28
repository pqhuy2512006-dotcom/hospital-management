package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "GiuongBenh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GiuongBenh {

    @Id
    @Column(name = "MaGiuong", length = 15)
    private String maGiuong;

    @Column(name = "MaKhoa", nullable = false, length = 10)
    private String maKhoa;

    @Column(name = "SoGiuong", nullable = false, length = 20)
    private String soGiuong;

    @Column(name = "TrangThai", nullable = false, length = 20)
    private String trangThai = "Trong";

    @Column(name = "DonGiaNgay", nullable = false)
    private BigDecimal donGiaNgay = BigDecimal.ZERO;
}
