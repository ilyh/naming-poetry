<template>
  <view class="hero">
    <text class="hero-title">古诗文起名</text>
    <view class="hero-divider">
      <view class="hero-divider-line" />
      <text class="hero-divider-dot">◇</text>
      <view class="hero-divider-line" />
    </view>
    <text class="hero-subtitle">「从千年诗词中，觅一个好名」</text>
    <view class="hero-divider hero-divider-sm">
      <view class="hero-divider-line-short" />
      <text class="hero-divider-dot-sm">·</text>
      <view class="hero-divider-line-short" />
    </view>
  </view>
</template>

<style scoped>
.hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 64rpx 48rpx 40rpx;
}

.hero-title {
  font-family: "Songti SC", "SimSun", serif;
  font-size: 64rpx;
  color: #1A1A1A;
  letter-spacing: 12rpx;
  margin-bottom: 16rpx;
}

.hero-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
}

.hero-divider-line {
  width: 80rpx;
  height: 1px;
  background: #D6D3D1;
}

.hero-divider-dot {
  margin: 0 12rpx;
  font-size: 24rpx;
  color: #A8A29E;
}

.hero-divider-sm {
  margin-top: 12rpx;
  margin-bottom: 0;
}

.hero-divider-line-short {
  width: 48rpx;
  height: 1px;
  background: rgba(214, 211, 209, 0.6);
}

.hero-divider-dot-sm {
  margin: 0 12rpx;
  font-size: 20rpx;
  color: #A8A29E;
}

.hero-subtitle {
  font-size: 30rpx;
  color: #5C5C5C;
  letter-spacing: 4rpx;
}
</style>
