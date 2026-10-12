package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.ChiTietHoaDon;
import com.nhom12.hospital.entity.HoaDon;
import com.nhom12.hospital.entity.BenhNhan;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.BenhNhanRepository;
import com.nhom12.hospital.repository.ChiTietHoaDonRepository;
import com.nhom12.hospital.repository.HoaDonRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;
    private final BenhNhanRepository benhNhanRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    public HoaDonService(HoaDonRepository hoaDonRepository,
                         ChiTietHoaDonRepository chiTietHoaDonRepository,
                         BenhNhanRepository benhNhanRepository,
                         TaiKhoanRepository taiKhoanRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
        this.benhNhanRepository = benhNhanRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    public List<Map<String, Object>> getAll(Long accountId) {
        List<HoaDon> list;
        if (isPatientAccount(accountId)) {
            list = findCurrentPatient(accountId)
                    .map(patient -> hoaDonRepository.findByMaBenhNhan(patient.getMaBenhNhan()))
                    .orElseGet(List::of);
        } else {
            list = hoaDonRepository.findAll();
        }
        List<Map<String, Object>> res = new ArrayList<>();

        for (HoaDon hd : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("billId", hd.getMaHoaDon());
            map.put("patientId", hd.getMaBenhNhan());
            map.put("total", hd.getTongTienDichVu());
            map.put("bhyt", hd.getBhytChiTra());
            map.put("pay", hd.getTongTienDichVu().subtract(hd.getBhytChiTra()));
            map.put("status", "DaThanhToan".equalsIgnoreCase(hd.getTrangThaiTT()) ? "PAID" : "UNPAID");
            map.put("method", hd.getHinhThucThanhToan());
            map.put("date", hd.getNgayLap());

            benhNhanRepository.findById(hd.getMaBenhNhan()).ifPresent(bn -> {
                map.put("patientName", bn.getHoTen());
                map.put("bhytCode", bn.getMaBHYT() != null ? bn.getMaBHYT() : "Không có (Khám dịch vụ)");
            });

            List<ChiTietHoaDon> details = chiTietHoaDonRepository.findByMaHoaDon(hd.getMaHoaDon());
            List<Map<String, Object>> items = new ArrayList<>();
            for (ChiTietHoaDon d : details) {
                Map<String, Object> it = new HashMap<>();
                it.put("name", d.getTenDichVu());
                it.put("type", d.getLoaiDichVu());
                it.put("qty", d.getSoLuong());
                it.put("price", d.getDonGia());
                it.put("total", d.getDonGia().multiply(java.math.BigDecimal.valueOf(d.getSoLuong())));
                items.add(it);
            }
            map.put("items", items);
            res.add(map);
        }

        return res;
    }

    public Map<String, Object> thanhToan(String id, Map<String, Object> req, Long accountId) {
        String method = req != null && req.containsKey("method") ? (String) req.get("method") : "TienMat";
        Optional<HoaDon> invoice = hoaDonRepository.findById(id);
        if (invoice.isEmpty()) {
            throw new NoSuchElementException("Không tìm thấy hóa đơn.");
        }
        Optional<BenhNhan> patient = findCurrentPatient(accountId);
        if (isPatientAccount(accountId) && (patient.isEmpty()
                || !patient.get().getMaBenhNhan().equals(invoice.get().getMaBenhNhan()))) {
            throw new IllegalStateException("Không có quyền truy cập hóa đơn này.");
        }
        HoaDon bill = invoice.get();
        bill.setTrangThaiTT("DaThanhToan");
        bill.setHinhThucThanhToan(method);
        bill.setNgayThanhToan(LocalDateTime.now());
        hoaDonRepository.save(bill);
        return Map.of("success", true, "message", "Xác nhận thanh toán hóa đơn thành công!");
    }

    private Optional<BenhNhan> findCurrentPatient(Long accountId) {
        if (accountId == null || !isPatientAccount(accountId)) {
            return Optional.empty();
        }
        return benhNhanRepository.findByMaTaiKhoan(accountId);
    }

    private boolean isPatientAccount(Long accountId) {
        return accountId != null && taiKhoanRepository.findById(accountId)
                .map(TaiKhoan::getVaiTro)
                .filter("BenhNhan"::equalsIgnoreCase)
                .isPresent();
    }
}

