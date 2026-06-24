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

<style>
page {
  background-color: #FAF8F5;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
  color: #1A1A1A;
  font-size: 28rpx;
}
</style>
