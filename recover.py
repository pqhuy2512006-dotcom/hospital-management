import json

path = r'C:\Users\Anh Quoc\.gemini\antigravity\brain\ae229d1e-5fe6-47f5-a4b9-7ab2f96f9db4\.system_generated\logs\transcript_full.jsonl'
with open(path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

for i in range(len(lines)-1, -1, -1):
    if 'write_to_file' in lines[i] and 'referrals.html' in lines[i]:
        print(f"Found write_to_file at line {i}")
        data = json.loads(lines[i])
        for tc in data.get('tool_calls', []):
            if tc['name'] == 'write_to_file' and 'referrals.html' in tc['args'].get('TargetFile', ''):
                with open('recovered_referrals.html', 'w', encoding='utf-8') as out:
                    out.write(tc['args']['CodeContent'])
                print("Recovered referrals.html!")
                break
        break
