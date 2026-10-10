import json

path = r'C:\Users\Anh Quoc\.gemini\antigravity\brain\ae229d1e-5fe6-47f5-a4b9-7ab2f96f9db4\.system_generated\logs\transcript_full.jsonl'
with open(path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

print(lines[3027][:1000])
