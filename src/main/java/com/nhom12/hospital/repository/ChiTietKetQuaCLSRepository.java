package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.ChiTietKetQuaCLS;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChiTietKetQuaCLSRepository {

    private static final BeanPropertyRowMapper<ChiTietKetQuaCLS> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(ChiTietKetQuaCLS.class);

    private final JdbcTemplate jdbcTemplate;

    public ChiTietKetQuaCLSRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ChiTietKetQuaCLS> findByMaKetQua(String maKetQua) {
        return jdbcTemplate.query(
                "SELECT * FROM dbo.vw_ChiTietKetQuaCLS WHERE MaKetQua = ? ORDER BY MaChiTietXN",
                ROW_MAPPER,
                maKetQua
        );
    }

    public Optional<ChiTietKetQuaCLS> findById(String id) {
        List<ChiTietKetQuaCLS> rows = jdbcTemplate.query(
                "SELECT * FROM dbo.vw_ChiTietKetQuaCLS WHERE MaChiTietXN = ?",
                ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    public ChiTietKetQuaCLS save(ChiTietKetQuaCLS entity) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Entity_Save @EntityName = ?, @Payload = ?",
                "ChiTietKetQuaCLS",
                serializeEntity(entity)
        );
        return entity;
    }

    private String serializeEntity(ChiTietKetQuaCLS entity) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"MaChiTietXN\":").append(toJsonValue(entity.getMaChiTietXN())).append(',');
        json.append("\"MaKetQua\":").append(toJsonValue(entity.getMaKetQua())).append(',');
        json.append("\"TenChiSo\":").append(toJsonValue(entity.getTenChiSo())).append(',');
        json.append("\"GiaTri\":").append(toJsonValue(entity.getGiaTri())).append(',');
        json.append("\"DonVi\":").append(toJsonValue(entity.getDonVi())).append(',');
        json.append("\"KhoangThamChieu\":").append(toJsonValue(entity.getKhoangThamChieu())).append(',');
        json.append("\"DanhGia\":").append(toJsonValue(entity.getDanhGia()));
        return json.append('}').toString();
    }

    private String toJsonValue(Object value) {
        if (value == null) {
            return "null";
        }
        return '"' + value.toString()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + '"';
    }
}
