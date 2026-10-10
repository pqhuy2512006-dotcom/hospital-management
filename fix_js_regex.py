import os
import re

path = 'd:/DOCUMENTS/CNPM/test/hospital-management/src/main/resources/static/referrals.html'
with open(path, 'r', encoding='utf-8', errors='ignore') as f:
    content = f.read()

# Replace any character that is not ASCII in 'fetch', 'catch', 'function', 'doctor', 'document', 'const'
content = re.sub(r'fet[^a-z]ch', 'fetch', content)
content = re.sub(r'cat[^a-z]ch', 'catch', content)
content = re.sub(r'c[^a-z]onst', 'const', content)
content = re.sub(r'doc[^a-z]ument', 'document', content)
content = re.sub(r'func[^a-z]tion', 'function', content)
content = re.sub(r'doct[^a-z]or', 'doctor', content)
content = re.sub(r'c[^a-z]huyenkhoa', 'chuyenkhoa', content)
content = re.sub(r'forEac[^a-z]h', 'forEach', content)
content = re.sub(r'selec[^a-z]t', 'select', content, flags=re.IGNORECASE)
content = re.sub(r'Selec[^a-z]t', 'Select', content)
content = re.sub(r'replac[^a-z]e', 'replace', content)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Regex fixed JS")
