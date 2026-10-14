package com.nhom12.hospital.service;

import com.nhom12.hospital.dto.LichSuPhanCongDTO;
import com.nhom12.hospital.dto.PhanCongRequest;
import com.nhom12.hospital.entity.Khoa;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.repository.KhoaRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.PhanCongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PhanCongService {
    private final PhanCongRepository phanCongRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhoaRepository khoaRepository;

    public PhanCongService(PhanCongRepository phanCongRepository,
                           NhanVienRepository nhanVienRepository,
                           KhoaRepository khoaRepository) {
        this.phanCongRepository = phanCongRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khoaRepository = khoaRepository;
    }

    public List<LichSuPhanCongDTO> getLichSuByNhanVien(String maNhanVien) {
        return phanCongRepository.getLichSuByNhanVien(maNhanVien);
    }

    @Transactional
    public void phanCongHoacDieuChuyen(PhanCongRequest req) {
        if (req.getMaNhanVien() == null || req.getMaNhanVien().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống!");
        }
        if (req.getMaKhoa() == null || req.getMaKhoa().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn khoa tiếp nhận!");
        }

        NhanVien nv = nhanVienRepository.findById(req.getMaNhanVien())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên: " + req.getMaNhanVien()));

        Khoa khoa = khoaRepository.findById(req.getMaKhoa())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoa: " + req.getMaKhoa()));

        // Kiểm tra 1: Không được điều chuyển vào chính khoa đang công tác
        if (nv.getMaKhoa() != null && req.getMaKhoa().equalsIgnoreCase(nv.getMaKhoa())) {
            throw new IllegalArgumentException("Nhân viên " + nv.getHoTen() + " hiện đã thuộc " + khoa.getTenKhoa() + " rồi!");
        }

        // Kiểm tra 2: Ràng buộc chức vụ / vai trò và Khoa
        kiemTraChucVuVaKhoa(nv, khoa);

        LocalDate ngayBatDau = req.getNgayBatDau() != null ? req.getNgayBatDau() : LocalDate.now();

        // Đóng phân công cũ nếu có
        phanCongRepository.dongPhanCongHienTai(nv.getMaNhanVien(), ngayBatDau);

        // Tạo phân công mới
        String chucVu = (nv.getTrinhDoChuyenMon() != null && !nv.getTrinhDoChuyenMon().trim().isEmpty())
                        ? nv.getTrinhDoChuyenMon() 
                        : nv.getVaiTro();
        String lyDo = (req.getLyDo() != null && !req.getLyDo().trim().isEmpty())
                      ? req.getLyDo().trim() 
                      : (nv.getMaKhoa() == null ? "Phân công công tác ban đầu" : "Điều chuyển công tác theo quyết định");

        phanCongRepository.themPhanCongMoi(nv.getMaNhanVien(), req.getMaKhoa(), chucVu, ngayBatDau, lyDo);

        // Cập nhật mã khoa hiện tại trong bảng NhanVien
        phanCongRepository.capNhatKhoaNhanVien(nv.getMaNhanVien(), req.getMaKhoa());
    }

    private void kiemTraChucVuVaKhoa(NhanVien nv, Khoa khoa) {
        String vaiTro = nv.getVaiTro() != null ? nv.getVaiTro() : "";
        String maKhoa = khoa.getMaKhoa() != null ? khoa.getMaKhoa().toUpperCase() : "";
        String tenKhoa = khoa.getTenKhoa() != null ? khoa.getTenKhoa().toLowerCase() : "";

        // Kỹ thuật viên (KTV) chỉ được vào Khoa Cận Lâm Sàng / Xét nghiệm
        if ("KTV".equalsIgnoreCase(vaiTro)) {
            boolean isCLS = maKhoa.contains("CLS") || tenKhoa.contains("cận lâm sàng") || tenKhoa.contains("xét nghiệm") || tenKhoa.contains("chẩn đoán");
            if (!isCLS) {
                throw new IllegalArgumentException("Kỹ thuật viên chỉ có thể phân công vào Khoa Cận Lâm Sàng / Xét nghiệm!");
            }
        }

        // Dược sĩ (DuocSi) thường thuộc Khoa Dược hoặc Khoa Khám Bệnh
        if ("DuocSi".equalsIgnoreCase(vaiTro)) {
            boolean isDuoc = maKhoa.contains("DUOC") || tenKhoa.contains("dược") || maKhoa.equals("KKB");
            if (!isDuoc) {
                throw new IllegalArgumentException("Dược sĩ chỉ có thể phân công vào Khoa Dược hoặc Khoa Khám Bệnh (Quầy thuốc)!");
            }
        }

        // Thu ngân (ThuNgan) và Lễ tân (LeTan) chỉ thuộc Khoa Khám Bệnh hoặc phòng ban hành chính
        if ("ThuNgan".equalsIgnoreCase(vaiTro) || "LeTan".equalsIgnoreCase(vaiTro)) {
            boolean isTiepNhan = maKhoa.equals("KKB") || tenKhoa.contains("khám bệnh") || tenKhoa.contains("tiếp đón") || tenKhoa.contains("tài chính") || tenKhoa.contains("hành chính");
            if (!isTiepNhan) {
                throw new IllegalArgumentException("Nhân viên vị trí " + vaiTro + " chỉ có thể phân công vào Khoa Khám Bệnh hoặc phòng ban hành chính!");
            }
        }
    }
}

