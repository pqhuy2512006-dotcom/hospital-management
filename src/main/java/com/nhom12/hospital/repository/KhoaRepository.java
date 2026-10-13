package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.Khoa;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class KhoaRepository {

	private static final BeanPropertyRowMapper<Khoa> ROW_MAPPER =
			BeanPropertyRowMapper.newInstance(Khoa.class);

	private final JdbcTemplate jdbcTemplate;

	public KhoaRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Khoa> findAll() {
        return jdbcTemplate.query("SELECT * FROM dbo.vw_Khoa WHERE TrangThai = 'HoatDong'", ROW_MAPPER);
    }
	
	public List<Khoa> findAllForAdmin() {
    return jdbcTemplate.query("SELECT * FROM dbo.vw_Khoa", ROW_MAPPER);
}
	public Optional<Khoa> findById(String id) {
		return jdbcTemplate.query(
				"SELECT * FROM dbo.vw_Khoa WHERE MaKhoa = ?",
				ROW_MAPPER,
				id
		).stream().findFirst();
	}

	public boolean existsById(String id) {
		Integer count = jdbcTemplate.queryForObject(
				"SELECT COUNT(1) FROM dbo.vw_Khoa WHERE MaKhoa = ?",
				Integer.class,
				id
		);
		return count != null && count > 0;
	}

	// mới 
	    // Lưu khoa (Thêm mới hoặc Cập nhật) thông qua Stored Procedure usp_Khoa_Save
    public Khoa save(Khoa khoa) {
        jdbcTemplate.update(
                "EXEC dbo.usp_Khoa_Save @MaKhoa = ?, @TenKhoa = ?, @MoTa = ?, @MaTruongKhoa = ?, @TrangThai = ?",
                khoa.getMaKhoa(),
                khoa.getTenKhoa(),
                khoa.getMoTa(),
                khoa.getMaTruongKhoa(),
                khoa.getTrangThai() != null ? khoa.getTrangThai() : "HoatDong"
        );
        return khoa;
    }

    // Xóa mềm thông qua Stored Procedure usp_Khoa_SoftDelete
    public void deleteById(String id) {
        jdbcTemplate.update("EXEC dbo.usp_Khoa_SoftDelete @MaKhoa = ?", id);
    }
}