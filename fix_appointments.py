import codecs

file_path = 'src/main/resources/static/appointments.html'
with codecs.open(file_path, 'r', 'utf-8') as f:
    content = f.read()

old_logic = """            if (isPatientUser()) {
                document.body.classList.add('patient-mode');
                document.getElementById('appointmentTabs').remove();
                document.getElementById('pane-walkin').remove();
                document.querySelectorAll('.staff-only').forEach(element => element.remove());
                document.querySelector('#pane-online .btn-submit-action').innerHTML = '<i class="fa-solid fa-calendar-plus"></i> Gửi yêu cầu đặt lịch';
                document.getElementById('onlTime').value = '08:00';
            }"""

new_logic = """            if (isPatientUser()) {
                document.body.classList.add('patient-mode');
                document.getElementById('appointmentTabs').remove();
                document.getElementById('pane-walkin').remove();
                document.querySelectorAll('.staff-only').forEach(element => element.remove());
                document.querySelector('#pane-online .btn-submit-action').innerHTML = '<i class="fa-solid fa-calendar-plus"></i> Gửi yêu cầu đặt lịch';
                document.getElementById('onlTime').value = '08:00';
            } else {
                // Lễ tân không được đặt khám Online mà chỉ tạo phiếu Walk-in
                const appTabs = document.getElementById('appointmentTabs');
                if (appTabs) appTabs.remove();
                
                const paneOnline = document.getElementById('pane-online');
                if (paneOnline) paneOnline.remove();
                
                const paneWalkin = document.getElementById('pane-walkin');
                if (paneWalkin) paneWalkin.classList.add('active');
            }"""

if old_logic in content:
    content = content.replace(old_logic, new_logic)
    with codecs.open(file_path, 'w', 'utf-8') as f:
        f.write(content)
    print("Replaced logic!")
else:
    print("Logic block not found. Checking if it's already updated or garbled.")

