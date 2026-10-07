package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.LichHen;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LichHenRepository {

    private static final BeanPropertyRowMapper<LichHen> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(LichHen.class);

    private final JdbcTemplate jdbcTemplate;

    public LichHenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<LichHen> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_LichHen", ROW_MAPPER);
    }

    public Optional<LichHen> findById(String id) {
        List<LichHen> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_LichHen WHERE MaLichHen = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public List<LichHen> findByMaBacSi(String maBacSi) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_LichHen WHERE MaBacSi = ? ORDER BY NgayKham DESC, GioKham DESC",
                ROW_MAPPER,
                maBacSi
        );
    }

    public List<LichHen> findByMaBacSiAndNgayKham(String maBacSi, java.time.LocalDate ngayKham) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_LichHen WHERE MaBacSi = ? AND NgayKham = ? ORDER BY GioKham",
                ROW_MAPPER,
                maBacSi,
                ngayKham
        );
    }

    public List<LichHen> findByMaBenhNhan(String maBenhNhan) {
        return jdbcTemplate.query(
            "SELECT appointment.*, queue.ViTriHangDoi "
                + "FROM dbo.vw_LichHen appointment "
                + "LEFT JOIN ( "
                        + "    SELECT MaLichHen, CAST(ROW_NUMBER() OVER ( "
                + "        PARTITION BY MaBacSi, NgayKham ORDER BY GioKham, MaLichHen "
                        + "    ) AS INT) AS ViTriHangDoi "
                + "    FROM dbo.vw_LichHen "
                        + "    WHERE TrangThai = 'DangChoKham' AND MaBacSi IS NOT NULL "
                + ") queue ON queue.MaLichHen = appointment.MaLichHen "
                + "WHERE appointment.MaBenhNhan = ? "
                + "ORDER BY appointment.NgayKham DESC, appointment.GioKham DESC",
                ROW_MAPPER,
                maBenhNhan
        );
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM dbo.vw_LichHen",
                Long.class
        );
        return count == null ? 0L : count;
    }

    public void startExamination(String maLichHen, String maPhieuKham, String maBacSi, String maKhoa) {
        jdbcTemplate.update(
                "EXEC sp_TiepNhanBenhNhan @MaLichHen=?, @MaPhieuKham=?, @MaBacSi=?, @MaKhoa=?",
                maLichHen, maPhieuKham, maBacSi, maKhoa
        );
    }

    public List<java.util.Map<String, Object>> getQueueForDoctor(String maBacSi, java.time.LocalDate date) {
        return jdbcTemplate.queryForList(
                "SELECT * FROM fn_TinhHangDoiKham(?, ?)",
                maBacSi, date
        );
    }

    public LichHen save(LichHen entity) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "LichHen",
                serializeEntity(entity)
        );
        return entity;
    }

    public void deleteById(String id) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Delete @EntityName = ?, @IdValue = ?",
                "LichHen",
                id
        );
    }

    private String serializeEntity(LichHen entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaLichHen\":").append(toJsonValue(entity.getMaLichHen())).append(',');
        json.append("\"MaBenhNhan\":").append(toJsonValue(entity.getMaBenhNhan())).append(',');
        json.append("\"MaKhoa\":").append(toJsonValue(entity.getMaKhoa())).append(',');
        json.append("\"MaBacSi\":").append(toJsonValue(entity.getMaBacSi())).append(',');
        json.append("\"NgayKham\":").append(toJsonValue(entity.getNgayKham())).append(',');
        json.append("\"GioKham\":").append(toJsonValue(entity.getGioKham())).append(',');
        json.append("\"LoaiKham\":").append(toJsonValue(entity.getLoaiKham())).append(',');
        json.append("\"ThoiGianKhamDuKien\":").append(toJsonValue(entity.getThoiGianKhamDuKien())).append(',');
        json.append("\"HinhThucDat\":").append(toJsonValue(entity.getHinhThucDat())).append(',');
        json.append("\"LyDoKham\":").append(toJsonValue(entity.getLyDoKham())).append(',');
        json.append("\"TrangThai\":").append(toJsonValue(entity.getTrangThai())).append(',');
        json.append("\"NgayDatLich\":").append(toJsonValue(entity.getNgayDatLich()));
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
        if (value instanceof java.time.LocalTime time) {
            return '"' + time.toString() + '"';
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

