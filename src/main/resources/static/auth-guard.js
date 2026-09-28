/**
 * MEDICARE HIS - Authentication & Role-Based Access Control (RBAC) System
 * Phân quyền chi tiết theo vai trò chuẩn y tế (UC12 RBAC)
 */
(function () {
    // 1. Ma trận phân quyền các phân hệ (Module Permission Matrix - Chuẩn 11 tác nhân y tế)
    const ROLE_PERMISSIONS = {
        'dashboard.html': ['QuanTri', 'ADMIN', 'GiamDoc', 'DIRECTOR', 'BGD'],
        'appointments.html': ['QuanTri', 'ADMIN', 'LeTan', 'RECEPTIONIST', 'BenhNhan', 'PATIENT'],
        'patients.html': ['QuanTri', 'ADMIN', 'BacSi', 'DOCTOR', 'LeTan', 'RECEPTIONIST', 'DieuDuong', 'NURSE', 'BenhNhan', 'PATIENT'],
        'doctors.html': ['QuanTri', 'ADMIN', 'NhanSu', 'HR', 'BacSi', 'DOCTOR', 'LeTan', 'RECEPTIONIST'],
        'examination.html': ['QuanTri', 'ADMIN', 'BacSi', 'DOCTOR'],
        'laboratory.html': ['QuanTri', 'ADMIN', 'KTV', 'TECHNICIAN', 'BacSi', 'DOCTOR'],
        'pharmacy.html': ['QuanTri', 'ADMIN', 'DuocSi', 'PHARMACIST'],
        'inpatient.html': ['QuanTri', 'ADMIN', 'DieuDuong', 'NURSE', 'BacSi', 'DOCTOR', 'LeTan', 'RECEPTIONIST'],
        'billing.html': ['QuanTri', 'ADMIN', 'ThuNgan', 'CASHIER', 'BenhNhan', 'PATIENT'],
        'settings.html': ['QuanTri', 'ADMIN']
    };

    // 2. Trang làm việc mặc định theo từng vai trò (Role Landing Pages)
    const ROLE_LANDING_PAGES = {
        'QuanTri': '/dashboard.html',
        'ADMIN': '/dashboard.html',
        'GiamDoc': '/dashboard.html',
        'DIRECTOR': '/dashboard.html',
        'BGD': '/dashboard.html',
        'NhanSu': '/doctors.html',
        'HR': '/doctors.html',
        'BacSi': '/examination.html',
        'DOCTOR': '/examination.html',
        'ThuNgan': '/billing.html',
        'CASHIER': '/billing.html',
        'DuocSi': '/pharmacy.html',
        'PHARMACIST': '/pharmacy.html',
        'KTV': '/laboratory.html',
        'TECHNICIAN': '/laboratory.html',
        'DieuDuong': '/inpatient.html',
        'NURSE': '/inpatient.html',
        'LeTan': '/appointments.html',
        'RECEPTIONIST': '/appointments.html',
        'BenhNhan': '/appointments.html',
        'PATIENT': '/appointments.html'
    };

    // 3. Tên hiển thị & màu sắc nhận diện vai trò
    const ROLE_META = {
        'QuanTri': { name: 'Quản Trị Viên Hệ Thống (ADMIN)', short: 'Quản Trị', color: '#991b1b', bg: '#fee2e2', icon: 'fa-shield-halved' },
        'ADMIN': { name: 'Quản Trị Viên Hệ Thống (ADMIN)', short: 'Quản Trị', color: '#991b1b', bg: '#fee2e2', icon: 'fa-shield-halved' },
        'GiamDoc': { name: 'Ban Giám Đốc (EXECUTIVE / DIRECTOR)', short: 'Ban Giám Đốc', color: '#1e40af', bg: '#dbeafe', icon: 'fa-crown' },
        'DIRECTOR': { name: 'Ban Giám Đốc (EXECUTIVE / DIRECTOR)', short: 'Ban Giám Đốc', color: '#1e40af', bg: '#dbeafe', icon: 'fa-crown' },
        'BGD': { name: 'Ban Giám Đốc (EXECUTIVE / DIRECTOR)', short: 'Ban Giám Đốc', color: '#1e40af', bg: '#dbeafe', icon: 'fa-crown' },
        'NhanSu': { name: 'Quản Lý Nhân Sự (HR MANAGER)', short: 'Nhân Sự', color: '#c026d3', bg: '#fae8ff', icon: 'fa-users-gear' },
        'HR': { name: 'Quản Lý Nhân Sự (HR MANAGER)', short: 'Nhân Sự', color: '#c026d3', bg: '#fae8ff', icon: 'fa-users-gear' },
        'BacSi': { name: 'Bác Sĩ Điều Trị (DOCTOR)', short: 'Bác Sĩ', color: '#0369a1', bg: '#e0f2fe', icon: 'fa-user-doctor' },
        'DOCTOR': { name: 'Bác Sĩ Điều Trị (DOCTOR)', short: 'Bác Sĩ', color: '#0369a1', bg: '#e0f2fe', icon: 'fa-user-doctor' },
        'ThuNgan': { name: 'Thu Ngân / Kế Toán Viện Phí', short: 'Thu Ngân', color: '#b45309', bg: '#fef3c7', icon: 'fa-cash-register' },
        'CASHIER': { name: 'Thu Ngân / Kế Toán Viện Phí', short: 'Thu Ngân', color: '#b45309', bg: '#fef3c7', icon: 'fa-cash-register' },
        'DuocSi': { name: 'Dược Sĩ Kho Dược (PHARMACIST)', short: 'Dược Sĩ', color: '#065f46', bg: '#d1fae5', icon: 'fa-pills' },
        'PHARMACIST': { name: 'Dược Sĩ Kho Dược (PHARMACIST)', short: 'Dược Sĩ', color: '#065f46', bg: '#d1fae5', icon: 'fa-pills' },
        'KTV': { name: 'Kỹ Thuật Viên Cận Lâm Sàng (CLS)', short: 'KTV Xét Nghiệm', color: '#0f766e', bg: '#ccfbf1', icon: 'fa-flask-vial' },
        'TECHNICIAN': { name: 'Kỹ Thuật Viên Cận Lâm Sàng (CLS)', short: 'KTV Xét Nghiệm', color: '#0f766e', bg: '#ccfbf1', icon: 'fa-flask-vial' },
        'DieuDuong': { name: 'Điều Dưỡng Nội Trú (NURSE)', short: 'Điều Dưỡng', color: '#7c3aed', bg: '#ede9fe', icon: 'fa-bed-pulse' },
        'NURSE': { name: 'Điều Dưỡng Nội Trú (NURSE)', short: 'Điều Dưỡng', color: '#7c3aed', bg: '#ede9fe', icon: 'fa-bed-pulse' },
        'LeTan': { name: 'Tiếp Đón / Lễ Tân (RECEPTIONIST)', short: 'Lễ Tân', color: '#4338ca', bg: '#e0e7ff', icon: 'fa-calendar-check' },
        'RECEPTIONIST': { name: 'Tiếp Đón / Lễ Tân (RECEPTIONIST)', short: 'Lễ Tân', color: '#4338ca', bg: '#e0e7ff', icon: 'fa-calendar-check' },
        'BenhNhan': { name: 'Bệnh Nhân (PATIENT)', short: 'Bệnh Nhân', color: '#0284c7', bg: '#e0f2fe', icon: 'fa-hospital-user' },
        'PATIENT': { name: 'Bệnh Nhân (PATIENT)', short: 'Bệnh Nhân', color: '#0284c7', bg: '#e0f2fe', icon: 'fa-hospital-user' }
    };

    // 4. Interceptor tự động gắn thông tin vai trò vào mọi lệnh gọi API fetch
    const originalFetch = window.fetch;
    window.fetch = function (url, options = {}) {
        options = options || {};
        options.headers = options.headers || {};
        const userStr = sessionStorage.getItem('authenticatedUser');
        if (userStr) {
            try {
                const user = JSON.parse(userStr);
                if (user.vaiTro) {
                    if (options.headers instanceof Headers) {
                        options.headers.set('X-User-Role', user.vaiTro);
                        options.headers.set('X-User-Name', user.username || '');
                    } else if (Array.isArray(options.headers)) {
                        options.headers.push(['X-User-Role', user.vaiTro]);
                        options.headers.push(['X-User-Name', user.username || '']);
                    } else {
                        options.headers['X-User-Role'] = user.vaiTro;
                        options.headers['X-User-Name'] = user.username || '';
                    }
                }
            } catch (e) {}
        }
        return originalFetch(url, options);
    };

    // 5. Kiểm tra quyền truy cập trang hiện tại
    const path = window.location.pathname;
    const currentPage = path.substring(path.lastIndexOf('/') + 1) || 'index.html';
    const publicPages = ['login.html', 'index.html', ''];

    if (publicPages.includes(currentPage)) {
        return;
    }

    const userJson = sessionStorage.getItem('authenticatedUser');
    if (!userJson) {
        // Chưa đăng nhập -> Chuyển hướng về login
        window.location.replace('/login.html?redirect=' + encodeURIComponent(currentPage));
        return;
    }

    let currentUser = null;
    try {
        currentUser = JSON.parse(userJson);
    } catch (e) {
        sessionStorage.removeItem('authenticatedUser');
        window.location.replace('/login.html');
        return;
    }

    const userRole = currentUser.vaiTro || 'BacSi';
    const allowedRoles = ROLE_PERMISSIONS[currentPage];

    // Hàm chuẩn hóa vai trò so sánh
    function checkPermission(role, allowedList) {
        if (!allowedList) return true;
        const normRole = role.toUpperCase();
        return allowedList.some(r => r.toUpperCase() === normRole);
    }

    const hasAccess = checkPermission(userRole, allowedRoles);

    // Xử lý giao diện khi DOM sẵn sàng
    document.addEventListener('DOMContentLoaded', function () {
        const meta = ROLE_META[userRole] || {
            name: userRole,
            short: userRole,
            color: '#0284c7',
            bg: '#e0f2fe',
            icon: 'fa-user'
        };

        // A. Cập nhật Badge người dùng trên Top Header
        const header = document.querySelector('.top-header');
        if (header) {
            // Kiểm tra xem đã có badge chưa
            if (!document.getElementById('authHeaderBadge')) {
                const badgeContainer = document.createElement('div');
                badgeContainer.id = 'authHeaderBadge';
                badgeContainer.style.cssText = `
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    margin-left: auto;
                    margin-right: 16px;
                    background: #f8fafc;
                    border: 1px solid #e2e8f0;
                    padding: 6px 14px;
                    border-radius: 30px;
                `;

                badgeContainer.innerHTML = `
                    <div style="text-align: right; line-height: 1.25;">
                        <div style="font-weight: 700; font-size: 13.5px; color: #0f172a;">${currentUser.hoTen || currentUser.username}</div>
                        <div style="font-size: 11.5px; color: #64748b;">${meta.name}</div>
                    </div>
                    <span style="
                        background: ${meta.bg};
                        color: ${meta.color};
                        font-weight: 700;
                        font-size: 11.5px;
                        padding: 4px 10px;
                        border-radius: 20px;
                        display: inline-flex;
                        align-items: center;
                        gap: 5px;
                    ">
                        <i class="fa-solid ${meta.icon}"></i> ${meta.short}
                    </span>
                `;

                // Chèn trước nút Đăng xuất
                const logoutBtn = header.querySelector('.btn-logout');
                if (logoutBtn) {
                    header.insertBefore(badgeContainer, logoutBtn);
                } else {
                    header.appendChild(badgeContainer);
                }
            }
        }

        // B. Lọc Menu Sidebar theo quyền của vai trò (Dynamic Sidebar Filtering)
        const sidebarLinks = document.querySelectorAll('.sidebar-menu li a');
        sidebarLinks.forEach(link => {
            const href = link.getAttribute('href');
            if (href) {
                const targetPage = href.replace('/', '').split('?')[0];
                const pageAllowed = ROLE_PERMISSIONS[targetPage];
                if (pageAllowed && !checkPermission(userRole, pageAllowed)) {
                    // Ẩn menu không có quyền truy cập
                    link.parentElement.style.display = 'none';
                }
            }
        });

        // C. Nếu KHÔNG CÓ QUYỀN vào trang hiện tại: Hiển thị màn hình 403 Forbidden
        if (!hasAccess) {
            render403Screen(meta);
        }
    });

    function render403Screen(meta) {
        // Ẩn nội dung chính để bảo mật dữ liệu
        const main = document.querySelector('.main-container') || document.querySelector('.content-body') || document.body;
        
        const overlay = document.createElement('div');
        overlay.id = 'forbiddenOverlay';
        overlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100vw;
            height: 100vh;
            background: rgba(15, 23, 42, 0.95);
            backdrop-filter: blur(8px);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 999999;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            color: #ffffff;
            padding: 20px;
            box-sizing: border-box;
        `;

        const homeUrl = ROLE_LANDING_PAGES[userRole] || '/dashboard.html';
        const allowedNames = (allowedRoles || []).map(r => (ROLE_META[r] ? ROLE_META[r].short : r)).join(', ');

        overlay.innerHTML = `
            <div style="
                background: #1e293b;
                border: 1px solid #334155;
                border-radius: 16px;
                max-width: 540px;
                width: 100%;
                padding: 36px 30px;
                text-align: center;
                box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
            ">
                <div style="
                    width: 72px;
                    height: 72px;
                    margin: 0 auto 20px;
                    border-radius: 50%;
                    background: rgba(239, 68, 68, 0.15);
                    color: #ef4444;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 34px;
                ">
                    <i class="fa-solid fa-ban"></i>
                </div>

                <h2 style="font-size: 22px; font-weight: 800; margin-bottom: 8px; color: #f87171;">
                    TRUY CẬP BỊ GIỚI HẠN (403 FORBIDDEN)
                </h2>

                <p style="font-size: 14px; color: #94a3b8; line-height: 1.6; margin-bottom: 20px;">
                    Xin chào <strong>${currentUser.hoTen || currentUser.username}</strong>!<br>
                    Tài khoản của bạn đang có vai trò là <strong style="color: ${meta.color}; background: ${meta.bg}; padding: 2px 8px; border-radius: 6px;">${meta.name}</strong>.<br>
                    Bạn <strong>không có quyền truy cập</strong> vào phân hệ này theo chính sách bảo mật và phân quyền y tế (RBAC UC12).
                </p>

                <div style="background: rgba(255,255,255,0.05); border: 1px solid #334155; border-radius: 8px; padding: 12px; margin-bottom: 24px; font-size: 13px; color: #cbd5e1; text-align: left;">
                    <i class="fa-solid fa-circle-info" style="color: #38bdf8; margin-right: 6px;"></i>
                    <strong>Phân hệ này chỉ cho phép:</strong> ${allowedNames}
                </div>

                <div style="display: flex; gap: 12px; justify-content: center;">
                    <button onclick="window.location.href='${homeUrl}'" style="
                        background: #0284c7;
                        color: #ffffff;
                        border: none;
                        border-radius: 8px;
                        padding: 12px 20px;
                        font-weight: 600;
                        font-size: 14px;
                        cursor: pointer;
                        display: flex;
                        align-items: center;
                        gap: 8px;
                    ">
                        <i class="fa-solid fa-house-chimney-medical"></i> Về Phân Hệ Làm Việc Của Tôi
                    </button>

                    <button onclick="logout()" style="
                        background: transparent;
                        color: #94a3b8;
                        border: 1px solid #475569;
                        border-radius: 8px;
                        padding: 12px 18px;
                        font-weight: 600;
                        font-size: 14px;
                        cursor: pointer;
                    ">
                        <i class="fa-solid fa-arrow-right-from-bracket"></i> Đăng Xuất
                    </button>
                </div>
            </div>
        `;

        document.body.appendChild(overlay);
    }

    // Định nghĩa hàm logout toàn cục
    window.logout = function () {
        sessionStorage.removeItem('authenticatedUser');
        window.location.href = '/login.html';
    };
})();
