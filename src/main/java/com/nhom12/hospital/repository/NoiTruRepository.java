package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.NoiTru;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NoiTruRepository {

    private static final BeanPropertyRowMapper<NoiTru> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(NoiTru.class);

    private final JdbcTemplate jdbcTemplate;

    public NoiTruRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<NoiTru> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_NoiTru", ROW_MAPPER);
    }

    public List<NoiTru> findByTrangThai(String trangThai) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_NoiTru WHERE TrangThai = ? ORDER BY NgayNhapVien DESC",
                ROW_MAPPER,
                trangThai
        );
    }

    public List<NoiTru> findByMaBenhNhan(String maBenhNhan) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_NoiTru WHERE MaBenhNhan = ? ORDER BY NgayNhapVien DESC",
                ROW_MAPPER,
                maBenhNhan
        );
    }

    public Optional<NoiTru> findById(String id) {
        List<NoiTru> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_NoiTru WHERE MaNoiTru = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public NoiTru save(NoiTru entity) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "NoiTru",
                serializeEntity(entity)
        );
        return entity;
    }

    private String serializeEntity(NoiTru entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaNoiTru\":").append(toJsonValue(entity.getMaNoiTru())).append(',');
        json.append("\"MaBenhNhan\":").append(toJsonValue(entity.getMaBenhNhan())).append(',');
        json.append("\"MaGiuong\":").append(toJsonValue(entity.getMaGiuong())).append(',');
        json.append("\"MaPhieuKham\":").append(toJsonValue(entity.getMaPhieuKham())).append(',');
        json.append("\"NgayNhapVien\":").append(toJsonValue(entity.getNgayNhapVien())).append(',');
        json.append("\"NgayXuatVien\":").append(toJsonValue(entity.getNgayXuatVien())).append(',');
        json.append("\"TrangThai\":").append(toJsonValue(entity.getTrangThai()));
        return json.append('}').toString();
    }

    private String toJsonValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof java.time.LocalDateTime dateTime) {
            return '"' + dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + '"';
        }
        return '"' + value.toString()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + '"';
    }
}
