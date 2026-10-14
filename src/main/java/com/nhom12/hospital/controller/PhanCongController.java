package com.nhom12.hospital.controller;

import com.nhom12.hospital.dto.LichSuPhanCongDTO;
import com.nhom12.hospital.dto.PhanCongRequest;
import com.nhom12.hospital.service.PhanCongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/phancong")
@CrossOrigin(origins = "*")
public class PhanCongController {
    private final PhanCongService phanCongService;

    public PhanCongController(PhanCongService phanCongService) {
        this.phanCongService = phanCongService;
    }

    @GetMapping("/lich-su/{maNhanVien}")
    public List<LichSuPhanCongDTO> getLichSuByNhanVien(@PathVariable String maNhanVien) {
        return phanCongService.getLichSuByNhanVien(maNhanVien);
    }

    @PostMapping("/dieu-chuyen")
    public ResponseEntity<?> phanCongHoacDieuChuyen(@RequestBody PhanCongRequest request) {
        try {
            phanCongService.phanCongHoacDieuChuyen(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thực hiện phân công / điều chuyển nhân sự thành công!"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: " + e.getMessage()
            ));
        }
    }
}

