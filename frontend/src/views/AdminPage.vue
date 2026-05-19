<template>
  <main class="max-w-6xl mx-auto px-6 py-8">
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
