package com.nhom12.hospital.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhanQuyenChiTiet {
    private Long maPhanQuyen;
    private String maVaiTro;
    private String tenVaiTro;
    private String tenTiengAnh;
    private String maChucNang;
    private String tenChucNang;
    private String maPhanHe;
    private String tenPhanHe;
    private String urlTrang;
    private String phuongThucApi;
    private String endpointApi;
    private Boolean quyenXem;
    private Boolean quyenThem;
    private Boolean quyenSua;
    private Boolean quyenXoa;
    private String ghiChu;
    private LocalDateTime ngayCapNhat;
}
