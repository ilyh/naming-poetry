<template>
  <view class="modal-overlay">
    <view class="modal-mask" @click="emit('close')" />
    <view class="modal-panel">
      <view class="modal-header">
        <text class="modal-name">{{ name.surname }}<text class="modal-given">{{ name.givenName }}</text></text>
      </view>

      <view class="modal-source-box">
        <text class="modal-source-label">出处诗句</text>
        <rich-text class="modal-source-text" :nodes="highlightedNodes"></rich-text>
        <text v-if="name.sourceNote" class="modal-source-note">出处：{{ name.sourceNote }}</text>
      </view>

      <view class="modal-char-row">
        <view
          v-for="(char, idx) in name.givenName.split('')"
          :key="idx"
          class="modal-char-circle"
        >
          <text class="modal-char-text">{{ char }}</text>
        </view>
      </view>

      <view class="btn-close" hover-class="hover-press" @click="emit('close')">
        <text class="btn-close-text">关闭</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { buildHighlightNodes } from '../composables/useHighlight'

const props = defineProps({ name: { type: Object, required: true } })
const emit = defineEmits(['close'])

const highlightedNodes = computed(() => {
  const sentence = props.name.sources?.[0] || ''
  if (!sentence || !props.name.givenName) {
    return [{ type: 'text', text: '「' + sentence + '」' }]
  }
  return buildHighlightNodes(sentence, props.name.givenName)
})
</script>

<style lang="scss" scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
  padding: $spacing-xl $spacing-xl calc(#{$spacing-xl} + env(safe-area-inset-bottom));
  animation: modal-fade 200ms ease both;
}

@keyframes modal-fade {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
}

.modal-panel {
  position: relative;
  z-index: 1;
  background: $color-paper;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  width: 100%;
  max-width: 600rpx;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 16rpx 64rpx rgba(0, 0, 0, 0.1);
  animation: modal-rise 240ms ease both;
}

@keyframes modal-rise {
  from { opacity: 0; transform: translateY(24rpx) scale(0.96); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-header {
  margin-bottom: $spacing-lg;
}

.modal-name {
  font-family: $font-serif;
  font-size: 56rpx;
  color: $color-warm-brown;
}

.modal-given {
  color: $color-teal-warm;
}

.modal-source-box {
  background: $color-cream-dark;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: $radius-md;
  padding: $spacing-md;
  margin-bottom: $spacing-md;
}

.modal-source-label {
  font-size: 26rpx;
  color: $color-warm-gray;
  font-weight: 500;
  display: block;
  margin-bottom: $spacing-xs;
}

.modal-source-text {
  font-size: 30rpx;
  color: $color-warm-brown;
  line-height: 1.8;
}

.modal-source-note {
  font-size: 22rpx;
  color: $color-warm-gray;
  margin-top: $spacing-sm;
  padding-top: $spacing-sm;
  border-top: 1px solid rgba($color-stone-300, 0.4);
  display: block;
}

.modal-char-row {
  display: flex;
  gap: $spacing-sm;
  margin-bottom: $spacing-lg;
}

.modal-char-circle {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: $color-teal-mist;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-char-text {
  font-size: 40rpx;
  font-family: $font-serif;
  font-weight: 700;
  color: $color-teal-warm;
}

.btn-close {
  width: 100%;
  padding: $spacing-md 0;
  border-radius: $radius-full;
  border: 1px solid rgba($color-teal-warm, 0.2);
  background: rgba(250, 248, 245, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-close-text {
  font-size: 28rpx;
  font-weight: 700;
  color: $color-teal-warm;
}
</style>
