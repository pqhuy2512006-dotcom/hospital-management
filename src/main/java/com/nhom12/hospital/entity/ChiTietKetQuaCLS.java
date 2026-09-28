package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ChiTietKetQuaCLS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietKetQuaCLS {

    @Id
    @Column(name = "MaChiTietXN", length = 15)
    private String maChiTietXN;

    @Column(name = "MaKetQua", nullable = false, length = 15)
    private String maKetQua;

    @Column(name = "TenChiSo", nullable = false, length = 100)
    private String tenChiSo;

    @Column(name = "GiaTri", length = 30)
    private String giaTri;

    @Column(name = "DonVi", length = 20)
    private String donVi;

    @Column(name = "KhoangThamChieu", length = 50)
    private String khoangThamChieu;

    @Column(name = "DanhGia", length = 20)
    private String danhGia;
}
