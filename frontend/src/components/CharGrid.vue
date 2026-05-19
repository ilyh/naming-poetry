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
