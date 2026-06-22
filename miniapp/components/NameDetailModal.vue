<template>
  <view class="modal-overlay" @click="emit('close')">
    <view class="modal-panel" @click.stop>
      <view class="modal-header">
        <text class="modal-name">{{ name.surname }}<text class="modal-given">{{ name.givenName }}</text></text>
      </view>

      <view class="modal-source-box">
        <text class="modal-source-label">出处诗句</text>
        <text class="modal-source-text" v-html="highlightedSentence"></text>
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

      <view class="btn-close" @click="emit('close')">
        <text class="btn-close-text">关闭</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ name: { type: Object, required: true } })
const emit = defineEmits(['close'])

const highlightedSentence = computed(() => {
  const sentence = props.name.sources?.[0] || ''
  if (!sentence || !props.name.givenName) return '「' + sentence + '」'
  const chars = props.name.givenName.split('')
  let result = ''
  for (const ch of sentence) {
    if (chars.includes(ch)) {
      result += `<span style="color:#11554F;font-weight:600;">${ch}</span>`
    } else {
      result += ch
    }
  }
  return '「' + result + '」'
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
  padding: 48rpx;
}

.modal-panel {
  background: #FAF8F5;
  border-radius: 24rpx;
  padding: 48rpx;
  width: 100%;
  max-width: 600rpx;
  box-shadow: 0 16rpx 64rpx rgba(0, 0, 0, 0.1);
}

.modal-header {
  margin-bottom: 32rpx;
}

.modal-name {
  font-family: "Songti SC", "SimSun", serif;
  font-size: 56rpx;
  color: #1A1A1A;
}

.modal-given {
  color: #11554F;
}

.modal-source-box {
  background: #F8F6F2;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
}

.modal-source-label {
  font-size: 26rpx;
  color: #5C5C5C;
  font-weight: 500;
  display: block;
  margin-bottom: 8rpx;
}

.modal-source-text {
  font-size: 30rpx;
  color: #1A1A1A;
  line-height: 1.8;
}

.modal-source-note {
  font-size: 22rpx;
  color: #5C5C5C;
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1px solid rgba(214, 211, 209, 0.3);
  display: block;
}

.modal-char-row {
  display: flex;
  gap: 16rpx;
  margin-bottom: 32rpx;
}

.modal-char-circle {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background: #EEF5F4;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-char-text {
  font-size: 40rpx;
  font-family: "Songti SC", "SimSun", serif;
  font-weight: 700;
  color: #11554F;
}

.btn-close {
  width: 100%;
  padding: 24rpx 0;
  border-radius: 9999rpx;
  border: 1px solid rgba(17, 85, 79, 0.2);
  background: rgba(250, 248, 245, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-close-text {
  font-size: 28rpx;
  font-weight: 700;
  color: #11554F;
}
</style>
