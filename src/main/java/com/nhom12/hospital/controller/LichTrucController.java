package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.LichTruc;
import com.nhom12.hospital.repository.LichTrucRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/lichtruc")
@CrossOrigin(origins = "*")
public class LichTrucController {

    private final LichTrucRepository lichTrucRepository;
    private final NhanVienRepository nhanVienRepository;

    public LichTrucController(LichTrucRepository lichTrucRepository, NhanVienRepository nhanVienRepository) {
        this.lichTrucRepository = lichTrucRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        List<LichTruc> list = lichTrucRepository.findAll();
        List<Map<String, Object>> res = new ArrayList<>();
        for (LichTruc lt : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", lt.getMaLichTruc());
            map.put("ngay", lt.getNgay());
            map.put("ca", lt.getCa());
            map.put("maKhoa", lt.getMaKhoa());
            map.put("maNhanVien", lt.getMaNhanVien());
            nhanVienRepository.findById(lt.getMaNhanVien()).ifPresent(nv -> {
                map.put("tenNhanVien", nv.getHoTen());
                map.put("chuyenKhoa", nv.getChuyenKhoa());
            });
            res.add(map);
        }
        return res;
    }
}
