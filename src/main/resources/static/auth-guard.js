/**
 * MEDICARE HIS - Authentication & Role-Based Access Control (RBAC) System
 * Phân quyền chi tiết theo vai trò chuẩn y tế (UC12 RBAC)
 */
(function () {
    // 1. Ma trận phân quyền các phân hệ (Module Permission Matrix - Chuẩn 11 tác nhân y tế)
    const ROLE_PERMISSIONS = {
        'dashboard.html': ['QuanTri', 'ADMIN'],
        'appointments.html': ['LeTan', 'RECEPTIONIST', 'BenhNhan', 'PATIENT'],
        'patients.html': ['BacSi', 'DOCTOR', 'LeTan', 'RECEPTIONIST', 'DieuDuong', 'NURSE', 'BenhNhan', 'PATIENT'],
        'doctors.html': ['NhanSu', 'HR', 'LeTan', 'RECEPTIONIST'],
        'examination.html': ['BacSi', 'DOCTOR'],
        'laboratory.html': ['KTV', 'TECHNICIAN', 'BacSi', 'DOCTOR'],
        'pharmacy.html': ['DuocSi', 'PHARMACIST'],
        'inpatient.html': ['DieuDuong', 'NURSE'],
        'billing.html': ['ThuNgan', 'CASHIER', 'BenhNhan', 'PATIENT'],
        'settings.html': ['QuanTri', 'ADMIN', 'BacSi', 'DOCTOR', 'LeTan', 'RECEPTIONIST', 'DuocSi', 'PHARMACIST', 'KTV', 'TECHNICIAN', 'DieuDuong', 'NURSE', 'ThuNgan', 'CASHIER']
    };

    // 2. Trang làm việc mặc định theo từng vai trò (Role Landing Pages)
    const ROLE_LANDING_PAGES = {
        'QuanTri': '/dashboard.html',
        'ADMIN': '/dashboard.html',
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

    // Session cookie được server kiểm tra; role header không được dùng để cấp quyền.
    const originalFetch = window.fetch;
    window.fetch = function (url, options = {}) {
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

    if (!hasAccess) {
        window.location.replace(ROLE_LANDING_PAGES[userRole] || '/login.html');
        return;
    }

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
                    logoutBtn.parentElement.insertBefore(badgeContainer, logoutBtn);
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
                    link.parentElement.remove();
                }
            }
        });

        if (userRole !== 'QuanTri' && userRole !== 'ADMIN') {
            const profileLink = document.querySelector('.sidebar-menu a[href="/settings.html"]');
            if (profileLink) profileLink.innerHTML = '<i class="fa-solid fa-user-pen"></i> Thông tin cá nhân';
            
            if (currentPage === 'settings.html') {
                document.querySelectorAll('.tab-btn:not(:first-child)').forEach(button => button.remove());
                document.querySelectorAll('.tab-content:not(#profileTab)').forEach(tab => tab.remove());
                const title = document.querySelector('.top-header .header-title h2');
                if (title) title.textContent = 'Thông tin cá nhân';
            }
        }

        if (userRole === 'BenhNhan' || userRole === 'PATIENT') {
            const patientMenuLabels = {
                '/appointments.html': 'Lịch khám của tôi',
                '/patients.html': 'Hồ sơ cá nhân',
                '/billing.html': 'Hóa đơn của tôi'
            };
            Object.entries(patientMenuLabels).forEach(([href, label]) => {
                const link = document.querySelector(`.sidebar-menu a[href="${href}"]`);
                if (link) link.innerHTML = `<i class="${href === '/appointments.html' ? 'fa-solid fa-calendar-check' : href === '/patients.html' ? 'fa-solid fa-id-card' : 'fa-solid fa-file-invoice-dollar'}"></i> ${label}`;
            });
            const titles = {
                'appointments.html': 'Đặt lịch khám trực tuyến',
                'patients.html': 'Hồ sơ cá nhân',
                'billing.html': 'Hóa đơn viện phí của tôi'
            };
            const title = document.querySelector('.top-header .header-title h2');
            if (title && titles[currentPage]) title.textContent = titles[currentPage];
        }

        if (userRole === 'BacSi' || userRole === 'DOCTOR') {
            const menuLabels = {
                '/examination.html': 'Khám bệnh & Đơn thuốc',
                '/laboratory.html': 'Kết quả Cận lâm sàng (CLS)',
                '/settings.html': 'Thông tin cá nhân'
            };
            Object.entries(menuLabels).forEach(([href, label]) => {
                const link = document.querySelector(`.sidebar-menu a[href="${href}"]`);
                if (link) link.innerHTML = `<i class="${href === '/settings.html' ? 'fa-solid fa-user-pen' : href === '/laboratory.html' ? 'fa-solid fa-flask-vial' : 'fa-solid fa-stethoscope'}"></i> ${label}`;
            });

            if (currentPage === 'patients.html') {
                document.querySelector('.btn-create-patient')?.remove();
                document.getElementById('patientModal')?.remove();
            }
        }
    });

    // Định nghĩa hàm logout toàn cục
    window.logout = async function () {
        try {
            await originalFetch('/api/v1/auth/logout', { method: 'POST' });
        } finally {
            sessionStorage.removeItem('authenticatedUser');
            window.location.href = '/login.html';
        }
    };
})();
