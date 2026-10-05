package com.nhom12.hospital.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class TaiKhoan {
    private Long maTaiKhoan;
    private String tenDangNhap;
    private String matKhauHash;
    private String email;
    private String soDienThoai;
    private String vaiTro;
    private Boolean trangThai;
    private LocalDateTime ngayTao;
}
