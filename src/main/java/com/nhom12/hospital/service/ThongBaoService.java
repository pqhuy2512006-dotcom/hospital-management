package com.nhom12.hospital.service;

import com.nhom12.hospital.entity.ThongBao;
import com.nhom12.hospital.repository.ThongBaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.List;

@Service
@Transactional
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;

    public ThongBaoService(ThongBaoRepository thongBaoRepository) {
        this.thongBaoRepository = thongBaoRepository;
    }

    public List<ThongBao> getByMaTaiKhoan(Long accountId) {
        return thongBaoRepository.findByMaTaiKhoan(accountId);
    }

    public void markAsRead(Integer id, Long accountId) {
        thongBaoRepository.markAsRead(id, accountId);
    }

    public void createThongBao(Long accountId, String tieuDe, String noiDung, String loaiThongBao) {
        thongBaoRepository.create(accountId, tieuDe, noiDung, loaiThongBao);
    }
        public int broadcast(String tieuDe, String noiDung, String loaiThongBao, String targetRole) {
        return thongBaoRepository.broadcast(tieuDe, noiDung, loaiThongBao, targetRole);
    }

    public List<Map<String, Object>> getBroadcastHistory() {
        return thongBaoRepository.getBroadcastHistory();
    }
}

