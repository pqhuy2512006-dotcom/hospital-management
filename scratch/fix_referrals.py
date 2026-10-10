import re

with open('src/main/resources/static/referrals.html', 'r', encoding='utf-8') as f:
    html = f.read()

# Extract from beginning up to `<main class="content-body">`
match_start = re.search(r'(.*?<main class="content-body">)', html, re.DOTALL)
if not match_start:
    print("Could not find content-body start.")
    exit(1)
start_part = match_start.group(1)

# Extract the CSS block to modify grid
css_match = re.search(r'(\.content-body\s*{[^}]+grid-template-columns:)\s*360px 1fr;([^}]+})', start_part, re.DOTALL)
if css_match:
    start_part = start_part[:css_match.start()] + css_match.group(1) + ' 1fr;' + css_match.group(2) + start_part[css_match.end():]

# Look for the "Chuyển khoa / Hội chẩn" section and "Yêu cầu hội chẩn đến"
consultation_match = re.search(r'(<section class="section-box" style="margin-top:20px;">\s*<div class="section-title">\s*<span><i class="fa-solid fa-arrow-right-arrow-left"></i> Chuyển khoa / Hội chẩn</span>.*?</section>\s*<section class="section-box" style="margin-top:20px;" id="consultationSection".*?</section>)', html, re.DOTALL)
if not consultation_match:
    print("Could not find consultation section.")
    exit(1)
consultation_part = consultation_match.group(1)

# Extract scripts
script_match = re.search(r'(</main>.*?</html>)', html, re.DOTALL)
if script_match:
    end_part = script_match.group(1)
else:
    script_match2 = re.search(r'(<script>.*?</html>)', html, re.DOTALL)
    if not script_match2:
        print("Could not find script block.")
        exit(1)
    end_part = '</main>\n    </div>\n\n' + script_match2.group(1)

# Remove duplicate body tags or appended stuff after </html>
end_part = re.sub(r'</html>.*', '</html>', end_part, flags=re.DOTALL)

# Combine
new_html = start_part + '\n' + consultation_part + '\n' + end_part

with open('src/main/resources/static/referrals.html', 'w', encoding='utf-8') as f:
    f.write(new_html)

print("Fixed referrals.html successfully.")

