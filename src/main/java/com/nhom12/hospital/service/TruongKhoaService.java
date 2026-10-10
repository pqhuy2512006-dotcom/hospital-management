package com.nhom12.hospital.service;

import com.nhom12.hospital.dto.KhoaTruongKhoaDTO;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.repository.TruongKhoaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruongKhoaService {
    private final TruongKhoaRepository repository;

    public TruongKhoaService(TruongKhoaRepository repository) {
        this.repository = repository;
    }

    public List<KhoaTruongKhoaDTO> getAllKhoaWithTruongKhoa() {
        return repository.getAllKhoaWithTruongKhoa();
    }

    public List<NhanVien> getUngVienByKhoa(String maKhoa) {
        return repository.getUngVienByKhoa(maKhoa);
    }

    public void boNhiemTruongKhoa(String maKhoa, String maNhanVien) {
        repository.boNhiemTruongKhoa(maKhoa, maNhanVien);
    }

    public void mienNhiemTruongKhoa(String maKhoa) {
        repository.mienNhiemTruongKhoa(maKhoa);
    }
}

