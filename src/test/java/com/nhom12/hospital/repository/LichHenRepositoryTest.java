package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.LichHen;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LichHenRepositoryTest {

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final LichHenRepository repository = new LichHenRepository(jdbcTemplate);

    @Test
    void patientAppointmentsRankBookedVisitsByDoctorDateAndTime() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<LichHen>>any(), eq("BN-TEST")))
                .thenReturn(List.of());

        repository.findByMaBenhNhan("BN-TEST");

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sql.capture(), ArgumentMatchers.<RowMapper<LichHen>>any(), eq("BN-TEST"));
        assertTrue(sql.getValue().contains("WHERE TrangThai = 'DaDatLich'"));
        assertTrue(sql.getValue().contains("PARTITION BY MaBacSi, NgayKham ORDER BY GioKham, MaLichHen"));
    }
}