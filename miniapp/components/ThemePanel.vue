<template>
  <view>
    <view class="theme-tags">
      <view
        v-for="theme in allThemes"
        :key="theme"
        class="theme-tag"
        :class="{ 'theme-tag--active': selectedThemes.includes(theme) }"
        @click="toggleTheme(theme)"
        hover-class="hover-press"
      >
        <text>{{ theme }}</text>
      </view>
    </view>

    <view class="panel-actions">
      <view class="btn-primary" hover-class="hover-press" @click="generate()" :class="{ 'btn--disabled': loading || selectedThemes.length === 0 }">
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
      <text class="empty-title">选择意境标签，点击生成</text>
      <text class="empty-desc">系统会从匹配意境的诗句中提取名字候选。</text>
    </view>

    <NameDetailModal v-if="detailName" :name="detailName" @close="detailName = null" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { generateTheme } from '../api/index.js'
import NameCard from './NameCard.vue'
import NameDetailModal from './NameDetailModal.vue'

const props = defineProps({
  surname: { type: String, required: true },
  length: { type: Number, default: 2 },
  sources: { type: Array, default: () => [] }
})

const allThemes = ['山水', '豪迈', '婉约', '清雅', '离别', '田园', '爱情', '志向']
const selectedThemes = ref([])
const names = ref([])
const loading = ref(false)
const detailName = ref(null)

function toggleTheme(theme) {
  const idx = selectedThemes.value.indexOf(theme)
  if (idx >= 0) selectedThemes.value.splice(idx, 1)
  else selectedThemes.value.push(theme)
}

async function generate() {
  if (selectedThemes.value.length === 0) return
  if (loading.value) return
  loading.value = true
  try {
    const { data } = await generateTheme({
      surname: props.surname,
      themes: selectedThemes.value,
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
.theme-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: $spacing-sm;
  margin-bottom: $spacing-md;
}

.theme-tag {
  padding: 12rpx 28rpx;
  border-radius: $radius-full;
  border: 1px solid rgba($color-stone-300, 0.6);
  background: rgba(250, 248, 245, 0.8);
  transition: background 0.2s, border-color 0.2s, color 0.2s;
}

.theme-tag text {
  font-size: 26rpx;
  font-weight: 500;
  color: $color-warm-gray;
}

.theme-tag--active {
  background: $color-teal-warm;
  border-color: $color-teal-warm;
}

.theme-tag--active text {
  color: #FFFFFF;
}
</style>
