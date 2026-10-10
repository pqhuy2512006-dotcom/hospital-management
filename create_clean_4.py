import subprocess
import re

# Fetch file directly from git using python subprocess to get raw bytes
result = subprocess.run(['git', 'cat-file', '-p', 'HEAD:src/main/resources/static/examination.html'], capture_output=True)
content = result.stdout.decode('utf-8')

# 1. Extract the Chuyển khoa section
idx = content.find('Chuyển khoa / Hội chẩn')
start_idx = content.rfind('<section', 0, idx)

depth = 0
end_idx = -1
for i in range(start_idx, len(content)):
    if content[i:i+8] == '<section':
        depth += 1
    elif content[i:i+10] == '</section>':
        depth -= 1
        if depth == 0:
            end_idx = i + 10
            break

section_html = content[start_idx:end_idx]

# 2. Add the "Tiếp nhận hội chẩn" section to section_html
tiep_nhan_html = """
                <section class="section-box" style="margin-top:20px;" id="consultationSection" style="display:none;">
                    <div class="section-title">
                        <span><i class="fa-solid fa-handshake"></i> Yêu cầu hội chẩn đến</span>
                    </div>
                    <div style="overflow-x:auto;">
                        <table>
                            <thead><tr><th>Mã yêu cầu</th><th>Mã bệnh nhân</th><th>Lý do</th><th>Thao tác</th></tr></thead>
                            <tbody id="consultationTableBody"></tbody>
                        </table>
                    </div>
                </section>
"""
section_html += tiep_nhan_html

# 3. Create the layout
content_body_start = content.find('<div class="content-body">')
content_body_end = content.find('</div>\n    </div>\n</body>')
new_content_body = '<div class="content-body">\n' + section_html + '\n</div>'

new_html = content[:content_body_start] + new_content_body + content[content_body_end:]
new_html = new_html.replace('<title>Khám Bệnh & Kê Đơn - MEDICARE HIS</title>', '<title>Chuyển khoa & Hội chẩn - MEDICARE HIS</title>')

# Update sidebar
new_html = new_html.replace('<li><a href="/examination.html" class="active">', '<li><a href="/examination.html">')
new_html = new_html.replace('</ul>', '    <li><a href="/referrals.html" class="active"><i class="fa-solid fa-arrows-turn-right"></i> Chuyển khoa & Hội chẩn</a></li>\n        </ul>')

