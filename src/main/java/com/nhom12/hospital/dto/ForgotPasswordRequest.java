package com.nhom12.hospital.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {

    @NotBlank(message = "Vui lòng nhập địa chỉ email.")
    @Email(message = "Địa chỉ email không đúng định dạng.")
    private String email;

    private String otp;

    private String newPassword;
}