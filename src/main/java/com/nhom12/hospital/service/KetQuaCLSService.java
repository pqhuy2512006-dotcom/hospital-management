package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.ChiTietKetQuaCLS;
import com.nhom12.hospital.entity.KetQuaCLS;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.repository.BenhNhanRepository;
import com.nhom12.hospital.repository.ChiTietKetQuaCLSRepository;
import com.nhom12.hospital.repository.KetQuaCLSRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class KetQuaCLSService {

    private final KetQuaCLSRepository ketQuaCLSRepository;
    private final ChiTietKetQuaCLSRepository chiTietKetQuaCLSRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final NhanVienRepository nhanVienRepository;

    public KetQuaCLSService(KetQuaCLSRepository ketQuaCLSRepository,
                            ChiTietKetQuaCLSRepository chiTietKetQuaCLSRepository,
                            BenhNhanRepository benhNhanRepository,
                            NhanVienRepository nhanVienRepository) {
        this.ketQuaCLSRepository = ketQuaCLSRepository;
        this.chiTietKetQuaCLSRepository = chiTietKetQuaCLSRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    public List<Map<String, Object>> getAll() {
        return mapResults(ketQuaCLSRepository.findAll());
    }

    public List<Map<String, Object>> getMyResults(Long accountId) {
        if (accountId == null) {
            throw new IllegalStateException("Không tìm thấy hồ sơ bác sĩ.");
        }
        Optional<NhanVien> doctor = nhanVienRepository.findByMaTaiKhoan(accountId)
                .filter(employee -> "BacSi".equalsIgnoreCase(employee.getVaiTro()));
        if (doctor.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy hồ sơ bác sĩ.");
        }
        return mapResults(ketQuaCLSRepository.findByMaBacSiDoc(doctor.get().getMaNhanVien()));
    }

    private List<Map<String, Object>> mapResults(List<KetQuaCLS> list) {
        List<Map<String, Object>> res = new ArrayList<>();

        for (KetQuaCLS kq : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", kq.getMaKetQua());
            map.put("patientId", kq.getMaBenhNhan());
            map.put("service", kq.getLoaiXetNghiem());
            map.put("conclusion", kq.getKetLuan());
            map.put("hinhAnhFile", kq.getHinhAnhFile());
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

    @Transactional
    public Map<String, Object> capNhatKetQua(String id, Map<String, Object> payload) {
        Optional<KetQuaCLS> kqOpt = ketQuaCLSRepository.findById(id);
        if (kqOpt.isEmpty()) {
            throw new NoSuchElementException("Không tìm thấy kết quả cận lâm sàng!");
        }
        KetQuaCLS kq = kqOpt.get();
        String conclusion = (String) payload.get("conclusion");
        String hinhAnh = (String) payload.get("hinhAnh");
        kq.setKetLuan(conclusion);
        if (hinhAnh != null) kq.setHinhAnhFile(hinhAnh);
        ketQuaCLSRepository.save(kq);

        @SuppressWarnings("unchecked")
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
        return Map.of("success", true, "message", "Đã lưu và trả kết quả xét nghiệm thành công!");
    }
}

