import os

path = 'd:/DOCUMENTS/CNPM/test/hospital-management/src/main/resources/static/referrals.html'
with open(path, 'r', encoding='utf-8', errors='ignore') as f:
    content = f.read()

content = content.replace('Opt?ion', 'Option')
content = content.replace('fetch', 'fetch')
content = content.replace('catch', 'catch')
content = content.replace('doct?or', 'doctor')
content = content.replace('funct?ion', 'function')
content = content.replace('doct?ors', 'doctors')
content = content.replace('doct?orSelect', 'doctorSelect')

# Fix UI text
content = content.replace('Ch?n phi?u khm', 'Chọn phiếu khám')
content = content.replace('Ch?n khoa nh?n', 'Chọn khoa nhận')
content = content.replace('Ch?n b?c si', 'Chọn bác sĩ')
content = content.replace('Ch?n phi?u kh?m', 'Chọn phiếu khám')
content = content.replace('Chua c? y?u c?u', 'Chưa có yêu cầu')
content = content.replace('Kh?ng th? t?i', 'Không thể tải')
content = content.replace('y?u c?u chuy?n khoa/h?i ch?n', 'yêu cầu chuyển khoa/hội chẩn')

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Fixed JS")
