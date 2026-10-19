package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.Phong;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PhongRepository {
    private final JdbcTemplate jdbcTemplate;

    public PhongRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Phong> findByKhoaAndLoaiPhong(String maKhoa, String loaiPhong) {
        boolean hasKhoa = maKhoa != null && !maKhoa.trim().isEmpty() && !maKhoa.equalsIgnoreCase("ALL");
        boolean hasLoai = loaiPhong != null && !loaiPhong.trim().isEmpty() && !loaiPhong.equalsIgnoreCase("ALL");
        // 1. Lọc cả Khoa và Loại phòng
        if (hasKhoa && hasLoai) {
            return jdbcTemplate.query(
                    "SELECT * FROM dbo.vw_Phong WHERE MaKhoa = ? AND LoaiPhong = ?",
                    new BeanPropertyRowMapper<>(Phong.class),
                    maKhoa, loaiPhong);
        }
        // 2. Chỉ lọc theo Khoa
        if (hasKhoa) {
            return jdbcTemplate.query(
                    "SELECT * FROM dbo.vw_Phong WHERE MaKhoa = ?",
                    new BeanPropertyRowMapper<>(Phong.class),
                    maKhoa);
        }
        // 3. Chỉ lọc theo Loại phòng
        if (hasLoai) {
            return jdbcTemplate.query(
                    "SELECT * FROM dbo.vw_Phong WHERE LoaiPhong = ?",
                    new BeanPropertyRowMapper<>(Phong.class),
                    loaiPhong);
        }
        // 4. Không lọc gì (lấy tất cả)
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_Phong",
                new BeanPropertyRowMapper<>(Phong.class));
    }

    public Optional<Phong> findById(String id) {
        List<Phong> list = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_Phong WHERE MaPhong = ?",
                new BeanPropertyRowMapper<>(Phong.class),
                id);
        return list.stream().findFirst();
    }

    public void save(Phong phong) {
        jdbcTemplate.update(
                "EXEC dbo.sp_Phong_Save @MaPhong = ?, @TenPhong = ?, @LoaiPhong = ?, @MaKhoa = ?",
                phong.getMaPhong(),
                phong.getTenPhong(),
                phong.getLoaiPhong(),
                phong.getMaKhoa());
    }

    public void toggleStatus(String maPhong) {
        jdbcTemplate.update("EXEC dbo.sp_Phong_ToggleStatus @MaPhong = ?", maPhong);
    }
}