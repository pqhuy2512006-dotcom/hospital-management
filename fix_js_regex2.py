import os
import re

path = 'd:/DOCUMENTS/CNPM/test/hospital-management/src/main/resources/static/referrals.html'
with open(path, 'r', encoding='utf-8', errors='ignore') as f:
    content = f.read()

content = re.sub(r'Opt[^a-z]ion', 'Option', content)
content = re.sub(r'funct[^a-z]ion', 'function', content)
content = re.sub(r'doct[^a-z]or', 'doctor', content)
content = re.sub(r'c[^a-z]onsole', 'console', content)
content = re.sub(r'c[^a-z]atch', 'catch', content)

# Fix strings
content = content.replace('Ch?n khoa nh?n', 'Chọn khoa nhận')
content = content.replace('Ch?n b?c si', 'Chọn bác sĩ')
content = content.replace('Kh?ng th? t?i y?u c?u chuy?n khoa/h?i ch?n.', 'Không thể tải yêu cầu chuyển khoa/hội chẩn.')
content = content.replace('Ch?n phi?u kh?m', 'Chọn phiếu khám')
content = content.replace('Ch?n phi?u khm', 'Chọn phiếu khám')

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Regex fixed JS again")
