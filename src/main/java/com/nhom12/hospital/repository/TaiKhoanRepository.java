package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.TaiKhoan;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TaiKhoanRepository {

    private static final BeanPropertyRowMapper<TaiKhoan> ROW_MAPPER =
            BeanPropertyRowMapper.newInstance(TaiKhoan.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcCall insertAccount;
    private final SimpleJdbcCall updateAccount;

    public TaiKhoanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAccount = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_TaiKhoan_Insert")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("TenDangNhap", Types.VARCHAR),
                        new SqlParameter("MatKhauHash", Types.VARCHAR),
                        new SqlParameter("Email", Types.VARCHAR),
                        new SqlParameter("SoDienThoai", Types.VARCHAR),
                        new SqlParameter("VaiTro", Types.VARCHAR),
                        new SqlParameter("TrangThai", Types.BIT),
                        new SqlParameter("NgayTao", Types.TIMESTAMP),
                        new SqlOutParameter("MaTaiKhoan", Types.BIGINT));
        this.updateAccount = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("usp_TaiKhoan_Update")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("MaTaiKhoan", Types.BIGINT),
                        new SqlParameter("MatKhauHash", Types.VARCHAR),
                        new SqlParameter("Email", Types.VARCHAR),
                        new SqlParameter("SoDienThoai", Types.VARCHAR),
                        new SqlParameter("VaiTro", Types.VARCHAR),
                        new SqlParameter("TrangThai", Types.BIT),
                        new SqlParameter("NgayTao", Types.TIMESTAMP));
    }

    public Optional<TaiKhoan> findById(Long id) {
        return queryOne("SELECT * FROM dbo.vw_TaiKhoan WHERE MaTaiKhoan = ?", id);
    }

    public Optional<TaiKhoan> findByTenDangNhap(String username) {
        return queryOne("SELECT * FROM dbo.vw_TaiKhoan WHERE TenDangNhap = ?", username);
    }

    public Optional<TaiKhoan> findByEmail(String email) {
        return queryOne("SELECT * FROM dbo.vw_TaiKhoan WHERE Email = ?", email);
    }

    public Optional<TaiKhoan> findBySoDienThoai(String soDienThoai) {
        return queryOne("SELECT * FROM dbo.vw_TaiKhoan WHERE SoDienThoai = ?", soDienThoai);
    }

    public List<TaiKhoan> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_TaiKhoan", ROW_MAPPER);
    }

    public TaiKhoan save(TaiKhoan account) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("MatKhauHash", account.getMatKhauHash(), Types.VARCHAR)
                .addValue("Email", account.getEmail(), Types.VARCHAR)
                .addValue("SoDienThoai", account.getSoDienThoai(), Types.VARCHAR)
                .addValue("VaiTro", account.getVaiTro(), Types.VARCHAR)
                .addValue("TrangThai", account.getTrangThai(), Types.BIT)
                .addValue("NgayTao", account.getNgayTao(), Types.TIMESTAMP);

        if (account.getMaTaiKhoan() == null) {
            parameters.addValue("TenDangNhap", account.getTenDangNhap(), Types.VARCHAR);
            Map<String, Object> output = insertAccount.execute(parameters);
            account.setMaTaiKhoan(((Number) output.get("MaTaiKhoan")).longValue());
        } else {
            parameters.addValue("MaTaiKhoan", account.getMaTaiKhoan(), Types.BIGINT);
            updateAccount.execute(parameters);
        }
        return account;
    }

    private Optional<TaiKhoan> queryOne(String sql, Object value) {
        List<TaiKhoan> accounts = jdbcTemplate.query(sql, ROW_MAPPER, value);
        return accounts.stream().findFirst();
    }
}
