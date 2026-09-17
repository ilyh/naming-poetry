<template>
  <view class="poem-page">
    <view v-if="loading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>
    <view v-else-if="error" class="error-state">
      <text class="error-text">{{ error }}</text>
      <view class="btn-retry" hover-class="hover-press" @click="loadPoem()">
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

<style lang="scss" scoped>
.poem-page {
  min-height: 100vh;
  background: $color-paper;
  padding: $spacing-md $spacing-md calc(#{$spacing-md} + env(safe-area-inset-bottom));
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
  color: $color-warm-gray;
}

.error-text {
  font-size: 28rpx;
  color: $color-warm-gray;
  margin-bottom: $spacing-lg;
}

.btn-retry {
  padding: $spacing-sm $spacing-xl;
  border-radius: $radius-full;
  border: 1px solid rgba($color-teal-warm, 0.2);
  background: rgba(250, 248, 245, 0.8);
}

.btn-retry-text {
  font-size: 28rpx;
  font-weight: 700;
  color: $color-teal-warm;
}
</style>
