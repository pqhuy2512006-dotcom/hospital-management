import os
import re

path = 'd:/DOCUMENTS/CNPM/test/hospital-management/src/main/resources/static/referrals.html'
with open(path, 'r', encoding='utf-8', errors='ignore') as f:
    content = f.read()

content = re.sub(r'Phi[^ ]+ kh[^ ]+ \*', 'Phiếu khám *', content)
content = re.sub(r'Khoa nh[^ ]+ \*', 'Khoa nhận *', content)
content = re.sub(r'L[^ ]+ do chuy[^ ]+ khoa / n[^ ]+i dung h[^ ]+i ch[^ ]+n \*', 'Lý do chuyển khoa / nội dung hội chẩn *', content)
content = re.sub(r'B[^ ]+c si h[^ ]+i ch[^ ]+n', 'Bác sĩ hội chẩn', content)
content = re.sub(r'Lo[^ ]+i y[^ ]+u c[^ ]+u \*', 'Loại yêu cầu *', content)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Regex fixed HTML labels")
