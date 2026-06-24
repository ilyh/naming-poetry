<template>
  <view>
    <view class="panel-actions">
      <view class="btn-primary" @click="generate" :class="{ 'btn--disabled': loading }">
        <text>{{ loading ? '翻检诗卷中…' : '生成 6 个名字' }}</text>
      </view>
      <view v-if="names.length > 0" class="btn-secondary" @click="generate" :class="{ 'btn--disabled': loading }">
        <text>换一组</text>
      </view>
    </view>

    <view v-if="names.length > 0" class="name-grid">
      <NameCard
        v-for="(name, i) in names"
        :key="name._key"
        :name="name"
        :index="i"
        @detail="detailName = name"
      />
    </view>

    <view v-else class="empty-state">
      <text class="empty-badge">尚未生成</text>
      <text class="empty-title">点击上方按钮开始生成</text>
      <text class="empty-desc">系统会从古诗文中抽取适合入名的字词，并为每个候选附上原诗句出处。</text>
    </view>

    <NameDetailModal v-if="detailName" :name="detailName" @close="detailName = null" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { generateRandom } from '../api/index.js'
import NameCard from './NameCard.vue'
import NameDetailModal from './NameDetailModal.vue'

const props = defineProps({
  surname: { type: String, required: true },
  length: { type: Number, default: 2 },
  sources: { type: Array, default: () => [] }
})

const names = ref([])
const loading = ref(false)
const detailName = ref(null)

async function generate() {
  if (loading.value) return
  loading.value = true
  try {
    const { data } = await generateRandom({
      surname: props.surname,
      count: 6,
      length: props.length,
      sources: props.sources.length > 0 ? props.sources : null
    })
    const stamp = Date.now()
    names.value = (data.names || []).map((n, idx) => ({ ...n, _key: stamp + '_' + idx }))
  } catch (e) {
    console.error('Generate failed', e)
    uni.showToast({ title: e.message || '生成失败，请重试', icon: 'none' })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.panel-actions {
  display: flex;
  justify-content: center;
  gap: 24rpx;
  margin-bottom: 32rpx;
}

.btn-primary {
  padding: 20rpx 48rpx;
  border-radius: 9999rpx;
  background: #11554F;
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
  border-radius: 9999rpx;
  border: 1px solid rgba(17, 85, 79, 0.2);
  background: rgba(250, 248, 245, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-secondary text {
  color: #11554F;
  font-size: 28rpx;
  font-weight: 700;
  letter-spacing: 4rpx;
}

.btn--disabled {
  opacity: 0.6;
  pointer-events: none;
}

.name-grid {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 600rpx;
  border-radius: 24rpx;
  border: 2rpx dashed #D6D3D1;
  background: #FAF8F5;
  padding: 48rpx;
}

.empty-badge {
  font-size: 22rpx;
  font-weight: 700;
  color: #5C5C5C;
  letter-spacing: 6rpx;
  margin-bottom: 12rpx;
}

.empty-title {
  font-family: "Songti SC", "SimSun", serif;
  font-size: 40rpx;
  color: #1A1A1A;
  margin-bottom: 16rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: #5C5C5C;
  text-align: center;
  line-height: 1.6;
  max-width: 520rpx;
}
</style>
