<template>
  <view class="history-page">
    <view v-if="loading && records.length === 0" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>
    <view v-else-if="records.length === 0" class="empty-state">
      <text class="empty-text">暂无记录</text>
    </view>
    <view v-else class="history-list">
      <view v-for="r in records" :key="r.id" class="history-item">
        <view class="history-name">
          <text class="history-surname">{{ r.surname }}</text>
          <text class="history-given">{{ r.givenName }}</text>
        </view>
        <text class="history-mode">{{ r.mode === 'random' ? '随机' : r.mode === 'keyword' ? '关键词' : r.mode === 'theme' ? '主题' : r.mode }}</text>
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

<style scoped>
.history-page {
  min-height: 100vh;
  background: #FAF8F5;
  padding: 24rpx;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 200rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #5C5C5C;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 200rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #A8A29E;
}

.history-list {
  display: flex;
  flex-direction: column;
}

.history-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1px solid rgba(214, 211, 209, 0.3);
}

.history-name {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
}

.history-surname {
  font-size: 28rpx;
  color: #A8A29E;
}

.history-given {
  font-size: 28rpx;
  font-weight: 500;
  color: #1A1A1A;
}

.history-mode {
  font-size: 22rpx;
  color: #A8A29E;
}

.loading-more,
.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32rpx 0;
}

.loading-more-text,
.no-more-text {
  font-size: 24rpx;
  color: #A8A29E;
}
</style>
