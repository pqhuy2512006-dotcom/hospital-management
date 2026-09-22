/**
 * MEDICARE HIS - RBAC Frontend Guard
 * Tự động kiểm tra phiên đăng nhập và lọc menu sidebar theo vai trò
 */
(function () {
    // 1. Ma trận phân quyền chi tiết
    const rolePermissions = {
        'ADMIN': ['*'],
        'DOCTOR': ['dashboard.html', 'examination.html', 'laboratory.html', 'patients.html'],
        'CASHIER': ['dashboard.html', 'billing.html', 'patients.html'],
        'PHARMACIST': ['dashboard.html', 'pharmacy.html'],
        'RECEPTIONIST': ['dashboard.html', 'appointments.html', 'patients.html', 'doctors.html'],
        'TECHNICIAN': ['dashboard.html', 'laboratory.html'],
        'NURSE': ['dashboard.html', 'inpatient.html', 'patients.html'],
        'PATIENT': ['appointments.html', 'billing.html']
    };

    // Trang công khai không cần đăng nhập
    const publicPages = ['login.html', ''];

    // 2. Lấy trang hiện tại trên URL
    const path = window.location.pathname;
    const currentPage = path.substring(path.lastIndexOf('/') + 1) || 'index.html';

    // Bỏ qua kiểm tra nếu đang ở trang đăng nhập
    if (publicPages.includes(currentPage)) {
        return;
    }

    // 3. Đọc thông tin người dùng từ sessionStorage
    const userJson = sessionStorage.getItem('authenticatedUser');

    // Nếu chưa đăng nhập -> đuổi về trang login.html
    if (!userJson) {
        alert('Phiên làm việc đã hết hạn hoặc bạn chưa đăng nhập! Vui lòng đăng nhập.');
        window.location.href = '/login.html';
        return;
    }

    const user = JSON.parse(userJson);
    const userRole = user.role || 'GUEST';
    const allowedPages = rolePermissions[userRole] || [];

    // 4. Chặn truy cập trái quyền (Nếu cố tình gõ link vào thanh địa chỉ)
    const isAllowed = allowedPages.includes('*') || allowedPages.includes(currentPage);
    if (!isAllowed) {
        alert(`Tài khoản vai trò [${userRole}] không có quyền truy cập vào phân hệ này!`);
        // Chuyển về trang đầu tiên họ được phép truy cập
        const redirectPage = allowedPages[0] !== '*' ? allowedPages[0] : 'dashboard.html';
        window.location.href = `/${redirectPage}`;
        return;
    }

    // 5. Khi DOM tải xong -> Tự động ẩn các nút menu mà người đó không có quyền
    document.addEventListener('DOMContentLoaded', function () {
        // Cập nhật tên người dùng lên góc trên nếu có thẻ hiển thị
        const usernameDisplay = document.getElementById('userFullnameDisplay');
        if (usernameDisplay) {
            usernameDisplay.innerText = `${user.fullName} (${user.role})`;
        }

        if (allowedPages.includes('*')) return; // ADMIN thì thấy tất cả

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
    });
})();