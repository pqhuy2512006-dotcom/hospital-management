package com.nhom12.hospital.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PhanCongRequest {
    private String maNhanVien;
    private String maKhoa;
    private LocalDate ngayBatDau;
    private String lyDo;
}

