import os
import re

path = 'd:/DOCUMENTS/CNPM/test/hospital-management/src/main/resources/static/referrals.html'
with open(path, 'r', encoding='utf-8', errors='ignore') as f:
    content = f.read()

# Replace the whole loadReferralOptions function
new_func = """        async function loadReferralOptions() {
            const examSelect = document.getElementById('referralExam');
            if (examSelect) {
                examSelect.replaceChildren(new Option('Chọn phiếu khám', ''));
                try {
                    const res = await fetch('/api/v1/phieukham/me');
                    if (res.ok) {
                        const exams = await res.json();
                        exams.forEach(e => examSelect.add(new Option(e.maPhieuKham + ' - BN: ' + e.maBenhNhan, e.maPhieuKham)));
                    }
                } catch(e) { console.error(e); }
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
                } catch(e) { console.error(e); }
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
                } catch(e) { console.error(e); }
            }
        }"""

content = re.sub(r'        async function loadReferralOptions\(\) \{.*?^\s*\}', new_func, content, flags=re.MULTILINE | re.DOTALL)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Replaced loadReferralOptions")
