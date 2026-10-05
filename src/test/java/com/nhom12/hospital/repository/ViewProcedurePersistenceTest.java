package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.BenhNhan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ViewProcedurePersistenceTest {

    @Autowired
    private BenhNhanRepository benhNhanRepository;

    @Test
    void savesThroughProcedureAndReadsThroughView() {
        BenhNhan patient = new BenhNhan();
        patient.setMaBenhNhan("BNVIEWPROC01");
        patient.setHoTen("Persistence Probe");
        patient.setNgaySinh(LocalDate.of(1990, 1, 1));
        patient.setGioiTinh("Nam");
        patient.setSoDienThoai("0900000011");
        patient.setNgayTaoHoSo(LocalDateTime.now());

        benhNhanRepository.save(patient);

        BenhNhan loaded = benhNhanRepository.findById(patient.getMaBenhNhan()).orElseThrow();
        assertEquals("Persistence Probe", loaded.getHoTen());
        assertEquals("0900000011", loaded.getSoDienThoai());
        assertTrue(benhNhanRepository.existsById(patient.getMaBenhNhan()));

        loaded.setHoTen("Updated Probe");
        benhNhanRepository.save(loaded);
        assertEquals("Updated Probe", benhNhanRepository.findById(patient.getMaBenhNhan()).orElseThrow().getHoTen());

        benhNhanRepository.deleteById(patient.getMaBenhNhan());
        assertTrue(benhNhanRepository.findById(patient.getMaBenhNhan()).isEmpty());
    }
}