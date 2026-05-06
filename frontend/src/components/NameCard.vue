<template>
  <div class="bg-white rounded-xl shadow-sm border border-stone-200 p-5 text-center hover:shadow-md transition">
    <div class="text-3xl mb-2">
      <span class="text-stone-500 text-lg">{{ name.surname }}</span>
      <span class="text-stone-900 font-bold tracking-wide">{{ name.givenName }}</span>
    </div>
    <div class="text-sm text-stone-500 space-y-1 mb-3">
      <div
        v-for="(char, idx) in name.givenName.split('')"
        :key="idx"
        class="flex items-start gap-1"
      >
        <span class="font-medium text-stone-500 shrink-0 mt-px">{{ char }}</span>
        <span class="leading-relaxed">{{ (name.sources || [])[idx] ? '— ' + (name.sources || [])[idx] : '— 未知' }}</span>
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
