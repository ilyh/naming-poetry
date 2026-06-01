#!/usr/bin/env python3
"""
从 chinese-poetry GitHub 仓库获取宋词 JSON 数据，
转换为项目 sample_poems.json 格式并增量合并。

用法:
  python3 scripts/import_songci.py                    # 下载并合并全部
  python3 scripts/import_songci.py --files 0,1000     # 只下载指定分片
  python3 scripts/import_songci.py --dry-run          # 预览，不写入
"""

import json
import os
import re
import sys
import urllib.request
import argparse

REPO_BASE = "https://raw.githubusercontent.com/chinese-poetry/chinese-poetry/master/%E5%AE%8B%E8%AF%8D"

TARGET_FILE = os.path.join(os.path.dirname(__file__), "..", "backend", "src", "main", "resources", "data", "sample_poems.json")

# GitHub 上的宋词分片文件名
ALL_FILES = [
    "ci.song.0.json",
    "ci.song.1000.json",
    "ci.song.2000.json",
    "ci.song.3000.json",
    "ci.song.4000.json",
    "ci.song.5000.json",
    "ci.song.6000.json",
    "ci.song.7000.json",
    "ci.song.8000.json",
    "ci.song.9000.json",
    "ci.song.10000.json",
    "ci.song.11000.json",
    "ci.song.12000.json",
    "ci.song.13000.json",
    "ci.song.14000.json",
    "ci.song.15000.json",
    "ci.song.16000.json",
    "ci.song.17000.json",
    "ci.song.18000.json",
    "ci.song.19000.json",
    "ci.song.20000.json",
    "ci.song.21000.json",
    "ci.song.2019y.json",
    "宋词三百首.json",
]


def download_json(filename, retries=3):
    """下载单个 JSON 文件并返回解析后的数据"""
    import urllib.parse
    encoded = urllib.parse.quote(filename)
    url = f"{REPO_BASE}/{encoded}"
    for attempt in range(retries):
        try:
            with urllib.request.urlopen(url, timeout=60) as resp:
                raw = resp.read()
                data = json.loads(raw)
                if attempt > 0:
                    print(f"[重试{attempt}次成功]", end=" ", flush=True)
                print(f"{len(data)} 条")
                return data
        except Exception as e:
            if attempt < retries - 1:
                print(f"[{attempt+1}/{retries}失败, 重试...]", end=" ", flush=True)
                continue
            print(f"失败: {e}")
    return []


def make_title(rhythmic, paragraphs):
    """用词牌名 + 首句前缀构造唯一标题"""
    if not rhythmic:
        return "无题"
    if not paragraphs or not paragraphs[0]:
        return rhythmic
    first_line = paragraphs[0].strip()
    # 只保留汉字，取前 6 字作为副标题
    clean = ''.join(ch for ch in first_line if '一' <= ch <= '鿿')
    prefix = clean[:6] if clean else first_line[:6]
    return f"{rhythmic}·{prefix}"


def convert_entry(entry):
    """将 chinese-poetry 格式转为项目格式"""
    rhythmic = (entry.get("rhythmic") or "").strip()
    author = (entry.get("author") or "").strip() or "佚名"
    paragraphs = entry.get("paragraphs", [])

    if not paragraphs:
        return None

    # 拼接正文，去掉标点和空白
    content = "".join(p.strip() for p in paragraphs if p.strip())
    if len(content) < 8:
        return None  # 太短跳过

    return {
        "title": make_title(rhythmic, paragraphs),
        "author": author,
        "source": "song",
        "dynasty": "宋",
        "content": content,
    }


def load_existing(path):
    """加载现有数据"""
    if not os.path.exists(path):
        return []
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)


def build_existing_keys(poems):
    """构建已有诗词的去重键集合"""
    keys = set()
    for p in poems:
        key = (p.get("title", ""), p.get("author", ""), p.get("source", ""))
        keys.add(key)
    return keys


def main():
    parser = argparse.ArgumentParser(description="增量导入宋词到 sample_poems.json")
    parser.add_argument("--files", type=str, help="逗号分隔的分片索引，如 '0,1000,2000'")
    parser.add_argument("--dry-run", action="store_true", help="只预览不写入")
    parser.add_argument("--all", action="store_true", help="下载所有分片（默认）")
    args = parser.parse_args()

    # 确定要下载的文件
    if args.files:
        file_indices = [int(x.strip()) for x in args.files.split(",")]
        files = [f"ci.song.{i}.json" for i in file_indices]
    else:
        files = ALL_FILES

    # 加载现有数据
    existing = load_existing(TARGET_FILE)
    existing_keys = build_existing_keys(existing)
    print(f"现有诗词: {len(existing)} 首")

    # 下载并转换
    all_new = []
    seen_keys = set()
    total_raw = 0

    for filename in files:
        raw_entries = download_json(filename)
        total_raw += len(raw_entries)
        for entry in raw_entries:
            converted = convert_entry(entry)
            if converted is None:
                continue
            key = (converted["title"], converted["author"], converted["source"])
            if key in existing_keys or key in seen_keys:
                continue
            seen_keys.add(key)
            all_new.append(converted)

    print(f"\n下载总计: {total_raw} 条")
    print(f"转换有效: {len(all_new)} 条新增")

    if not all_new:
        print("没有新诗词需要导入。")
        return

    if args.dry_run:
        print("\n[预览] 前 10 条:")
        for p in all_new[:10]:
            print(f"  {p['title']} — {p['author']}")
        return

    # 合并并写入
    merged = existing + all_new
    with open(TARGET_FILE, "w", encoding="utf-8") as f:
        json.dump(merged, f, ensure_ascii=False, indent=2)
    print(f"已写入 {TARGET_FILE} ({len(merged)} 首)")

    # 统计
    sources = {}
    for p in merged:
        s = p.get("source", "?")
        sources[s] = sources.get(s, 0) + 1
    print("按来源:", sources)


if __name__ == "__main__":
    main()
