package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.CreateStaffAccountRequest;
import com.nhom12.hospital.dto.StaffAccountResponse;
import com.nhom12.hospital.dto.UpdateStaffRoleRequest;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import com.nhom12.hospital.security.AccountStatus;
import com.nhom12.hospital.security.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/accounts")
public class AdminAccountController {

    private static final String DEFAULT_TEMPORARY_PASSWORD = "123456";

    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAccountController(
            TaiKhoanRepository taiKhoanRepository,
            NhanVienRepository nhanVienRepository,
            PasswordEncoder passwordEncoder) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_STAFF_ACCOUNT_ISSUE')")
    public List<StaffAccountResponse> listStaffAccounts() {
        List<TaiKhoan> accounts = taiKhoanRepository.findAll().stream()
                .filter(account -> {
                    Role role = Role.fromValue(account.getVaiTro());
                    return role != Role.BENH_NHAN && role != Role.QUAN_TRI;
                })
                .toList();
        Map<Long, NhanVien> employeesByAccountId = accounts.isEmpty()
            ? Map.of()
            : nhanVienRepository.findAllByMaTaiKhoanIn(
                    accounts.stream().map(TaiKhoan::getMaTaiKhoan).toList())
                .stream()
                .collect(Collectors.toMap(NhanVien::getMaTaiKhoan, employee -> employee));

        return accounts.stream()
                .map(account -> toResponse(account, employeesByAccountId.get(account.getMaTaiKhoan())))
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_STAFF_ACCOUNT_ISSUE')")
    @Transactional
    public StaffAccountResponse createStaffAccount(@Valid @RequestBody CreateStaffAccountRequest request) {
        String username = request.username().trim();
        String email = request.email().trim();
        Role role = Role.fromValue(request.role());
        if (role == Role.BENH_NHAN || role == Role.QUAN_TRI) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ được cấp tài khoản cho nhân viên, không thể tạo tài khoản bệnh nhân hoặc Admin.");
        }
        if (taiKhoanRepository.findByTenDangNhap(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên đăng nhập đã tồn tại.");
        }
        if (taiKhoanRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã được sử dụng.");
        }
        TaiKhoan account = new TaiKhoan();
        account.setTenDangNhap(username);
        account.setEmail(email);
        account.setMatKhauHash(passwordEncoder.encode(request.password()));
        account.setVaiTro(role.getValue());
        account.setTrangThai(false);
        account.setNgayTao(LocalDateTime.now());
        TaiKhoan savedAccount = taiKhoanRepository.saveAndFlush(account);

        return toResponse(savedAccount, null);
    }

    @PatchMapping("/{accountId}/role")
    @PreAuthorize("hasAuthority('PERMISSION_RBAC_MANAGE')")
    @Transactional
    public StaffAccountResponse updateStaffRole(
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateStaffRoleRequest request) {
        TaiKhoan account = findEmployeeAccount(accountId);
        Role role = Role.fromValue(request.role());
        if (role == Role.BENH_NHAN || role == Role.QUAN_TRI) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ được gán vai trò nhân viên hợp lệ.");
        }

        account.setVaiTro(role.getValue());
        TaiKhoan savedAccount = taiKhoanRepository.save(account);
        NhanVien employee = nhanVienRepository.findByMaTaiKhoan(accountId).orElse(null);
        if (employee != null) {
            employee.setVaiTro(role.getValue());
            nhanVienRepository.save(employee);
        }
        return toResponse(savedAccount, employee);
    }

    @PostMapping("/{accountId}/password/reset")
    @PreAuthorize("hasAuthority('PERMISSION_STAFF_PASSWORD_RESET')")
    public Map<String, String> resetStaffPassword(@PathVariable Long accountId) {
        TaiKhoan account = findEmployeeAccount(accountId);
        account.setMatKhauHash(passwordEncoder.encode(DEFAULT_TEMPORARY_PASSWORD));
        taiKhoanRepository.save(account);
        return Map.of("message", "Đã đặt lại mật khẩu tài khoản nhân viên.");
    }

    private TaiKhoan findEmployeeAccount(Long accountId) {
        TaiKhoan account = taiKhoanRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản."));
        Role role = Role.fromValue(account.getVaiTro());
        if (role == Role.BENH_NHAN || role == Role.QUAN_TRI) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ được cập nhật tài khoản nhân viên, không thao tác tài khoản bệnh nhân hoặc Admin.");
        }
        return account;
    }

    private static StaffAccountResponse toResponse(TaiKhoan account, NhanVien employee) {
        return new StaffAccountResponse(
                account.getMaTaiKhoan(),
                employee == null ? null : employee.getMaNhanVien(),
                account.getTenDangNhap(),
                employee == null ? null : employee.getHoTen(),
                employee == null ? null : employee.getSoDienThoai(),
                account.getEmail(),
                Role.fromValue(account.getVaiTro()).getValue(),
                AccountStatus.fromActive(
                    Boolean.TRUE.equals(account.getTrangThai()) && employee != null));
    }
}
