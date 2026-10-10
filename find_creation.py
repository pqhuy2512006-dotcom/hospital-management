import json

path = r'C:\Users\Anh Quoc\.gemini\antigravity\brain\ae229d1e-5fe6-47f5-a4b9-7ab2f96f9db4\.system_generated\logs\transcript_full.jsonl'
with open(path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if 'referrals.html' in line and ('write_to_file' in line or 'open(' in line):
        if 'PLANNER_RESPONSE' in line:
            # We found a potential match
            with open(f'match_{i}.txt', 'w', encoding='utf-8') as out:
                out.write(line)
