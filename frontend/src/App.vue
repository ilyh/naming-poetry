<template>
  <div class="min-h-screen" style="background: radial-gradient(circle at top left, rgba(47,127,115,0.10), transparent 36%), radial-gradient(circle at right 18%, rgba(176,106,60,0.10), transparent 28%), linear-gradient(180deg, #f9f4eb 0%, #f6efe3 52%, #efe4d3 100%)">
    <NavBar @toggle-history="showHistory = !showHistory" />
    <router-view />
    <HistoryDrawer v-if="showHistory" @close="showHistory = false" />
    <footer class="text-center py-6 text-xs text-warm-gray/50">
      <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer" class="hover:text-warm-gray/70 transition-colors">鲁ICP备2026025186号</a>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import NavBar from './components/NavBar.vue'
import HistoryDrawer from './components/HistoryDrawer.vue'

const showHistory = ref(false)
</script>
