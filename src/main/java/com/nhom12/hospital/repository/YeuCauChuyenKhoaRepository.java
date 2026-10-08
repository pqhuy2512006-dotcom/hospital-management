package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.YeuCauChuyenKhoa;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class YeuCauChuyenKhoaRepository {

    private static final BeanPropertyRowMapper<YeuCauChuyenKhoa> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(YeuCauChuyenKhoa.class);

    private final JdbcTemplate jdbcTemplate;

    public YeuCauChuyenKhoaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<YeuCauChuyenKhoa> findByMaBacSiGuiOrderByNgayTaoDesc(String maBacSiGui) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_YeuCauChuyenKhoa WHERE MaBacSiGui = ? ORDER BY NgayTao DESC",
                ROW_MAPPER,
                maBacSiGui
        );
    }

    public List<YeuCauChuyenKhoa> findByMaKhoaNhanAndTrangThaiOrderByNgayTaoDesc(String maKhoaNhan, String trangThai) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_YeuCauChuyenKhoa WHERE MaKhoaNhan = ? AND TrangThai = ? ORDER BY NgayTao DESC",
                ROW_MAPPER,
                maKhoaNhan, trangThai
        );
    }

    public Optional<YeuCauChuyenKhoa> findById(String id) {
        List<YeuCauChuyenKhoa> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_YeuCauChuyenKhoa WHERE MaYeuCau = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public boolean existsById(String id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM dbo.vw_YeuCauChuyenKhoa WHERE MaYeuCau = ?",
                Integer.class,
                id
        );
        return count != null && count > 0;
    }

    public YeuCauChuyenKhoa save(YeuCauChuyenKhoa entity) {
        String payload = serializeEntity(entity);
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "YeuCauChuyenKhoa",
                payload
        );
        return entity;
    }

    public void deleteById(String id) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Delete @EntityName = ?, @IdValue = ?",
                "YeuCauChuyenKhoa",
                id
        );
    }

    private String serializeEntity(YeuCauChuyenKhoa entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaYeuCau\":").append(toJsonValue(entity.getMaYeuCau())).append(',');
        json.append("\"LoaiYeuCau\":").append(toJsonValue(entity.getLoaiYeuCau())).append(',');
        json.append("\"MaPhieuKham\":").append(toJsonValue(entity.getMaPhieuKham())).append(',');
        json.append("\"MaBenhNhan\":").append(toJsonValue(entity.getMaBenhNhan())).append(',');
        json.append("\"MaBacSiGui\":").append(toJsonValue(entity.getMaBacSiGui())).append(',');
        json.append("\"MaKhoaNhan\":").append(toJsonValue(entity.getMaKhoaNhan())).append(',');
        json.append("\"MaBacSiDuocMoi\":").append(toJsonValue(entity.getMaBacSiDuocMoi())).append(',');
        json.append("\"LyDo\":").append(toJsonValue(entity.getLyDo())).append(',');
        json.append("\"TrangThai\":").append(toJsonValue(entity.getTrangThai())).append(',');
        json.append("\"NgayTao\":").append(toJsonValue(entity.getNgayTao()));
        return json.append('}').toString();
    }

    private String toJsonValue(Object value) {
        if (value == null) {
            return "null";
        }
        return '"' + value.toString()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + '"';
    }
}