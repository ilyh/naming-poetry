# 诗词取名小程序 — 设计文档

## 概述

从唐诗、宋词、诗经、楚辞等古典诗词中挑选汉字生成名字的 Web 应用。用户先输入姓氏（姓），系统从诗词中挑选 1-2 个字作为名，组合成完整姓名。支持随机生成、关键词筛选、主题意境三种模式。

## 技术栈

| 层 | 技术 |
|---|------|
| 前端 | Vue 3 + Vite + Tailwind CSS + Axios |
| 后端 | SpringBoot 3.x + JPA/Hibernate |
| 数据库 | MySQL 8.x |
| 数据源 | chinese-poetry 开源 JSON 数据集，一次性导入 |

## 架构

```
用户浏览器 → Vue 3 SPA (Vite) → SpringBoot REST API → MySQL
```

前后端分离，通过 RESTful JSON API 通信。

## 项目结构

```
naming-poetry/
├── frontend/              # Vue 3 + Vite
│   └── src/
│       ├── views/         # RandomPanel, KeywordPanel, ThemePanel
│       ├── components/    # NameCard, NameTabs, NavBar, HistoryDrawer, NameDetailModal
│       ├── api/           # Axios 封装
│       └── App.vue
├── backend/               # SpringBoot Maven 项目
│   └── src/main/java/com/example/naming/
│       ├── controller/    # NameController
│       ├── service/       # NameService（核心生成逻辑）
│       ├── repository/    # JPA Repository
│       └── entity/        # Poem, PoemWord, NameRecord
└── data/                  # 数据导入脚本
    └── import/
```

## 数据库设计

### poem — 诗句元信息
- id, title, author, source(tang/song/shijing/chuci), content, dynasty

### poem_word — 单字索引（核心表）
- id, poem_id(FK), word(单汉字), position, context(前后5字片段), prev_word, next_word, meaning_tag
- 索引：word, meaning_tag, poem_id

### name_record — 生成记录（可选）
- id, surname, given_name, full_name, source1_id(FK→poem_word), source2_id, source3_id, mode, created_at

## API 设计

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/name/random | 随机生成名字 |
| POST | /api/name/keyword | 关键词筛选生成 |
| POST | /api/name/theme | 主题/意境生成 |
| GET | /api/name/{id} | 名字溯源详情 |
| GET | /api/name/history | 历史记录（分页） |

### 请求/响应示例

所有生成接口均需传入姓氏（`surname`），`length` 仅指名（不含姓）的字数。

**随机生成：**
- Request: `{ "surname": "李", "count": 5, "length": 2 }`
- Response: `{ "names": [{"text": "李清远", "surname": "李", "givenName": "清远", "source": "明月松间照，清泉石上流"}, ...] }`

**关键词筛选：**
- Request: `{ "surname": "王", "keyword": "清", "count": 5, "length": 2 }`
- Response: `{ "names": [{"text": "王清风", "surname": "王", "givenName": "清风", "source": "清风徐来，水波不兴"}, ...] }`

**主题生成：**
- Request: `{ "surname": "张", "themes": ["山水", "清雅"], "count": 5, "length": 2 }`
- Response: `{ "names": [{"text": "张云溪", "surname": "张", "givenName": "云溪", "source": "...", "themes": ["山水"]}, ...] }`

## 生成算法

| 模式 | 算法 |
|------|------|
| 随机 | 从 poem_word 随机抽样，确保名字中多个字不来自同一诗句 |
| 关键词 | 用关键词匹配 poem_word.word，另一半从前字的 next_word 或随机匹配 |
| 主题 | 用 meaning_tag 过滤候选集，再按组合算法生成 |

## 前端设计

单页应用，三个模式通过 Tab 切换（非 Router）。

**全局输入区（Tab 上方公共区域）：**
- 姓氏输入框：单字，必填，默认值"李"，用户可修改为自己的姓
- 名字字数选择：1字 或 2字（此为名的长度，不含姓。1字名则全名为姓+1字，2字名则全名为姓+2字）

**各模式面板：**
- 模式特有输入（随机：无；关键词：输入框；主题：标签多选）
- 朝代过滤（可折叠，多选唐诗/宋词/诗经/楚辞）

**结果展示区：**
- NameCard 组件网格排列，每张卡片展示完整姓名（姓+名），姓和名用不同样式区分
- 每个名字支持溯源操作
- 溯源弹窗：NameDetailModal 展示每个字的诗句出处

**其他：**
- 历史侧栏：HistoryDrawer 查看之前的生成记录

### 组件树
```
App.vue
├── NavBar.vue
├── SurnameInput.vue          # 姓氏输入框（公共）
├── LengthSelector.vue        # 名字字数选择（公共）
├── NameTabs.vue
├── RandomPanel.vue / KeywordPanel.vue / ThemePanel.vue
├── NameCard.vue (复用)        # 显示完整姓名，区分姓/名样式
├── NameDetailModal.vue
└── HistoryDrawer.vue
```
