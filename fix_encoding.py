import os

path = 'd:/DOCUMENTS/CNPM/test/hospital-management/src/main/resources/static/referrals.html'
with open(path, 'r', encoding='utf-8', errors='ignore') as f:
    content = f.read()

replacements = {
    'Ch?n phi?u kh?m': 'Chọn phiếu khám',
    'Ch?n khoa nh?n': 'Chọn khoa nhận',
    'Ch?n b?c si': 'Chọn bác sĩ',
    'Kh?ng th? t?i y?u c?u chuy?n khoa/h?i ch?n.': 'Không thể tải yêu cầu chuyển khoa/hội chẩn.',
    'Chua c? y?u c?u.': 'Chưa có yêu cầu.',
    'Ch? duy?t': 'Chờ duyệt',
    'Ch? x?p giu?ng': 'Chờ xếp giường',
    'Ch? ti?p nh?n': 'Chờ tiếp nhận',
    '?? ti?p nh?n': 'Đã tiếp nhận',
    'H?i ch?n': 'Hội chẩn',
    'Chuy?n khoa': 'Chuyển khoa',
    'Kh?ng c? y?u c?u ch? duy?t.': 'Không có yêu cầu chờ duyệt.',
    'Duy?t': 'Duyệt',
    'Kh?ng th? t?i danh s?ch duy?t:': 'Không thể tải danh sách duyệt:',
    '?? g?i y?u c?u th?nh c?ng!': 'Đã gửi yêu cầu thành công!',
    'L?i: ': 'Lỗi: ',
    'L?i h? th?ng': 'Lỗi hệ thống',
    'X?c nh?n duy?t y?u c?u n?y?': 'Xác nhận duyệt yêu cầu này?',
    '?? duy?t!': 'Đã duyệt!',
    'Ti?p nh?n h?i ch?n': 'Tiếp nhận hội chẩn',
    'M? y?u c?u': 'Mã yêu cầu',
    'M? b?nh nh?n': 'Mã bệnh nhân',
    'L? do': 'Lý do',
    'Thao t?c': 'Thao tác',
    'tu?i': 'tuổi',
    'Chuy?n khoa & H?i ch?n': 'Chuyển khoa & Hội chẩn',
    'U?ng sau an': 'Uống sau ăn',
    'Xc nh?n ti?p nh?n h?i ch?n?': 'Xác nhận tiếp nhận hội chẩn?',
    'don v?': 'đơn vị'
}

for old, new in replacements.items():
    content = content.replace(old, new)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

print('Done.')
