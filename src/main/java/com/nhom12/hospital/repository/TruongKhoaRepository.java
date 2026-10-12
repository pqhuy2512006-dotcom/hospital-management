package com.nhom12.hospital.repository;

import com.nhom12.hospital.dto.KhoaTruongKhoaDTO;
import com.nhom12.hospital.entity.NhanVien;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TruongKhoaRepository {
    private final JdbcTemplate jdbcTemplate;

    public TruongKhoaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<KhoaTruongKhoaDTO> getAllKhoaWithTruongKhoa() {
        String sql = "SELECT k.MaKhoa, k.TenKhoa, k.MaTruongKhoa, nv.HoTen as tenTruongKhoa, nv.TrinhDoChuyenMon as hocViTruongKhoa " +
                     "FROM Khoa k " +
                     "LEFT JOIN NhanVien nv ON k.MaTruongKhoa = nv.MaNhanVien";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(KhoaTruongKhoaDTO.class));
    }

    public List<NhanVien> getUngVienByKhoa(String maKhoa) {
        String sql = "SELECT * FROM NhanVien WHERE MaKhoa = ?  AND TrangThai = 'DangLamViec'";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(NhanVien.class), maKhoa);
    }

    public void boNhiemTruongKhoa(String maKhoa, String maNhanVien) {
        // Update Khoa
        jdbcTemplate.update("UPDATE Khoa SET MaTruongKhoa = ? WHERE MaKhoa = ?", maNhanVien, maKhoa);
        
        // Check if there's an active term, end it
        jdbcTemplate.update("UPDATE LichSuTruongKhoa SET NgayMienNhiem = GETDATE() WHERE MaKhoa = ? AND NgayMienNhiem IS NULL", maKhoa);
        
        // Insert new history
        jdbcTemplate.update("INSERT INTO LichSuTruongKhoa (MaKhoa, MaNhanVien, NgayBoNhiem) VALUES (?, ?, GETDATE())", maKhoa, maNhanVien);
    }

    public void mienNhiemTruongKhoa(String maKhoa) {
        jdbcTemplate.update("UPDATE Khoa SET MaTruongKhoa = NULL WHERE MaKhoa = ?", maKhoa);
        jdbcTemplate.update("UPDATE LichSuTruongKhoa SET NgayMienNhiem = GETDATE() WHERE MaKhoa = ? AND NgayMienNhiem IS NULL", maKhoa);
    }
}

