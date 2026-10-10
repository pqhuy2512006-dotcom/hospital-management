import os

path = 'd:/DOCUMENTS/CNPM/test/hospital-management/src/main/resources/static/referrals.html'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('có', 'c')
content = content.replace('Có', 'C')
content = content.replace('tc', 'tóc') # Wait, I replaced 'tc' with 'tóc'? No.

# Fix the specific words
content = content.replace('Khng c', 'Không có')
content = content.replace('Chua c ', 'Chưa có ')

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Restored C")
