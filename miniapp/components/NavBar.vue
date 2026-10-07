<template>
  <view class="history-menu">
    <view class="history-menu-content">
      <navigator
        class="history-button"
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
  padding: 0;
}

.history-button {
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
