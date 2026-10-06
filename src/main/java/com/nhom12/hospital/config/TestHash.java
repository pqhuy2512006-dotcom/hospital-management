package com.nhom12.hospital.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestHash {
    public static void main(String[] args) {
        // Sử dụng BCryptPasswordEncoder của Spring Security
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Điền mật khẩu admin bạn muốn đặt ở đây
        String rawPassword = "admin123";

        // Tiến hành hash mật khẩu
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("--------------------------------------------------");
        System.out.println("Mật khẩu gốc: " + rawPassword);
        System.out.println("Chuỗi Hash để dán vào Database:");
        System.out.println(encodedPassword);
        System.out.println("--------------------------------------------------");
        
        // Kiểm tra thử tính đúng đắn (kết quả trả về true)
        boolean isMatch = encoder.matches(rawPassword, encodedPassword);
        System.out.println("Kiểm tra khớp mật khẩu: " + isMatch);
    }
}