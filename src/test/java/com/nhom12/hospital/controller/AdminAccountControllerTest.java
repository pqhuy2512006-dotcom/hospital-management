package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.CreateStaffAccountRequest;
import com.nhom12.hospital.dto.StaffAccountResponse;
import com.nhom12.hospital.dto.UpdateStaffRoleRequest;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import com.nhom12.hospital.security.AccountStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminAccountControllerTest {

    private TaiKhoanRepository taiKhoanRepository;
    private NhanVienRepository nhanVienRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private AdminAccountController controller;

    @BeforeEach
    void setUp() {
        taiKhoanRepository = mock(TaiKhoanRepository.class);
        nhanVienRepository = mock(NhanVienRepository.class);
        passwordEncoder = new BCryptPasswordEncoder(4);
        controller = new AdminAccountController(
                taiKhoanRepository,
                nhanVienRepository,
                passwordEncoder);
    }

    @Test
    void shouldCreateLockedAccountWithoutCreatingEmployeeProfile() {
        CreateStaffAccountRequest request = new CreateStaffAccountRequest(
                "doctor1", "an@example.com", "StrongPass1", "BacSi");
        when(taiKhoanRepository.findByTenDangNhap("doctor1")).thenReturn(Optional.empty());
        when(taiKhoanRepository.findByEmail("an@example.com")).thenReturn(Optional.empty());
        when(taiKhoanRepository.saveAndFlush(any(TaiKhoan.class))).thenAnswer(invocation -> {
            TaiKhoan account = invocation.getArgument(0);
            account.setMaTaiKhoan(22L);
            return account;
        });
        StaffAccountResponse response = controller.createStaffAccount(request);

        assertNull(response.employeeId());
        assertNull(response.fullName());
        assertNull(response.phone());
        assertEquals("BacSi", response.role());
        assertEquals(AccountStatus.BI_KHOA, response.status());

        var accountCaptor = org.mockito.ArgumentCaptor.forClass(TaiKhoan.class);
        verify(taiKhoanRepository).saveAndFlush(accountCaptor.capture());
        assertTrue(passwordEncoder.matches("StrongPass1", accountCaptor.getValue().getMatKhauHash()));
        assertEquals("BacSi", accountCaptor.getValue().getVaiTro());
        assertFalse(accountCaptor.getValue().getTrangThai());
        verify(nhanVienRepository, never()).save(any(NhanVien.class));
    }

    @Test
    void shouldResetStaffPasswordToTheTemporaryDefault() {
        TaiKhoan account = new TaiKhoan();
        account.setMaTaiKhoan(22L);
        account.setTenDangNhap("doctor1");
        account.setVaiTro("BacSi");
        account.setMatKhauHash(passwordEncoder.encode("OldPassword1"));
        when(taiKhoanRepository.findById(22L)).thenReturn(Optional.of(account));
        when(taiKhoanRepository.save(account)).thenReturn(account);

        var response = controller.resetStaffPassword(22L);

        assertTrue(passwordEncoder.matches("123456", account.getMatKhauHash()));
        assertFalse(response.containsKey("123456"));
    }

    @Test
    void shouldUpdateRoleInAccountAndLinkedEmployee() {
        TaiKhoan account = new TaiKhoan();
        account.setMaTaiKhoan(22L);
        account.setTenDangNhap("doctor1");
        account.setVaiTro("BacSi");
        account.setTrangThai(true);
        NhanVien employee = new NhanVien();
        employee.setMaNhanVien("NV0001");
        employee.setMaTaiKhoan(22L);
        employee.setVaiTro("BacSi");
        employee.setHoTen("Nguyen Van An");
        when(taiKhoanRepository.findById(22L)).thenReturn(Optional.of(account));
        when(taiKhoanRepository.save(account)).thenReturn(account);
        when(nhanVienRepository.findByMaTaiKhoan(22L)).thenReturn(Optional.of(employee));
        when(nhanVienRepository.save(employee)).thenReturn(employee);

        StaffAccountResponse response = controller.updateStaffRole(22L, new UpdateStaffRoleRequest("KTV"));

        assertEquals("KTV", account.getVaiTro());
        assertEquals("KTV", employee.getVaiTro());
        assertEquals("KTV", response.role());
    }

    @Test
    void shouldJoinEmployeeNameAndPhoneToAccountList() {
        TaiKhoan account = new TaiKhoan();
        account.setMaTaiKhoan(22L);
        account.setTenDangNhap("doctor1");
        account.setEmail("an@example.com");
        account.setVaiTro("BacSi");
        account.setTrangThai(true);
        NhanVien employee = new NhanVien();
        employee.setMaNhanVien("NV0001");
        employee.setMaTaiKhoan(22L);
        employee.setHoTen("Nguyen Van An");
        employee.setSoDienThoai("0901234567");
        when(taiKhoanRepository.findAll()).thenReturn(List.of(account));
        when(nhanVienRepository.findAllByMaTaiKhoanIn(List.of(22L))).thenReturn(List.of(employee));

        List<StaffAccountResponse> result = controller.listStaffAccounts();

        assertEquals(1, result.size());
        assertEquals("Nguyen Van An", result.get(0).fullName());
        assertEquals("0901234567", result.get(0).phone());
    }

    @Test
    void shouldShowUnlinkedStaffAccountAsLocked() {
        TaiKhoan account = new TaiKhoan();
        account.setMaTaiKhoan(22L);
        account.setTenDangNhap("doctor1");
        account.setVaiTro("BacSi");
        account.setTrangThai(true);
        when(taiKhoanRepository.findAll()).thenReturn(List.of(account));
        when(nhanVienRepository.findAllByMaTaiKhoanIn(List.of(22L))).thenReturn(List.of());

        List<StaffAccountResponse> result = controller.listStaffAccounts();

        assertEquals(AccountStatus.BI_KHOA, result.get(0).status());
    }

}
