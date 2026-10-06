package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.ChucNang;
import com.nhom12.hospital.entity.PhanQuyenChiTiet;
import com.nhom12.hospital.entity.VaiTro;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PhanQuyenRepository {

    private static final BeanPropertyRowMapper<VaiTro> ROLE_MAPPER =
            BeanPropertyRowMapper.newInstance(VaiTro.class);
    private static final BeanPropertyRowMapper<ChucNang> FUNCTION_MAPPER =
            BeanPropertyRowMapper.newInstance(ChucNang.class);
    private static final BeanPropertyRowMapper<PhanQuyenChiTiet> DETAIL_MAPPER =
            BeanPropertyRowMapper.newInstance(PhanQuyenChiTiet.class);

    private final JdbcTemplate jdbcTemplate;

    public PhanQuyenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VaiTro> findAllRoles() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_VaiTro ORDER BY MaVaiTro", ROLE_MAPPER);
    }

    public List<ChucNang> findAllFunctions() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_ChucNang ORDER BY ThuTuHienThi, MaChucNang", FUNCTION_MAPPER);
    }

    public List<PhanQuyenChiTiet> findAllPermissions() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_PhanQuyenChiTiet ORDER BY MaVaiTro, MaPhanHe, MaChucNang", DETAIL_MAPPER);
    }

    public List<PhanQuyenChiTiet> findByRole(String role) {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_PhanQuyenChiTiet WHERE MaVaiTro = ? ORDER BY MaPhanHe, MaChucNang", DETAIL_MAPPER, role);
    }

    public void assignPermission(String maVaiTro, String maChucNang, Boolean quyenXem, Boolean quyenThem, Boolean quyenSua, Boolean quyenXoa, String ghiChu) {
        jdbcTemplate.update(
                "EXEC dbo.usp_PhanQuyen_GanChucNang @MaVaiTro=?, @MaChucNang=?, @QuyenXem=?, @QuyenThem=?, @QuyenSua=?, @QuyenXoa=?, @GhiChu=?",
                maVaiTro,
                maChucNang,
                quyenXem != null && quyenXem ? 1 : 0,
                quyenThem != null && quyenThem ? 1 : 0,
                quyenSua != null && quyenSua ? 1 : 0,
                quyenXoa != null && quyenXoa ? 1 : 0,
                ghiChu
        );
    }
}
