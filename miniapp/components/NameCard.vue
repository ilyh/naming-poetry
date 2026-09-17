<template>
  <view class="paper-card name-card card-enter" :style="{ animationDelay: (index || 0) * 55 + 'ms' }">
    <view class="name-header">
      <view class="name-title-group" @click="emit('detail', name)">
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
      <view class="copy-btn" hover-class="hover-press" @click="copyName()">
        <text class="copy-btn-text">{{ copied ? '已复制' : '复制' }}</text>
      </view>
    </view>

    <view class="name-sentence" @click="emit('detail', name)">
      <rich-text class="name-sentence-text" :nodes="highlightedNodes"></rich-text>
    </view>

    <view class="name-footer" hover-class="hover-surface" @click="openPoem()">
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

<style lang="scss" scoped>
.name-card {
  padding: $spacing-lg;
}

.name-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: $spacing-sm;
}

.name-title-group {
  flex: 1;
  min-width: 0;
}

.name-badge {
  font-size: 22rpx;
  font-weight: 700;
  color: $color-warm-gray;
  letter-spacing: 6rpx;
  display: block;
  margin-bottom: $spacing-xs;
}

.name-display {
  display: flex;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 4rpx;
}

.name-surname {
  font-size: 52rpx;
  font-family: $font-serif;
  color: $color-warm-brown;
  line-height: 1.2;
  margin-right: $spacing-xs;
}

.name-char-group {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.name-char-pinyin {
  font-size: 20rpx;
  color: $color-warm-gray;
  line-height: 1;
  margin-bottom: 2rpx;
}

.name-char-text {
  font-size: 52rpx;
  font-family: $font-serif;
  color: $color-teal-warm;
  line-height: 1.2;
}

.copy-btn {
  padding: 12rpx $spacing-md;
  border-radius: $radius-full;
  border: 1px solid rgba($color-teal-warm, 0.2);
  background: rgba(250, 248, 245, 0.8);
  flex-shrink: 0;
  transition: background 0.2s, border-color 0.2s;
}

.copy-btn-text {
  font-size: 24rpx;
  font-weight: 700;
  color: $color-teal-warm;
}

.name-sentence {
  margin-bottom: $spacing-sm;
}

.name-sentence-text {
  font-size: 30rpx;
  color: $color-warm-brown;
  line-height: 1.8;
}

.name-footer {
  padding-top: $spacing-sm;
  border-top: 1px solid rgba($color-stone-300, 0.4);
}

.name-source {
  font-size: 24rpx;
  color: $color-warm-gray;
}
</style>
