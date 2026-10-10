import sys
import io
import re
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

with open('src/main/java/com/nhom12/hospital/repository/TruongKhoaRepository.java', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace("AND VaiTro = 'BacSi'", "")

with open('src/main/java/com/nhom12/hospital/repository/TruongKhoaRepository.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("Done")
