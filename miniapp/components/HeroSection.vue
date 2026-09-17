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

<style lang="scss" scoped>
.hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx $spacing-xl $spacing-md;
}

.hero-title {
  font-family: $font-serif;
  font-size: 56rpx;
  font-weight: 700;
  color: $color-warm-brown;
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
  background: rgba($color-stone-300, 0.8);
}

.hero-divider-dot {
  margin: 0 10rpx;
  font-size: 20rpx;
  color: $color-stone-400;
}

.hero-subtitle {
  font-size: 24rpx;
  color: rgba($color-warm-gray, 0.75);
  letter-spacing: 3rpx;
}
</style>