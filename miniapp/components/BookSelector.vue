<template>
  <view class="book-selector">
    <text class="section-label">典籍</text>
    <label
      v-for="book in books"
      :key="book.value"
      class="book-item"
      :class="{ 'book-item--selected': selected.includes(book.value) }"
    >
      <checkbox
        :value="book.value"
        :checked="selected.includes(book.value)"
        @change="toggle(book.value)"
        class="book-checkbox"
        color="#11554F"
      />
      <view class="book-info">
        <view class="book-header">
          <text class="book-name">{{ book.name }}</text>
          <text class="book-count">{{ book.count }}首</text>
        </view>
        <text class="book-desc">{{ book.description }}</text>
      </view>
    </label>
    <text class="book-hint">{{ selected.length === 0 ? '未选择时使用全部典籍' : '已选 ' + selected.length + ' 部' }}</text>
  </view>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { getStats } from '../api/index.js'

const props = defineProps({
  modelValue: { type: Array, default: () => [] }
})
const emit = defineEmits(['update:modelValue'])

const selected = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const defaults = [
  { value: 'shijing', name: '诗经', count: 100, description: '先秦风雅，适合温润清朗的名字。' },
  { value: 'chuci', name: '楚辞', count: 28, description: '瑰丽浪漫，适合大气华美的名字。' },
  { value: 'tang', name: '唐诗', count: 200, description: '意象明朗，适合开阔俊逸的名字。' },
  { value: 'song', name: '宋词', count: 200, description: '婉约含蓄，适合柔和灵秀的名字。' },
  { value: 'yuefu', name: '乐府诗集', count: 34, description: '语言生动，适合自然鲜活的名字。' },
  { value: 'gushi', name: '古诗', count: 11, description: '经典凝练，适合耐看沉静的名字。' },
  { value: 'cifu', name: '著名辞赋', count: 30, description: '铺陈华采，适合丰沛典雅的名字。' },
]

const books = ref([...defaults])

let statsCache = null
let statsCacheTime = 0
const STATS_CACHE_TTL = 5 * 60 * 1000

onMounted(async () => {
  const now = Date.now()
  if (statsCache && now - statsCacheTime < STATS_CACHE_TTL) {
    books.value = statsCache
    return
  }
  try {
    const { data } = await getStats()
    const updated = defaults.map(b => ({
      ...b,
      count: data[b.value] || b.count
    }))
    statsCache = updated
    statsCacheTime = now
    books.value = updated
  } catch (e) {
    // keep defaults on error
  }
})

function toggle(value) {
  const arr = [...selected.value]
  const idx = arr.indexOf(value)
  if (idx >= 0) arr.splice(idx, 1)
  else arr.push(value)
  emit('update:modelValue', arr)
}
</script>

<style scoped>
.book-selector {
  margin-bottom: 24rpx;
}

.section-label {
  font-size: 24rpx;
  font-weight: 700;
  color: #11554F;
  letter-spacing: 6rpx;
  margin-bottom: 16rpx;
  display: block;
}

.book-item {
  display: flex;
  align-items: flex-start;
  padding: 16rpx 20rpx;
  margin-bottom: 8rpx;
  border-radius: 16rpx;
  border: 1px solid #D6D3D1;
  background: #FFFFFF;
  transition: border-color 0.2s, background 0.2s;
}

.book-item--selected {
  border-color: rgba(17, 85, 79, 0.3);
  background: rgba(17, 85, 79, 0.04);
}

.book-checkbox {
  margin-top: 4rpx;
  margin-right: 16rpx;
  flex-shrink: 0;
}

.book-info {
  flex: 1;
  min-width: 0;
}

.book-header {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
}

.book-name {
  font-size: 28rpx;
  font-weight: 500;
  color: #1A1A1A;
}

.book-count {
  font-size: 22rpx;
  color: rgba(92, 92, 92, 0.5);
}

.book-desc {
  font-size: 22rpx;
  color: rgba(92, 92, 92, 0.5);
  margin-top: 4rpx;
  line-height: 1.4;
}

.book-hint {
  font-size: 22rpx;
  color: rgba(92, 92, 92, 0.4);
  margin-top: 8rpx;
  display: block;
}
</style>
