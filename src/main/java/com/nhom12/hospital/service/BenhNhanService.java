package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.BenhNhan;
import com.nhom12.hospital.entity.DonThuoc;
import com.nhom12.hospital.entity.KetQuaCLS;
import com.nhom12.hospital.entity.PhieuKham;
import com.nhom12.hospital.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BenhNhanService {

    private final BenhNhanRepository benhNhanRepository;
    private final PhieuKhamRepository phieuKhamRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhoaRepository khoaRepository;
    private final DonThuocRepository donThuocRepository;
    private final ThuocRepository thuocRepository;
    private final KetQuaCLSRepository ketQuaCLSRepository;

    public BenhNhanService(BenhNhanRepository benhNhanRepository,
                           PhieuKhamRepository phieuKhamRepository,
                           NhanVienRepository nhanVienRepository,
                           KhoaRepository khoaRepository,
                           DonThuocRepository donThuocRepository,
                           ThuocRepository thuocRepository,
                           KetQuaCLSRepository ketQuaCLSRepository) {
        this.benhNhanRepository = benhNhanRepository;
        this.phieuKhamRepository = phieuKhamRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khoaRepository = khoaRepository;
        this.donThuocRepository = donThuocRepository;
        this.thuocRepository = thuocRepository;
        this.ketQuaCLSRepository = ketQuaCLSRepository;
    }

    public List<BenhNhan> getAll() {
        return benhNhanRepository.findAll();
    }

    public Optional<BenhNhan> getMyProfile(Long accountId) {
        if (accountId == null) return Optional.empty();
        return benhNhanRepository.findByMaTaiKhoan(accountId);
    }

    public BenhNhan createMyProfile(Long accountId, BenhNhan profile) {
        if (benhNhanRepository.findByMaTaiKhoan(accountId).isPresent()) {
            throw new IllegalStateException("Hồ sơ bệnh nhân đã được tạo.");
        }
        if (!hasRequiredProfileFields(profile)) {
            throw new IllegalArgumentException("Vui lòng nhập họ tên, ngày sinh, giới tính và số điện thoại.");
        }
        profile.setMaBenhNhan("BN2026" + String.format("%06d", benhNhanRepository.count() + 1));
        profile.setMaTaiKhoan(accountId);
        if (profile.getNgayTaoHoSo() == null) {
            profile.setNgayTaoHoSo(LocalDateTime.now());
        }
        return benhNhanRepository.save(profile);
    }

    public BenhNhan updateMyProfile(Long accountId, BenhNhan updated) {
        if (!hasRequiredProfileFields(updated)) {
            throw new IllegalArgumentException("Vui lòng nhập họ tên, ngày sinh, giới tính và số điện thoại.");
        }
        Optional<BenhNhan> existing = benhNhanRepository.findByMaTaiKhoan(accountId);
        if (existing.isEmpty()) {
            return null;
        }
        BenhNhan profile = existing.get();
        updateBenhNhanFields(profile, updated);
        return benhNhanRepository.save(profile);
    }

    public Optional<BenhNhan> getById(String id) {
        return benhNhanRepository.findById(id);
    }

    public BenhNhan create(BenhNhan benhNhan) {
        if (benhNhan.getMaBenhNhan() == null || benhNhan.getMaBenhNhan().trim().isEmpty()) {
            benhNhan.setMaBenhNhan("BN2026" + String.format("%06d", benhNhanRepository.count() + 1));
        }
        if (benhNhan.getNgayTaoHoSo() == null) {
            benhNhan.setNgayTaoHoSo(LocalDateTime.now());
        }
        if (benhNhan.getSoCCCD() != null && !benhNhan.getSoCCCD().trim().isEmpty()) {
            boolean exists = benhNhanRepository.findAll().stream()
                    .anyMatch(b -> benhNhan.getSoCCCD().equals(b.getSoCCCD()));
            if (exists) {
                throw new IllegalArgumentException("Số CCCD (" + benhNhan.getSoCCCD() + ") đã tồn tại trong hệ thống!");
            }
        }
        return benhNhanRepository.save(benhNhan);
    }

    public Optional<BenhNhan> update(String id, BenhNhan updated) {
        return benhNhanRepository.findById(id).map(bn -> {
            updateBenhNhanFields(bn, updated);
            return benhNhanRepository.save(bn);
        });
    }

    public List<Map<String, Object>> buildHistory(String id) {
        List<PhieuKham> list = phieuKhamRepository.findByMaBenhNhan(id);
        List<Map<String, Object>> res = new ArrayList<>();

        for (PhieuKham pk : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("ngayKham", pk.getNgayKham());
            map.put("chanDoan", pk.getChanDoan());

            nhanVienRepository.findById(pk.getMaBacSi()).ifPresent(nv -> map.put("bacSi", nv.getHoTen()));
            khoaRepository.findById(pk.getMaKhoa()).ifPresent(k -> map.put("khoa", k.getTenKhoa()));

            List<DonThuoc> dts = donThuocRepository.findByMaPhieuKham(pk.getMaPhieuKham());
            List<String> medNames = new ArrayList<>();
            for (DonThuoc dt : dts) {
                thuocRepository.findById(dt.getMaThuoc()).ifPresent(d -> 
                    medNames.add(d.getTenThuoc() + " (" + dt.getSoLuong() + " " + dt.getDonViTinh() + ")")
                );
            }
            map.put("donThuoc", String.join(", ", medNames));

            List<KetQuaCLS> kqs = ketQuaCLSRepository.findByMaPhieuKham(pk.getMaPhieuKham());
            List<String> clsNames = new ArrayList<>();
            for (KetQuaCLS kq : kqs) {
                clsNames.add(kq.getLoaiXetNghiem() + (kq.getKetLuan() != null ? ": " + kq.getKetLuan() : ""));
            }
            map.put("cls", String.join("; ", clsNames));

            res.add(map);
        }
        return res;
    }

    private void updateBenhNhanFields(BenhNhan target, BenhNhan source) {
        target.setHoTen(source.getHoTen());
        target.setNgaySinh(source.getNgaySinh());
        target.setGioiTinh(source.getGioiTinh());
        target.setSoDienThoai(source.getSoDienThoai());
        target.setSoCCCD(source.getSoCCCD());
        target.setMaBHYT(source.getMaBHYT());
        target.setNhomMau(source.getNhomMau());
        target.setDiaChi(source.getDiaChi());
        target.setEmail(source.getEmail());
        target.setNgheNghiep(source.getNgheNghiep());
        target.setNguoiLienHeKhanCap(source.getNguoiLienHeKhanCap());
        target.setSdtNguoiLienHe(source.getSdtNguoiLienHe());
        target.setQuanHeNguoiLienHe(source.getQuanHeNguoiLienHe());
        target.setTienSuBenhNen(source.getTienSuBenhNen());
        target.setTienSuDiUng(source.getTienSuDiUng());
    }

    private boolean hasRequiredProfileFields(BenhNhan profile) {
        return profile.getHoTen() != null && !profile.getHoTen().trim().isEmpty()
                && profile.getNgaySinh() != null
                && profile.getGioiTinh() != null && !profile.getGioiTinh().trim().isEmpty()
                && profile.getSoDienThoai() != null && !profile.getSoDienThoai().trim().isEmpty();
    }
}

