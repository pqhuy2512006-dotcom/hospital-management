import json
import urllib.request
import urllib.error

url = "http://localhost:8080/api/v1/auth/login"
req = urllib.request.Request(url, headers={'Content-Type': 'application/json'}, data=json.dumps({
    "email": "le.tan@hospital.vn",
    "matKhau": "LeTan@123"
}).encode('utf-8'))
try:
    with urllib.request.urlopen(req) as response:
        res = json.loads(response.read().decode())
        token = res['token']
        print("Got token")
except Exception as e:
    print("Login failed:", e)

# Now book walkin
url2 = "http://localhost:8080/api/v1/lichhen"
req2 = urllib.request.Request(url2, headers={
    'Content-Type': 'application/json',
    'Authorization': f'Bearer {token}'
}, data=json.dumps({
    "patientName": "Test Walkin",
    "cccd": "123456789012",
    "maKhoa": "KNT",
    "ngayKham": "2026-10-06",
    "gioKham": "09:00",
    "maBacSi": "NV-DOC01",
    "lyDoKham": "Test",
    "loaiKham": "KhamThuong",
    "hinhThucDat": "TrucTiep"
}).encode('utf-8'))

try:
    with urllib.request.urlopen(req2) as response:
        res2 = json.loads(response.read().decode())
        print("Success:", res2)
except urllib.error.HTTPError as e:
    print("Failed:", e.code, e.read().decode())
