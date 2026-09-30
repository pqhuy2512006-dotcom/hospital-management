package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.PhieuNhapKho;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PhieuNhapKhoRepository {

	private static final BeanPropertyRowMapper<PhieuNhapKho> ROW_MAPPER =
			BeanPropertyRowMapper.newInstance(PhieuNhapKho.class);

	private final JdbcTemplate jdbcTemplate;

	public PhieuNhapKhoRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<PhieuNhapKho> findAll() {
		return jdbcTemplate.query("SELECT * FROM dbo.vw_PhieuNhapKho", ROW_MAPPER);
	}

	public Optional<PhieuNhapKho> findById(String id) {
		return jdbcTemplate.query(
				"SELECT * FROM dbo.vw_PhieuNhapKho WHERE MaPhieuNhap = ?",
				ROW_MAPPER,
				id
		).stream().findFirst();
	}

	public PhieuNhapKho save(PhieuNhapKho receipt) {
		if (receipt.getNgayNhap() == null) {
			receipt.setNgayNhap(LocalDate.now());
		}
		Map<String, Object> columns = new LinkedHashMap<>();
		columns.put("MaPhieuNhap", receipt.getMaPhieuNhap());
		columns.put("NgayNhap", receipt.getNgayNhap());
		columns.put("NhaCungCap", receipt.getNhaCungCap());
		columns.put("SoHoaDonNCC", receipt.getSoHoaDonNCC());
		columns.put("NguoiNhap", receipt.getNguoiNhap());
		columns.put("NguoiDuyet", receipt.getNguoiDuyet());
		columns.put("TongTien", receipt.getTongTien());
		EntityProcedureSupport.save(jdbcTemplate, "PhieuNhapKho", columns);
		return receipt;
	}
}
