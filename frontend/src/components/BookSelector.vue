<template>
  <div class="space-y-1.5">
    <p class="text-xs font-bold text-teal-warm tracking-widest mb-2">选择典籍</p>
    <label
      v-for="book in books"
      :key="book.value"
      class="flex items-start gap-3 p-3 rounded-xl cursor-pointer transition border"
      :class="selected.includes(book.value)
        ? 'border-teal-warm/40 bg-teal-warm/5 shadow-sm'
        : 'border-stone-300/20 bg-cream/80 hover:-translate-y-0.5'"
    >
      <input
        type="checkbox"
        :value="book.value"
        :checked="selected.includes(book.value)"
        @change="toggle(book.value)"
        class="mt-0.5 accent-teal-warm"
      />
      <div>
        <span class="text-sm font-bold text-warm-brown">{{ book.name }}</span>
        <span class="text-xs text-warm-gray ml-2">{{ book.count }}首</span>
        <p class="text-xs text-warm-gray/70 mt-0.5">{{ book.description }}</p>
      </div>
    </label>
    <p class="text-xs text-warm-gray/50 mt-1">{{ selected.length === 0 ? '未选择时将使用全部典籍' : '已选 ' + selected.length + ' 部典籍' }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: Array, default: () => [] }
})
const emit = defineEmits(['update:modelValue'])

const selected = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const books = [
  { value: 'shijing', name: '诗经', count: 100, description: '先秦风雅，适合温润清朗的名字。' },
  { value: 'chuci', name: '楚辞', count: 28, description: '瑰丽浪漫，适合大气华美的名字。' },
  { value: 'tang', name: '唐诗', count: 200, description: '意象明朗，适合开阔俊逸的名字。' },
  { value: 'song', name: '宋词', count: 200, description: '婉约含蓄，适合柔和灵秀的名字。' },
  { value: 'yuefu', name: '乐府诗集', count: 34, description: '语言生动，适合自然鲜活的名字。' },
  { value: 'gushi', name: '古诗', count: 11, description: '经典凝练，适合耐看沉静的名字。' },
  { value: 'cifu', name: '著名辞赋', count: 30, description: '铺陈华采，适合丰沛典雅的名字。' },
]

function toggle(value) {
  const arr = [...selected.value]
  const idx = arr.indexOf(value)
  if (idx >= 0) arr.splice(idx, 1)
  else arr.push(value)
  emit('update:modelValue', arr)
}
</script>
