import codecs

file_path = 'src/main/resources/static/settings.html'
with codecs.open(file_path, 'r', 'utf-8') as f:
    content = f.read()

old_str = "if (currentUser.vaiTro === 'BacSi' || currentUser.vaiTro === 'DOCTOR') {"
new_str = "if (currentUser.vaiTro !== 'QuanTri' && currentUser.vaiTro !== 'ADMIN') {"

content = content.replace(old_str, new_str)

with codecs.open(file_path, 'w', 'utf-8') as f:
    f.write(content)
