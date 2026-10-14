package com.nhom12.hospital.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LichSuPhanCongDTO {
    private Integer maPhanCong;
    private String maNhanVien;
    private String hoTen;
    private String maKhoa;
    private String tenKhoa;
    private String chucVu;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String lyDo;
    private String trangThai;
}

