package com.nhom12.hospital.controller;

import com.nhom12.hospital.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardControllerTest {

    private final DashboardService dashboardService = mock(DashboardService.class);
    private final DashboardController controller = new DashboardController(dashboardService);

    @Test
    void reportsVisitsPaidRevenueAndMostCommonDiagnosesForSelectedDays() {
        LocalDate from = LocalDate.of(2026, 9, 1);
        LocalDate to = LocalDate.of(2026, 9, 30);

        Map<String, Object> mockStats = Map.of(
                "totalVisits", 3,
                "totalRevenue", new BigDecimal("100000"),
                "topDiseases", List.of(Map.of("diagnosis", "Cúm", "count", 2L))
        );

        when(dashboardService.getStats(from, to)).thenReturn(mockStats);

        ResponseEntity<Map<String, Object>> response = controller.getStats(from, to);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(3, response.getBody().get("totalVisits"));
        assertEquals(new BigDecimal("100000"), response.getBody().get("totalRevenue"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topDiseases = (List<Map<String, Object>>) response.getBody().get("topDiseases");
        assertEquals(Map.of("diagnosis", "Cúm", "count", 2L), topDiseases.getFirst());
    }
}
