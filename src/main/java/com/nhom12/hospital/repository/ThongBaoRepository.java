package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.ThongBao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ThongBaoRepository {

    private final JdbcTemplate jdbcTemplate;

    public ThongBaoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ThongBao> findByMaTaiKhoan(Long maTaiKhoan) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_ThongBao WHERE MaTaiKhoan = ? ORDER BY NgayTao DESC",
                new BeanPropertyRowMapper<>(ThongBao.class),
                maTaiKhoan
        );
    }

    public void markAsRead(Integer maThongBao, Long maTaiKhoan) {
        jdbcTemplate.update(
                "EXEC dbo.sp_ThongBao_MarkRead @MaThongBao=?, @MaTaiKhoan=?",
                maThongBao, maTaiKhoan
        );
    }

    public void create(Long maTaiKhoan, String tieuDe, String noiDung, String loaiThongBao) {
        jdbcTemplate.update(
                "INSERT INTO ThongBao (MaTaiKhoan, TieuDe, NoiDung, LoaiThongBao, DaDoc, NgayTao) VALUES (?, ?, ?, ?, 0, GETDATE())",
                maTaiKhoan, tieuDe, noiDung, loaiThongBao
        );
    }
}
