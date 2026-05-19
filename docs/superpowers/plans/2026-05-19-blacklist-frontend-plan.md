# Blacklist Management Frontend Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a blacklist management page at `/admin` with batch editing and per-character grid management, plus hot-reload via backend API.

**Architecture:** Introduce vue-router with two routes (`/` for the name tool, `/admin` for blacklist management). Extract current App.vue content into HomePage.vue. AdminPage.vue composes BatchEditor and CharGrid components, calling the existing backend admin API.

**Tech Stack:** Vue 3.5 + Vite 8 + Tailwind CSS 4 + axios + vue-router 4

---

### Task 1: Fix backend characters response format

**Files:**
- Modify: `backend/src/main/java/com/example/naming/controller/BlacklistController.java:18-23`

The GET endpoint returns `Set.toString()` which produces `[不, 丧, 乱]`. Frontend needs clean comma-separated string.

- [ ] **Step 1: Change `getBlacklist()` to return comma-separated string**

Replace the GET method:

```java
@GetMapping("/blacklist")
public Map<String, Object> getBlacklist() {
    Set<Character> chars = blacklistConfig.getBadChars();
    String charsStr = chars.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
    return Map.of(
        "blacklistSize", chars.size(),
        "characters", charsStr
    );
}
```

Add import at top: `import java.util.stream.Collectors;`

- [ ] **Step 2: Fix POST response character format too**

Replace the POST `/blacklist` response:

```java
@PostMapping("/blacklist")
public Map<String, Object> updateBlacklist(@RequestBody Map<String, String> body) {
    String chars = body.get("chars");
    if (chars == null || chars.trim().isEmpty()) {
        return Map.of("status", "error", "message", "chars 不能为空");
    }
    try {
        blacklistConfig.writeToFile(chars);
        blacklistConfig.reloadConfig();
        Set<Character> updated = blacklistConfig.getBadChars();
        String charsStr = updated.stream().map(String::valueOf).collect(Collectors.joining(","));
        return Map.of(
            "status", "success",
            "message", "黑名单已更新",
            "blacklistSize", updated.size(),
            "characters", charsStr
        );
    } catch (Exception e) {
        return Map.of("status", "error", "message", "更新失败: " + e.getMessage());
    }
}
```

Same fix for the `/reload` endpoint.

- [ ] **Step 3: Compile and commit**

```bash
cd backend && mvn compile -q
git add backend/src/main/java/com/example/naming/controller/BlacklistController.java
git commit -m "fix: return clean comma-separated string from blacklist API"
```

---

### Task 2: Install vue-router

**Files:**
- Modify: `frontend/package.json`

- [ ] **Step 1: Install vue-router**

```bash
cd frontend && npm install vue-router@4
```

- [ ] **Step 2: Commit**

```bash
git add frontend/package.json frontend/package-lock.json
git commit -m "chore: add vue-router dependency"
```

---

### Task 3: Create router config

**Files:**
- Create: `frontend/src/router/index.js`

- [ ] **Step 1: Write router config**

```js
import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'

const routes = [
  { path: '/', name: 'home', component: HomePage },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('../views/AdminPage.vue'),
  },
]

export default createRouter({
  history: createWebHistory(),
  routes,
})
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/router/index.js
git commit -m "feat: add router config with / and /admin routes"
```

---

### Task 4: Register router in main.js

**Files:**
- Modify: `frontend/src/main.js`

- [ ] **Step 1: Update main.js**

```js
import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'

createApp(App).use(router).mount('#app')
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/main.js
git commit -m "feat: register vue-router in main.js"
```

---

### Task 5: Create HomePage.vue

**Files:**
- Create: `frontend/src/views/HomePage.vue`

- [ ] **Step 1: Write HomePage.vue (extract from current App.vue)**

