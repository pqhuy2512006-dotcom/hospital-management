package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.KetQuaCLS;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class KetQuaCLSRepository {

    private static final BeanPropertyRowMapper<KetQuaCLS> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(KetQuaCLS.class);

    private final JdbcTemplate jdbcTemplate;

    public KetQuaCLSRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<KetQuaCLS> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_KetQuaCLS", ROW_MAPPER);
    }

    public Optional<KetQuaCLS> findById(String id) {
        List<KetQuaCLS> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_KetQuaCLS WHERE MaKetQua = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public List<KetQuaCLS> findByMaBenhNhan(String maBenhNhan) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_KetQuaCLS WHERE MaBenhNhan = ? ORDER BY NgayThucHien DESC",
                ROW_MAPPER,
                maBenhNhan
        );
    }

    public List<KetQuaCLS> findByMaPhieuKham(String maPhieuKham) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_KetQuaCLS WHERE MaPhieuKham = ? ORDER BY NgayThucHien DESC",
                ROW_MAPPER,
                maPhieuKham
        );
    }

    public List<KetQuaCLS> findByMaBacSiDoc(String maBacSiDoc) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_KetQuaCLS WHERE MaBacSiDoc = ? ORDER BY NgayThucHien DESC",
                ROW_MAPPER,
                maBacSiDoc
        );
    }

    public KetQuaCLS save(KetQuaCLS entity) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "KetQuaCLS",
                serializeEntity(entity)
        );
        return entity;
    }

    @Transactional
    public List<KetQuaCLS> saveAll(Iterable<KetQuaCLS> entities) {
        List<KetQuaCLS> saved = new ArrayList<>();
        for (KetQuaCLS entity : entities) {
            saved.add(save(entity));
        }
        return saved;
    }

    public void deleteById(String id) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Delete @EntityName = ?, @IdValue = ?",
                "KetQuaCLS",
                id
        );
    }

    private String serializeEntity(KetQuaCLS entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaKetQua\":").append(toJsonValue(entity.getMaKetQua())).append(',');
        json.append("\"MaPhieuKham\":").append(toJsonValue(entity.getMaPhieuKham())).append(',');
        json.append("\"MaBenhNhan\":").append(toJsonValue(entity.getMaBenhNhan())).append(',');
        json.append("\"MaDichVu\":").append(toJsonValue(entity.getMaDichVu())).append(',');
        json.append("\"MaKTV\":").append(toJsonValue(entity.getMaKTV())).append(',');
        json.append("\"MaBacSiDoc\":").append(toJsonValue(entity.getMaBacSiDoc())).append(',');
        json.append("\"LoaiXetNghiem\":").append(toJsonValue(entity.getLoaiXetNghiem())).append(',');
        json.append("\"KetLuan\":").append(toJsonValue(entity.getKetLuan())).append(',');
        json.append("\"HinhAnhFile\":").append(toJsonValue(entity.getHinhAnhFile())).append(',');
        json.append("\"NgayThucHien\":").append(toJsonValue(entity.getNgayThucHien()));
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
