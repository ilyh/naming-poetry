<template>
  <div class="min-h-screen bg-stone-50">
    <NavBar @toggle-history="showHistory = !showHistory" />
    <main class="max-w-2xl mx-auto px-4 py-8">
      <div class="flex items-center gap-8 mb-6">
        <SurnameInput v-model="surname" />
        <LengthSelector v-model="nameLength" />
      </div>

      <NameTabs v-model="activeTab" />

      <div class="mt-6">
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
      </div>
    </main>

    <HistoryDrawer v-if="showHistory" @close="showHistory = false" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import NavBar from './components/NavBar.vue'
import SurnameInput from './components/SurnameInput.vue'
import LengthSelector from './components/LengthSelector.vue'
import NameTabs from './components/NameTabs.vue'
import RandomPanel from './components/RandomPanel.vue'
import KeywordPanel from './components/KeywordPanel.vue'
import ThemePanel from './components/ThemePanel.vue'
import HistoryDrawer from './components/HistoryDrawer.vue'

const surname = ref('李')
const nameLength = ref(2)
const activeTab = ref('random')
const showHistory = ref(false)
const selectedSources = ref([])
</script>
