package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.HoaDon;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class HoaDonRepository {

    private static final BeanPropertyRowMapper<HoaDon> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(HoaDon.class);

    private final JdbcTemplate jdbcTemplate;

    public HoaDonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<HoaDon> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_HoaDon", ROW_MAPPER);
    }

    public Optional<HoaDon> findById(String id) {
        List<HoaDon> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_HoaDon WHERE MaHoaDon = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public List<HoaDon> findByMaBenhNhan(String maBenhNhan) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_HoaDon WHERE MaBenhNhan = ? ORDER BY NgayLap DESC",
                ROW_MAPPER,
                maBenhNhan
        );
    }

    public List<HoaDon> findByTrangThaiTT(String trangThaiTT) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_HoaDon WHERE TrangThaiTT = ? ORDER BY NgayLap DESC",
                ROW_MAPPER,
                trangThaiTT
        );
    }

    public List<HoaDon> findByTrangThaiTTAndNgayThanhToanGreaterThanEqualAndNgayThanhToanLessThan(
            String trangThaiTT, LocalDateTime from, LocalDateTime toExclusive) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_HoaDon WHERE TrangThaiTT = ? AND NgayThanhToan >= ? AND NgayThanhToan < ? ORDER BY NgayThanhToan",
                ROW_MAPPER,
                trangThaiTT,
                from,
                toExclusive
        );
    }

    public HoaDon save(HoaDon entity) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "HoaDon",
                serializeEntity(entity)
        );
        return entity;
    }

    private String serializeEntity(HoaDon entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaHoaDon\":").append(toJsonValue(entity.getMaHoaDon())).append(',');
        json.append("\"MaBenhNhan\":").append(toJsonValue(entity.getMaBenhNhan())).append(',');
        json.append("\"MaPhieuKham\":").append(toJsonValue(entity.getMaPhieuKham())).append(',');
        json.append("\"NgayLap\":").append(toJsonValue(entity.getNgayLap())).append(',');
        json.append("\"TongTienDichVu\":").append(toJsonValue(entity.getTongTienDichVu())).append(',');
        json.append("\"BHYTChiTra\":").append(toJsonValue(entity.getBhytChiTra())).append(',');
        json.append("\"HinhThucThanhToan\":").append(toJsonValue(entity.getHinhThucThanhToan())).append(',');
        json.append("\"TrangThaiTT\":").append(toJsonValue(entity.getTrangThaiTT())).append(',');
        json.append("\"NhanVienThu\":").append(toJsonValue(entity.getNhanVienThu())).append(',');
        json.append("\"NgayThanhToan\":").append(toJsonValue(entity.getNgayThanhToan()));
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
