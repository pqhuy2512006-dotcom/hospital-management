package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.LichTruc;
import com.nhom12.hospital.repository.LichTrucRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LichTrucService {

    private final LichTrucRepository lichTrucRepository;
    private final NhanVienRepository nhanVienRepository;

    public LichTrucService(LichTrucRepository lichTrucRepository, NhanVienRepository nhanVienRepository) {
        this.lichTrucRepository = lichTrucRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

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
                map.put("chuyenKhoa", nv.getMaKhoa());
            });
            res.add(map);
        }
        return res;
    }

    public LichTruc create(LichTruc lt) {
        if (lt.getMaLichTruc() == null || lt.getMaLichTruc().trim().isEmpty()) {
            lt.setMaLichTruc("LT" + (System.currentTimeMillis() % 10000000));
        }
        return lichTrucRepository.save(lt);
    }

    public Map<String, Object> delete(String id) {
        if (lichTrucRepository.existsById(id)) {
            lichTrucRepository.deleteById(id);
            return Map.of("success", true, "message", "Đã xóa phân công lịch trực!");
        }
        throw new NoSuchElementException("Không tìm thấy lịch trực.");
    }
}

