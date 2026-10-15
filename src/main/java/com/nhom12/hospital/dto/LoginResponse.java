package com.nhom12.hospital.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private boolean success;
    private String message;
    private String hoTen;
    private String vaiTro;

    // Constructor rỗng
    public LoginResponse() {
    }

    // Constructor đầy đủ tham số (thay thế hoàn toàn cho @AllArgsConstructor)
    public LoginResponse(boolean success, String message, String hoTen, String vaiTro) {
        this.success = success;
        this.message = message;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
    }
}