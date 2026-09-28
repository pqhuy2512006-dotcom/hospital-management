/**
 * MEDICARE HIS - Authentication & Navigation Helper
 * Kiểm tra phiên đăng nhập và hỗ trợ đồng bộ thông tin người dùng
 */
(function () {
    const publicPages = ['login.html', ''];
    const path = window.location.pathname;
    const currentPage = path.substring(path.lastIndexOf('/') + 1) || 'index.html';

    if (publicPages.includes(currentPage)) {
        return;
    }

    const userJson = sessionStorage.getItem('authenticatedUser');
    if (!userJson) {
        // Cho phép truy cập hoặc nhắc nhở đăng nhập nếu chưa có phiên
        console.warn('Chưa đăng nhập. Khuyến nghị đăng nhập tại /login.html');
    }

    document.addEventListener('DOMContentLoaded', function () {
        if (userJson) {
            try {
                const user = JSON.parse(userJson);
                const nameDisplay = document.getElementById('userFullnameDisplay');
                if (nameDisplay) {
                    nameDisplay.innerText = `${user.hoTen || user.username} (${user.vaiTro || 'Thành viên'})`;
                }
            } catch (e) {
                console.error(e);
            }
        }
    });
})();
