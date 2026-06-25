<template>
  <view class="hero">
    <text class="hero-title">古诗文起名</text>
    <view class="hero-divider">
      <view class="hero-divider-line" />
      <text class="hero-divider-dot">◇</text>
      <view class="hero-divider-line" />
    </view>
    <text class="hero-subtitle">从千年诗词中，觅一个好名</text>
  </view>
</template>

<style scoped>
.hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 48rpx 24rpx;
}

.hero-title {
  font-family: "Songti SC", "SimSun", "Noto Serif SC", serif;
  font-size: 56rpx;
  font-weight: 700;
  color: #1A1A1A;
  letter-spacing: 10rpx;
  margin-bottom: 12rpx;
}

.hero-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10rpx;
}

.hero-divider-line {
  width: 60rpx;
  height: 1px;
  background: rgba(214, 211, 209, 0.8);
}

.hero-divider-dot {
  margin: 0 10rpx;
  font-size: 20rpx;
  color: #A8A29E;
}

.hero-subtitle {
  font-size: 24rpx;
  color: rgba(92, 92, 92, 0.7);
  letter-spacing: 3rpx;
}
</style>