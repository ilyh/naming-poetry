<template>
  <view class="length-selector">
    <text class="length-label">名字字数</text>
    <view class="length-buttons">
      <view
        v-for="n in [1, 2]"
        :key="n"
        class="length-btn"
        :class="{ 'length-btn--active': modelValue === n }"
        @click="$emit('update:modelValue', n)"
      >
        <text>{{ n }}字</text>
      </view>
    </view>
  </view>
</template>

<script setup>
defineProps({ modelValue: { type: Number, default: 2 } })
defineEmits(['update:modelValue'])
</script>

<style scoped>
.length-selector {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.length-label {
  font-size: 28rpx;
  font-weight: 700;
  color: #5C5C5C;
  letter-spacing: 4rpx;
}

.length-buttons {
  display: flex;
  gap: 16rpx;
}

.length-btn {
  padding: 12rpx 32rpx;
  border-radius: 9999rpx;
  font-size: 28rpx;
  font-weight: 500;
  color: #5C5C5C;
  border: 1px solid rgba(214, 211, 209, 0.4);
  background: rgba(250, 248, 245, 0.8);
  transition: all 0.2s;
}

.length-btn--active {
  background: #11554F;
  color: #FFFFFF;
  border-color: #11554F;
}
</style>
