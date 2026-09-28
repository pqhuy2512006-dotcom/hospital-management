package com.nhom12.hospital.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nhom12.hospital.config.SecurityConfig;
import com.nhom12.hospital.entity.NhanVien;
import com.nhom12.hospital.entity.TaiKhoan;
import com.nhom12.hospital.repository.NhanVienRepository;
import com.nhom12.hospital.repository.TaiKhoanRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private static final byte[] TEST_SECRET = new byte[32];

    static {
        Arrays.fill(TEST_SECRET, (byte) 42);
    }

    @Test
    void shouldIssueSignedJwtWithRoleAndPermissions() {
        SecretKey secretKey = new SecretKeySpec(TEST_SECRET, "HmacSHA256");
        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
        JwtDecoder decoder = jwtDecoder(secretKey, true);
        JwtService jwtService = new JwtService(encoder, 600);

        String token = jwtService.createToken("doctor", Role.BAC_SI);
        Jwt decoded = decoder.decode(token);
        List<String> authorities = decoded.getClaimAsStringList("authorities");

        assertEquals("doctor", decoded.getSubject());
        assertEquals("https://hospital-management.local", decoded.getIssuer().toString());
        assertTrue(authorities.contains(Role.BAC_SI.getValue()));
        assertTrue(authorities.contains("PERMISSION_" + Permission.ICD10_DIAGNOSIS_RECORD.name()));
        assertNotNull(decoded.getExpiresAt());

        var authentication = new SecurityConfig().jwtAuthenticationConverter().convert(decoded);
        assertTrue(authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("PERMISSION_ICD10_DIAGNOSIS_RECORD")));
    }

    @Test
    void shouldRejectTokenSignedWithAnotherKey() {
        SecretKey signingKey = new SecretKeySpec(TEST_SECRET, "HmacSHA256");
        byte[] differentSecret = new byte[32];
        Arrays.fill(differentSecret, (byte) 1);
        SecretKey differentKey = new SecretKeySpec(differentSecret, "HmacSHA256");
        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(signingKey));
        JwtDecoder decoder = jwtDecoder(differentKey, true);

        String token = new JwtService(encoder, 600).createToken("doctor", Role.BAC_SI);
        assertThrows(Exception.class, () -> decoder.decode(token));
    }

    @Test
    void shouldReadJwtFromTheHttpOnlyCookieRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(JwtCookieBearerTokenResolver.COOKIE_NAME, "signed.jwt.value"));

        assertEquals("signed.jwt.value", new JwtCookieBearerTokenResolver().resolve(request));
    }

    @Test
    void shouldRejectJwtWhenAccountIsLockedOrDeleted() {
        SecretKey secretKey = new SecretKeySpec(TEST_SECRET, "HmacSHA256");
        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
        JwtDecoder lockedAccountDecoder = jwtDecoder(secretKey, false);
        String token = new JwtService(encoder, 600).createToken("doctor", Role.BAC_SI);

        assertThrows(Exception.class, () -> lockedAccountDecoder.decode(token));
        assertThrows(Exception.class, () -> jwtDecoder(secretKey, null).decode(token));
    }

    @Test
    void shouldRejectJwtAfterDatabaseRoleChanges() {
        SecretKey secretKey = new SecretKeySpec(TEST_SECRET, "HmacSHA256");
        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
        String oldRoleToken = new JwtService(encoder, 600).createToken("doctor", Role.BAC_SI);

        assertThrows(Exception.class, () -> jwtDecoder(secretKey, true, "KTV").decode(oldRoleToken));
    }

    @Test
    void shouldRejectActiveStaffAccountWithoutEmployeeProfile() {
        SecretKey secretKey = new SecretKeySpec(TEST_SECRET, "HmacSHA256");
        TaiKhoanRepository accounts = mock(TaiKhoanRepository.class);
        NhanVienRepository employees = mock(NhanVienRepository.class);
        TaiKhoan account = new TaiKhoan();
        account.setMaTaiKhoan(22L);
        account.setTenDangNhap("doctor");
        account.setVaiTro("BacSi");
        account.setTrangThai(true);
        when(accounts.findByTenDangNhap("doctor")).thenReturn(Optional.of(account));
        when(employees.findByMaTaiKhoan(22L)).thenReturn(Optional.empty());

        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
        JwtDecoder decoder = new SecurityConfig().jwtDecoder(
                secretKey,
                new AccountStatusJwtValidator(accounts, employees));
        String token = new JwtService(encoder, 600).createToken("doctor", Role.BAC_SI);

        assertThrows(Exception.class, () -> decoder.decode(token));
    }

    @Test
    void shouldMapDatabaseStatusToBusinessStatus() {
        assertEquals(AccountStatus.HOAT_DONG, AccountStatus.fromActive(true));
        assertEquals(AccountStatus.BI_KHOA, AccountStatus.fromActive(false));
        assertTrue(AccountStatus.HOAT_DONG.isActive());
        assertFalse(AccountStatus.BI_KHOA.isActive());
    }

    private JwtDecoder jwtDecoder(SecretKey secretKey, Boolean accountActive) {
        return jwtDecoder(secretKey, accountActive, "BacSi");
    }

    private JwtDecoder jwtDecoder(SecretKey secretKey, Boolean accountActive, String storedRole) {
        TaiKhoanRepository repository = mock(TaiKhoanRepository.class);
        NhanVienRepository employeeRepository = mock(NhanVienRepository.class);
        if (accountActive != null) {
            TaiKhoan account = new TaiKhoan();
            account.setTenDangNhap("doctor");
            account.setMaTaiKhoan(22L);
            account.setVaiTro(storedRole);
            account.setTrangThai(accountActive);
            when(repository.findByTenDangNhap("doctor")).thenReturn(Optional.of(account));
            if (accountActive) {
                NhanVien employee = new NhanVien();
                employee.setMaNhanVien("NV0001");
                employee.setMaTaiKhoan(22L);
                when(employeeRepository.findByMaTaiKhoan(22L)).thenReturn(Optional.of(employee));
            }
        } else {
            when(repository.findByTenDangNhap("doctor")).thenReturn(Optional.empty());
        }
        return new SecurityConfig().jwtDecoder(
                secretKey,
                new AccountStatusJwtValidator(repository, employeeRepository));
    }

}