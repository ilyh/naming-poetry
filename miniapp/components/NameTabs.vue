<template>
  <view class="name-tabs">
    <view
      v-for="tab in tabs"
      :key="tab.key"
      class="tab-item"
      :class="{ 'tab-item--active': modelValue === tab.key }"
      @click="$emit('update:modelValue', tab.key)"
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

<style scoped>
.name-tabs {
  display: flex;
  border-bottom: 2rpx solid rgba(214, 211, 209, 0.3);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  font-size: 28rpx;
  font-weight: 500;
  letter-spacing: 4rpx;
  color: #5C5C5C;
  border-bottom: 4rpx solid transparent;
  margin-bottom: -2rpx;
  transition: color 0.2s, border-bottom-color 0.2s;
}

.tab-item--active {
  color: #11554F;
  border-bottom-color: #11554F;
}
</style>
