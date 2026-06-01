<template>
  <div class="fixed inset-0 bg-black/40 flex justify-end z-50" @click.self="$emit('close')">
    <div class="bg-white w-80 h-full overflow-y-auto shadow-xl p-6">
      <div class="flex items-center justify-between mb-4">
        <h3 class="font-bold text-stone-800">历史记录</h3>
        <button @click="$emit('close')" class="text-stone-400 hover:text-stone-600 text-lg">&times;</button>
      </div>
      <div v-if="records.length === 0" class="text-sm text-stone-400 text-center py-8">暂无记录</div>
      <div v-for="r in records" :key="r.id" class="py-2 border-b border-stone-100 text-sm">
        <span class="text-stone-500">{{ r.surname }}</span>
        <span class="font-medium text-stone-800">{{ r.givenName }}</span>
        <span class="text-stone-400 ml-2 text-xs">{{ r.mode }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHistory } from '../api/index.js'

defineEmits(['close'])

const records = ref([])

onMounted(async () => {
  try {
    const { data } = await getHistory()
    records.value = data.content || []
  } catch (e) {
    console.error('Failed to load history', e)
  }
})
</script>
