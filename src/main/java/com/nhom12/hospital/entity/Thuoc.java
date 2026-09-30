package com.nhom12.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@org.hibernate.annotations.Immutable
@Table(name = "vw_Thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Thuoc {

    @Id
    @Column(name = "MaThuoc", length = 15)
    private String maThuoc;

    @Column(name = "TenThuoc", nullable = false, length = 150)
    private String tenThuoc;

    @Column(name = "DonViTinh", nullable = false, length = 20)
    private String donViTinh;

    @Column(name = "DonGiaBan", nullable = false)
    private BigDecimal donGiaBan;

    @Column(name = "TonKhoHienTai", nullable = false)
    private Integer tonKhoHienTai = 0;

    @Column(name = "NguongCanhBao", nullable = false)
    private Integer nguongCanhBao = 10;
}
