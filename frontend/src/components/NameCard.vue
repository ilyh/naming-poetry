<template>
  <div class="bg-white rounded-lg shadow-sm border border-stone-200 p-4 text-center hover:shadow-md transition">
    <div class="text-2xl mb-1">
      <span class="text-stone-500 text-base">{{ name.surname }}</span>
      <span class="text-stone-900 font-bold tracking-wide">{{ name.givenName }}</span>
    </div>
    <div class="text-xs text-stone-400 truncate mb-2" :title="name.source">{{ name.source || '—' }}</div>
    <button @click="$emit('detail', name)" class="text-xs text-amber-600 hover:text-amber-800 transition">
      溯源
    </button>
  </div>
</template>

<script setup>
defineProps({ name: { type: Object, required: true } })
defineEmits(['detail'])
</script>
