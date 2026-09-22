package com.nhom12.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private String hoTen;
    private String vaiTro;
}