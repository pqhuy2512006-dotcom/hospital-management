package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.ChucNang;
import com.nhom12.hospital.entity.PhanQuyenChiTiet;
import com.nhom12.hospital.entity.VaiTro;
import com.nhom12.hospital.repository.PhanQuyenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/phanquyen")
@CrossOrigin(origins = "*")
public class PhanQuyenController {

    private final PhanQuyenRepository phanQuyenRepository;

    public PhanQuyenController(PhanQuyenRepository phanQuyenRepository) {
        this.phanQuyenRepository = phanQuyenRepository;
    }

    @GetMapping
    public List<PhanQuyenChiTiet> getAll() {
        return phanQuyenRepository.findAllPermissions();
    }

    @GetMapping("/vaitro")
    public List<VaiTro> getAllRoles() {
        return phanQuyenRepository.findAllRoles();
    }

    @GetMapping("/chucnang")
    public List<ChucNang> getAllFunctions() {
        return phanQuyenRepository.findAllFunctions();
    }

    @GetMapping("/role/{role}")
    public List<PhanQuyenChiTiet> getByRole(@PathVariable String role) {
        return phanQuyenRepository.findByRole(role);
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assignPermission(@RequestBody Map<String, Object> req) {
        String role = (String) req.get("maVaiTro");
        String function = (String) req.get("maChucNang");
        if (role == null || function == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Thiếu maVaiTro hoặc maChucNang!"));
        }

        Boolean xem = req.get("quyenXem") instanceof Boolean b ? b : Boolean.TRUE;
        Boolean them = req.get("quyenThem") instanceof Boolean b ? b : Boolean.FALSE;
        Boolean sua = req.get("quyenSua") instanceof Boolean b ? b : Boolean.FALSE;
        Boolean xoa = req.get("quyenXoa") instanceof Boolean b ? b : Boolean.FALSE;
        String ghiChu = (String) req.get("ghiChu");

        phanQuyenRepository.assignPermission(role, function, xem, them, sua, xoa, ghiChu);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Gán quyền thành công cho vai trò " + role + " đối với chức năng " + function
        ));
    }
}
