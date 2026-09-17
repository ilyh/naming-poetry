<template>
  <view class="surname-input">
    <text class="surname-label">姓氏</text>
    <input
      :value="modelValue"
      @input="onInput($event)"
      @compositionstart="composing = true"
      @compositionend="onCompositionEnd($event)"
      @blur="onBlur($event)"
      maxlength="4"
      placeholder="李"
      class="surname-field"
      placeholder-class="surname-placeholder"
    />
    <text class="surname-hint">支持复姓</text>
  </view>
</template>

<script setup>
import { ref } from 'vue'

defineProps({ modelValue: { type: String, default: '' } })
const emit = defineEmits(['update:modelValue'])

const composing = ref(false)

function normalize(value) {
  return (value || '').replace(/\s/g, '').slice(0, 4)
}

function onInput(e) {
  if (composing.value) return
  emit('update:modelValue', normalize(e.detail.value))
}

function onCompositionEnd(e) {
  composing.value = false
  emit('update:modelValue', normalize(e.detail.value))
}

function onBlur(e) {
  emit('update:modelValue', normalize(e.detail.value))
}
</script>

<style lang="scss" scoped>
.surname-input {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
}

.surname-label {
  font-size: 28rpx;
  font-weight: 700;
  color: $color-warm-gray;
  letter-spacing: 4rpx;
  white-space: nowrap;
}

.surname-field {
  width: 120rpx;
  text-align: center;
  font-size: 36rpx;
  border-bottom: 4rpx solid rgba($color-teal-warm, 0.2);
  padding: 8rpx 0;
  color: $color-warm-brown;
}

.surname-placeholder {
  color: rgba($color-warm-gray, 0.5);
}

.surname-hint {
  font-size: 22rpx;
  color: rgba($color-warm-gray, 0.75);
}
</style>
