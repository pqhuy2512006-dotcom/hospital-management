import urllib.request
import json
import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

try:
    with urllib.request.urlopen('http://localhost:8080/api/v1/phong') as response:
        phongs = json.loads(response.read().decode())
        print('PHONG:')
        for p in phongs:
            print(p['tenPhong'], '|', p['loaiPhong'], '|', p['maKhoa'])
except Exception as e:
    print('Error:', e)
