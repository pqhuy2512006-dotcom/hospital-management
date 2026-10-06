package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.LichTruc;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class LichTrucRepository {

    private static final BeanPropertyRowMapper<LichTruc> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(LichTruc.class);

    private final JdbcTemplate jdbcTemplate;

    public LichTrucRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LichTruc> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_LichTruc", ROW_MAPPER);
    }

    public List<LichTruc> findByNgayBetween(LocalDate from, LocalDate to) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_LichTruc WHERE Ngay BETWEEN ? AND ? ORDER BY Ngay, Ca",
                ROW_MAPPER,
                from,
                to
        );
    }

    public List<LichTruc> findByMaNhanVien(String maNhanVien) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_LichTruc WHERE MaNhanVien = ? ORDER BY Ngay DESC",
                ROW_MAPPER,
                maNhanVien
        );
    }

    public Optional<LichTruc> findById(String id) {
        List<LichTruc> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_LichTruc WHERE MaLichTruc = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public boolean existsById(String id) {
        Integer found = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM dbo.vw_LichTruc WHERE MaLichTruc = ?",
                Integer.class,
                id
        );
        return found != null && found > 0;
    }

    public LichTruc save(LichTruc entity) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "LichTruc",
                serializeEntity(entity)
        );
        return entity;
    }

    public void deleteById(String id) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Delete @EntityName = ?, @IdValue = ?",
                "LichTruc",
                id
        );
    }

    private String serializeEntity(LichTruc entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaLichTruc\":").append(toJsonValue(entity.getMaLichTruc())).append(',');
        json.append("\"MaNhanVien\":").append(toJsonValue(entity.getMaNhanVien())).append(',');
        json.append("\"Ngay\":").append(toJsonValue(entity.getNgay())).append(',');
        json.append("\"Ca\":").append(toJsonValue(entity.getCa())).append(',');
        json.append("\"MaKhoa\":").append(toJsonValue(entity.getMaKhoa()));
        return json.append('}').toString();
    }

    private String toJsonValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof java.time.LocalDate date) {
            return '"' + date.toString() + '"';
        }
        return '"' + value.toString()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + '"';
    }
}
