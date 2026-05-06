<template>
  <header class="py-4 px-6 flex items-center justify-between" style="background:rgba(47,38,31,0.92); color:#fffdf8">
    <h1 class="text-lg font-serif-name font-bold tracking-widest">古诗文起名</h1>
    <button @click="$emit('toggle-history')" class="text-sm text-white/60 hover:text-white/90 transition">
      历史记录
    </button>
  </header>
</template>

<script setup>
defineEmits(['toggle-history'])
</script>
