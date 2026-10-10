import os
import re

path = 'd:/DOCUMENTS/CNPM/test/hospital-management/src/main/resources/static/referrals.html'
with open(path, 'r', encoding='utf-8', errors='ignore') as f:
    content = f.read()

replacements = [
    (r'Phi\?u kh.*m', 'Phiếu khám'),
    (r'Khoa nh\?n', 'Khoa nhận'),
    (r'L\? do', 'Lý do'),
    (r'chuy\?n khoa', 'chuyển khoa'),
    (r'Chuy\?n khoa', 'Chuyển khoa'),
    (r'h\?i ch\?n', 'hội chẩn'),
    (r'H\?i ch\?n', 'Hội chẩn'),
    (r'B\?c si', 'Bác sĩ'),
    (r'b\?c si', 'bác sĩ'),
    (r'Ti\?p nh\?n', 'Tiếp nhận'),
    (r'ti\?p nh\?n', 'tiếp nhận'),
    (r'M\? y\?u c\?u', 'Mã yêu cầu'),
    (r'M\? b\?nh nh\?n', 'Mã bệnh nhân'),
    (r'Thao t\?c', 'Thao tác'),
    (r'y\?u c\?u', 'yêu cầu'),
    (r'Y\?u c\?u', 'Yêu cầu'),
    (r'ch\? duy\?t', 'chờ duyệt'),
    (r'Ch\? duy\?t', 'Chờ duyệt'),
    (r'Ch\? x\?p giu\?ng', 'Chờ xếp giường'),
    (r'Duy\?t', 'Duyệt'),
    (r'duy\?t', 'duyệt'),
    (r'th\?nh c.*?ng', 'thành công'),
    (r'L\?i', 'Lỗi'),
    (r'X\?c nh\?n', 'Xác nhận'),
    (r'h\? th.*?ng', 'hệ thống'),
    (r'c\?a t.*?i', 'của tôi'),
    (r'b\?nh nh.*?n', 'bệnh nhân'),
    (r'B\?nh nh.*?n', 'Bệnh nhân'),
    (r'Kh.*?ng th\?', 'Không thể'),
    (r'kh.*?ng th\?', 'không thể'),
    (r'Kh.*?ng c\?', 'Không có'),
    (r'kh.*?ng c\?', 'không có'),
    (r'danh s\?ch', 'danh sách'),
    (r't\?i danh', 'tải danh'),
]

for old, new in replacements:
    content = re.sub(old, new, content)

# Specific fixes
content = content.replace('?? ti?p nh?n', 'Đã tiếp nhận')
content = content.replace('?? g?i', 'Đã gửi')
content = content.replace('?? duy?t', 'Đã duyệt')
content = content.replace('Chua c?', 'Chưa có')

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Regex fixed UI texts")
