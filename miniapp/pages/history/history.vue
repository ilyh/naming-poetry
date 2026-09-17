<template>
  <view class="history-page">
    <view v-if="loading && records.length === 0" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>
    <view v-else-if="records.length === 0" class="empty-state">
      <text class="empty-text">暂无记录</text>
    </view>
    <view v-else class="history-list paper-card">
      <view v-for="r in records" :key="r.id" class="history-item">
        <view class="history-name">
          <text class="history-surname">{{ r.surname }}</text>
          <text class="history-given">{{ r.givenName }}</text>
        </view>
        <view class="history-meta">
          <text class="history-mode">{{ modeLabel(r.mode) }}</text>
          <text v-if="r.createdAt" class="history-time">{{ formatTime(r.createdAt) }}</text>
        </view>
      </view>
      <view v-if="loadingMore" class="loading-more">
        <text class="loading-more-text">加载中...</text>
      </view>
      <view v-else-if="!hasMore" class="no-more">
        <text class="no-more-text">没有更多了</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onReachBottom, onPullDownRefresh } from '@dcloudio/uni-app'
import { getHistory } from '../../api/index.js'

const records = ref([])
const loading = ref(true)
const loadingMore = ref(false)
const page = ref(0)
const hasMore = ref(true)
const PAGE_SIZE = 20

const MODE_LABELS = { random: '随机', keyword: '关键词', theme: '主题' }

function modeLabel(mode) {
  return MODE_LABELS[mode] || mode
}

function formatTime(value) {
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return ''
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

async function loadFirst() {
  loading.value = true
  page.value = 0
  hasMore.value = true
  try {
    const { data } = await getHistory(0, PAGE_SIZE)
    records.value = data.content || []
    hasMore.value = !data.last
  } catch (e) {
    console.error('Failed to load history', e)
    uni.showToast({ title: e.message || '加载历史记录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  if (loadingMore.value || !hasMore.value) return
  loadingMore.value = true
  try {
    const nextPage = page.value + 1
    const { data } = await getHistory(nextPage, PAGE_SIZE)
    records.value = records.value.concat(data.content || [])
    page.value = nextPage
    hasMore.value = !data.last
  } catch (e) {
    console.error('Failed to load more history', e)
  } finally {
    loadingMore.value = false
  }
}

onReachBottom(() => {
  loadMore()
})

onPullDownRefresh(async () => {
  await loadFirst()
  uni.stopPullDownRefresh()
})

loadFirst()
</script>

<style lang="scss" scoped>
.history-page {
  min-height: 100vh;
  background: $color-paper;
  padding: $spacing-md $spacing-md calc(#{$spacing-md} + env(safe-area-inset-bottom));
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 200rpx;
}

.loading-text {
  font-size: 28rpx;
  color: $color-warm-gray;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 200rpx;
}

.empty-text {
  font-size: 28rpx;
  color: rgba($color-warm-gray, 0.75);
}

.history-list {
  padding: 0 $spacing-lg;
}

.history-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-md 0;
  border-bottom: 1px solid rgba($color-stone-300, 0.4);
}

.history-item:last-child {
  border-bottom: none;
}

.history-name {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
}

.history-surname {
  font-size: 28rpx;
  color: rgba($color-warm-gray, 0.75);
}

.history-given {
  font-size: 28rpx;
  font-weight: 500;
  color: $color-warm-brown;
}

.history-meta {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
}

.history-mode {
  font-size: 22rpx;
  color: $color-teal-warm;
  background: $color-teal-mist;
  padding: 4rpx $spacing-sm;
  border-radius: $radius-full;
}

.history-time {
  font-size: 22rpx;
  color: rgba($color-warm-gray, 0.6);
}

.loading-more,
.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-lg 0;
}

.loading-more-text,
.no-more-text {
  font-size: 24rpx;
  color: rgba($color-warm-gray, 0.6);
}
</style>
