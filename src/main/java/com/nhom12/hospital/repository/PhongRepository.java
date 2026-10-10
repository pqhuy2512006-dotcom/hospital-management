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
        StringBuilder sql = new StringBuilder("SELECT * FROM Phong WHERE TrangThai = 1");
        
        if (maKhoa != null && !maKhoa.isEmpty()) {
            sql.append(" AND MaKhoa = '").append(maKhoa).append("'");
        }
        if (loaiPhong != null && !loaiPhong.isEmpty()) {
            sql.append(" AND LoaiPhong = N'").append(loaiPhong).append("'");
        }
        
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Phong.class));
    }
}