onload_replacement = """        window.onload = async function() {
            try {
                const user = JSON.parse(sessionStorage.getItem('authenticatedUser') || '{}');
                if (!user.token) {
                    window.location.href = '/login.html';
                    return;
                }
                const title = document.querySelector('.top-header .header-title h2');
                if (title && (user.hoTen || user.username)) {
                    title.textContent = `Phòng khám - ${user.hoTen || user.username}`;
                }
                
                await Promise.all([
                    loadReferralOptions(),
                    loadMyReferrals()
                ]);
                
                if (user.vaiTro === 'BacSi' || user.vaiTro === 'TruongKhoa') {
                    await loadIncomingConsultations();
                }
                
                // Load approval requests if TruongKhoa
                try { await loadApprovalRequests(); } catch(e) {}
                
            } catch (error) {
                console.error(error);
                if (error.message.includes('401') || error.message.includes('403')) {
                    logout();
                }
            }
        };

        async function loadReferralOptions() {
            const examSelect = document.getElementById('referralExam');
            if (examSelect) {
                examSelect.replaceChildren(new Option('Chọn phiếu khám', ''));
                try {
                    const res = await fetch('/api/v1/phieukham/me');
                    if (res.ok) {
                        const exams = await res.json();
                        exams.forEach(e => examSelect.add(new Option(e.maPhieuKham + ' - BN: ' + e.maBenhNhan, e.maPhieuKham)));
                    }
                } catch(e) {}
            }
            
            const departmentSelect = document.getElementById('referralDepartment');
            if (departmentSelect) {
                departmentSelect.replaceChildren(new Option('Chọn khoa nhận', ''));
                try {
                    const res = await fetch('/api/v1/khoa');
                    if(res.ok) {
                        const departments = await res.json();
                        departments.forEach(d => departmentSelect.add(new Option(d.tenKhoa, d.maKhoa)));
                    }
                } catch(e) {}
            }

            const doctorSelect = document.getElementById('invitedDoctor');
            if (doctorSelect) {
                doctorSelect.replaceChildren(new Option('Chọn bác sĩ', ''));
                try {
                    const res = await fetch('/api/v1/nhanvien/doctors');
                    if(res.ok) {
                        const doctors = await res.json();
                        doctors.forEach(doctor => doctorSelect.add(new Option(doctor.name + ' - ' + (doctor.specialty || ''), doctor.id)));
                    }
                } catch(e) {}
            }
        }

        async function loadMyReferrals() {
            try {
                const response = await fetch('/api/v1/chuyenkhoa/me');
                if (!response.ok) throw new Error('Lỗi tải danh sách');
                const referrals = await response.json();
                const body = document.getElementById('referralTableBody');
                body.replaceChildren();
                if (referrals.length === 0) {
                    const row = body.insertRow();
                    row.insertCell().colSpan = 6;
                    row.cells[0].textContent = 'Chưa có yêu cầu.';
                    return;
                }
                referrals.forEach(referral => {
                    const row = body.insertRow();
                    let statusMap = {
                        'CHO_DUYET': 'Chờ duyệt',
                        'CHO_XEP_GIUONG': 'Chờ xếp giường',
                        'CHO_TIEP_NHAN': 'Chờ tiếp nhận',
                        'DA_TIEP_NHAN': 'Đã tiếp nhận'
                    };
                    let displayStatus = statusMap[referral.trangThai] || referral.trangThai;
                    [new Date(referral.ngayTao).toLocaleString('vi-VN'), referral.loaiYeuCau === 'HOI_CHAN' ? 'Hội chẩn' : 'Chuyển khoa',
                        referral.maPhieuKham, referral.maKhoaNhan, referral.lyDo, displayStatus]
                        .forEach(value => row.insertCell().textContent = String(value || ''));
                });
            } catch (e) {}
        }
        
        async function submitReferral(event) {
            event.preventDefault();
            const type = document.getElementById('referralType').value;
            try {
                const response = await fetch('/api/v1/chuyenkhoa', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        loaiYeuCau: type,
                        maPhieuKham: document.getElementById('referralExam').value,
                        maKhoaNhan: document.getElementById('referralDepartment').value,
                        maBacSiDuocMoi: type === 'HOI_CHAN' ? document.getElementById('invitedDoctor').value : null,
                        lyDo: document.getElementById('referralReason').value.trim()
                    })
                });
                if (response.ok) {
                    alert('Đã gửi yêu cầu thành công!');
                    event.target.reset();
                    toggleInvitedDoctor();
                    loadMyReferrals();
                } else {
                    const error = await response.json();
                    alert('Lỗi: ' + error.message);
                }
            } catch (e) {
                alert('Lỗi hệ thống');
            }
        }

        function toggleInvitedDoctor() {
            const isConsult = document.getElementById('referralType').value === 'HOI_CHAN';
            document.getElementById('invitedDoctorGroup').style.display = isConsult ? 'block' : 'none';
            document.getElementById('invitedDoctor').required = isConsult;
        }

        async function loadIncomingConsultations() {
            try {
                const res = await fetch('/api/v1/chuyenkhoa/cho-tiep-nhan');
                if (res.ok) {
                    const requests = await res.json();
                    const user = JSON.parse(sessionStorage.getItem('authenticatedUser') || '{}');
                    
                    const myConsultations = requests.filter(req => req.loaiYeuCau === 'HOI_CHAN');
                    
                    if (myConsultations.length > 0) {
                        document.getElementById('consultationSection').style.display = 'block';
                        const body = document.getElementById('consultationTableBody');
                        body.replaceChildren();
                        
                        myConsultations.forEach(req => {
                            const row = body.insertRow();
                            row.innerHTML = `
                                <td>${req.maYeuCau}</td>
                                <td>${req.maBenhNhan}</td>
                                <td>${req.lyDo}</td>
                                <td>
                                    <button type="button" class="btn-add-item" onclick="acceptConsult('${req.maYeuCau}')">
                                        <i class="fa-solid fa-check"></i> Tiếp nhận
                                    </button>
                                </td>
                            `;
                        });
                    } else {
                        document.getElementById('consultationSection').style.display = 'none';
                    }
                }
            } catch (err) {}
        }

        async function acceptConsult(maYeuCau) {
            if(!confirm('Xác nhận tiếp nhận hội chẩn?')) return;
            try {
                const res = await fetch(`/api/v1/chuyenkhoa/${maYeuCau}/tiep-nhan`, { method: 'POST' });
                if(res.ok) {
                    alert('Đã tiếp nhận hội chẩn!');
                    loadIncomingConsultations();
                } else {
                    const err = await res.json();
                    alert('Lỗi: ' + err.message);
                }
            } catch(e) {}
        }
"""

new_html = re.sub(r'        window\.onload = async function\(\) \{.*\}', onload_replacement, new_html, flags=re.MULTILINE | re.DOTALL)

with open('src/main/resources/static/referrals.html', 'w', encoding='utf-8') as f:
    f.write(new_html)
