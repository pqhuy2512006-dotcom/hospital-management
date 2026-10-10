package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.NhanVien;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class NhanVienRepository {

	private static final BeanPropertyRowMapper<NhanVien> ROW_MAPPER =
			BeanPropertyRowMapper.newInstance(NhanVien.class);

	private final JdbcTemplate jdbcTemplate;

	public NhanVienRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<NhanVien> findAll() {
		return jdbcTemplate.query("SELECT * FROM dbo.vw_NhanVien", ROW_MAPPER);
	}

	public Optional<NhanVien> findById(String id) {
		return jdbcTemplate.query(
				"SELECT * FROM dbo.vw_NhanVien WHERE MaNhanVien = ?",
				ROW_MAPPER,
				id
		).stream().findFirst();
	}

	public Optional<NhanVien> findByMaTaiKhoan(Long maTaiKhoan) {
		return jdbcTemplate.query(
				"SELECT * FROM dbo.vw_NhanVien WHERE MaTaiKhoan = ?",
				ROW_MAPPER,
				maTaiKhoan
		).stream().findFirst();
	}

	public boolean existsBySoDienThoai(String soDienThoai) {
		return exists("SELECT COUNT(1) FROM dbo.vw_NhanVien WHERE SoDienThoai = ?", soDienThoai);
	}

	public boolean existsBySoDienThoaiAndMaNhanVienNot(String soDienThoai, String maNhanVien) {
		return exists(
				"SELECT COUNT(1) FROM dbo.vw_NhanVien WHERE SoDienThoai = ? AND MaNhanVien <> ?",
				soDienThoai,
				maNhanVien
		);
	}

	public long count() {
		Long count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM dbo.vw_NhanVien", Long.class);
		return count == null ? 0L : count;
	}

	public NhanVien save(NhanVien employee) {
		Map<String, Object> columns = new LinkedHashMap<>();
		columns.put("MaNhanVien", employee.getMaNhanVien());
		columns.put("MaTaiKhoan", employee.getMaTaiKhoan());
		columns.put("HoTen", employee.getHoTen());
		columns.put("NgaySinh", employee.getNgaySinh());
		columns.put("GioiTinh", employee.getGioiTinh());
		columns.put("SoCCCD", employee.getSoCCCD());
		columns.put("SoDienThoai", employee.getSoDienThoai());
		columns.put("Email", employee.getEmail());
		columns.put("DiaChi", employee.getDiaChi());
		columns.put("ChungChiHanhNghe", employee.getChungChiHanhNghe());
		columns.put("TrinhDoChuyenMon", employee.getTrinhDoChuyenMon());
		columns.put("NgayVaoLam", employee.getNgayVaoLam());
		columns.put("VaiTro", employee.getVaiTro());
		columns.put("MaKhoa", employee.getMaKhoa());
		columns.put("TrangThai", employee.getTrangThai());
		EntityProcedureSupport.save(jdbcTemplate, "NhanVien", columns);
		return employee;
	}

	private boolean exists(String sql, Object... args) {
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
		return count != null && count > 0;
	}
}
