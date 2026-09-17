<template>
  <view class="book-selector">
    <text class="section-label">典籍</text>
    <checkbox-group class="book-list" @change="onBooksChange($event)">
      <label
        v-for="book in books"
        :key="book.value"
        class="book-item"
        :class="{ 'book-item--selected': selected.includes(book.value) }"
      >
        <checkbox
          :value="book.value"
          :checked="selected.includes(book.value)"
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
    </checkbox-group>
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
  { value: 'nalan', name: '纳兰词', count: 258, description: '清丽哀婉，适合深情隽秀的名字。' },
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
    if (Array.isArray(data) && data.length > 0) {
      const updated = data.map(b => ({
        value: b.value,
        name: b.name || b.value,
        count: b.count || 0,
        description: b.description || ''
      }))
      statsCache = updated
      statsCacheTime = now
      books.value = updated
    }
  } catch (e) {
    // keep defaults on error
  }
})

function onBooksChange(e) {
  const values = (e.detail && e.detail.value) || []
  const known = books.value.map(b => b.value)
  emit('update:modelValue', values.filter(v => known.includes(v)))
}
</script>

<style lang="scss" scoped>
.book-selector {
  margin-bottom: $spacing-md;
}

.section-label {
  font-size: 24rpx;
  font-weight: 700;
  color: $color-teal-warm;
  letter-spacing: 6rpx;
  margin-bottom: $spacing-sm;
  display: block;
}

.book-list {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-sm;
}

.book-item {
  display: flex;
  align-items: flex-start;
  width: calc(50% - #{$spacing-sm} / 2);
  box-sizing: border-box;
  padding: $spacing-sm;
  border-radius: $radius-md;
  border: 1px solid $color-stone-300;
  background: #FFFFFF;
  transition: border-color 0.2s, background 0.2s;
}

.book-item--selected {
  border-color: rgba($color-teal-warm, 0.3);
  background: rgba($color-teal-warm, 0.04);
}

.book-checkbox {
  margin-top: 4rpx;
  margin-right: 12rpx;
  flex-shrink: 0;
}

.book-info {
  flex: 1;
  min-width: 0;
}

.book-header {
  display: flex;
  align-items: baseline;
  flex-wrap: wrap;
  gap: 8rpx;
}

.book-name {
  font-size: 28rpx;
  font-weight: 500;
  color: $color-warm-brown;
}

.book-count {
  font-size: 22rpx;
  color: rgba($color-warm-gray, 0.75);
}

.book-desc {
  font-size: 22rpx;
  color: rgba($color-warm-gray, 0.75);
  margin-top: 4rpx;
  line-height: 1.4;
}

.book-hint {
  font-size: 22rpx;
  color: rgba($color-warm-gray, 0.6);
  margin-top: $spacing-sm;
  display: block;
}
</style>
