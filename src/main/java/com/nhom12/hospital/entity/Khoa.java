package com.nhom12.hospital.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Khoa", schema = "dbo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Khoa {

    @Id
    @Column(name = "MaKhoa", length = 10)
    private String maKhoa;

    @Column(name = "TenKhoa", nullable = false, length = 100)
    private String tenKhoa;

    @Column(name = "MoTa", length = 300)
    private String moTa;
}