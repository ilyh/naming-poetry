<template>
  <view class="poem-page">
    <view v-if="loading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>
    <view v-else-if="error" class="error-state">
      <text class="error-text">{{ error }}</text>
      <view class="btn-retry" @click="loadPoem">
        <text class="btn-retry-text">重试</text>
      </view>
    </view>
    <view v-else-if="poem" class="poem-container">
      <PoemContent :poem="poem" />
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getPoem } from '../../api/index.js'
import PoemContent from '../../components/PoemContent.vue'

const poemId = ref('')
const poem = ref(null)
const loading = ref(true)
const error = ref('')

onLoad((options) => {
  poemId.value = options?.id
  if (poemId.value) {
    loadPoem()
  } else {
    loading.value = false
    error.value = '缺少诗词ID'
  }
})

async function loadPoem() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await getPoem(poemId.value)
    poem.value = data
  } catch (e) {
    console.error('Failed to load poem', e)
    error.value = '加载诗词失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.poem-page {
  min-height: 100vh;
  background: #FAF8F5;
  padding: 24rpx;
}

.poem-container {
  max-width: 700rpx;
  margin: 0 auto;
}

.loading-state,
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 200rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #5C5C5C;
}

.error-text {
  font-size: 28rpx;
  color: #5C5C5C;
  margin-bottom: 32rpx;
}

.btn-retry {
  padding: 16rpx 48rpx;
  border-radius: 9999rpx;
  border: 1px solid rgba(17, 85, 79, 0.2);
  background: rgba(250, 248, 245, 0.8);
}

.btn-retry-text {
  font-size: 28rpx;
  font-weight: 700;
  color: #11554F;
}
</style>
