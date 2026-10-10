import os
import re

path = 'd:/DOCUMENTS/CNPM/test/hospital-management/src/main/resources/static/referrals.html'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix the title
content = re.sub(r'<title>.*?</title>', '<title>Chuyển khoa & Hội chẩn - MEDICARE HIS</title>', content)

# Fix sidebar
content = re.sub(r'Chuy.n khoa & H.Ti ch.cn', 'Chuyển khoa & Hội chẩn', content)

# Write back
with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

