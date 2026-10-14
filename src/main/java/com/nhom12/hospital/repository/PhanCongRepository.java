package com.nhom12.hospital.repository;

import com.nhom12.hospital.dto.LichSuPhanCongDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class PhanCongRepository {
    private final JdbcTemplate jdbcTemplate;

    public PhanCongRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LichSuPhanCongDTO> getLichSuByNhanVien(String maNhanVien) {
        String sql = "SELECT ls.MaPhanCong, ls.MaNhanVien, nv.HoTen, ls.MaKhoa, k.TenKhoa, " +
                     "ls.ChucVu, ls.NgayBatDau, ls.NgayKetThuc, ls.LyDo, ls.TrangThai " +
                     "FROM LichSuPhanCongKhoa ls " +
                     "JOIN NhanVien nv ON ls.MaNhanVien = nv.MaNhanVien " +
                     "JOIN Khoa k ON ls.MaKhoa = k.MaKhoa " +
                     "WHERE ls.MaNhanVien = ? " +
                     "ORDER BY ls.NgayBatDau DESC, ls.MaPhanCong DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(LichSuPhanCongDTO.class), maNhanVien);
    }

    public void dongPhanCongHienTai(String maNhanVien, LocalDate ngayKetThuc) {
        String sql = "UPDATE LichSuPhanCongKhoa " +
                     "SET NgayKetThuc = ?, TrangThai = N'DaChuyenKhoa' " +
                     "WHERE MaNhanVien = ? AND NgayKetThuc IS NULL";
        jdbcTemplate.update(sql, ngayKetThuc, maNhanVien);
    }

    public void themPhanCongMoi(String maNhanVien, String maKhoa, String chucVu, LocalDate ngayBatDau, String lyDo) {
        String sql = "INSERT INTO LichSuPhanCongKhoa (MaNhanVien, MaKhoa, ChucVu, NgayBatDau, TrangThai, LyDo) " +
                     "VALUES (?, ?, ?, ?, N'DangCongTac', ?)";
        jdbcTemplate.update(sql, maNhanVien, maKhoa, chucVu, ngayBatDau, lyDo);
    }

    public void capNhatKhoaNhanVien(String maNhanVien, String maKhoa) {
        String sql = "UPDATE NhanVien SET MaKhoa = ? WHERE MaNhanVien = ?";
        jdbcTemplate.update(sql, maKhoa, maNhanVien);
    }
}

