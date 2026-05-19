<template>
  <header class="py-4 px-6 flex items-center justify-between" style="background:rgba(47,38,31,0.92); color:#fffdf8">
    <router-link to="/" class="text-lg font-serif-name font-bold tracking-widest hover:text-white/80 transition">
      古诗文起名
    </router-link>
    <nav class="flex items-center gap-4">
      <router-link to="/admin" class="text-sm text-white/60 hover:text-white/90 transition">
        管理
      </router-link>
      <button @click="$emit('toggle-history')" class="text-sm text-white/60 hover:text-white/90 transition">
        历史记录
      </button>
    </nav>
  </header>
</template>

<script setup>
defineEmits(['toggle-history'])
</script>
