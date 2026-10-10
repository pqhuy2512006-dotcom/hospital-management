import sys
import io
import re
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

with open('src/main/resources/static/doctors.html', 'r', encoding='utf-8') as f:
    content = f.read()

match = re.search(r'<script>([\s\S]*?)<\/script>', content)
if match:
    lines = match.group(1).split('\n')
    for i in range(len(lines)-20, len(lines)):
        print(f'{i+1}: {lines[i]}')
