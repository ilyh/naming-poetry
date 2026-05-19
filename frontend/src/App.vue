<template>
  <div class="min-h-screen bg-paper">
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
