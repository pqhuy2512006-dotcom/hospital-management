package com.nhom12.hospital;

import com.nhom12.hospital.entity.BenhNhan;
import com.nhom12.hospital.service.BenhNhanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;

@SpringBootTest
public class BenhNhanTest {
    @Autowired
    private BenhNhanService benhNhanService;

    @Test
    public void testCreate() {
        try {
            BenhNhan bn = new BenhNhan();
            bn.setHoTen("Test Patient");
            bn.setNgaySinh(LocalDate.of(1990, 1, 1));
            bn.setGioiTinh("Nu");
            bn.setSoDienThoai("0987654321");
            bn.setSoCCCD("112345678912");
            benhNhanService.create(bn);
            System.out.println("SUCCESS!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
