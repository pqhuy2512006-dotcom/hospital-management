package com.nhom12.hospital.dto;

import com.nhom12.hospital.security.AccountStatus;

public record StaffAccountResponse(
        Long id,
        String employeeId,
        String username,
        String fullName,
        String phone,
        String email,
        String role,
        AccountStatus status) {
}
