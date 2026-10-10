import os
import re

path = 'src/main/resources/static/auth-guard.js'
with open(path, 'r', encoding='utf-8', errors='ignore') as f:
    content = f.read()

content = content.replace("'referrals.html': ['BacSi', 'DOCTOR', 'DieuDuong', 'NURSE']", "'referrals.html': ['BacSi', 'DOCTOR', 'DieuDuong', 'NURSE', 'TruongKhoa']")
content = content.replace("'referrals.html': ['BacSi', 'DOCTOR', 'DieuDuong', 'NURSE', 'QuanTri', 'ADMIN', 'QuanLyNhanSu']", "'referrals.html': ['BacSi', 'DOCTOR', 'DieuDuong', 'NURSE', 'QuanTri', 'ADMIN', 'QuanLyNhanSu', 'TruongKhoa']")

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated auth-guard.js")
