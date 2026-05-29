# Hero 区域 + NameCard 拼音展示 设计文档

## 目标

1. 在首页工具区域上方添加简洁品牌型 Hero 区域
2. 在 NameCard 的名字上用 ruby 注音展示拼音

## 范围

- 新增 Hero 区域组件
- 安装 pinyin-pro 前端库
- 修改 NameCard.vue 添加拼音注音
- 不改动后端、详情弹窗、HistoryDrawer

## Hero 区域

### 位置

`HomePage.vue` 中，工具区域（sidebar + main）上方插入 Hero 组件。

### 布局

- 垂直居中，高度约 160-200px
- 大标题：「古诗文起名」，使用 `font-serif-name` 衬线字体，`text-warm-brown`
- 副标题：「从千年诗词中，觅一个好名」，`text-warm-gray`，较小字号
- 装饰：标题与副标题之间、副标题下方各一条淡色分隔线，用 CSS `border` 实现，左右带装饰符号（· 或 ◇）
- 背景：`bg-paper`，与页面一致

### 响应式

- 移动端缩小字号，padding 相应调整

## NameCard 拼音展示

### 依赖

- 安装 `pinyin-pro`（前端 npm 包）

### Composable

创建 `frontend/src/composables/usePinyin.js`：

- 导入 `pinyin-pro` 的 `pinyin` 函数
- 导出 `getPinyin(char)` —— 返回单个汉字的带声调拼音（如 "朝" → "zhāo"）
- 导出 `getPinyinArray(text)` —— 返回字符串中每个汉字的拼音数组

### NameCard 修改

在 `NameCard.vue` 中，名字显示区域改为：

```html
<span v-for="(char, i) in nameChars" :key="i">
  <ruby v-if="isChinese(char)">
    {{ char }}
    <rp>(</rp><rt>{{ getPinyin(char) }}</rt><rp>)</rp>
  </ruby>
  <span v-else>{{ char }}</span>
</span>
```

### 拼音样式

- `<rt>` 标签：`text-xs text-warm-gray`，位于汉字正上方
- 使用 pinyin-pro 默认的带声调符号拼音（如 zhāo、míng）
- `isChinese` 判断：用正则 `/[一-鿿]/` 区分汉字与非汉字字符（如连字符）

## 不改动的部分

- NameDetailModal（详情弹窗）
- HistoryDrawer
- 后端 DTO 和生成逻辑
- RandomPanel / KeywordPanel / ThemePanel（仅通过 NameCard 间接受益）
