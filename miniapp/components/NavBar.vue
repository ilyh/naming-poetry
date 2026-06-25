<template>
  <view class="navbar">
    <view class="navbar-content">
      <text class="navbar-title">古诗文起名</text>
      <view class="navbar-right">
        <text class="navbar-link" @click="goHistory()">历史记录</text>
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

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding-top: env(safe-area-inset-top);
  background: #FAF8F5;
  border-bottom: 1px solid #D6D3D1;
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx;
}

.navbar-title {
  font-family: "Songti SC", "SimSun", serif;
  font-size: 36rpx;
  font-weight: 700;
  letter-spacing: 8rpx;
  color: #1A1A1A;
}

.navbar-right {
  display: flex;
  align-items: center;
}

.navbar-link {
  font-size: 28rpx;
  color: #5C5C5C;
}

.navbar-placeholder {
  height: calc(88rpx + env(safe-area-inset-top));
}
</style>
