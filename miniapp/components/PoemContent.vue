<template>
  <view class="poem-card paper-card">
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

const isCi = computed(() => props.poem.source === 'song' || props.poem.source === 'nalan')

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

<style lang="scss" scoped>
.poem-card {
  padding: $spacing-xl 40rpx;
}

.poem-header {
  text-align: center;
  margin-bottom: $spacing-md;
}

.poem-title {
  font-family: $font-kaiti;
  font-size: 44rpx;
  font-weight: 700;
  color: $color-warm-brown;
  letter-spacing: 4rpx;
  line-height: 1.4;
  display: block;
  margin-bottom: 8rpx;
}

.poem-meta {
  font-size: 26rpx;
  color: rgba($color-warm-gray, 0.75);
  letter-spacing: 2rpx;
}

.poem-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-sm;
  margin-bottom: $spacing-lg;
}

.poem-divider--end {
  margin-top: $spacing-lg;
  margin-bottom: 0;
}

.poem-divider-line {
  width: 80rpx;
  height: 1px;
  background: $color-stone-300;
}

.poem-divider-dot {
  font-size: 22rpx;
  color: $color-stone-400;
}

.poem-body {
  font-family: $font-kaiti;
  color: $color-ink;
  text-align: center;
}

.poem-line {
  display: block;
  font-size: 32rpx;
  line-height: 2.2;
  letter-spacing: 4rpx;
}
</style>
