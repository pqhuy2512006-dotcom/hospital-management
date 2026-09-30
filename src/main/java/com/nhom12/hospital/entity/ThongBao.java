package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThongBao {
    private Integer maThongBao;
    private Long maTaiKhoan;
    private String tieuDe;
    private String noiDung;
    private String loaiThongBao;
    private Boolean daDoc;
    private LocalDateTime ngayTao;
}
