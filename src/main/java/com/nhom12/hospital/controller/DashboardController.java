package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.ChiTietHoaDon;
import com.nhom12.hospital.entity.GiuongBenh;
import com.nhom12.hospital.entity.HoaDon;
import com.nhom12.hospital.repository.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final BenhNhanRepository benhNhanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final LichHenRepository lichHenRepository;
    private final GiuongBenhRepository giuongBenhRepository;
    private final NoiTruRepository noiTruRepository;
    private final HoaDonRepository hoaDonRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;

    public DashboardController(BenhNhanRepository benhNhanRepository,
                               NhanVienRepository nhanVienRepository,
                               LichHenRepository lichHenRepository,
                               GiuongBenhRepository giuongBenhRepository,
                               NoiTruRepository noiTruRepository,
                               HoaDonRepository hoaDonRepository,
                               ChiTietHoaDonRepository chiTietHoaDonRepository) {
        this.benhNhanRepository = benhNhanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.lichHenRepository = lichHenRepository;
        this.giuongBenhRepository = giuongBenhRepository;
        this.noiTruRepository = noiTruRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> res = new HashMap<>();

        long totalPatients = benhNhanRepository.count();
        long totalStaff = nhanVienRepository.count();
        long totalAppointments = lichHenRepository.count();
        long totalInpatients = noiTruRepository.findByTrangThai("DangNam").size();

        List<GiuongBenh> allBeds = giuongBenhRepository.findAll();
        long totalBeds = allBeds.size();
        long availableBeds = allBeds.stream().filter(b -> "Trong".equalsIgnoreCase(b.getTrangThai())).count();
        long occupiedBeds = totalBeds - availableBeds;
        double occupancyRate = totalBeds > 0 ? ((double) occupiedBeds / totalBeds) * 100 : 0.0;

        List<HoaDon> allInvoices = hoaDonRepository.findAll();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (HoaDon hd : allInvoices) {
            if ("DaThanhToan".equalsIgnoreCase(hd.getTrangThaiTT())) {
                BigDecimal pay = hd.getTongTienDichVu().subtract(hd.getBhytChiTra() != null ? hd.getBhytChiTra() : BigDecimal.ZERO);
                totalRevenue = totalRevenue.add(pay);
            }
        }

        // Doanh thu theo loại dịch vụ
        List<ChiTietHoaDon> allDetails = chiTietHoaDonRepository.findAll();
        BigDecimal revKham = BigDecimal.ZERO;
        BigDecimal revCLS = BigDecimal.ZERO;
        BigDecimal revThuoc = BigDecimal.ZERO;
        BigDecimal revGiuong = BigDecimal.ZERO;

        for (ChiTietHoaDon d : allDetails) {
            BigDecimal lineTotal = d.getDonGia().multiply(BigDecimal.valueOf(d.getSoLuong()));
            if ("Kham".equalsIgnoreCase(d.getLoaiDichVu())) revKham = revKham.add(lineTotal);
            else if ("CanLamSang".equalsIgnoreCase(d.getLoaiDichVu())) revCLS = revCLS.add(lineTotal);
            else if ("Thuoc".equalsIgnoreCase(d.getLoaiDichVu())) revThuoc = revThuoc.add(lineTotal);
            else if ("Giuong".equalsIgnoreCase(d.getLoaiDichVu())) revGiuong = revGiuong.add(lineTotal);
        }

        res.put("totalPatients", totalPatients);
        res.put("totalStaff", totalStaff);
        res.put("totalAppointments", totalAppointments);
        res.put("totalInpatients", totalInpatients);
        res.put("totalBeds", totalBeds);
        res.put("availableBeds", availableBeds);
        res.put("occupiedBeds", occupiedBeds);
        res.put("occupancyRate", Math.round(occupancyRate * 10.0) / 10.0);
        res.put("totalRevenue", totalRevenue);
        res.put("revKham", revKham);
        res.put("revCLS", revCLS);
        res.put("revThuoc", revThuoc);
        res.put("revGiuong", revGiuong);

        return res;
    }
}
