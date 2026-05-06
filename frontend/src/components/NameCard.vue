<template>
  <div class="bg-white rounded-lg shadow-sm border border-stone-200 p-4 text-center hover:shadow-md transition">
    <div class="text-2xl mb-1">
      <span class="text-stone-500 text-base">{{ name.surname }}</span>
      <span class="text-stone-900 font-bold tracking-wide">{{ name.givenName }}</span>
    </div>
    <div class="text-xs text-stone-400 space-y-0.5 mb-2">
      <div
        v-for="(char, idx) in name.givenName.split('')"
        :key="idx"
        class="flex items-start gap-1 min-w-0"
      >
        <span class="font-medium text-stone-500 shrink-0">{{ char }}</span>
        <span class="truncate">{{ (name.sources || [])[idx] ? '— ' + (name.sources || [])[idx] : '— 未知' }}</span>
      </div>
    </div>
    <button @click="$emit('detail', name)" class="text-xs text-amber-600 hover:text-amber-800 transition">
      溯源
    </button>
  </div>
</template>

<script setup>
defineProps({ name: { type: Object, required: true } })
defineEmits(['detail'])
</script>
