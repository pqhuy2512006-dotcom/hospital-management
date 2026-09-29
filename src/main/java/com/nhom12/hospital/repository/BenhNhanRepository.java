package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.BenhNhan;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BenhNhanRepository {

    private static final BeanPropertyRowMapper<BenhNhan> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(BenhNhan.class);

    private final JdbcTemplate jdbcTemplate;

    public BenhNhanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<BenhNhan> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_BenhNhan", ROW_MAPPER);
    }

    public Optional<BenhNhan> findById(String id) {
        List<BenhNhan> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_BenhNhan WHERE MaBenhNhan = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM dbo.vw_BenhNhan",
                Long.class
        );
        return count == null ? 0L : count;
    }

    public boolean existsById(String id) {
        Integer found = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM dbo.vw_BenhNhan WHERE MaBenhNhan = ?",
                Integer.class,
                id
        );
        return found != null && found > 0;
    }

    public BenhNhan save(BenhNhan entity) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "BenhNhan",
                serializeEntity(entity)
        );
        return entity;
    }

    public void deleteById(String id) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Delete @EntityName = ?, @IdValue = ?",
                "BenhNhan",
                id
        );
    }

    private String serializeEntity(BenhNhan entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaBenhNhan\":").append(toJsonValue(entity.getMaBenhNhan())).append(',');
        json.append("\"MaTaiKhoan\":").append(toJsonValue(entity.getMaTaiKhoan())).append(',');
        json.append("\"HoTen\":").append(toJsonValue(entity.getHoTen())).append(',');
        json.append("\"NgaySinh\":").append(toJsonValue(entity.getNgaySinh())).append(',');
        json.append("\"GioiTinh\":").append(toJsonValue(entity.getGioiTinh())).append(',');
        json.append("\"SoCCCD\":").append(toJsonValue(entity.getSoCCCD())).append(',');
        json.append("\"MaBHYT\":").append(toJsonValue(entity.getMaBHYT())).append(',');
        json.append("\"NhomMau\":").append(toJsonValue(entity.getNhomMau())).append(',');
        json.append("\"NgheNghiep\":").append(toJsonValue(entity.getNgheNghiep())).append(',');
        json.append("\"DiaChi\":").append(toJsonValue(entity.getDiaChi())).append(',');
        json.append("\"SoDienThoai\":").append(toJsonValue(entity.getSoDienThoai())).append(',');
        json.append("\"Email\":").append(toJsonValue(entity.getEmail())).append(',');
        json.append("\"NguoiLienHeKhanCap\":").append(toJsonValue(entity.getNguoiLienHeKhanCap())).append(',');
        json.append("\"QuanHeNguoiLienHe\":").append(toJsonValue(entity.getQuanHeNguoiLienHe())).append(',');
        json.append("\"SdtNguoiLienHe\":").append(toJsonValue(entity.getSdtNguoiLienHe())).append(',');
        json.append("\"TienSuBenhNen\":").append(toJsonValue(entity.getTienSuBenhNen())).append(',');
        json.append("\"TienSuDiUng\":").append(toJsonValue(entity.getTienSuDiUng())).append(',');
        json.append("\"NgayTaoHoSo\":").append(toJsonValue(entity.getNgayTaoHoSo()));
        return json.append('}').toString();
    }

    private String toJsonValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof java.time.LocalDate date) {
            return '"' + date.toString() + '"';
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