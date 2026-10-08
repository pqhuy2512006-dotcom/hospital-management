package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.NoiTru;
import com.nhom12.hospital.repository.BenhNhanRepository;
import com.nhom12.hospital.repository.GiuongBenhRepository;
import com.nhom12.hospital.repository.NoiTruRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class NoiTruService {

    private final NoiTruRepository noiTruRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final GiuongBenhRepository giuongBenhRepository;

    public NoiTruService(NoiTruRepository noiTruRepository,
                         BenhNhanRepository benhNhanRepository,
                         GiuongBenhRepository giuongBenhRepository) {
        this.noiTruRepository = noiTruRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.giuongBenhRepository = giuongBenhRepository;
    }

    public List<Map<String, Object>> getAll() {
        List<NoiTru> list = noiTruRepository.findAll();
        List<Map<String, Object>> res = new ArrayList<>();
        for (NoiTru nt : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("maNoiTru", nt.getMaNoiTru());
            map.put("maBenhNhan", nt.getMaBenhNhan());
            map.put("maGiuong", nt.getMaGiuong());
            map.put("maPhieuKham", nt.getMaPhieuKham());
            map.put("ngayNhapVien", nt.getNgayNhapVien());
            map.put("ngayXuatVien", nt.getNgayXuatVien());
            map.put("trangThai", nt.getTrangThai());

            benhNhanRepository.findById(nt.getMaBenhNhan()).ifPresent(bn -> {
                map.put("tenBenhNhan", bn.getHoTen());
                map.put("gioiTinh", bn.getGioiTinh());
                map.put("ngaySinh", bn.getNgaySinh());
                map.put("maBHYT", bn.getMaBHYT());
                map.put("soDienThoai", bn.getSoDienThoai());
            });

            giuongBenhRepository.findById(nt.getMaGiuong()).ifPresent(g -> {
                map.put("soGiuong", g.getSoGiuong());
                map.put("maKhoa", g.getMaKhoa());
                map.put("donGiaNgay", g.getDonGiaNgay());
            });

            res.add(map);
        }
        return res;
    }

    public NoiTru nhapVien(NoiTru noiTru) {
        if (noiTru.getMaNoiTru() == null || noiTru.getMaNoiTru().trim().isEmpty()) {
            noiTru.setMaNoiTru("NT" + System.currentTimeMillis() % 10000000);
        }
        if (noiTru.getNgayNhapVien() == null) {
            noiTru.setNgayNhapVien(LocalDateTime.now());
        }
        if (noiTru.getTrangThai() == null) {
            noiTru.setTrangThai("DangNam");
        }
        if (noiTru.getMaPhieuKham() == null) {
            noiTru.setMaPhieuKham("PK2026000001");
        }

        // Cập nhật trạng thái giường
        giuongBenhRepository.findById(noiTru.getMaGiuong()).ifPresent(g -> {
            g.setTrangThai("DangSuDung");
            giuongBenhRepository.save(g);
        });

        return noiTruRepository.save(noiTru);
    }

    public Optional<NoiTru> xuatVien(String id) {
        return noiTruRepository.findById(id).map(nt -> {
            nt.setTrangThai("DaXuatVien");
            nt.setNgayXuatVien(LocalDateTime.now());
            noiTruRepository.save(nt);

            // Trả giường về 'Trong'
            giuongBenhRepository.findById(nt.getMaGiuong()).ifPresent(g -> {
                g.setTrangThai("Trong");
                giuongBenhRepository.save(g);
            });

            return nt;
        });
    }
}

