<template>
  <main class="max-w-6xl mx-auto px-6 py-8">
    <div class="grid grid-cols-1 lg:grid-cols-[20rem_1fr] gap-6">
      <aside class="paper-card rounded-2xl p-6">
        <h2 class="font-serif-name text-xl text-warm-brown mb-1">偏好设置</h2>
        <p class="text-sm text-warm-gray/60 mb-6">选择典籍、姓氏与名字长度</p>
        <div class="space-y-6">
          <BookSelector v-model="selectedSources" />
          <div class="border-t border-stone-300 pt-6 space-y-5">
            <SurnameInput v-model="surname" />
            <LengthSelector v-model="nameLength" />
            <NameTabs v-model="activeTab" />
          </div>
        </div>
      </aside>

      <section class="paper-card rounded-2xl p-6 min-h-[34rem]">
        <h2 class="font-serif-name text-xl text-warm-brown mb-6">候选名字</h2>

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
      </section>
    </div>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import BookSelector from '../components/BookSelector.vue'
import SurnameInput from '../components/SurnameInput.vue'
import LengthSelector from '../components/LengthSelector.vue'
import NameTabs from '../components/NameTabs.vue'
import RandomPanel from '../components/RandomPanel.vue'
import KeywordPanel from '../components/KeywordPanel.vue'
import ThemePanel from '../components/ThemePanel.vue'

const surname = ref('李')
const nameLength = ref(2)
const activeTab = ref('random')
const selectedSources = ref([])
</script>
