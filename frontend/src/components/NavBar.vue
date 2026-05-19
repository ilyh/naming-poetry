<template>
  <header class="py-5 px-8 flex items-center justify-between border-b border-stone-300">
    <router-link to="/" class="text-lg font-serif-name font-bold tracking-wider text-warm-brown hover:text-warm-brown/70 transition">
      古诗文起名
    </router-link>
    <nav class="flex items-center gap-6">
      <router-link to="/admin" class="text-sm text-warm-gray hover:text-warm-brown transition">
        管理
      </router-link>
      <button @click="$emit('toggle-history')" class="text-sm text-warm-gray hover:text-warm-brown transition">
        历史记录
      </button>
    </nav>
  </header>
</template>

<script setup>
defineEmits(['toggle-history'])
</script>
