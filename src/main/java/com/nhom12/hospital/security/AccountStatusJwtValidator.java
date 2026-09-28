package com.nhom12.hospital.security;

import com.nhom12.hospital.repository.TaiKhoanRepository;
import com.nhom12.hospital.repository.NhanVienRepository;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountStatusJwtValidator implements OAuth2TokenValidator<Jwt> {

    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;

    public AccountStatusJwtValidator(
            TaiKhoanRepository taiKhoanRepository,
            NhanVienRepository nhanVienRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        boolean active = taiKhoanRepository.findByTenDangNhap(token.getSubject())
            .filter(account -> Boolean.TRUE.equals(account.getTrangThai()))
            .filter(account -> {
                Role role = Role.fromValue(account.getVaiTro());
                List<String> authorities = token.getClaimAsStringList("authorities");
                boolean tokenRoleMatches = authorities != null && authorities.contains(role.getValue());
                return tokenRoleMatches && (role == Role.BENH_NHAN || role == Role.QUAN_TRI
                    || nhanVienRepository.findByMaTaiKhoan(account.getMaTaiKhoan()).isPresent());
            })
            .isPresent();
        if (active) {
            return OAuth2TokenValidatorResult.success();
        }

        OAuth2Error error = new OAuth2Error(
                "invalid_token",
                "Tài khoản không tồn tại hoặc đã bị khóa.",
                null);
        return OAuth2TokenValidatorResult.failure(error);
    }
}
