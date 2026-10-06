package com.nhom12.hospital.controller;

import com.nhom12.hospital.entity.ChucNang;
import com.nhom12.hospital.entity.PhanQuyenChiTiet;
import com.nhom12.hospital.entity.VaiTro;
import com.nhom12.hospital.repository.PhanQuyenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class PhanQuyenControllerTest {

    private final PhanQuyenRepository repository = mock(PhanQuyenRepository.class);
    private final PhanQuyenController controller = new PhanQuyenController(repository);

    @Test
    void getRolesReturnsAllRoles() {
        when(repository.findAllRoles()).thenReturn(List.of(
                new VaiTro("ThuNgan", "Thu Ngân", "CASHIER", "Mô tả", true),
                new VaiTro("DieuDuong", "Điều Dưỡng", "NURSE", "Mô tả", true),
                new VaiTro("NhanSu", "Nhân Sự", "HR", "Mô tả", true)
        ));

        List<VaiTro> roles = controller.getAllRoles();
        assertEquals(3, roles.size());
        assertEquals("ThuNgan", roles.get(0).getMaVaiTro());
    }

    @Test
    void getByRoleReturnsPermissionsForRole() {
        PhanQuyenChiTiet perm = new PhanQuyenChiTiet();
        perm.setMaVaiTro("ThuNgan");
        perm.setMaChucNang("VP_THU_TIEN_THANH_TOAN");
        perm.setQuyenXem(true);
        perm.setQuyenThem(true);

        when(repository.findByRole("ThuNgan")).thenReturn(List.of(perm));

        List<PhanQuyenChiTiet> result = controller.getByRole("ThuNgan");
        assertEquals(1, result.size());
        assertEquals("VP_THU_TIEN_THANH_TOAN", result.get(0).getMaChucNang());
    }

    @Test
    void assignPermissionCallsRepository() {
        Map<String, Object> req = Map.of(
                "maVaiTro", "DieuDuong",
                "maChucNang", "NT_TIEP_NHAN_XEP_GIUONG",
                "quyenXem", true,
                "quyenThem", true,
                "ghiChu", "Xếp giường nội trú"
        );

        ResponseEntity<?> response = controller.assignPermission(req);
        assertEquals(200, response.getStatusCode().value());
        verify(repository).assignPermission("DieuDuong", "NT_TIEP_NHAN_XEP_GIUONG", true, true, false, false, "Xếp giường nội trú");
    }
}
