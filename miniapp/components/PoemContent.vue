<template>
  <view class="poem-card">
    <!-- 标题与作者 -->
    <view class="poem-header">
      <text class="poem-title">{{ poem.title }}</text>
      <view class="poem-meta">
        <text v-if="poem.dynasty">〔{{ poem.dynasty }}〕</text>
        <text v-if="poem.author">{{ poem.author }}</text>
      </view>
    </view>

    <!-- 分隔线 -->
    <view class="poem-divider">
      <view class="poem-divider-line" />
      <text class="poem-divider-dot">◇</text>
      <view class="poem-divider-line" />
    </view>

    <!-- 正文 -->
    <view :class="['poem-body', isCi ? 'poem-body--ci' : 'poem-body--shi']">
      <view v-for="(nodes, i) in formattedNodes" :key="i" class="poem-line">
        <rich-text :nodes="nodes" />
      </view>
    </view>

    <!-- 底部分隔 -->
    <view class="poem-divider poem-divider--end">
      <view class="poem-divider-line" />
      <text class="poem-divider-dot">·</text>
      <view class="poem-divider-line" />
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { buildPunctuationFadedNodes } from '../composables/useHighlight'

const props = defineProps({
  poem: { type: Object, required: true }
})

const isCi = computed(() => props.poem.source === 'song')

const formattedNodes = computed(() => {
  const content = props.poem.content || ''
  const normalized = content.replace(/\n+/g, '。').replace(/。{2,}/g, '。')
  const parts = normalized
    .split(/(?<=[。！？；])/)
    .map(s => s.trim())
    .filter(Boolean)
    .map(line => buildPunctuationFadedNodes(line))

  return parts
})
</script>

<style scoped>
.poem-card {
  background: #fbfaf7;
  border-radius: 24rpx;
  padding: 48rpx 40rpx;
  box-shadow: 0 16rpx 64rpx rgba(0, 0, 0, 0.06), 0 4rpx 16rpx rgba(0, 0, 0, 0.03);
}

.poem-header {
  text-align: center;
  margin-bottom: 24rpx;
}

.poem-title {
  font-family: "STKaiti", "Kaiti SC", serif;
  font-size: 44rpx;
  font-weight: 700;
  color: #1A1A1A;
  letter-spacing: 4rpx;
  line-height: 1.4;
  display: block;
  margin-bottom: 8rpx;
}

.poem-meta {
  font-size: 26rpx;
  color: #888;
  letter-spacing: 2rpx;
}

.poem-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  margin-bottom: 32rpx;
}

.poem-divider--end {
  margin-top: 32rpx;
  margin-bottom: 0;
}

.poem-divider-line {
  width: 80rpx;
  height: 1px;
  background: #D4D0CA;
}

.poem-divider-dot {
  font-size: 22rpx;
  color: #B0A89A;
}

.poem-body {
  font-family: "STKaiti", "Kaiti SC", "Songti SC", "SimSun", serif;
  color: #2C2C2C;
}

.poem-body--shi {
  text-align: center;
}

.poem-body--shi .poem-line {
  font-size: 32rpx;
  line-height: 2.4;
  letter-spacing: 8rpx;
  display: block;
  white-space: nowrap;
  overflow-x: auto;
}

.poem-body--ci {
  text-align: center;
}

.poem-body--ci .poem-line {
  display: inline-block;
  text-align: left;
  font-size: 30rpx;
  line-height: 2.2;
  letter-spacing: 4rpx;
}
</style>
