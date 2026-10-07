<template>
  <div>
    <section v-if="checkingSession" class="max-w-md mx-auto px-6 py-20 text-center text-sm text-warm-gray">
      正在验证管理权限…
    </section>

    <section v-else-if="!authenticated" class="max-w-md mx-auto px-6 py-16">
      <div class="paper-card rounded-2xl px-8 py-9">
        <div class="mb-7 text-center">
          <div class="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-cream-dark text-warm-brown">
            <svg viewBox="0 0 24 24" class="h-6 w-6" fill="none" stroke="currentColor" stroke-width="1.7" aria-hidden="true">
              <rect x="4" y="10" width="16" height="11" rx="2" />
              <path d="M8 10V7a4 4 0 0 1 8 0v3M12 14v3" />
            </svg>
          </div>
          <h1 class="font-serif-name text-2xl font-bold text-warm-brown">管理验证</h1>
          <p class="mt-2 text-sm text-warm-gray">请输入管理员密码以继续</p>
        </div>

        <form class="space-y-4" @submit.prevent="handleLogin">
          <label for="admin-password" class="block text-sm font-medium text-warm-gray">管理员密码</label>
          <input
            id="admin-password"
            v-model="loginPassword"
            type="password"
            autocomplete="current-password"
            required
            autofocus
            :disabled="loggingIn"
            class="w-full rounded-xl border border-stone-300 bg-white px-4 py-3 text-sm text-warm-brown outline-none transition focus:border-warm-brown focus:ring-2 focus:ring-warm-brown/10 disabled:opacity-60"
            placeholder="输入管理员密码"
          />
          <p v-if="loginError" role="alert" class="text-sm text-red-700">{{ loginError }}</p>
          <button
            type="submit"
            :disabled="loggingIn || !loginPassword"
            class="w-full rounded-xl bg-warm-brown px-4 py-3 text-sm font-semibold text-white transition hover:bg-warm-brown/80 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {{ loggingIn ? '验证中…' : '进入管理页面' }}
          </button>
        </form>
      </div>
    </section>

    <div v-else>
      <div class="mx-auto flex max-w-6xl justify-end px-6 pt-5">
        <button @click="handleLogout" class="text-sm text-warm-gray transition hover:text-warm-brown">退出管理</button>
      </div>

      <main class="max-w-6xl mx-auto px-6 py-5">
        <div class="grid grid-cols-1 lg:grid-cols-[20rem_1fr] gap-6">
          <AdminSidebar />
          <div class="space-y-6">
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
            <PhrasePanel :toast="showToast" />
          </div>
        </div>
        <Teleport to="body">
          <transition name="toast">
            <div v-if="toast" class="fixed bottom-6 left-1/2 -translate-x-1/2 px-4 py-2 rounded-lg text-sm font-bold text-white shadow-lg z-50" style="background:#1A1A1A">
              {{ toast }}
            </div>
          </transition>
        </Teleport>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import AdminSidebar from '../components/AdminSidebar.vue'
import BatchEditor from '../components/BatchEditor.vue'
import CharGrid from '../components/CharGrid.vue'
import PhrasePanel from '../components/PhrasePanel.vue'
import { getBlacklist, loginAdmin, logoutAdmin, updateBlacklist, verifyAdminSession } from '../api'

const ADMIN_TOKEN_KEY = 'adminToken'
const chars = ref('')
const loading = ref(false)
const toast = ref('')
const authenticated = ref(false)
const checkingSession = ref(true)
const loginPassword = ref('')
const loginError = ref('')
const loggingIn = ref(false)

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

function handleSessionExpired() {
  authenticated.value = false
  chars.value = ''
  loginError.value = '登录状态已过期，请重新验证'
}

async function restoreSession() {
  if (!sessionStorage.getItem(ADMIN_TOKEN_KEY)) {
    checkingSession.value = false
    return
  }

  try {
    await verifyAdminSession()
    authenticated.value = true
    await loadBlacklist()
  } catch (error) {
    sessionStorage.removeItem(ADMIN_TOKEN_KEY)
    if (error.response?.status !== 401) {
      loginError.value = error.response?.data?.message || '登录状态验证失败，请重试'
    }
  } finally {
    checkingSession.value = false
  }
}

async function handleLogin() {
  if (!loginPassword.value || loggingIn.value) return

  loggingIn.value = true
  loginError.value = ''
  try {
    const response = await loginAdmin(loginPassword.value)
    sessionStorage.setItem(ADMIN_TOKEN_KEY, response.data.token)
    const blacklist = await getBlacklist()
    chars.value = blacklist.data.characters || ''
    authenticated.value = true
    loginPassword.value = ''
  } catch (error) {
    sessionStorage.removeItem(ADMIN_TOKEN_KEY)
    loginError.value = error.response?.data?.message || '验证失败，请检查密码后重试'
  } finally {
    loggingIn.value = false
  }
}

async function handleLogout() {
  try {
    await logoutAdmin()
  } catch {
    // Clear the local session even if the server is unavailable.
  }
  sessionStorage.removeItem(ADMIN_TOKEN_KEY)
  authenticated.value = false
  chars.value = ''
  loginPassword.value = ''
  loginError.value = ''
}

onMounted(() => {
  window.addEventListener('admin-session-expired', handleSessionExpired)
  restoreSession()
})
onUnmounted(() => {
  clearTimeout(timer)
  window.removeEventListener('admin-session-expired', handleSessionExpired)
})

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
    const res = await updateBlacklist(newChars)
    // 后端写文件失败时返回的是 HTTP 200 + status:"error"，
    // 只看 HTTP 状态会把失败当成功报出去
    if (res.data?.status === 'error') {
      showToast(res.data.message || '保存失败，请重试')
      return
    }
    await loadBlacklist()
    showToast('黑名单已热更新')
  } catch (e) {
    showToast(e.response?.data?.message || '保存失败，请重试')
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

</script>

<style scoped>
.toast-enter-active { transition: all 0.3s ease-out; }
.toast-leave-active { transition: all 0.3s ease-in; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translate(-50%, 1rem); }
</style>
