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

    public GiuongBenh save(GiuongBenh entity) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "GiuongBenh",
                serializeEntity(entity)
        );
        return entity;
    }

    private String serializeEntity(GiuongBenh entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaGiuong\":").append(toJsonValue(entity.getMaGiuong())).append(',');
        json.append("\"MaKhoa\":").append(toJsonValue(entity.getMaKhoa())).append(',');
        json.append("\"SoGiuong\":").append(toJsonValue(entity.getSoGiuong())).append(',');
        json.append("\"TrangThai\":").append(toJsonValue(entity.getTrangThai())).append(',');
        json.append("\"DonGiaNgay\":").append(toJsonValue(entity.getDonGiaNgay()));
        return json.append('}').toString();
    }

    private String toJsonValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof java.math.BigDecimal decimal) {
            return '"' + decimal.toPlainString() + '"';
        }
        return '"' + value.toString()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + '"';
    }
}