```vue
<template>
  <main class="max-w-5xl mx-auto px-4 py-6">
    <div class="grid grid-cols-1 lg:grid-cols-[18rem_1fr] gap-5">
      <aside class="glass-card rounded-2xl p-5">
        <p class="text-xs font-bold text-teal-warm tracking-widest mb-3">STEP 01</p>
        <h2 class="font-serif-name text-2xl text-warm-brown mb-2">挑选偏好</h2>
        <p class="text-sm text-warm-gray leading-relaxed mb-4">输入姓氏，选择选字方式和名字长度。</p>
        <div class="space-y-5">
          <BookSelector v-model="selectedSources" />
          <div class="border-t border-stone-300/20 pt-5 space-y-4">
            <SurnameInput v-model="surname" />
            <LengthSelector v-model="nameLength" />
            <NameTabs v-model="activeTab" />
          </div>
        </div>
      </aside>

      <section class="glass-card rounded-2xl p-5 min-h-[34rem]">
        <div class="mb-4">
          <p class="text-xs font-bold text-teal-warm tracking-widest mb-1">STEP 02</p>
          <h2 class="font-serif-name text-2xl text-warm-brown">诗意候选</h2>
        </div>

        <RandomPanel
          v-if="activeTab === 'random'"
          :surname="surname"
          :length="nameLength"
          :sources="selectedSources"
        />
        <KeywordPanel
          v-if="activeTab === 'keyword'"
          :surname="surname"
          :length="nameLength"
          :sources="selectedSources"
        />
        <ThemePanel
          v-if="activeTab === 'theme'"
          :surname="surname"
          :length="nameLength"
          :sources="selectedSources"
        />
      </section>
    </div>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import BookSelector from '../components/BookSelector.vue'
import SurnameInput from '../components/SurnameInput.vue'
import LengthSelector from '../components/LengthSelector.vue'
import NameTabs from '../components/NameTabs.vue'
import RandomPanel from '../components/RandomPanel.vue'
import KeywordPanel from '../components/KeywordPanel.vue'
import ThemePanel from '../components/ThemePanel.vue'

const surname = ref('李')
const nameLength = ref(2)
const activeTab = ref('random')
const selectedSources = ref([])
</script>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/views/HomePage.vue
git commit -m "feat: extract HomePage from App.vue"
```

---

### Task 6: Rewrite App.vue as shell

**Files:**
- Modify: `frontend/src/App.vue`

- [ ] **Step 1: Rewrite App.vue**

```vue
<template>
  <div class="min-h-screen" style="background: radial-gradient(circle at top left, rgba(47,127,115,0.10), transparent 36%), radial-gradient(circle at right 18%, rgba(176,106,60,0.10), transparent 28%), linear-gradient(180deg, #f9f4eb 0%, #f6efe3 52%, #efe4d3 100%)">
    <NavBar @toggle-history="showHistory = !showHistory" />
    <router-view />
    <HistoryDrawer v-if="showHistory" @close="showHistory = false" />
    <footer class="text-center py-6 text-xs text-warm-gray/50">
      <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer" class="hover:text-warm-gray/70 transition-colors">鲁ICP备2026025186号</a>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import NavBar from './components/NavBar.vue'
import HistoryDrawer from './components/HistoryDrawer.vue'

const showHistory = ref(false)
</script>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/App.vue
git commit -m "refactor: rewrite App.vue as shell with router-view"
```

---

### Task 7: Update NavBar with management link

**Files:**
- Modify: `frontend/src/components/NavBar.vue`

- [ ] **Step 1: Add router-link and management entry**

```vue
<template>
  <header class="py-4 px-6 flex items-center justify-between" style="background:rgba(47,38,31,0.92); color:#fffdf8">
    <router-link to="/" class="text-lg font-serif-name font-bold tracking-widest hover:text-white/80 transition">
      古诗文起名
    </router-link>
    <nav class="flex items-center gap-4">
      <router-link to="/admin" class="text-sm text-white/60 hover:text-white/90 transition">
        管理
      </router-link>
      <button @click="$emit('toggle-history')" class="text-sm text-white/60 hover:text-white/90 transition">
        历史记录
      </button>
    </nav>
  </header>
</template>

<script setup>
defineEmits(['toggle-history'])
</script>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/components/NavBar.vue
git commit -m "feat: add admin link to NavBar"
```

---

### Task 8: Add blacklist API functions

**Files:**
- Modify: `frontend/src/api/index.js`

- [ ] **Step 1: Add three API functions**

Append before the `export` block end:

```js
export function getBlacklist() {
  return api.get('/admin/blacklist')
}

export function updateBlacklist(chars) {
  return api.post('/admin/blacklist', { chars })
}

export function reloadBlacklist() {
  return api.post('/admin/blacklist/reload')
}
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/api/index.js
git commit -m "feat: add blacklist API functions"
```

---

### Task 9: Create AdminSidebar component

**Files:**
- Create: `frontend/src/components/AdminSidebar.vue`

- [ ] **Step 1: Write AdminSidebar.vue**

