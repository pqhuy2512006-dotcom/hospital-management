import sys
import io
import re
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

with open('src/main/resources/static/doctors.html', 'r', encoding='utf-8') as f:
    html = f.read()

js_code = """
        // --- QUẢN LÝ TRƯỞNG KHOA JS ---
        async function loadTruongKhoaList() {
            try {
                const response = await fetch('/api/v1/truongkhoa/khoa-list');
                if (response.ok) {
                    const data = await response.json();
                    const tbody = document.getElementById('truongKhoaTbody');
                    tbody.innerHTML = '';
                    data.forEach(item => {
                        const hasTruongKhoa = item.maTruongKhoa != null;
                        
                        let actions = '';
                        if (hasTruongKhoa) {
                            actions = 
                                <button class="btn-tbl-edit" title="Thay đổi Trưởng khoa" onclick="openBoNhiemModal('', '', '')"><i class="fa-solid fa-user-pen"></i></button>
                                <button class="btn-tbl-delete" title="Miễn nhiệm Trưởng khoa" onclick="mienNhiemTruongKhoa('', '', '')"><i class="fa-solid fa-user-minus"></i></button>
                            ;
                        } else {
                            actions = 
                                <button class="btn-add-doc" style="padding: 5px 10px; font-size:12px;" onclick="openBoNhiemModal('', '', null)"><i class="fa-solid fa-user-plus"></i> Bổ Nhiệm</button>
                            ;
                        }
                        
                        tbody.innerHTML += 
                            <tr>
                                <td></td>
                                <td><strong></strong></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td style="text-align:right;"></td>
                            </tr>
                        ;
                    });
                }
            } catch (err) {
                console.error("Lỗi tải danh sách trưởng khoa", err);
            }
        }

        async function openBoNhiemModal(maKhoa, tenKhoa, tenTruongKhoaHienTai) {
            document.getElementById('bnMaKhoa').value = maKhoa;
            document.getElementById('bnTenKhoa').innerText = tenKhoa;
            
            if (tenTruongKhoaHienTai && tenTruongKhoaHienTai !== 'null') {
                document.getElementById('boNhiemModalTitle').innerHTML = '<i class="fa-solid fa-user-pen" style="color:var(--warning);"></i> Thay Đổi Trưởng Khoa';
                document.getElementById('bnHienTai').innerText = 'Trưởng khoa hiện tại: ' + tenTruongKhoaHienTai;
            } else {
                document.getElementById('boNhiemModalTitle').innerHTML = '<i class="fa-solid fa-user-tie" style="color:var(--primary);"></i> Bổ Nhiệm Trưởng Khoa';
                document.getElementById('bnHienTai').innerText = 'Hiện chưa có trưởng khoa.';
            }

            const select = document.getElementById('bnNhanVien');
            select.innerHTML = '<option value="">-- Đang tải danh sách --</option>';
            document.getElementById('boNhiemModal').style.display = 'flex';

            try {
                const response = await fetch(/api/v1/truongkhoa/khoa//ung-vien);
                if (response.ok) {
                    const ungViens = await response.json();
                    select.innerHTML = '<option value="">-- Chọn Nhân Viên --</option>';
                    ungViens.forEach(uv => {
                        const opt = document.createElement('option');
                        opt.value = uv.maNhanVien;
                        opt.textContent = ${uv.maNhanVien} -  ();
                        select.appendChild(opt);
                    });
                    if (ungViens.length === 0) {
                        select.innerHTML = '<option value="">(Không có nhân viên đủ điều kiện)</option>';
                    }
                }
            } catch (err) {
                console.error(err);
                select.innerHTML = '<option value="">Lỗi tải dữ liệu</option>';
            }
        }

        function closeBoNhiemModal() {
            document.getElementById('boNhiemModal').style.display = 'none';
        }

        async function submitBoNhiem(e) {
            e.preventDefault();
            const maKhoa = document.getElementById('bnMaKhoa').value;
            const maNhanVien = document.getElementById('bnNhanVien').value;
            if (!maNhanVien) { alert("Vui lòng chọn nhân viên!"); return; }

            try {
                const response = await fetch(/api/v1/truongkhoa/khoa//bo-nhiem, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ maNhanVien: maNhanVien })
                });
                if (response.ok) {
                    alert("Thành công!");
                    closeBoNhiemModal();
                    loadTruongKhoaList();
                } else {
                    alert("Có lỗi xảy ra!");
                }
            } catch (err) {
                console.error(err);
            }
        }

        async function mienNhiemTruongKhoa(maKhoa, tenKhoa, tenHienTai) {
            if (confirm(Bạn có chắc muốn miễn nhiệm chức vụ Trưởng khoa của [] thuộc ?)) {
                try {
                    const response = await fetch(/api/v1/truongkhoa/khoa//mien-nhiem, { method: 'POST' });
                    if (response.ok) {
                        alert("Đã miễn nhiệm thành công!");
                        loadTruongKhoaList();
                    } else {
                        alert("Lỗi khi miễn nhiệm!");
                    }
                } catch (err) {
                    console.error(err);
                }
            }
        }
"""

# Replace switchTab function to include loadTruongKhoaList
switch_tab_orig = """function switchTab(tabId, btn) {
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));

            btn.classList.add('active');
            document.getElementById(tabId).classList.add('active');
        }"""
switch_tab_new = """function switchTab(tabId, btn) {
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));

            btn.classList.add('active');
            document.getElementById(tabId).classList.add('active');
            
            if (tabId === 'truongKhoaTab') {
                loadTruongKhoaList();
            }
        }"""
html = html.replace(switch_tab_orig, switch_tab_new)

# Inject the js code just before </script>
html = html.replace('</script>\n    <script src="/auth-guard.js?v=2">', js_code + '\n</script>\n    <script src="/auth-guard.js?v=2">')

# Also, fix the inline style="display:none;" in truongKhoaTab
html = html.replace('<div id="truongKhoaTab" class="tab-content" style="display:none;">', '<div id="truongKhoaTab" class="tab-content">')

with open('src/main/resources/static/doctors.html', 'w', encoding='utf-8') as f:
    f.write(html)
