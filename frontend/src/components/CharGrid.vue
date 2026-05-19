<template>
  <div class="paper-card rounded-2xl p-6">
    <h3 class="font-serif-name text-lg text-warm-brown mb-3">逐字管理</h3>
    <div class="flex gap-2 mb-4">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="搜索字符..."
        class="flex-1 px-3 py-2 rounded-lg border text-sm text-warm-brown focus:outline-none focus:ring-1 focus:ring-teal-warm/30"
        style="background:#FAF8F5; border-color:#EBE8E3"
      />
      <template v-if="showAdd">
        <input
          ref="addRef"
          v-model="addChar"
          type="text"
          maxlength="1"
          placeholder="字"
          class="w-12 px-2 py-2 rounded-lg border text-sm text-center text-warm-brown focus:outline-none focus:ring-1 focus:ring-teal-warm/30"
          style="background:#FAF8F5; border-color:#EBE8E3"
          @keyup.enter="confirmAdd"
          @keyup.escape="cancelAdd"
        />
        <button
          @click="confirmAdd"
          class="px-3 py-2 rounded-lg text-sm font-bold bg-teal-warm text-white hover:bg-teal-light transition"
        >&#10003;</button>
      </template>
      <button
        v-else
        @click="openAdd"
        class="px-3 py-2 rounded-lg text-sm font-medium text-warm-gray hover:text-warm-brown hover:bg-stone-300/20 transition"
      >+ 添加</button>
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
import { ref, computed, nextTick } from 'vue'

const props = defineProps({
  chars: { type: String, default: '' },
})

const emit = defineEmits(['remove', 'add'])

const searchQuery = ref('')
const showAdd = ref(false)
const addChar = ref('')
const addRef = ref(null)

const charList = computed(() => {
  if (!props.chars) return []
  return props.chars.split(',').map(c => c.trim()).filter(Boolean)
})

const filteredChars = computed(() => {
  if (!searchQuery.value) return charList.value
  return charList.value.filter(c => c.includes(searchQuery.value))
})

function openAdd() {
  showAdd.value = true
  nextTick(() => addRef.value?.focus())
}

function confirmAdd() {
  const c = addChar.value.trim()
  if (!c) {
    cancelAdd()
    return
  }
  emit('add', c)
  addChar.value = ''
  showAdd.value = false
}

function cancelAdd() {
  addChar.value = ''
  showAdd.value = false
}
</script>

<style scoped>
.char-tag {
  background: #FAF8F5;
  border: 1px solid rgba(0, 0, 0, 0.06);
  color: #1A1A1A;
}
.char-tag:hover {
  background: #FEE2E2;
  border-color: #FECACA;
  color: #991B1B;
}
</style>
