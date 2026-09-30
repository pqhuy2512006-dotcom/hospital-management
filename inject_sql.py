import codecs
import re

file_path = 'database/QueueManagement.sql'
with codecs.open(file_path, 'r', 'utf-8') as f:
    content = f.read()

# Inject the early check
early_check = """
    -- Tính số phút trễ (khoảng cách từ lúc hẹn đến hiện tại)
    SET @DelayMinutes = DATEDIFF(MINUTE, @GioHen, GETDATE());
    
    -- Kiểm tra nếu bác sĩ gọi khám quá sớm (trước giờ hẹn)
    IF @DelayMinutes < 0
    BEGIN
        RAISERROR(N'Chưa tới giờ hẹn. Không thể bắt đầu khám sớm hơn lịch trình được xếp!', 16, 1);
        RETURN;
    END

    -- Nếu đến trễ quá 10 phút, TỰ ĐỘNG HỦY"""

# Use regex to find where to insert
content = re.sub(r"-- T[^\<]*nh s[^\<]* ph[^\<]*t tr[^\<]* \(kho[^\<]*ng c[^\<]*ch t[^\<]* l[^\<]*c h[^\<]*n [^\<]*n hi[^\<]*n t[^\<]*i\)\s*SET @DelayMinutes = DATEDIFF\(MINUTE, @GioHen, GETDATE\(\)\);\s*-- N[^\<]*u [^\<]*n tr[^\<]* qu[^\<]* 10 ph[^\<]*t, T[^\<]* [^\<]*NG H[^\<]*Y", early_check, content)

with codecs.open(file_path, 'w', 'utf-8') as f:
    f.write(content)
