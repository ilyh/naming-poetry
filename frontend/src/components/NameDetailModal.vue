<template>
  <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" @click.self="$emit('close')">
    <div class="bg-cream rounded-2xl shadow-xl max-w-md w-full mx-4 p-6">
      <h3 class="font-serif-name text-3xl text-warm-brown mb-4">
        {{ name.surname }}<span class="text-teal-warm">{{ name.givenName }}</span>
      </h3>

      <div class="rounded-xl p-4 mb-4" style="background:linear-gradient(180deg, rgba(47,127,115,0.06), rgba(255,253,248,0.8)); border:1px solid rgba(47,127,115,0.14)">
        <p class="text-sm text-warm-gray leading-lax mb-1 font-medium">出处诗句</p>
        <p class="text-base text-warm-brown leading-relaxed" v-html="highlightedSentence"></p>
      </div>

      <div class="flex gap-2 mb-4">
        <span
          v-for="(char, idx) in name.givenName.split('')"
          :key="idx"
          class="w-12 h-12 rounded-full flex items-center justify-center text-xl font-serif-name font-bold"
          style="background:rgba(47,127,115,0.12); color:#1f5e55"
        >{{ char }}</span>
      </div>

      <button @click="$emit('close')" class="w-full py-2.5 rounded-full text-sm font-bold text-teal-warm border border-teal-warm/20 bg-cream/80 hover:-translate-y-0.5 transition">
        关闭
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ name: { type: Object, required: true } })
defineEmits(['close'])

const highlightedSentence = computed(() => {
  const sentence = props.name.sources?.[0] || ''
  if (!sentence || !props.name.givenName) return sentence
  const chars = props.name.givenName.split('')
  let result = ''
  for (const ch of sentence) {
    if (chars.includes(ch)) {
      result += `<span class="sentence-highlight">${ch}</span>`
    } else {
      result += ch
    }
  }
  return '「' + result + '」'
})
</script>
