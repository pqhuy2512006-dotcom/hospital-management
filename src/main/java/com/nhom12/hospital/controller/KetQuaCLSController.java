package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.ChiTietKetQuaCLS;
import com.nhom12.hospital.entity.KetQuaCLS;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.repository.BenhNhanRepository;
import com.nhom12.hospital.repository.ChiTietKetQuaCLSRepository;
import com.nhom12.hospital.repository.KetQuaCLSRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/cls")
@CrossOrigin(origins = "*")
public class KetQuaCLSController {

    private final KetQuaCLSRepository ketQuaCLSRepository;
    private final ChiTietKetQuaCLSRepository chiTietKetQuaCLSRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final NhanVienRepository nhanVienRepository;

    public KetQuaCLSController(KetQuaCLSRepository ketQuaCLSRepository,
                               ChiTietKetQuaCLSRepository chiTietKetQuaCLSRepository,
                               BenhNhanRepository benhNhanRepository,
                               NhanVienRepository nhanVienRepository) {
        this.ketQuaCLSRepository = ketQuaCLSRepository;
        this.chiTietKetQuaCLSRepository = chiTietKetQuaCLSRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return mapResults(ketQuaCLSRepository.findAll());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyResults(HttpServletRequest request) {
        Long accountId = (Long) request.getAttribute(SessionAttributes.ACCOUNT_ID);
        Optional<NhanVien> doctor = accountId == null ? Optional.empty()
                : nhanVienRepository.findByMaTaiKhoan(accountId)
                        .filter(employee -> "BacSi".equalsIgnoreCase(employee.getVaiTro()));
        if (doctor.isEmpty()) {
            return ResponseEntity.status(403).body(Map.of("message", "Không tìm thấy hồ sơ bác sĩ."));
        }
        return ResponseEntity.ok(mapResults(ketQuaCLSRepository.findByMaBacSiDoc(doctor.get().getMaNhanVien())));
    }

    private List<Map<String, Object>> mapResults(List<KetQuaCLS> list) {
        List<Map<String, Object>> res = new ArrayList<>();

        for (KetQuaCLS kq : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", kq.getMaKetQua());
            map.put("patientId", kq.getMaBenhNhan());
            map.put("service", kq.getLoaiXetNghiem());
            map.put("conclusion", kq.getKetLuan());
            map.put("status", kq.getKetLuan() != null && !kq.getKetLuan().trim().isEmpty() ? "COMPLETED" : "PENDING");
            map.put("dept", "Khoa Cận Lâm Sàng");

            benhNhanRepository.findById(kq.getMaBenhNhan()).ifPresent(bn -> {
                map.put("patientName", bn.getHoTen());
            });

            nhanVienRepository.findById(kq.getMaBacSiDoc() != null ? kq.getMaBacSiDoc() : kq.getMaKTV()).ifPresent(nv -> {
                map.put("doctor", nv.getHoTen());
            });

            List<ChiTietKetQuaCLS> details = chiTietKetQuaCLSRepository.findByMaKetQua(kq.getMaKetQua());
            List<Map<String, Object>> params = new ArrayList<>();
            for (ChiTietKetQuaCLS d : details) {
                Map<String, Object> p = new HashMap<>();
                p.put("name", d.getTenChiSo());
                p.put("val", d.getGiaTri());
                p.put("unit", d.getDonVi());
                p.put("ref", d.getKhoangThamChieu());
                p.put("eval", d.getDanhGia());

                // parse refMin/refMax if possible
                if (d.getKhoangThamChieu() != null && d.getKhoangThamChieu().contains("-")) {
                    String[] parts = d.getKhoangThamChieu().split("-");
                    try {
                        p.put("refMin", Double.parseDouble(parts[0].trim()));
                        p.put("refMax", Double.parseDouble(parts[1].trim()));
                    } catch (Exception ignored) {
                        p.put("refMin", null);
                        p.put("refMax", null);
                    }
                } else {
                    p.put("refMin", null);
                    p.put("refMax", null);
                }
                params.add(p);
            }
            map.put("params", params);
            res.add(map);
        }

        return res;
    }

    @PutMapping("/{id}/ketqua")
    @Transactional
    public ResponseEntity<?> capNhatKetQua(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        return ketQuaCLSRepository.findById(id).map(kq -> {
            String conclusion = (String) payload.get("conclusion");
            kq.setKetLuan(conclusion);
            ketQuaCLSRepository.save(kq);

            List<Map<String, Object>> params = (List<Map<String, Object>>) payload.get("params");
            if (params != null) {
                for (Map<String, Object> p : params) {
                    String name = (String) p.get("name");
                    String val = (String) p.get("val");
                    String eval = (String) p.get("eval");

                    List<ChiTietKetQuaCLS> details = chiTietKetQuaCLSRepository.findByMaKetQua(id);
                    Optional<ChiTietKetQuaCLS> targetOpt = details.stream().filter(d -> d.getTenChiSo().equals(name)).findFirst();
                    if (targetOpt.isPresent()) {
                        ChiTietKetQuaCLS target = targetOpt.get();
                        target.setGiaTri(val);
                        if (eval != null) target.setDanhGia(eval);
                        chiTietKetQuaCLSRepository.save(target);
                    } else {
                        ChiTietKetQuaCLS newDetail = new ChiTietKetQuaCLS();
                        newDetail.setMaChiTietXN("CT" + System.currentTimeMillis() % 1000000);
                        newDetail.setMaKetQua(id);
                        newDetail.setTenChiSo(name);
                        newDetail.setGiaTri(val);
                        newDetail.setDonVi((String) p.getOrDefault("unit", ""));
                        newDetail.setKhoangThamChieu((String) p.getOrDefault("ref", ""));
                        newDetail.setDanhGia(eval != null ? eval : "BinhThuong");
                        chiTietKetQuaCLSRepository.save(newDetail);
                    }
                }
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "Đã lưu và trả kết quả xét nghiệm thành công!"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
