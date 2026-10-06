package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.Thuoc;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ThuocRepository {

    private static final BeanPropertyRowMapper<Thuoc> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(Thuoc.class);

    private final JdbcTemplate jdbcTemplate;

    public ThuocRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Thuoc> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_Thuoc", ROW_MAPPER);
    }

    public Optional<Thuoc> findById(String id) {
        List<Thuoc> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_Thuoc WHERE MaThuoc = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public List<Thuoc> findByTenThuocContainingIgnoreCase(String tenThuoc) {
        String keyword = tenThuoc == null ? "%" : "%" + tenThuoc + "%";
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_Thuoc WHERE LOWER(TenThuoc) LIKE LOWER(?)",
                ROW_MAPPER,
                keyword
        );
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM dbo.vw_Thuoc",
                Long.class
        );
        return count == null ? 0L : count;
    }

    public Thuoc save(Thuoc entity) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "Thuoc",
                serializeEntity(entity)
        );
        return entity;
    }

    public void deleteById(String id) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Delete @EntityName = ?, @IdValue = ?",
                "Thuoc",
                id
        );
    }

    private String serializeEntity(Thuoc entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaThuoc\":").append(toJsonValue(entity.getMaThuoc())).append(',');
        json.append("\"TenThuoc\":").append(toJsonValue(entity.getTenThuoc())).append(',');
        json.append("\"DonViTinh\":").append(toJsonValue(entity.getDonViTinh())).append(',');
        json.append("\"DonGiaBan\":").append(toJsonValue(entity.getDonGiaBan())).append(',');
        json.append("\"TonKhoHienTai\":").append(toJsonValue(entity.getTonKhoHienTai())).append(',');
        json.append("\"NguongCanhBao\":").append(toJsonValue(entity.getNguongCanhBao()));
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