```vue
<template>
  <aside class="glass-card rounded-2xl p-5">
    <p class="text-xs font-bold text-teal-warm tracking-widest mb-3">MANAGE</p>
    <h2 class="font-serif-name text-2xl text-warm-brown mb-2">黑名单管理</h2>
    <p class="text-sm text-warm-gray leading-relaxed mb-4">管理生成名字时过滤的字符，修改后即时生效无需重启。</p>
    <router-link to="/" class="text-sm text-teal-warm hover:text-teal-light transition">
      &larr; 返回取名工具
    </router-link>
  </aside>
</template>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/components/AdminSidebar.vue
git commit -m "feat: add AdminSidebar component"
```

---

### Task 10: Create BatchEditor component

**Files:**
- Create: `frontend/src/components/BatchEditor.vue`

- [ ] **Step 1: Write BatchEditor.vue**

```vue
<template>
  <div class="glass-card rounded-2xl p-5">
    <h3 class="font-serif-name text-lg text-warm-brown mb-3">批量编辑</h3>
    <textarea
      v-model="localChars"
      class="w-full h-40 p-3 rounded-lg border text-sm text-warm-brown font-mono resize-y focus:outline-none focus:ring-1 focus:ring-teal-warm/30"
      style="background:rgba(255,255,255,0.6); border-color:rgba(89,72,56,0.16)"
      placeholder="输入黑名单字符，用逗号分隔"
    ></textarea>
    <div class="flex items-center justify-between mt-3">
      <span class="text-sm text-warm-gray">字符数：<b class="text-teal-warm">{{ charCount }}</b></span>
      <button
        @click="$emit('save', localChars)"
        :disabled="loading || !localChars.trim()"
        class="px-4 py-2 rounded-lg text-sm font-bold text-white transition"
        :class="loading || !localChars.trim() ? 'bg-stone-400 cursor-not-allowed' : 'bg-teal-warm hover:bg-teal-light'"
      >
        {{ loading ? '保存中...' : '保存并热更新' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  chars: { type: String, default: '' },
  charCount: { type: Number, default: 0 },
  loading: { type: Boolean, default: false },
})

defineEmits(['save'])

const localChars = ref(props.chars)
watch(() => props.chars, (v) => { localChars.value = v })
</script>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/components/BatchEditor.vue
git commit -m "feat: add BatchEditor component"
```

---

### Task 11: Create CharGrid component

**Files:**
- Create: `frontend/src/components/CharGrid.vue`

- [ ] **Step 1: Write CharGrid.vue**

```vue
<template>
  <div class="glass-card rounded-2xl p-5">
    <h3 class="font-serif-name text-lg text-warm-brown mb-3">逐字管理</h3>
    <div class="flex gap-2 mb-4">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="搜索字符..."
        class="flex-1 px-3 py-2 rounded-lg border text-sm text-warm-brown focus:outline-none focus:ring-1 focus:ring-teal-warm/30"
        style="background:rgba(255,255,255,0.6); border-color:rgba(89,72,56,0.16)"
      />
      <input
        v-model="addCharInput"
        type="text"
        maxlength="1"
        placeholder="添加"
        class="w-16 px-3 py-2 rounded-lg border text-sm text-warm-brown focus:outline-none focus:ring-1 focus:ring-teal-warm/30"
        style="background:rgba(255,255,255,0.6); border-color:rgba(89,72,56,0.16)"
        @keyup.enter="handleAdd"
      />
      <button
        @click="handleAdd"
        class="px-3 py-2 rounded-lg text-sm font-bold bg-teal-warm text-white hover:bg-teal-light transition"
      >
        添加
      </button>
    </div>
    <div class="flex flex-wrap gap-2 max-h-72 overflow-y-auto p-1">
      <button
        v-for="c in filteredChars"
        :key="c"
        @click="emit('remove', c)"
        class="w-10 h-10 rounded-lg text-base font-bold transition-colors char-tag"
      >
        {{ c }}
      </button>
      <p v-if="filteredChars.length === 0" class="text-sm text-warm-gray/50 py-4">
        {{ searchQuery ? '无匹配字符' : '黑名单为空' }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  chars: { type: String, default: '' },
})

const emit = defineEmits(['remove', 'add'])

const searchQuery = ref('')
const addCharInput = ref('')

const charList = computed(() => {
  if (!props.chars) return []
  return props.chars.split(',').map(c => c.trim()).filter(Boolean)
})

const filteredChars = computed(() => {
  if (!searchQuery.value) return charList.value
  return charList.value.filter(c => c.includes(searchQuery.value))
})

function handleAdd() {
  const c = addCharInput.value.trim()
  if (!c) return
  emit('add', c)
  addCharInput.value = ''
}
</script>

<style scoped>
.char-tag {
  background: rgba(251, 247, 241, 0.84);
  border: 1px solid rgba(89, 72, 56, 0.16);
  color: #2f261f;
}
.char-tag:hover {
  background: #fee2e2;
  border-color: #fca5a5;
  color: #991b1b;
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/components/CharGrid.vue
git commit -m "feat: add CharGrid component"
```

