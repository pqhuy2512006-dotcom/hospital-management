import os
import re

path = 'd:/DOCUMENTS/CNPM/test/hospital-management/src/main/resources/static/referrals.html'
with open(path, 'r', encoding='utf-8', errors='ignore') as f:
    content = f.read()

content = re.sub(r'Ch.n phi.u kh.m', 'Chọn phiếu khám', content)
content = re.sub(r'Ch.n khoa nh.n', 'Chọn khoa nhận', content)
content = re.sub(r'Ch.n b.c si', 'Chọn bác sĩ', content)
content = re.sub(r'Kh.ng th. t.i y.u c.u chuy.n khoa/h.i ch.n.', 'Không thể tải yêu cầu chuyển khoa/hội chẩn.', content)
content = re.sub(r'Chua c. y.u c.u.', 'Chưa có yêu cầu.', content)
content = re.sub(r'invitedDoct.or', 'invitedDoctor', content)
content = re.sub(r'Ch. duy.t', 'Chờ duyệt', content)
content = re.sub(r'Ch. x.p giu.ng', 'Chờ xếp giường', content)
content = re.sub(r'Ch. ti.p nh.n', 'Chờ tiếp nhận', content)
content = re.sub(r'.. ti.p nh.n', 'Đã tiếp nhận', content)
content = re.sub(r'H.i ch.n', 'Hội chẩn', content)
content = re.sub(r'Chuy.n khoa', 'Chuyển khoa', content)
content = re.sub(r'Kh.ng c. y.u c.u ch. duy.t.', 'Không có yêu cầu chờ duyệt.', content)
content = re.sub(r'Duy.t', 'Duyệt', content)
content = re.sub(r'Kh.ng th. t.i danh s.ch duy.t:', 'Không thể tải danh sách duyệt:', content)
content = re.sub(r'.. g.i y.u c.u th.nh c.ng!', 'Đã gửi yêu cầu thành công!', content)
content = re.sub(r'L.i h. th.ng', 'Lỗi hệ thống', content)
content = re.sub(r'X.c nh.n duy.t y.u c.u n.y\?', 'Xác nhận duyệt yêu cầu này?', content)
content = re.sub(r'.. duy.t!', 'Đã duyệt!', content)
content = re.sub(r'L.i: ', 'Lỗi: ', content)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Regex fixed strings")
