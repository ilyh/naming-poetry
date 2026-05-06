<template>
  <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" @click.self="$emit('close')">
    <div class="bg-white rounded-xl shadow-xl max-w-md w-full mx-4 p-6">
      <h3 class="text-xl font-bold text-stone-800 mb-4">
        {{ name.surname }}<span class="text-amber-700">{{ name.givenName }}</span>
      </h3>
      <div class="text-sm text-stone-500 mb-4">
        出处：{{ name.source || '未知' }}
      </div>
      <div class="flex gap-2 mb-4">
        <span
          v-for="char in name.givenName.split('')"
          :key="char"
          class="w-10 h-10 rounded-full bg-amber-100 text-amber-800 flex items-center justify-center text-lg font-bold"
        >{{ char }}</span>
      </div>
      <button @click="$emit('close')" class="w-full py-2 bg-stone-100 rounded-lg text-sm text-stone-600 hover:bg-stone-200 transition">
        关闭
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({ name: { type: Object, required: true } })
defineEmits(['close'])
</script>
