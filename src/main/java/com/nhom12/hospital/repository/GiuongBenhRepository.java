package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.GiuongBenh;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GiuongBenhRepository {

    private static final BeanPropertyRowMapper<GiuongBenh> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(GiuongBenh.class);

    private final JdbcTemplate jdbcTemplate;

    public GiuongBenhRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<GiuongBenh> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_GiuongBenh", ROW_MAPPER);
    }

    public List<GiuongBenh> findByMaKhoa(String maKhoa) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_GiuongBenh WHERE MaKhoa = ? ORDER BY SoGiuong",
                ROW_MAPPER,
                maKhoa
        );
    }

    public List<GiuongBenh> findByMaKhoaAndTrangThai(String maKhoa, String trangThai) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_GiuongBenh WHERE MaKhoa = ? AND TrangThai = ? ORDER BY SoGiuong",
                ROW_MAPPER,
                maKhoa, trangThai
        );
    }

    public List<GiuongBenh> findByTrangThai(String trangThai) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_GiuongBenh WHERE TrangThai = ? ORDER BY SoGiuong",
                ROW_MAPPER,
                trangThai
        );
    }

    public Optional<GiuongBenh> findById(String id) {
        List<GiuongBenh> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_GiuongBenh WHERE MaGiuong = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public void save(GiuongBenh entity) {
        jdbcTemplate.update(
                "EXEC dbo.sp_GiuongBenh_Save @MaGiuong = ?, @MaPhong = ?, @SoGiuong = ?, @DonGiaNgay = ?",
                entity.getMaGiuong(),
                entity.getMaPhong(),
                entity.getSoGiuong(),
                entity.getDonGiaNgay()
        );
    }
    
        public void toggleStatus(String maGiuong) {
        jdbcTemplate.update("EXEC dbo.sp_GiuongBenh_ToggleStatus @MaGiuong = ?", maGiuong);
    }
}