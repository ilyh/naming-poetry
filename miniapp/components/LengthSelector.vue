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
        hover-class="hover-press"
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

<style lang="scss" scoped>
.length-selector {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
}

.length-label {
  font-size: 28rpx;
  font-weight: 700;
  color: $color-warm-gray;
  letter-spacing: 4rpx;
}

.length-buttons {
  display: flex;
  gap: $spacing-sm;
}

.length-btn {
  padding: 12rpx $spacing-lg;
  border-radius: $radius-full;
  font-size: 28rpx;
  font-weight: 500;
  color: $color-warm-gray;
  border: 1px solid rgba($color-stone-300, 0.6);
  background: rgba(250, 248, 245, 0.8);
  transition: background 0.2s, color 0.2s, border-color 0.2s;
}

.length-btn--active {
  background: $color-teal-warm;
  color: #FFFFFF;
  border-color: $color-teal-warm;
}
</style>
