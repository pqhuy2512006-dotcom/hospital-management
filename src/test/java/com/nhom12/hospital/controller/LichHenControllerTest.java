package com.nhom12.hospital.controller;

import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.entity.BenhNhan;
import com.nhom12.hospital.entity.Khoa;
import com.nhom12.hospital.entity.LichHen;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.service.LichHenService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LichHenControllerTest {

    private final LichHenService lichHenService = mock(LichHenService.class);
    private final LichHenController controller = new LichHenController(lichHenService);

    @Test
    void assignsThirtyMinutesAndAllowsAppointmentStartingWhenPreviousEnds() {
        LocalDate date = LocalDate.now().plusDays(2);
        
        when(lichHenService.isPatient(7L)).thenReturn(true);
        when(lichHenService.createPatientAppointment(any(), org.mockito.ArgumentMatchers.eq(7L)))
            .thenAnswer(invocation -> {
                LichHen lh = new LichHen();
                lh.setThoiGianKhamDuKien(30);
                lh.setTrangThai("DaDatLich");
                return lh;
            });
            
        ResponseEntity<?> response = controller.create(booking(date, "08:45", "KhamThuong"), patientRequest());

        assertEquals(200, response.getStatusCode().value());
        verify(lichHenService).createPatientAppointment(any(), org.mockito.ArgumentMatchers.eq(7L));
    }

    @Test
    void assignsFortyFiveMinutesToServiceAppointments() {
        LocalDate date = LocalDate.now().plusDays(2);
        
        when(lichHenService.isPatient(7L)).thenReturn(true);
        when(lichHenService.createPatientAppointment(any(), org.mockito.ArgumentMatchers.eq(7L)))
            .thenAnswer(invocation -> {
                LichHen lh = new LichHen();
                lh.setThoiGianKhamDuKien(45);
                lh.setTrangThai("DaDatLich");
                return lh;
            });
            
        ResponseEntity<?> response = controller.create(booking(date, "08:00", "KhamDichVu"), patientRequest());

        assertEquals(200, response.getStatusCode().value());
        verify(lichHenService).createPatientAppointment(any(), org.mockito.ArgumentMatchers.eq(7L));
    }

    @Test
    void rejectsAppointmentOverlappingExistingFortyFiveMinuteServiceVisit() {
        LocalDate date = LocalDate.now().plusDays(2);
        
        when(lichHenService.isPatient(7L)).thenReturn(true);
        when(lichHenService.createPatientAppointment(any(), org.mockito.ArgumentMatchers.eq(7L)))
            .thenThrow(new IllegalStateException("Bác sĩ đã có lịch khám giao với khung giờ này."));
            
        ResponseEntity<?> response = controller.create(booking(date, "08:30", "KhamThuong"), patientRequest());

        assertEquals(409, response.getStatusCode().value());
    }

    private Map<String, Object> booking(LocalDate date, String time, String visitType) {
        return Map.of(
                "ngayKham", date.toString(),
                "gioKham", time,
                "maKhoa", "KNT",
                "maBacSi", "NV-DOC01",
                "loaiKham", visitType
        );
    }

    private MockHttpServletRequest patientRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(SessionAttributes.ACCOUNT_ID, 7L);
        return request;
    }
}