package com.nhom12.hospital.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class RoleAuthorizationFilter extends OncePerRequestFilter {

    private String normalizeRole(String rawRole) {
        if (rawRole == null || rawRole.trim().isEmpty()) return "";
        String r = rawRole.trim().toUpperCase();
        if (r.contains("GIAMDOC") || r.contains("BGD") || r.contains("DIRECTOR") || r.contains("EXECUTIVE")) return "GiamDoc";
        if (r.contains("NHANSU") || r.contains("HR")) return "NhanSu";
        if (r.contains("ADMIN") || r.contains("QUANTRI")) return "QuanTri";
        if (r.contains("DOC") || r.contains("BACSI")) return "BacSi";
        if (r.contains("CASHIER") || r.contains("THUNGAN")) return "ThuNgan";
        if (r.contains("PHARM") || r.contains("DUOC")) return "DuocSi";
        if (r.contains("RECEPT") || r.contains("LETAN") || r.contains("TIEPDON")) return "LeTan";
        if (r.contains("TECH") || r.contains("KTV") || r.contains("KYTHUAT")) return "KTV";
        if (r.contains("NURSE") || r.contains("DIEUDUONG")) return "DieuDuong";
        if (r.contains("BENHNHAN") || r.contains("PATIENT")) return "BenhNhan";
        return rawRole.trim();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod().toUpperCase();

        // Bỏ qua các endpoint công khai và GET dữ liệu
        if (path.startsWith("/api/v1/auth") || method.equals("GET") || method.equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        String rawRole = request.getHeader("X-User-Role");
        if (rawRole != null && !rawRole.trim().isEmpty()) {
            String role = normalizeRole(rawRole);

            // 4.9 Quản trị viên hệ thống có toàn quyền mọi endpoint
            if ("QuanTri".equals(role)) {
                filterChain.doFilter(request, response);
                return;
            }

            boolean isAllowed = true;
            String requiredRoleDesc = "";

            // 1. Phân hệ Khám bệnh & Kê đơn (4.3 Bác sĩ)
            if (path.startsWith("/api/v1/phieukham") && method.equals("POST")) {
                if (!"BacSi".equals(role)) {
                    isAllowed = false;
                    requiredRoleDesc = "Bác Sĩ Điều Trị (DOCTOR)";
                }
            }
            // 2. Phân hệ Kho Dược: Nhập kho & Phát thuốc (4.5 Dược sĩ)
            else if ((path.startsWith("/api/v1/thuoc/nhapkho") || path.contains("/donthuoc/") && path.endsWith("/xuat")) && method.equals("POST")) {
                if (!"DuocSi".equals(role)) {
                    isAllowed = false;
                    requiredRoleDesc = "Dược Sĩ Kho Dược (PHARMACIST)";
                }
            }
            // 3. Phân hệ Viện phí: Thu tiền & Đóng hóa đơn (4.6 Thu ngân, 4.1 Bệnh nhân thanh toán online)
            else if (path.startsWith("/api/v1/hoadon") && path.endsWith("/thanhtoan") && method.equals("PUT")) {
                if (!"ThuNgan".equals(role) && !"BenhNhan".equals(role)) {
                    isAllowed = false;
                    requiredRoleDesc = "Thu Ngân (CASHIER) hoặc Bệnh Nhân (Online)";
                }
            }
            // 4. Phân hệ Cận lâm sàng: Nhập & trả kết quả xét nghiệm (4.4 KTV)
            else if (path.startsWith("/api/v1/cls") && path.endsWith("/ketqua") && method.equals("PUT")) {
                if (!"KTV".equals(role)) {
                    isAllowed = false;
                    requiredRoleDesc = "Kỹ Thuật Viên Cận Lâm Sàng (KTV)";
                }
            }
            // 5. Phân hệ Giường nội trú: Tiếp nhận & Xuất viện (4.7 Điều dưỡng, 4.3 Bác sĩ)
            else if (path.startsWith("/api/v1/noitru") && (method.equals("POST") || method.equals("PUT"))) {
                if (!"DieuDuong".equals(role) && !"BacSi".equals(role)) {
                    isAllowed = false;
                    requiredRoleDesc = "Điều Dưỡng Nội Trú (NURSE) hoặc Bác Sĩ";
                }
            }
            // 6. Phân hệ Quản lý nhân sự: Thêm/Sửa nhân sự & Phân công lịch trực (4.8 Quản lý nhân sự)
            else if ((path.startsWith("/api/v1/nhanvien") || path.startsWith("/api/v1/lichtruc")) && (method.equals("POST") || method.equals("PUT") || method.equals("DELETE"))) {
                if (!"NhanSu".equals(role)) {
                    isAllowed = false;
                    requiredRoleDesc = "Quản Lý Nhân Sự (HR MANAGER)";
                }
            }
            // 7. Phân hệ Đặt lịch & Tiếp đón (4.2 Lễ tân, 4.1 Bệnh nhân, 4.3 Bác sĩ)
            else if (path.startsWith("/api/v1/lichhen") && (method.equals("POST") || method.equals("PUT"))) {
                if (!"LeTan".equals(role) && !"BenhNhan".equals(role) && !"BacSi".equals(role)) {
                    isAllowed = false;
                    requiredRoleDesc = "Tiếp Đón / Lễ Tân, Bác Sĩ hoặc Bệnh Nhân";
                }
            }
            // 8. Phân hệ Cấu hình hệ thống & Cấp tài khoản nhân sự (4.9 Quản trị viên)
            else if (path.startsWith("/api/v1/taikhoan") && (method.equals("POST") || method.equals("PUT") || method.equals("DELETE"))) {
                // Cho phép đổi mật khẩu cá nhân
                if (!path.endsWith("/change-password")) {
                    isAllowed = false;
                    requiredRoleDesc = "Quản Trị Viên Hệ Thống (ADMIN)";
                }
            }

            if (!isAllowed) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(String.format(
                        "{\"success\": false, \"status\": 403, \"message\": \"Truy cập bị từ chối (403 Forbidden): Vai trò '%s' không có thẩm quyền thực hiện nghiệp vụ này. Yêu cầu vai trò: %s.\"}",
                        rawRole, requiredRoleDesc
                ));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
