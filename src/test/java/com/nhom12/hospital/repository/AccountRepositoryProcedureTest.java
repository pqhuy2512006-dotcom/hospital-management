package com.nhom12.hospital.repository;

import com.nhom12.hospital.entity.TaiKhoan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class AccountRepositoryProcedureTest {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Test
    void insertsReadsAndUpdatesAccountThroughDatabaseObjects() {
        String username = "repo_proc_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        TaiKhoan account = new TaiKhoan();
        account.setTenDangNhap(username);
        account.setMatKhauHash("encoded-probe");
        account.setEmail(null);
        account.setSoDienThoai("0900000012");
        account.setVaiTro("BenhNhan");
        account.setTrangThai(true);
        account.setNgayTao(LocalDateTime.now().withNano(0));

        taiKhoanRepository.save(account);
        assertNotNull(account.getMaTaiKhoan());
        TaiKhoan loaded = taiKhoanRepository.findByTenDangNhap(username).orElseThrow();
        assertEquals("BenhNhan", loaded.getVaiTro());
        assertEquals("0900000012", loaded.getSoDienThoai());

        loaded.setSoDienThoai("0900000013");
        taiKhoanRepository.save(loaded);
        assertEquals("0900000013", taiKhoanRepository.findById(loaded.getMaTaiKhoan()).orElseThrow().getSoDienThoai());

        assertTrue(taiKhoanRepository.findByEmail("absent-account@example.com").isEmpty());
    }
}
