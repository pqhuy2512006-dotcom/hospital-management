import codecs

file_path = 'src/main/resources/static/settings.html'
with codecs.open(file_path, 'r', 'utf-8') as f:
    content = f.read()

old_load = """        async function loadMyProfile() {
            const response = await fetch('/api/v1/nhanvien/me');
            if (!response.ok) throw new Error('KhA'ng th ti h" s cA nhAn.');
            const profile = await response.json();
            document.getElementById('profileFullName').value = profile.hoTen || '';
            document.getElementById('profileSpecialty').value = profile.chuyenKhoa || '';
            document.getElementById('profileLicense').value = profile.chungChiHanhNghe || '';
            document.getElementById('profilePhone').value = profile.soDienThoai || '';
            document.getElementById('profileEmail').value = profile.email || '';
            document.getElementById('profileAddress').value = profile.diaChi || '';
            document.getElementById('personalProfileCard').style.display = 'block';
        }"""
        
# Since the text is garbled in the python script if I try to exact match, let's use regex
import re

pattern = re.compile(r"async function loadMyProfile\(\) \{[\s\S]*?document\.getElementById\('personalProfileCard'\)\.style\.display = 'block';\s*\}", re.MULTILINE)

new_load = """async function loadMyProfile() {
            const response = await fetch('/api/v1/nhanvien/me');
            if (!response.ok) throw new Error('Không thể tải hồ sơ cá nhân.');
            const profile = await response.json();
            document.getElementById('profileFullName').value = profile.hoTen || '';
            document.getElementById('profileSpecialty').value = profile.chuyenKhoa || '';
            document.getElementById('profileLicense').value = profile.chungChiHanhNghe || '';
            document.getElementById('profilePhone').value = profile.soDienThoai || '';
            document.getElementById('profileEmail').value = profile.email || '';
            document.getElementById('profileAddress').value = profile.diaChi || '';
            
            // Ẩn Chuyên khoa & Chứng chỉ nếu không có (dành cho nhân viên không phải bác sĩ)
            if (!profile.chuyenKhoa) document.getElementById('profileSpecialty').parentElement.style.display = 'none';
            if (!profile.chungChiHanhNghe) document.getElementById('profileLicense').parentElement.style.display = 'none';
            
            document.getElementById('personalProfileCard').style.display = 'block';
        }"""

content = pattern.sub(new_load, content)

with codecs.open(file_path, 'w', 'utf-8') as f:
    f.write(content)
