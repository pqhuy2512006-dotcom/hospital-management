import re

with open('src/main/resources/static/inpatient.html', 'r', encoding='utf-8') as f:
    content = f.read()

# Replace the broken script block
broken_script_pattern = re.compile(r'<script>\s*function switchInpatientTab.*?</script>', re.DOTALL)

correct_script = """<script>
        function switchInpatientTab(tabId, btn) {
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            btn.classList.add('active');
            document.getElementById(tabId).classList.add('active');
            
            if (tabId === 'transferTab') {
                loadIncomingRequests();
            }
        }

        async function loadIncomingRequests() {
            try {
                const res = await fetch('/api/v1/chuyenkhoa/khoa-nhan');
                const tbody = document.getElementById('transferList');
                if (res.ok) {
                    const data = await res.json();
                    if (data.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Không có yêu cầu chuyển khoa nào.</td></tr>';
                        return;
                    }
                    tbody.innerHTML = data.map(yc => `
                        <tr>
                            <td>${yc.maYeuCau}</td>
                            <td>${yc.maBenhNhan}</td>
                            <td>${yc.lyDo}</td>
                            <td>${new Date(yc.ngayTao).toLocaleString('vi-VN')}</td>
                            <td>
                                <button class="btn-accept" onclick="openAcceptModal('${yc.maYeuCau}')">
                                    <i class="fa-solid fa-check"></i> Tiếp nhận
                                </button>
                            </td>
                        </tr>
                    `).join('');
                } else {
                    tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; color: red;">Lỗi tải dữ liệu.</td></tr>';
                }
            } catch (e) {
                console.error(e);
            }
        }

        async function openAcceptModal(maYeuCau) {
            document.getElementById('acceptMaYeuCau').value = maYeuCau;
            document.getElementById('acceptTransferModal').style.display = 'flex';
            
            const selBed = document.getElementById('selNewBed');
            selBed.innerHTML = '<option value="">-- Đang tải --</option>';
            
            try {
                const meRes = await fetch('/api/v1/nhanvien/me');
                if (!meRes.ok) throw new Error("Khong the lay thong tin nhan vien");
                const meData = await meRes.json();
                const maKhoa = meData.maKhoa;
                
                const res = await fetch(`/api/v1/giuongbenh?khoa=${maKhoa}`);
                if (res.ok) {
                    const beds = await res.json();
                    const emptyBeds = beds.filter(b => b.trangThai === 'Trong');
                    selBed.innerHTML = '<option value="">-- Chọn giường --</option>' + emptyBeds.map(b => `<option value="${b.maGiuong}">${b.soGiuong}</option>`).join('');
                }
            } catch (e) {
                console.error(e);
                selBed.innerHTML = '<option value="">Lỗi tải giường</option>';
            }
        }

        function closeAcceptModal() {
            document.getElementById('acceptTransferModal').style.display = 'none';
            document.getElementById('acceptMaYeuCau').value = '';
            document.getElementById('selNewBed').innerHTML = '<option value="">-- Chọn giường --</option>';
        }

        async function submitAcceptTransfer() {
            const maYeuCau = document.getElementById('acceptMaYeuCau').value;
            const maGiuongMoi = document.getElementById('selNewBed').value;
            
            if (!maGiuongMoi) {
                alert('Vui lòng chọn một giường!');
                return;
            }
            
            try {
                const res = await fetch(`/api/v1/chuyenkhoa/${maYeuCau}/tiep-nhan`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ maGiuongMoi: maGiuongMoi })
                });
                
                if (res.ok) {
                    alert('Tiếp nhận bệnh nhân thành công!');
                    closeAcceptModal();
                    loadIncomingRequests();
                    if(typeof fetchAndRenderBeds === 'function') fetchAndRenderBeds(); 
                    if(typeof loadInpatientData === 'function') loadInpatientData();
                } else {
                    const err = await res.json();
                    alert(err.message || 'Lỗi tiếp nhận');
                }
            } catch (e) {
                console.error(e);
                alert('Lỗi kết nối');
            }
        }
    </script>"""

new_content = broken_script_pattern.sub(correct_script, content)
with open('src/main/resources/static/inpatient.html', 'w', encoding='utf-8') as f:
    f.write(new_content)
print("Fixed script")
