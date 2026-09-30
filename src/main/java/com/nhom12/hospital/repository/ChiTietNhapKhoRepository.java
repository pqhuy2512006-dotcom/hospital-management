package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.ChiTietNhapKho;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ChiTietNhapKhoRepository {

    private static final BeanPropertyRowMapper<ChiTietNhapKho> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(ChiTietNhapKho.class);

    private final JdbcTemplate jdbcTemplate;

    public ChiTietNhapKhoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ChiTietNhapKho> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_ChiTietNhapKho", ROW_MAPPER);
    }

    public Optional<ChiTietNhapKho> findById(String id) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_ChiTietNhapKho WHERE MaChiTietNhap = ?",
                ROW_MAPPER,
                id
        ).stream().findFirst();
    }

    public List<ChiTietNhapKho> findByMaPhieuNhap(String maPhieuNhap) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_ChiTietNhapKho WHERE MaPhieuNhap = ?",
                ROW_MAPPER,
                maPhieuNhap
        );
    }

    public ChiTietNhapKho save(ChiTietNhapKho detail) {
        Map<String, Object> columns = new LinkedHashMap<>();
        columns.put("MaChiTietNhap", detail.getMaChiTietNhap());
        columns.put("MaPhieuNhap", detail.getMaPhieuNhap());
        columns.put("MaThuoc", detail.getMaThuoc());
        columns.put("SoLo", detail.getSoLo());
        columns.put("SoLuong", detail.getSoLuong());
        columns.put("DonGia", detail.getDonGia());
        columns.put("HanSuDung", detail.getHanSuDung());
        EntityProcedureSupport.save(jdbcTemplate, "ChiTietNhapKho", columns);
        return detail;
    }

    public void deleteById(String id) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Delete @EntityName = ?, @IdValue = ?",
                "ChiTietNhapKho",
                id
        );
    }
}
