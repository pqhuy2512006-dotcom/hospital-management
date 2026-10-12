package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.DichVuCLS;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DichVuCLSRepository {

    private static final BeanPropertyRowMapper<DichVuCLS> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(DichVuCLS.class);

    private final JdbcTemplate jdbcTemplate;

    public DichVuCLSRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DichVuCLS> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_DichVuCLS", ROW_MAPPER);
    }

    public Optional<DichVuCLS> findById(String id) {
        List<DichVuCLS> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_DichVuCLS WHERE MaDichVu = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public DichVuCLS save(DichVuCLS entity) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "DichVuCLS",
                serializeEntity(entity)
        );
        return entity;
    }

    private String serializeEntity(DichVuCLS entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaDichVu\":").append(toJsonValue(entity.getMaDichVu())).append(',');
        json.append("\"TenDichVu\":").append(toJsonValue(entity.getTenDichVu())).append(',');
        json.append("\"DonGia\":").append(toJsonValue(entity.getDonGia())).append(',');
        json.append("\"DonViTinh\":").append(toJsonValue(entity.getDonViTinh())).append(',');
        json.append("\"KhoangThamChieu\":").append(toJsonValue(entity.getKhoangThamChieu()));
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
