<template>
  <view class="name-tabs">
    <view
      v-for="tab in tabs"
      :key="tab.key"
      class="tab-item"
      :class="{ 'tab-item--active': modelValue === tab.key }"
      @click="$emit('update:modelValue', tab.key)"
      hover-class="hover-press"
    >
      <text>{{ tab.label }}</text>
    </view>
  </view>
</template>

<script setup>
defineProps({ modelValue: { type: String, default: 'random' } })
defineEmits(['update:modelValue'])

const tabs = [
  { key: 'random', label: '随机生成' },
  { key: 'keyword', label: '关键词筛选' },
  { key: 'theme', label: '主题意境' },
]
</script>

<style lang="scss" scoped>
.name-tabs {
  display: flex;
  border-bottom: 2rpx solid rgba($color-stone-300, 0.5);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 28rpx;
  font-weight: 500;
  letter-spacing: 4rpx;
  color: $color-warm-gray;
  border-bottom: 4rpx solid transparent;
  margin-bottom: -2rpx;
  transition: color 0.2s, border-bottom-color 0.2s;
}

.tab-item--active {
  color: $color-teal-warm;
  border-bottom-color: $color-teal-warm;
}
</style>
