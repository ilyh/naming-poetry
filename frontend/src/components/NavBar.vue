<template>
  <header class="bg-stone-900 text-stone-100 py-4 px-6 flex items-center justify-between">
    <h1 class="text-xl font-bold tracking-widest">诗词取名</h1>
    <button @click="$emit('toggle-history')" class="text-sm text-stone-400 hover:text-stone-200 transition">
      历史
    </button>
  </header>
</template>

<script setup>
defineEmits(['toggle-history'])
</script>
