# 黑名单管理前端页面设计

## 概述

为现有取名工具增加黑名单管理页面，支持查看、批量编辑、逐字增删黑名单字符，通过后端 API 实现热更新。

## 路由架构

- 引入 `vue-router`，根组件 `App.vue` 精简为外壳（NavBar + `<router-view />` + Footer）
- 两个路由：
  - `/` → `HomePage.vue`（原 App.vue 主体内容）
  - `/admin` → `AdminPage.vue`（黑名单管理页）
- NavBar 新增"管理"入口，LOGO 点击返回主页

## 组件拆分

```
views/HomePage.vue           ← 原 App.vue 主体（两栏布局 + tab 切换）
views/AdminPage.vue          ← 管理页面容器，两栏布局，持有所有状态
components/AdminSidebar.vue  ← 左栏：标题 + 返回链接
components/BatchEditor.vue   ← 右栏上：批量编辑文本框 + 保存按钮
components/CharGrid.vue      ← 右栏下：搜索框 + 字标签网格 + 添加输入
```

## 状态管理

AdminPage 内部状态，通过 props 向下传递：

- `chars: string` — 完整黑名单字符串（逗号分隔）
- `searchQuery: string` — 逐字管理搜索过滤词
- `loading: boolean` — 请求中
- `toastMessage: string` — 操作提示

### 数据流

1. `onMounted` → `GET /api/admin/blacklist` → 填充 chars
2. BatchEditor 保存 → `POST /api/admin/blacklist { chars }` → 刷新
3. CharGrid 删除某字 → 从 chars 移除 → `POST /api/admin/blacklist`
4. CharGrid 添加某字 → 去重校验 → 加入 chars → `POST /api/admin/blacklist`

## API 新增

```js
// frontend/src/api/index.js
export function getBlacklist()         // GET  /admin/blacklist
export function updateBlacklist(chars) // POST /admin/blacklist { chars }
export function reloadBlacklist()      // POST /admin/blacklist/reload
```

## 页面布局

```
┌─────────────────────────────────────────────────────────┐
│  NavBar                          [管理] [历史记录]       │
├──────────────────┬──────────────────────────────────────┤
│  Step 01         │  ┌─ 批量编辑 ──────────────────────┐ │
│  黑名单管理       │  │  [textarea]                     │ │
│                  │  │  当前字符数: 230                  │ │
│  ← 返回取名工具   │  │  [保存并热更新]                 │ │
│                  │  └────────────────────────────────┘ │
│                  │  ┌─ 逐字管理 ──────────────────────┐ │
│                  │  │  [搜索...] [添加字] [→]         │ │
│                  │  │  [不] [丧] [乱] [亏] ...        │ │
│                  │  └────────────────────────────────┘ │
├──────────────────┴──────────────────────────────────────┤
│  Footer                                                │
└─────────────────────────────────────────────────────────┘
```

## 交互细节

- 逐字标签使用玻璃感小方块，hover 背景变红提示可删除
- 点击标签 → 删除，自动保存
- 添加时去重：已存在的字 toast 提示
- 添加时校验：仅接受单个汉字（CJK U+4E00 ~ U+9FFF）
- 保存成功/失败 toast 提示

## 视觉风格

- 延续主页面暖色调：`glass-card`、`text-teal-warm`、`text-warm-brown`、`font-serif-name`
- 背景渐变与主页面一致
- 两栏布局尺寸与主页面相同（左 18rem + 右 1fr）

## 文件变更清单

| 文件 | 动作 |
|------|------|
| `frontend/package.json` | 新增 `vue-router` 依赖 |
| `frontend/src/router/index.js` | 新建 — 路由配置 |
| `frontend/src/main.js` | 注册 router |
| `frontend/src/App.vue` | 改写为外壳组件 |
| `frontend/src/views/HomePage.vue` | 新建 — 原 App.vue 主体 |
| `frontend/src/views/AdminPage.vue` | 新建 — 黑名单管理页 |
| `frontend/src/components/AdminSidebar.vue` | 新建 — 管理页左栏 |
| `frontend/src/components/BatchEditor.vue` | 新建 — 批量编辑区 |
| `frontend/src/components/CharGrid.vue` | 新建 — 逐字管理区 |
| `frontend/src/components/NavBar.vue` | 新增管理入口 |
| `frontend/src/api/index.js` | 新增 3 个 API 函数 |
