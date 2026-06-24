<template>
  <view class="paper-card name-card card-enter" :style="{ animationDelay: (index || 0) * 55 + 'ms' }" @click="emit('detail', name)">
    <view class="name-header">
      <view class="name-title-group">
        <text class="name-badge">候选 {{ String(index + 1).padStart(2, '0') }}</text>
        <view class="name-display">
          <text class="name-surname">{{ name.surname }}</text>
          <template v-for="(ch, i) in name.givenName" :key="i">
            <view v-if="isChinese(ch)" class="name-char-group">
              <text class="name-char-pinyin">{{ getPinyin(ch) }}</text>
              <text class="name-char-text">{{ ch }}</text>
            </view>
            <text v-else class="name-char-text">{{ ch }}</text>
          </template>
        </view>
      </view>
      <view class="copy-btn" @click.stop="copyName">
        <text class="copy-btn-text">{{ copied ? '已复制' : '复制' }}</text>
      </view>
    </view>

    <view class="name-sentence">
      <rich-text class="name-sentence-text" :nodes="highlightedNodes"></rich-text>
    </view>

    <view class="name-footer" @click.stop="openPoem">
      <text class="name-source">出处：{{ name.sourceNote || (name.sources?.[0] || '未知') }}</text>
    </view>
  </view>
</template>

<script setup>
import { computed, ref, onUnmounted } from 'vue'
import { isChinese, getPinyin } from '../composables/usePinyin'
import { buildHighlightNodes } from '../composables/useHighlight'

const props = defineProps({
  name: { type: Object, required: true },
  index: { type: Number, default: 0 }
})
const emit = defineEmits(['detail'])

const copied = ref(false)
let copyTimer = null

function openPoem() {
  if (!props.name.poemId) return
  uni.navigateTo({
    url: '/pages/poem/poem?id=' + props.name.poemId
  })
}

const highlightedNodes = computed(() => {
  const sentence = props.name.sources?.[0] || ''
  if (!sentence || !props.name.givenName) {
    return [{ type: 'text', text: '「' + sentence + '」' }]
  }
  return buildHighlightNodes(sentence, props.name.givenName)
})

function copyName() {
  const text = (props.name.surname + props.name.givenName).trim()
  if (!text) return
  uni.setClipboardData({
    data: text,
    showToast: false,
    success() {
      copied.value = true
      if (copyTimer) clearTimeout(copyTimer)
      copyTimer = setTimeout(() => { copied.value = false }, 1500)
    }
  })
}

onUnmounted(() => {
  if (copyTimer) clearTimeout(copyTimer)
})
</script>

<style scoped>
.name-card {
  padding: 32rpx;
  border-radius: 24rpx;
}

.name-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.name-title-group {
  flex: 1;
  min-width: 0;
}

.name-badge {
  font-size: 22rpx;
  font-weight: 700;
  color: #5C5C5C;
  letter-spacing: 6rpx;
  display: block;
  margin-bottom: 8rpx;
}

.name-display {
  display: flex;
  align-items: flex-end;
  gap: 4rpx;
}

.name-surname {
  font-size: 52rpx;
  font-family: "Songti SC", "SimSun", serif;
  color: #1A1A1A;
  line-height: 1.2;
  margin-right: 8rpx;
}

.name-char-group {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.name-char-pinyin {
  font-size: 20rpx;
  color: #5C5C5C;
  line-height: 1;
  margin-bottom: 2rpx;
}

.name-char-text {
  font-size: 52rpx;
  font-family: "Songti SC", "SimSun", serif;
  color: #11554F;
  line-height: 1.2;
}

.copy-btn {
  padding: 12rpx 24rpx;
  border-radius: 9999rpx;
  border: 1px solid rgba(17, 85, 79, 0.2);
  background: rgba(250, 248, 245, 0.8);
  flex-shrink: 0;
  transition: background 0.2s, border-color 0.2s;
}

.copy-btn-text {
  font-size: 24rpx;
  font-weight: 700;
  color: #11554F;
}

.name-sentence {
  margin-bottom: 16rpx;
}

.name-sentence-text {
  font-size: 30rpx;
  color: #1A1A1A;
  line-height: 1.8;
}

.name-footer {
  padding-top: 16rpx;
  border-top: 1px solid rgba(214, 211, 209, 0.4);
}

.name-source {
  font-size: 24rpx;
  color: #5C5C5C;
}
</style>
