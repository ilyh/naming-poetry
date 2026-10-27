<template>
  <view class="paper-card name-card card-enter" :style="{ animationDelay: (index || 0) * 55 + 'ms' }">
    <view class="name-header">
      <view class="name-title-group" @click="emit('detail', name)">
        <text class="name-badge">{{ badge || ('候选 ' + String(index + 1).padStart(2, '0')) }}</text>
        <view class="name-display">
          <text class="name-surname">{{ name.surname }}</text>
          <template v-for="(ch, i) in name.givenName" :key="i">
            <view v-if="isChinese(ch)" class="name-char-group">
              <text class="name-char-pinyin">{{ getPinyin(ch) }}</text>
              <text class="name-char-text">{{ ch }}</text>
            </view>
            <text v-else class="name-char-text">{{ ch }}</text>
          </template>
        </view>
      </view>
      <view class="header-actions">
        <view
          class="copy-btn"
          :class="{ 'fav-btn--active': faved }"
          hover-class="hover-press"
          @click="toggleFav()"
        >
          <text class="copy-btn-text">{{ faved ? '已收藏' : '收藏' }}</text>
        </view>
        <view class="copy-btn" hover-class="hover-press" @click="copyName()">
          <text class="copy-btn-text">{{ copied ? '已复制' : '复制' }}</text>
        </view>
      </view>
    </view>

    <view class="name-sentence" @click="emit('detail', name)">
      <rich-text class="name-sentence-text" :nodes="highlightedNodes"></rich-text>
    </view>

    <view class="name-footer" hover-class="hover-surface" @click="openPoem()">
      <view class="source-texts">
        <text class="name-source">{{ sourceInfo.main }}</text>
        <text v-if="sourceInfo.author" class="source-author">{{ sourceInfo.author }}</text>
      </view>
      <view v-if="name.poemId" class="poem-link">
        <text class="poem-link-text">查看全诗</text>
        <view class="poem-link-arrow" aria-hidden="true" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref, onUnmounted } from 'vue'
import { isChinese, getPinyin } from '../composables/usePinyin'
import { buildHighlightNodes } from '../composables/useHighlight'
import { isFavorite, toggleFavorite } from '../composables/useFavorites'

const props = defineProps({
  name: { type: Object, required: true },
  index: { type: Number, default: 0 },
  badge: { type: String, default: '' }
})
const emit = defineEmits(['detail'])

const copied = ref(false)
let copyTimer = null

const faved = computed(() => isFavorite(props.name))

function toggleFav() {
  const added = toggleFavorite(props.name)
  uni.showToast({ title: added ? '已收藏' : '已取消收藏', icon: 'none' })
}

function openPoem() {
  if (!props.name.poemId) return
  uni.navigateTo({
    url: '/pages/poem/poem?id=' + props.name.poemId
  })
}

const highlightedNodes = computed(() => {
  const sentence = props.name.sources?.[0] || ''
  if (!sentence || !props.name.givenName) {
    return [{ type: 'text', text: '「' + sentence + '」' }]
  }
  return buildHighlightNodes(sentence, props.name.givenName)
})

// sourceNote 格式为「书名·诗名 作者 XXX」，按「 作者 」拆成两行；
// 无 sourceNote 时退回显示诗句，此时没有作者行
const sourceInfo = computed(() => {
  const note = props.name.sourceNote || props.name.sources?.[0] || '未知'
  const sep = ' 作者 '
  const idx = note.indexOf(sep)
  if (idx >= 0) {
    return { main: '出处：' + note.slice(0, idx), author: '作者 ' + note.slice(idx + sep.length) }
  }
  return { main: '出处：' + note, author: '' }
})

function copyName() {
  const text = (props.name.surname + props.name.givenName).trim()
  if (!text) return
  uni.setClipboardData({
    data: text,
    showToast: false,
    success() {
      copied.value = true
      if (copyTimer) clearTimeout(copyTimer)
      copyTimer = setTimeout(() => { copied.value = false }, 1500)
    }
  })
}

onUnmounted(() => {
  if (copyTimer) clearTimeout(copyTimer)
})
</script>

<style lang="scss" scoped>
.name-card {
  padding: $spacing-lg;
}

.name-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: $spacing-sm;
}

.name-title-group {
  flex: 1;
  min-width: 0;
}

.name-badge {
  font-size: 22rpx;
  font-weight: 700;
  color: $color-warm-gray;
  letter-spacing: 6rpx;
  display: block;
  margin-bottom: $spacing-xs;
}

.name-display {
  display: flex;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 4rpx;
}

.name-surname {
  font-size: 52rpx;
  font-family: $font-serif;
  color: $color-warm-brown;
  line-height: 1.2;
  margin-right: $spacing-xs;
}

.name-char-group {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.name-char-pinyin {
  font-size: 20rpx;
  color: $color-warm-gray;
  line-height: 1;
  margin-bottom: 2rpx;
}

.name-char-text {
  font-size: 52rpx;
  font-family: $font-serif;
  color: $color-teal-warm;
  line-height: 1.2;
}

.copy-btn {
  padding: 12rpx $spacing-md;
  border-radius: $radius-full;
  border: 1px solid rgba($color-teal-warm, 0.2);
  background: rgba(250, 248, 245, 0.8);
  flex-shrink: 0;
  transition: background 0.2s, border-color 0.2s;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  flex-shrink: 0;
}

.fav-btn--active {
  background: $color-teal-mist;
  border-color: rgba($color-teal-warm, 0.45);
}

.copy-btn-text {
  font-size: 24rpx;
  font-weight: 700;
  color: $color-teal-warm;
}

.name-sentence {
  margin-bottom: $spacing-sm;
}

.name-sentence-text {
  font-size: 30rpx;
  color: $color-warm-brown;
  line-height: 1.8;
}

.name-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-sm;
  padding-top: $spacing-sm;
  border-top: 1px solid rgba($color-stone-300, 0.4);
}

.source-texts {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.name-source {
  font-size: 24rpx;
  color: $color-warm-gray;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.source-author {
  font-size: 22rpx;
  color: rgba($color-warm-gray, 0.65);
}

.poem-link {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx $spacing-md;
  border-radius: $radius-full;
  background: $color-teal-mist;
  flex-shrink: 0;
}

.poem-link-text {
  font-size: 22rpx;
  font-weight: 700;
  color: $color-teal-warm;
}

.poem-link-arrow {
  width: 10rpx;
  height: 10rpx;
  border-top: 3rpx solid $color-teal-warm;
  border-right: 3rpx solid $color-teal-warm;
  transform: rotate(45deg);
}
</style>
