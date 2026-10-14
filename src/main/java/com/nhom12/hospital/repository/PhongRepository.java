package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.Phong;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PhongRepository {
    private final JdbcTemplate jdbcTemplate;

    public PhongRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Phong> findByKhoaAndLoaiPhong(String maKhoa, String loaiPhong) {
        StringBuilder sql = new StringBuilder("SELECT * FROM dbo.vw_Phong WHERE 1=1");
        
        if (maKhoa != null && !maKhoa.isEmpty() && !maKhoa.equalsIgnoreCase("ALL")) {
            sql.append(" AND MaKhoa = '").append(maKhoa).append("'");
        }
        if (loaiPhong != null && !loaiPhong.isEmpty() && !loaiPhong.equalsIgnoreCase("ALL")) {
            sql.append(" AND LoaiPhong = N'").append(loaiPhong).append("'");
        }
        
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Phong.class));
    }

    public void save(Phong phong) {
        jdbcTemplate.update(
                "EXEC dbo.sp_Phong_Save @MaPhong = ?, @TenPhong = ?, @LoaiPhong = ?, @MaKhoa = ?",
                phong.getMaPhong(),
                phong.getTenPhong(),
                phong.getLoaiPhong(),
                phong.getMaKhoa()
        );
    }

    public void toggleStatus(String maPhong) {
        jdbcTemplate.update("EXEC dbo.sp_Phong_ToggleStatus @MaPhong = ?", maPhong);
    }
}