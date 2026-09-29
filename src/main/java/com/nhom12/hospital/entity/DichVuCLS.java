package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@org.hibernate.annotations.Immutable
@Table(name = "vw_DichVuCLS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DichVuCLS {

    @Id
    @Column(name = "MaDichVu", length = 15)
    private String maDichVu;

    @Column(name = "TenDichVu", nullable = false, length = 150)
    private String tenDichVu;

    @Column(name = "DonGia", nullable = false)
    private BigDecimal donGia;

    @Column(name = "DonViTinh", length = 20)
    private String donViTinh;

    @Column(name = "KhoangThamChieu", length = 100)
    private String khoangThamChieu;
}
