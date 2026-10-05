package com.nhom12.hospital.controller;

import com.nhom12.hospital.config.SessionAttributes;
import com.nhom12.hospital.entity.BenhNhan;
import com.nhom12.hospital.entity.Khoa;
import com.nhom12.hospital.entity.LichHen;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.BenhNhanRepository;
import com.nhom12.hospital.repository.KhoaRepository;
import com.nhom12.hospital.repository.LichHenRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
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

    private final LichHenRepository lichHenRepository = mock(LichHenRepository.class);
    private final BenhNhanRepository benhNhanRepository = mock(BenhNhanRepository.class);
    private final KhoaRepository khoaRepository = mock(KhoaRepository.class);
    private final NhanVienRepository nhanVienRepository = mock(NhanVienRepository.class);
    private final TaiKhoanRepository taiKhoanRepository = mock(TaiKhoanRepository.class);
    private final LichHenController controller = new LichHenController(
            lichHenRepository,
            benhNhanRepository,
            khoaRepository,
            nhanVienRepository,
            taiKhoanRepository
    );

    @Test
    void assignsThirtyMinutesAndAllowsAppointmentStartingWhenPreviousEnds() {
        LocalDate date = LocalDate.now().plusDays(2);
        when(lichHenRepository.findByMaBacSiAndNgayKham("NV-DOC01", date))
                .thenReturn(List.of(appointment("LH-OLD", "08:00", 45)));
        when(lichHenRepository.count()).thenReturn(0L);
        when(lichHenRepository.save(any(LichHen.class))).thenAnswer(invocation -> invocation.getArgument(0));
        preparePatientBooking(date);

        ResponseEntity<?> response = controller.create(booking(date, "08:45", "KhamThuong"), patientRequest());

        assertEquals(200, response.getStatusCode().value());
        verify(lichHenRepository).save(org.mockito.ArgumentMatchers.argThat(saved ->
            saved.getThoiGianKhamDuKien() == 30 && "DaDatLich".equals(saved.getTrangThai())));
    }

    @Test
    void assignsFortyFiveMinutesToServiceAppointments() {
        LocalDate date = LocalDate.now().plusDays(2);
        when(lichHenRepository.findByMaBacSiAndNgayKham("NV-DOC01", date)).thenReturn(List.of());
        when(lichHenRepository.count()).thenReturn(0L);
        when(lichHenRepository.save(any(LichHen.class))).thenAnswer(invocation -> invocation.getArgument(0));
        preparePatientBooking(date);

        ResponseEntity<?> response = controller.create(booking(date, "08:00", "KhamDichVu"), patientRequest());

        assertEquals(200, response.getStatusCode().value());
        verify(lichHenRepository).save(org.mockito.ArgumentMatchers.argThat(saved ->
                saved.getThoiGianKhamDuKien() == 45));
    }

    @Test
    void rejectsAppointmentOverlappingExistingFortyFiveMinuteServiceVisit() {
        LocalDate date = LocalDate.now().plusDays(2);
        when(lichHenRepository.findByMaBacSiAndNgayKham("NV-DOC01", date))
                .thenReturn(List.of(appointment("LH-OLD", "08:00", 45)));
        when(lichHenRepository.count()).thenReturn(0L);
        preparePatientBooking(date);

        ResponseEntity<?> response = controller.create(booking(date, "08:30", "KhamThuong"), patientRequest());

        assertEquals(409, response.getStatusCode().value());
        verify(lichHenRepository, never()).save(any(LichHen.class));
    }

    private void preparePatientBooking(LocalDate date) {
        TaiKhoan account = new TaiKhoan();
        account.setVaiTro("BenhNhan");
        when(taiKhoanRepository.findById(7L)).thenReturn(Optional.of(account));

        BenhNhan patient = new BenhNhan();
        patient.setMaBenhNhan("BN-TEST");
        when(benhNhanRepository.findByMaTaiKhoan(7L)).thenReturn(Optional.of(patient));
        when(khoaRepository.findById("KNT")).thenReturn(Optional.of(new Khoa()));

        NhanVien doctor = new NhanVien();
        doctor.setVaiTro("BacSi");
        doctor.setTrangThai("DangLamViec");
        when(nhanVienRepository.findById("NV-DOC01")).thenReturn(Optional.of(doctor));
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

    private LichHen appointment(String id, String time, int duration) {
        LichHen appointment = new LichHen();
        appointment.setMaLichHen(id);
        appointment.setMaBacSi("NV-DOC01");
        appointment.setNgayKham(LocalDate.now().plusDays(2));
        appointment.setGioKham(LocalTime.parse(time));
        appointment.setLoaiKham("KhamDichVu");
        appointment.setThoiGianKhamDuKien(duration);
        appointment.setTrangThai("DaDatLich");
        return appointment;
    }
}