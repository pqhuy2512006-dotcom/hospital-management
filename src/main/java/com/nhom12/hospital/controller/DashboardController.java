package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.HoaDon;
import com.nhom12.hospital.entity.PhieuKham;
import com.nhom12.hospital.repository.HoaDonRepository;
import com.nhom12.hospital.repository.PhieuKhamRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final HoaDonRepository hoaDonRepository;
    private final PhieuKhamRepository phieuKhamRepository;

    public DashboardController(HoaDonRepository hoaDonRepository,
                               PhieuKhamRepository phieuKhamRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.phieuKhamRepository = phieuKhamRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@RequestParam LocalDate from,
                                                        @RequestParam LocalDate to) {
        if (from.isAfter(to)) {
            return ResponseEntity.badRequest().build();
        }

        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toExclusive = to.plusDays(1).atStartOfDay();
        List<PhieuKham> visits = phieuKhamRepository
            .findByNgayKhamGreaterThanEqualAndNgayKhamLessThan(fromTime, toExclusive);
        List<HoaDon> paidInvoices = hoaDonRepository
            .findByTrangThaiTTAndNgayThanhToanGreaterThanEqualAndNgayThanhToanLessThan(
                "DaThanhToan", fromTime, toExclusive);

        BigDecimal revenue = paidInvoices.stream()
                .map(invoice -> invoice.getTongTienDichVu().subtract(
                        invoice.getBhytChiTra() == null ? BigDecimal.ZERO : invoice.getBhytChiTra()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> diagnosisCounts = new HashMap<>();
        visits.stream()
                .map(PhieuKham::getChanDoan)
                .filter(diagnosis -> diagnosis != null && !diagnosis.isBlank())
                .map(String::trim)
                .forEach(diagnosis -> diagnosisCounts.merge(diagnosis, 1L, Long::sum));

        Map<String, Object> res = new HashMap<>();
        res.put("from", from);
        res.put("to", to);
        res.put("totalVisits", visits.size());
        res.put("totalRevenue", revenue);
        res.put("topDiseases", diagnosisCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> Map.of("diagnosis", entry.getKey(), "count", entry.getValue()))
                .toList());
        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/report", produces = "text/csv")
    public ResponseEntity<byte[]> exportReport(@RequestParam LocalDate from,
                                               @RequestParam LocalDate to) {
        ResponseEntity<Map<String, Object>> statsResponse = getStats(from, to);
        if (!statsResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, Object> stats = statsResponse.getBody();
        StringBuilder csv = new StringBuilder("\uFEFFChỉ số,Giá trị\r\n")
                .append("Khoảng ngày,").append(from).append(" - ").append(to).append("\r\n")
                .append("Lượt khám,").append(stats.get("totalVisits")).append("\r\n")
                .append("Doanh thu đã thanh toán (VND),").append(stats.get("totalRevenue")).append("\r\n\r\n")
                .append("Bệnh phổ biến,Lượt\r\n");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topDiseases = (List<Map<String, Object>>) stats.get("topDiseases");
        for (Map<String, Object> disease : topDiseases) {
            csv.append(csvCell(String.valueOf(disease.get("diagnosis"))))
                    .append(',').append(disease.get("count")).append("\r\n");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"hospital-report-" + from + "-" + to + ".csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String csvCell(String value) {
        String safeValue = value.matches("^[\\s]*[=+@\\-].*") ? "'" + value : value;
        return "\"" + safeValue.replace("\"", "\"\"") + "\"";
    }
}
