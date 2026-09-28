/**
 * MEDICARE HIS - RBAC Frontend Guard
 * Tự động kiểm tra phiên đăng nhập và lọc menu sidebar theo vai trò
 */
(async function () {
    // 1. Ma trận phân quyền chi tiết
    const rolePermissions = {
        'QuanTri': ['dashboard.html', 'settings.html'],
        'BacSi': ['examination.html', 'laboratory.html', 'patients.html'],
        'ThuNgan': ['billing.html', 'patients.html'],
        'DuocSi': ['pharmacy.html'],
        'LeTan': ['appointments.html', 'patients.html', 'doctors.html'],
        'KTV': ['laboratory.html'],
        'DieuDuong': ['inpatient.html', 'patients.html'],
        'BenhNhan': ['appointments.html', 'billing.html'],
        'QuanLyNhanSu': ['settings.html'],
        'BanGiamDoc': ['dashboard.html', 'settings.html']
    };

    const roleAliasMap = {
        ADMIN: 'QuanTri',
        QUANTRI: 'QuanTri',
        DOCTOR: 'BacSi',
        BACSI: 'BacSi',
        NURSE: 'DieuDuong',
        DIEUDUONG: 'DieuDuong',
        RECEPTIONIST: 'LeTan',
        LETAN: 'LeTan',
        PHARMACIST: 'DuocSi',
        DUOCSI: 'DuocSi',
        TECHNICIAN: 'KTV',
        KTV: 'KTV',
        CASHIER: 'ThuNgan',
        THUNGAN: 'ThuNgan',
        PATIENT: 'BenhNhan',
        BENHNHAN: 'BenhNhan',
        HR: 'QuanLyNhanSu',
        NHANSU: 'QuanLyNhanSu',
        QUANLYNHANSU: 'QuanLyNhanSu',
        DIRECTOR: 'BanGiamDoc',
        GIAMDOC: 'BanGiamDoc',
        BANGIAMDOC: 'BanGiamDoc'
    };

    function normalizeRole(role) {
        if (!role) return 'BenhNhan';
        const raw = String(role).trim();
        const key = raw.toUpperCase().replace(/[^A-Z0-9]/g, '');
        return roleAliasMap[key] || raw;
    }

    // Trang công khai không cần đăng nhập
    const publicPages = ['login.html', ''];

    // 2. Lấy trang hiện tại trên URL
    const path = window.location.pathname;
    const currentPage = path.substring(path.lastIndexOf('/') + 1) || 'index.html';

    // Bỏ qua kiểm tra nếu đang ở trang đăng nhập
    if (publicPages.includes(currentPage)) {
        return;
    }

    window.logout = async function () {
        try {
            const csrfResponse = await fetch('/api/v1/auth/csrf');
            if (!csrfResponse.ok) throw new Error('Không lấy được mã bảo mật.');
            const csrfToken = (await csrfResponse.json()).token;
            const response = await fetch('/api/v1/auth/logout', {
                method: 'POST',
                headers: { 'X-XSRF-TOKEN': csrfToken }
            });
            if (!response.ok) throw new Error('Máy chủ không xác nhận đăng xuất.');
            sessionStorage.removeItem('authenticatedUser');
            window.location.href = '/login.html';
        } catch {
            alert('Không thể đăng xuất an toàn. Vui lòng thử lại.');
        }
    };

    // 3. Lấy danh tính và vai trò từ JWT đã được backend xác minh
    let user;
    try {
        const response = await fetch('/api/v1/auth/me', {
            headers: { Accept: 'application/json' }
        });
        if (!response.ok) throw new Error('Phiên đăng nhập không hợp lệ.');

        const account = await response.json();
        const storedUser = JSON.parse(sessionStorage.getItem('authenticatedUser') || '{}');
        user = {
            ...storedUser,
            username: account.username,
            role: account.role,
            vaiTro: account.role,
            fullName: storedUser.fullName || account.username
        };
        sessionStorage.setItem('authenticatedUser', JSON.stringify(user));
    } catch {
        sessionStorage.removeItem('authenticatedUser');
        window.location.href = '/login.html';
        return;
    }

    const userRole = normalizeRole(user.role || user.vaiTro);
    const allowedPages = rolePermissions[userRole] || [];

    // 4. Chặn truy cập trái quyền (Nếu cố tình gõ link vào thanh địa chỉ)
    const isAllowed = allowedPages.includes(currentPage);
    if (!isAllowed) {
        alert(`Tài khoản vai trò [${userRole}] không có quyền truy cập vào phân hệ này!`);
        // Chuyển về trang đầu tiên họ được phép truy cập
        window.location.href = `/${allowedPages[0] || 'login.html'}`;
        return;
    }

    // 5. Khi DOM tải xong -> Tự động ẩn các nút menu mà người đó không có quyền
    function initializePage() {
        // Cập nhật tên người dùng lên góc trên nếu có thẻ hiển thị
        const usernameDisplay = document.getElementById('userFullnameDisplay');
        if (usernameDisplay) {
            usernameDisplay.innerText = `${user.fullName} (${user.role})`;
        }

        const menuItems = document.querySelectorAll('.sidebar-menu li a');
        menuItems.forEach(item => {
            const href = item.getAttribute('href');
            if (href) {
                const targetPage = href.replace('/', '');
                // Nếu trang trong menu không thuộc danh sách được phép -> ẩn mục đó đi
                if (!allowedPages.includes(targetPage)) {
                    item.parentElement.style.display = 'none';
                }
            }
        });

        if (userRole !== 'QuanTri') {
            document.querySelectorAll('[data-admin-only="true"]').forEach(element => {
                element.hidden = true;
            });
        }

        if (userRole === 'QuanTri') {
            document.querySelectorAll('[data-profile-update="true"]').forEach(element => {
                element.hidden = true;
            });
            const profileTabTitle = document.getElementById('profileTabTitle');
            if (profileTabTitle) {
                profileTabTitle.innerText = 'Đổi Mật Khẩu (UC04)';
            }
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initializePage, { once: true });
    } else {
        initializePage();
    }

})();