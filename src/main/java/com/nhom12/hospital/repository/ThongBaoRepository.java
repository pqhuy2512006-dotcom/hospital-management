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
                "EXEC dbo.sp_ThongBao_Create @MaTaiKhoan=?, @TieuDe=?, @NoiDung=?, @LoaiThongBao=?",
                maTaiKhoan, tieuDe, noiDung, loaiThongBao
        );
    }
    
        // Phát thông báo hàng loạt thông qua Stored Procedure
    public int broadcast(String tieuDe, String noiDung, String loaiThongBao, String targetRole) {
        Integer count = jdbcTemplate.queryForObject(
                "EXEC dbo.sp_ThongBao_Broadcast @TieuDe=?, @NoiDung=?, @LoaiThongBao=?, @TargetRole=?",
                Integer.class,
                tieuDe, noiDung, loaiThongBao, targetRole
        );
        return count != null ? count : 0;
    }

    // Lấy lịch sử thông báo thông qua VIEW dbo.vw_ThongBao
    public java.util.List<java.util.Map<String, Object>> getBroadcastHistory() {
        String sql = "SELECT TOP 30 TieuDe, NoiDung, LoaiThongBao, MIN(NgayTao) AS NgayTao, COUNT(*) AS SoLuongNguoiNhan " +
                     "FROM dbo.vw_ThongBao " +
                     "GROUP BY TieuDe, NoiDung, LoaiThongBao " +
                     "ORDER BY MIN(NgayTao) DESC";
        return jdbcTemplate.queryForList(sql);
    }
}
