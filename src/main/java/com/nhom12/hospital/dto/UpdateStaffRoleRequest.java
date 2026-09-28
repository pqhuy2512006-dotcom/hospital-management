package com.nhom12.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateStaffRoleRequest(@NotBlank @Size(max = 20) String role) {
}
