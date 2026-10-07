<template>
  <div class="min-h-screen bg-paper">
    <router-view />
    <HistoryDrawer v-if="showHistory" @close="showHistory = false" />
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import HistoryDrawer from './components/HistoryDrawer.vue'

const showHistory = ref(false)
const toggleHistory = () => {
  showHistory.value = !showHistory.value
}

onMounted(() => {
  window.addEventListener('naming-poetry:toggle-history', toggleHistory)
})

onBeforeUnmount(() => {
  window.removeEventListener('naming-poetry:toggle-history', toggleHistory)
})
</script>
