<template>
  <view class="surname-input">
    <text class="surname-label">姓氏</text>
    <input
      :value="modelValue"
      @input="onInput"
      maxlength="4"
      placeholder="李"
      class="surname-field"
      placeholder-class="surname-placeholder"
    />
    <text class="surname-hint">支持复姓</text>
  </view>
</template>

<script setup>
defineProps({ modelValue: { type: String, default: '' } })
const emit = defineEmits(['update:modelValue'])

function onInput(e) {
  emit('update:modelValue', e.detail.value)
}
</script>

<style scoped>
.surname-input {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.surname-label {
  font-size: 28rpx;
  font-weight: 700;
  color: #5C5C5C;
  letter-spacing: 4rpx;
  white-space: nowrap;
}

.surname-field {
  width: 120rpx;
  text-align: center;
  font-size: 36rpx;
  border-bottom: 4rpx solid rgba(17, 85, 79, 0.2);
  padding: 8rpx 0;
  color: #1A1A1A;
}

.surname-placeholder {
  color: rgba(92, 92, 92, 0.4);
}

.surname-hint {
  font-size: 22rpx;
  color: #5C5C5C;
}
</style>
