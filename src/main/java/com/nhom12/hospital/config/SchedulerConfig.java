package com.nhom12.hospital.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final JdbcTemplate jdbcTemplate;

    public SchedulerConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Chạy tự động mỗi 1 phút (60000 ms)
    @Scheduled(fixedRate = 60000)
    public void autoCancelOverdueAppointments() {
        String sql = "UPDATE dbo.LichHen " +
                     "SET TrangThai = 'DaHuy' " +
                     "WHERE TrangThai = 'DaDatLich' " +
                     "  AND DATEDIFF(MINUTE, CAST(NgayKham AS DATETIME) + CAST(GioKham AS DATETIME), GETDATE()) > 10";
        
        int rowsAffected = jdbcTemplate.update(sql);
        if (rowsAffected > 0) {
            System.out.println("[Auto-Task] Đã tự động hủy " + rowsAffected + " lịch hẹn quá hạn.");
        }

        // Tự động tạo thông báo nhắc lịch khám ngày mai
        int notifyCount = jdbcTemplate.update("EXEC dbo.sp_TaoThongBaoNhacLich");
        if (notifyCount > 0) {
            System.out.println("[Auto-Task] Đã gửi " + notifyCount + " thông báo nhắc lịch khám ngày mai.");
        }
    }
}