---

### Task 12: Create AdminPage view

**Files:**
- Create: `frontend/src/views/AdminPage.vue`

- [ ] **Step 1: Write AdminPage.vue**

```vue
<template>
  <main class="max-w-5xl mx-auto px-4 py-6">
    <div class="grid grid-cols-1 lg:grid-cols-[18rem_1fr] gap-5">
      <AdminSidebar />
      <div class="space-y-5">
        <BatchEditor
          :chars="chars"
          :char-count="charSet.size"
          :loading="loading"
          @save="handleSave"
        />
        <CharGrid
          :chars="chars"
          @remove="handleRemove"
          @add="handleAdd"
        />
      </div>
    </div>
    <Teleport to="body">
      <transition name="toast">
        <div v-if="toast" class="fixed bottom-6 left-1/2 -translate-x-1/2 px-4 py-2 rounded-lg text-sm font-bold text-white bg-warm-brown/90 shadow-lg z-50">
          {{ toast }}
        </div>
      </transition>
    </Teleport>
  </main>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import AdminSidebar from '../components/AdminSidebar.vue'
import BatchEditor from '../components/BatchEditor.vue'
import CharGrid from '../components/CharGrid.vue'
import { getBlacklist, updateBlacklist } from '../api'

const chars = ref('')
const loading = ref(false)
const toast = ref('')

const charSet = computed(() => {
  if (!chars.value) return new Set()
  return new Set(chars.value.split(',').map(c => c.trim()).filter(Boolean))
})

let timer = null
function showToast(msg) {
  toast.value = msg
  clearTimeout(timer)
  timer = setTimeout(() => { toast.value = '' }, 2500)
}
onUnmounted(() => clearTimeout(timer))

async function loadBlacklist() {
  try {
    const res = await getBlacklist()
    chars.value = res.data.characters || ''
  } catch {
    showToast('加载黑名单失败')
  }
}

async function handleSave(newChars) {
  loading.value = true
  try {
    await updateBlacklist(newChars)
    await loadBlacklist()
    showToast('黑名单已热更新')
  } catch {
    showToast('保存失败，请重试')
  } finally {
    loading.value = false
  }
}

async function handleRemove(char) {
  const arr = chars.value.split(',').map(c => c.trim()).filter(Boolean)
  const next = arr.filter(c => c !== char).join(',')
  await handleSave(next)
}

async function handleAdd(char) {
  if (charSet.value.has(char)) {
    showToast('该字已在黑名单中')
    return
  }
  if (char.length !== 1 || char.codePointAt(0) < 0x4E00 || char.codePointAt(0) > 0x9FFF) {
    showToast('请输入单个汉字')
    return
  }
  const next = chars.value ? chars.value + ',' + char : char
  await handleSave(next)
}

onMounted(loadBlacklist)
</script>

<style scoped>
.toast-enter-active { transition: all 0.3s ease-out; }
.toast-leave-active { transition: all 0.3s ease-in; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translate(-50%, 1rem); }
</style>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/views/AdminPage.vue
git commit -m "feat: add AdminPage with batch and per-char management"
```

---

### Task 13: Build and verify

- [ ] **Step 1: Build frontend**

```bash
cd frontend && npm run build
```
Expected: Build succeeds with no errors.

- [ ] **Step 2: Verify routes work in dev**

```bash
cd frontend && npm run dev &
```
Navigate to `http://localhost:5173/` (homepage with name tool) and `http://localhost:5173/admin` (blacklist management page).

- [ ] **Step 3: Test API integration**

On the admin page, verify:
- Blacklist chars load from backend on page enter
- Editing in textarea and clicking "保存并热更新" saves and shows toast
- Searching in the char grid filters the displayed characters
- Clicking a char tag removes it and auto-saves
- Adding a new char via the input saves it
- Adding a duplicate char shows "该字已在黑名单中"
- Adding a non-CJK character shows "请输入单个汉字"
