<template>
  <main class="max-w-5xl mx-auto px-4 py-6">
    <div class="grid grid-cols-1 lg:grid-cols-[18rem_1fr] gap-5">
      <aside class="glass-card rounded-2xl p-5">
        <p class="text-xs font-bold text-teal-warm tracking-widest mb-3">STEP 01</p>
        <h2 class="font-serif-name text-2xl text-warm-brown mb-2">挑选偏好</h2>
        <p class="text-sm text-warm-gray leading-relaxed mb-4">输入姓氏，选择选字方式和名字长度。</p>
        <div class="space-y-5">
          <BookSelector v-model="selectedSources" />
          <div class="border-t border-stone-300/20 pt-5 space-y-4">
            <SurnameInput v-model="surname" />
            <LengthSelector v-model="nameLength" />
            <NameTabs v-model="activeTab" />
          </div>
        </div>
      </aside>

      <section class="glass-card rounded-2xl p-5 min-h-[34rem]">
        <div class="mb-4">
          <p class="text-xs font-bold text-teal-warm tracking-widest mb-1">STEP 02</p>
          <h2 class="font-serif-name text-2xl text-warm-brown">诗意候选</h2>
        </div>

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
