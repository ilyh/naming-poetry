<script>
export default {
  onLaunch() {
    // console.log('古诗文起名 小程序启动')
  },
  onError(err) {
    uni.showToast({ title: '开小差了，请稍后再试', icon: 'none' })
  },
  onShow() {
    // console.log('App Show')
  },
  onHide() {
    // console.log('App Hide')
  }
}
</script>

<style lang="scss">
page {
  background-color: $color-paper;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
  color: $color-warm-brown;
  font-size: 28rpx;
}

.paper-card {
  background: #FFFFFF;
  border: 1px solid $color-border;
  box-shadow: $shadow-card;
  border-radius: $radius-lg;
}

@keyframes card-rise {
  from { opacity: 0; transform: translateY(12rpx); }
  to { opacity: 1; transform: translateY(0); }
}

.card-enter {
  animation: card-rise 420ms ease both;
}

.hover-press {
  opacity: 0.7;
}

.hover-surface {
  background-color: rgba($color-teal-warm, 0.06);
}

/* 生成操作按钮（三个面板共用） */
.panel-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: $spacing-md;
  margin-bottom: $spacing-lg;
  flex-wrap: wrap;
}

.btn-primary {
  padding: 20rpx 48rpx;
  border-radius: $radius-full;
  background: $color-teal-warm;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-primary text {
  color: #FFFFFF;
  font-size: 28rpx;
  font-weight: 700;
  letter-spacing: 4rpx;
}

.btn-secondary {
  padding: 20rpx 40rpx;
  border-radius: $radius-full;
  border: 1px solid rgba($color-teal-warm, 0.2);
  background: rgba(250, 248, 245, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-secondary text {
  color: $color-teal-warm;
  font-size: 28rpx;
  font-weight: 700;
  letter-spacing: 4rpx;
}

.btn--disabled {
  opacity: 0.6;
  pointer-events: none;
}

/* 名字卡片列表与空状态（三个面板共用） */
.name-grid {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 600rpx;
  border-radius: $radius-lg;
  border: 2rpx dashed $color-stone-300;
  background: $color-paper;
  padding: $spacing-xl;
}

.empty-badge {
  font-size: 22rpx;
  font-weight: 700;
  color: $color-warm-gray;
  letter-spacing: 6rpx;
  margin-bottom: 12rpx;
}

.empty-title {
  font-family: $font-serif;
  font-size: 40rpx;
  color: $color-warm-brown;
  margin-bottom: $spacing-sm;
}

.empty-desc {
  font-size: 26rpx;
  color: $color-warm-gray;
  text-align: center;
  line-height: 1.6;
  max-width: 520rpx;
}
</style>
