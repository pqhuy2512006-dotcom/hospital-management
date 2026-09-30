package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.PhieuKham;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PhieuKhamRepository {

    private static final BeanPropertyRowMapper<PhieuKham> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(PhieuKham.class);

    private final JdbcTemplate jdbcTemplate;

    public PhieuKhamRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PhieuKham> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_PhieuKham", ROW_MAPPER);
    }

    public Optional<PhieuKham> findById(String id) {
        List<PhieuKham> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_PhieuKham WHERE MaPhieuKham = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public List<PhieuKham> findByMaBenhNhan(String maBenhNhan) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_PhieuKham WHERE MaBenhNhan = ? ORDER BY NgayKham DESC",
                ROW_MAPPER,
                maBenhNhan
        );
    }

    public List<PhieuKham> findByMaBacSi(String maBacSi) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_PhieuKham WHERE MaBacSi = ? ORDER BY NgayKham DESC",
                ROW_MAPPER,
                maBacSi
        );
    }

    public List<PhieuKham> findByNgayKhamGreaterThanEqualAndNgayKhamLessThan(LocalDateTime from, LocalDateTime toExclusive) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_PhieuKham WHERE NgayKham >= ? AND NgayKham < ? ORDER BY NgayKham",
                ROW_MAPPER,
                from,
                toExclusive
        );
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM dbo.vw_PhieuKham",
                Long.class
        );
        return count == null ? 0L : count;
    }

    public PhieuKham save(PhieuKham entity) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "PhieuKham",
                serializeEntity(entity)
        );
        return entity;
    }

    public void deleteById(String id) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Delete @EntityName = ?, @IdValue = ?",
                "PhieuKham",
                id
        );
    }

    private String serializeEntity(PhieuKham entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaPhieuKham\":").append(toJsonValue(entity.getMaPhieuKham())).append(',');
        json.append("\"MaBenhNhan\":").append(toJsonValue(entity.getMaBenhNhan())).append(',');
        json.append("\"MaBacSi\":").append(toJsonValue(entity.getMaBacSi())).append(',');
        json.append("\"MaKhoa\":").append(toJsonValue(entity.getMaKhoa())).append(',');
        json.append("\"MaLichHen\":").append(toJsonValue(entity.getMaLichHen())).append(',');
        json.append("\"NgayKham\":").append(toJsonValue(entity.getNgayKham())).append(',');
        json.append("\"Mach\":").append(toJsonValue(entity.getMach())).append(',');
        json.append("\"NhietDo\":").append(toJsonValue(entity.getNhietDo())).append(',');
        json.append("\"HuyetAp\":").append(toJsonValue(entity.getHuyetAp())).append(',');
        json.append("\"NhipTho\":").append(toJsonValue(entity.getNhipTho())).append(',');
        json.append("\"CanNang\":").append(toJsonValue(entity.getCanNang())).append(',');
        json.append("\"ChieuCao\":").append(toJsonValue(entity.getChieuCao())).append(',');
        json.append("\"TrieuChung\":").append(toJsonValue(entity.getTrieuChung())).append(',');
        json.append("\"KetQuaKhamLS\":").append(toJsonValue(entity.getKetQuaKhamLS())).append(',');
        json.append("\"ChanDoan\":").append(toJsonValue(entity.getChanDoan())).append(',');
        json.append("\"LoiDanBacSi\":").append(toJsonValue(entity.getLoiDanBacSi())).append(',');
        json.append("\"NgayHenTaiKham\":").append(toJsonValue(entity.getNgayHenTaiKham()));
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
