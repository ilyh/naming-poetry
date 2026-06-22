<template>
  <view class="home-page">
    <NavBar />
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
      <text class="names-title">候选名字</text>

      <RandomPanel
        v-if="activeTab === 'random'"
        :surname="surname"
        :length="nameLength"
        :sources="selectedSources"
      />
      <KeywordPanel
        v-if="activeTab === 'keyword'"
        :surname="surname"
        :length="nameLength"
        :sources="selectedSources"
      />
      <ThemePanel
        v-if="activeTab === 'theme'"
        :surname="surname"
        :length="nameLength"
        :sources="selectedSources"
      />
    </view>

    <!-- 底部占位 -->
    <view class="bottom-spacer" />
  </view>
</template>

<script setup>
import { ref } from 'vue'
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
const nameLength = ref(2)
const activeTab = ref('random')
const selectedSources = ref([])
</script>

<style scoped>
.home-page {
  padding: 0 24rpx;
}

.settings-panel {
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.settings-title {
  font-family: "Songti SC", "SimSun", serif;
  font-size: 36rpx;
  color: #1A1A1A;
  display: block;
  margin-bottom: 4rpx;
}

.settings-desc {
  font-size: 26rpx;
  color: rgba(92, 92, 92, 0.6);
  display: block;
  margin-bottom: 32rpx;
}

.settings-section {
  margin-bottom: 0;
}

.settings-divider {
  height: 1px;
  background: rgba(214, 211, 209, 0.5);
  margin: 32rpx 0;
}

.settings-controls {
  display: flex;
  flex-direction: column;
  gap: 28rpx;
}

.names-panel {
  padding: 32rpx;
  min-height: 400rpx;
}

.names-title {
  font-family: "Songti SC", "SimSun", serif;
  font-size: 36rpx;
  color: #1A1A1A;
  display: block;
  margin-bottom: 32rpx;
}

.bottom-spacer {
  height: 48rpx;
}
</style>
