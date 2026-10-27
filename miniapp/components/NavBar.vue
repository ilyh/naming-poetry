<template>
  <view class="history-menu">
    <view class="history-menu-content">
      <navigator
        class="menu-pill"
        url="/pages/favorites/favorites"
        open-type="navigate"
        hover-class="hover-press"
      >
        <view class="bookmark-icon" aria-hidden="true">
          <view class="bookmark-point" />
        </view>
        <text>我的收藏</text>
      </navigator>
      <navigator
        class="menu-pill"
        url="/pages/history/history"
        open-type="navigate"
        hover-class="hover-press"
      >
        <view class="history-clock-icon" aria-hidden="true">
          <view class="history-clock-hour" />
          <view class="history-clock-minute" />
        </view>
        <text>历史记录</text>
      </navigator>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.history-menu {
  position: relative;
  z-index: 10;
  flex: none;
}

.history-menu-content {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: $spacing-sm;
  padding: 0;
}

.menu-pill {
  display: flex;
  align-items: center;
  gap: 14rpx;
  font-size: 26rpx;
  color: $color-teal-warm;
  padding: 14rpx $spacing-md;
  border: 1px solid rgba($color-teal-warm, 0.25);
  border-radius: $radius-full;
  background: rgba(250, 248, 245, 0.8);
}

/* 收藏书签图标 */
.bookmark-icon {
  position: relative;
  flex: none;
  width: 24rpx;
  height: 30rpx;
  border: 2rpx solid $color-teal-warm;
  border-bottom: none;
  border-radius: 4rpx 4rpx 0 0;
}

.bookmark-point {
  position: absolute;
  bottom: -2rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 12rpx solid transparent;
  border-right: 12rpx solid transparent;
  border-top: 12rpx solid $color-teal-warm;
}

.history-clock-icon {
  position: relative;
  flex: none;
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid $color-teal-warm;
  border-radius: 50%;
}

.history-clock-hour,
.history-clock-minute {
  position: absolute;
  border-radius: 2rpx;
  background: $color-teal-warm;
}

.history-clock-hour {
  top: 6rpx;
  left: 15rpx;
  width: 2rpx;
  height: 10rpx;
}

.history-clock-minute {
  top: 15rpx;
  left: 15rpx;
  width: 8rpx;
  height: 2rpx;
}

</style>
