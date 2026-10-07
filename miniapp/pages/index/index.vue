<template>
  <view class="home-page">
    <HeroSection />

    <!-- 设置面板 -->
    <view class="settings-panel paper-card">
      <text class="settings-title">偏好设置</text>
      <text class="settings-desc">选择典籍、姓氏与名字长度</text>

      <view class="settings-section">
        <BookSelector v-model="selectedSources" />
      </view>

      <view class="settings-divider" />

      <view class="settings-controls">
        <SurnameInput v-model="surname" />
        <LengthSelector v-model="nameLength" />
        <NameTabs v-model="activeTab" />
      </view>
    </view>

    <!-- 名字展示区 -->
    <view class="names-panel paper-card">
      <view class="names-heading">
        <text class="names-title">候选名字</text>
        <NavBar />
      </view>

      <RandomPanel
        v-show="activeTab === 'random'"
        :surname="normalizedSurname"
        :length="nameLength"
        :sources="selectedSources"
      />
      <KeywordPanel
        v-show="activeTab === 'keyword'"
        :surname="normalizedSurname"
        :length="nameLength"
        :sources="selectedSources"
      />
      <ThemePanel
        v-show="activeTab === 'theme'"
        :surname="normalizedSurname"
        :length="nameLength"
        :sources="selectedSources"
      />
    </view>

    <!-- 底部占位 -->
    <view class="bottom-spacer" />
  </view>
</template>

<script>
// 微信小程序要求页面声明 onShareAppMessage，右上角「转发」才可用
export default {
  onShareAppMessage() {
    return { title: '古诗文起名 · 从千年诗词中，觅一个好名', path: '/pages/index/index' }
  },
  onShareTimeline() {
    return { title: '古诗文起名 · 从千年诗词中，觅一个好名' }
  }
}
</script>

<script setup>
import { computed, ref } from 'vue'
import { normalizeSurname } from '../../composables/useSurname'
import NavBar from '../../components/NavBar.vue'
import HeroSection from '../../components/HeroSection.vue'
import BookSelector from '../../components/BookSelector.vue'
import SurnameInput from '../../components/SurnameInput.vue'
import LengthSelector from '../../components/LengthSelector.vue'
import NameTabs from '../../components/NameTabs.vue'
import RandomPanel from '../../components/RandomPanel.vue'
import KeywordPanel from '../../components/KeywordPanel.vue'
import ThemePanel from '../../components/ThemePanel.vue'

const surname = ref('李')
// 兜底：姓氏还没 blur 就直接点「生成」时，保证下发给面板的值已归一化
const normalizedSurname = computed(() => normalizeSurname(surname.value))
const nameLength = ref(2)
const activeTab = ref('random')
const selectedSources = ref([])
</script>

<style lang="scss" scoped>
.home-page {
  padding: 0 $spacing-md;
}

.settings-panel {
  padding: $spacing-lg;
  margin-bottom: $spacing-md;
}

.settings-title {
  font-family: $font-serif;
  font-size: 36rpx;
  color: $color-warm-brown;
  display: block;
  margin-bottom: 4rpx;
}

.settings-desc {
  font-size: 26rpx;
  color: rgba($color-warm-gray, 0.75);
  display: block;
  margin-bottom: $spacing-lg;
}

.settings-section {
  margin-bottom: 0;
}

.settings-divider {
  height: 1px;
  background: rgba($color-stone-300, 0.5);
  margin: $spacing-lg 0;
}

.settings-controls {
  display: flex;
  flex-direction: column;
  gap: 28rpx;
}

.names-panel {
  padding: $spacing-lg;
  min-height: 400rpx;
}

.names-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-md;
  margin-bottom: $spacing-lg;
}

.names-title {
  font-family: $font-serif;
  font-size: 36rpx;
  color: $color-warm-brown;
  display: block;
}

.bottom-spacer {
  height: calc(#{$spacing-xl} + env(safe-area-inset-bottom));
}
</style>
