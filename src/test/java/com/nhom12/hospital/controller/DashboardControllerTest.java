package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.HoaDon;
import com.nhom12.hospital.entity.PhieuKham;
import com.nhom12.hospital.repository.HoaDonRepository;
import com.nhom12.hospital.repository.PhieuKhamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardControllerTest {

    private final HoaDonRepository hoaDonRepository = mock(HoaDonRepository.class);
    private final PhieuKhamRepository phieuKhamRepository = mock(PhieuKhamRepository.class);
    private final DashboardController controller = new DashboardController(hoaDonRepository, phieuKhamRepository);

    @Test
    void reportsVisitsPaidRevenueAndMostCommonDiagnosesForSelectedDays() {
        LocalDate from = LocalDate.of(2026, 9, 1);
        LocalDate to = LocalDate.of(2026, 9, 30);
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime endExclusive = to.plusDays(1).atStartOfDay();

        PhieuKham first = visit("Cảm cúm");
        PhieuKham second = visit("Cảm cúm");
        PhieuKham third = visit("Viêm họng");
        when(phieuKhamRepository.findByNgayKhamGreaterThanEqualAndNgayKhamLessThan(start, endExclusive))
                .thenReturn(List.of(first, second, third));

        HoaDon paidInvoice = new HoaDon();
        paidInvoice.setTongTienDichVu(new BigDecimal("150000"));
        paidInvoice.setBhytChiTra(new BigDecimal("50000"));
        when(hoaDonRepository.findByTrangThaiTTAndNgayThanhToanGreaterThanEqualAndNgayThanhToanLessThan(
                "DaThanhToan", start, endExclusive)).thenReturn(List.of(paidInvoice));

        ResponseEntity<Map<String, Object>> response = controller.getStats(from, to);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(3, response.getBody().get("totalVisits"));
        assertEquals(new BigDecimal("100000"), response.getBody().get("totalRevenue"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topDiseases = (List<Map<String, Object>>) response.getBody().get("topDiseases");
        assertEquals(Map.of("diagnosis", "Cảm cúm", "count", 2L), topDiseases.getFirst());
    }

    private PhieuKham visit(String diagnosis) {
        PhieuKham visit = new PhieuKham();
        visit.setChanDoan(diagnosis);
        return visit;
    }
}
