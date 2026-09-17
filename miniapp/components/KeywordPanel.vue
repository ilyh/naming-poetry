<template>
  <view>
    <view class="panel-actions">
      <input
        @input="onInput($event)"
        @confirm="generate()"
        placeholder="输入偏好字，如：清"
        class="keyword-input"
        placeholder-class="keyword-placeholder"
      />
      <view class="btn-primary" hover-class="hover-press" @click="generate()" :class="{ 'btn--disabled': loading || !keyword }">
        <text>{{ loading ? '翻检诗卷中…' : '生成 6 个名字' }}</text>
      </view>
      <view v-if="names.length > 0" class="btn-secondary" hover-class="hover-press" @click="generate()" :class="{ 'btn--disabled': loading }">
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
      <text class="empty-title">输入一个偏好字，点击生成</text>
      <text class="empty-desc">包含该字的诗句中的两个字符将组成你的专属名字。</text>
    </view>

    <NameDetailModal v-if="detailName" :name="detailName" @close="detailName = null" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { generateKeyword } from '../api/index.js'
import NameCard from './NameCard.vue'
import NameDetailModal from './NameDetailModal.vue'

const props = defineProps({
  surname: { type: String, required: true },
  length: { type: Number, default: 2 },
  sources: { type: Array, default: () => [] }
})

const keyword = ref('')
const names = ref([])
const loading = ref(false)
const detailName = ref(null)

function onInput(e) {
  const val = (e.detail.value || '').trim()
  // 只取第一个字符作为偏好字；不回写 input 值，避免打断输入法组词
  keyword.value = val ? val.slice(0, 1) : ''
}

async function generate() {
  if (!keyword.value) return
  if (loading.value) return
  loading.value = true
  try {
    const { data } = await generateKeyword({
      surname: props.surname,
      keyword: keyword.value,
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

<style lang="scss" scoped>
.keyword-input {
  width: 160rpx;
  text-align: center;
  font-size: 36rpx;
  color: $color-warm-brown;
  border-bottom: 4rpx solid rgba($color-teal-warm, 0.3);
  padding: 8rpx 0;
}

.keyword-placeholder {
  color: rgba($color-warm-gray, 0.5);
  font-size: 28rpx;
}
</style>
