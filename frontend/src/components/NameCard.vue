<template>
  <div class="glass-card rounded-2xl p-5 card-enter flex flex-col" :style="{ animationDelay: (index || 0) * 55 + 'ms' }">
    <div class="flex items-start justify-between gap-3 mb-2">
      <div class="min-w-0">
        <p class="text-xs font-bold text-amber-warm tracking-widest mb-1">候选 {{ String(index + 1).padStart(2, '0') }}</p>
        <h3 class="font-serif-name text-4xl text-warm-brown leading-tight">
          {{ name.surname }}<span class="text-teal-warm">{{ name.givenName }}</span>
        </h3>
      </div>
      <button
        @click="copyName"
        class="shrink-0 px-3 py-1.5 text-xs font-bold text-teal-warm border border-teal-warm/20 rounded-full bg-cream/80 hover:-translate-y-0.5 hover:shadow-lg transition"
      >{{ copied ? '已复制' : '复制' }}</button>
    </div>

    <p class="text-base text-warm-brown leading-relaxed mt-2" v-html="highlightedSentence"></p>

    <div class="mt-auto pt-3 border-t border-stone-300/40 flex justify-between gap-2 text-xs text-warm-gray">
      <span>出处：{{ name.sources?.[0] || '未知' }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  name: { type: Object, required: true },
  index: { type: Number, default: 0 }
})
const emit = defineEmits(['detail'])

const copied = ref(false)

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

function copyName() {
  const text = (props.name.surname + props.name.givenName).trim()
  if (!text) return
  if (navigator.clipboard) {
    navigator.clipboard.writeText(text).then(() => {
      copied.value = true
      setTimeout(() => copied.value = false, 1500)
    })
    return
  }
  const ta = document.createElement('textarea')
  ta.value = text
  ta.style.position = 'absolute'
  ta.style.left = '-9999px'
  document.body.appendChild(ta)
  ta.select()
  document.execCommand('copy')
  document.body.removeChild(ta)
  copied.value = true
  setTimeout(() => copied.value = false, 1500)
}
</script>
