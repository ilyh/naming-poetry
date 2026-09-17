<template>
  <view class="navbar">
    <view class="navbar-content">
      <view class="navbar-right">
        <view class="navbar-link" hover-class="hover-press" @click="goHistory()">
          <text>历史记录</text>
        </view>
      </view>
    </view>
  </view>
  <!-- 占位高度，防止内容被状态栏遮挡 -->
  <view class="navbar-placeholder" />
</template>

<script setup>
function goHistory() {
  uni.navigateTo({ url: '/pages/history/history' })
}
</script>

<style lang="scss" scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding-top: env(safe-area-inset-top);
  background: $color-paper;
  border-bottom: 1px solid $color-stone-300;
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 88rpx;
  padding: 0 $spacing-lg;
}

.navbar-right {
  display: flex;
  align-items: center;
}

.navbar-link {
  font-size: 26rpx;
  color: $color-teal-warm;
  padding: 8rpx $spacing-md;
  border: 1px solid rgba($color-teal-warm, 0.25);
  border-radius: $radius-full;
  background: rgba(250, 248, 245, 0.8);
}

.navbar-placeholder {
  height: calc(88rpx + env(safe-area-inset-top));
}
</style>
