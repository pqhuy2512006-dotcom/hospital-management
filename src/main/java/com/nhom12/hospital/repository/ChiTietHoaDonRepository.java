package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.ChiTietHoaDon;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChiTietHoaDonRepository {

    private static final BeanPropertyRowMapper<ChiTietHoaDon> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(ChiTietHoaDon.class);

    private final JdbcTemplate jdbcTemplate;

    public ChiTietHoaDonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ChiTietHoaDon> findByMaHoaDon(String maHoaDon) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_ChiTietHoaDon WHERE MaHoaDon = ? ORDER BY MaChiTietHD",
                ROW_MAPPER,
                maHoaDon
        );
    }

    public Optional<ChiTietHoaDon> findById(String id) {
        List<ChiTietHoaDon> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_ChiTietHoaDon WHERE MaChiTietHD = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public ChiTietHoaDon save(ChiTietHoaDon entity) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "ChiTietHoaDon",
                serializeEntity(entity)
        );
        return entity;
    }

    private String serializeEntity(ChiTietHoaDon entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaChiTietHD\":").append(toJsonValue(entity.getMaChiTietHD())).append(',');
        json.append("\"MaHoaDon\":").append(toJsonValue(entity.getMaHoaDon())).append(',');
        json.append("\"TenDichVu\":").append(toJsonValue(entity.getTenDichVu())).append(',');
        json.append("\"LoaiDichVu\":").append(toJsonValue(entity.getLoaiDichVu())).append(',');
        json.append("\"DonGia\":").append(toJsonValue(entity.getDonGia())).append(',');
        json.append("\"SoLuong\":").append(toJsonValue(entity.getSoLuong())).append(',');
        json.append("\"ThanhTien\":").append(toJsonValue(entity.getThanhTien()));
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
