import re

with open('src/main/resources/static/doctors.html', 'r', encoding='utf-8') as f:
    html = f.read()

# Replace dChuyenKhoa select options
html = re.sub(
    r'<select id="dChuyenKhoa" class="form-control" required>.*?</select>',
    r'<select id="dChuyenKhoa" class="form-control" required>\n                                <option value="">-- Chọn Khoa --</option>\n                            </select>',
    html,
    flags=re.DOTALL
)

# Replace dPhongKham input with select
html = re.sub(
    r'<input type="text" id="dPhongKham" class="form-control" placeholder="Ví dụ: P\.204" required>',
    r'<select id="dPhongKham" class="form-control" required>\n                                <option value="">-- Chọn Phòng Khám --</option>\n                            </select>',
    html,
    flags=re.DOTALL
)

# Replace dPhongKham select text (if it was already partially modified)
html = re.sub(
    r'<input type="text" id="dPhongKham"[^>]*>',
    r'<select id="dPhongKham" class="form-control" required>\n                                <option value="">-- Chọn Phòng Khám --</option>\n                            </select>',
    html
)

# Insert JavaScript to handle loading Khoa and Phong
js_code = """
        // Load Khoa and Phong dynamic data
        async function loadKhoaForDoctor() {
            try {
                const response = await fetch('/api/v1/khoa');
                if (response.ok) {
                    const khoas = await response.json();
                    const dChuyenKhoa = document.getElementById('dChuyenKhoa');
                    dChuyenKhoa.innerHTML = '<option value="">-- Chọn Khoa --</option>';
                    khoas.forEach(k => {
                        // Assuming tenKhoa is used as value to match existing logic
                        const option = document.createElement('option');
                        option.value = k.tenKhoa;
                        option.dataset.makhoa = k.maKhoa;
                        option.textContent = k.tenKhoa;
                        dChuyenKhoa.appendChild(option);
                    });
                }
            } catch (err) {
                console.error('Error loading khoa:', err);
            }
        }

        document.getElementById('dChuyenKhoa').addEventListener('change', async function() {
            const selectedOption = this.options[this.selectedIndex];
            const maKhoa = selectedOption ? selectedOption.dataset.makhoa : null;
            const dPhongKham = document.getElementById('dPhongKham');
            
            dPhongKham.innerHTML = '<option value="">-- Chọn Phòng Khám --</option>';
            
            if (maKhoa) {
                try {
                    // Fetch all phong of the khoa
                    const response = await fetch(`/api/v1/phong?maKhoa=${maKhoa}`);
                    if (response.ok) {
                        const phongs = await response.json();
                        // Filter for loai phong "Phòng khám" if needed, or just show all
                        // (Assuming loaiPhong contains 'Phòng' or similar, but let's filter if it explicitly has 'khám')
                        const phongKhams = phongs.filter(p => p.loaiPhong && p.loaiPhong.toLowerCase().includes('khám'));
                        
                        // If no "Phòng khám" found, maybe the DB has different LoaiPhong. Fallback to all rooms:
                        const listToRender = phongKhams.length > 0 ? phongKhams : phongs;
                        
                        listToRender.forEach(p => {
                            const option = document.createElement('option');
                            option.value = p.tenPhong; // Value is tenPhong so it maps to diaChi like before
                            option.textContent = p.tenPhong + ' (' + p.loaiPhong + ')';
                            dPhongKham.appendChild(option);
                        });
                    }
                } catch (err) {
                    console.error('Error loading phong:', err);
                }
            }
        });

        // Initialize when opening modal
        document.getElementById('addBtn').addEventListener('click', () => {
            loadKhoaForDoctor();
        });
"""

# Inject the js code before "function openModal()"
html = html.replace('function openModal() {', js_code + '\n        function openModal() {')

# Also load Khoa when opening modal for edit
html = html.replace("document.getElementById('dChuyenKhoa').value = doc.chuyenKhoa;", """
            await loadKhoaForDoctor();
            document.getElementById('dChuyenKhoa').value = doc.chuyenKhoa;
            // trigger change to load Phong Khams
            const dChuyenKhoa = document.getElementById('dChuyenKhoa');
            const event = new Event('change');
            dChuyenKhoa.dispatchEvent(event);
            
            // wait a bit for phong to load, then set phongKham value
            setTimeout(() => {
                document.getElementById('dPhongKham').value = doc.diaChi;
            }, 500);
""")

with open('src/main/resources/static/doctors.html', 'w', encoding='utf-8') as f:
    f.write(html)

