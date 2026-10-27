<template>
  <div class="paper-card rounded-2xl p-5 card-enter flex flex-col" :style="{ animationDelay: (index || 0) * 55 + 'ms' }">
    <div class="flex items-start justify-between gap-3 mb-2">
      <div class="min-w-0">
        <p class="text-xs font-bold text-amber-warm tracking-widest mb-1">候选 {{ String(index + 1).padStart(2, '0') }}</p>
        <h3 class="font-serif-name text-4xl text-warm-brown leading-tight">
          {{ name.surname }}<template v-for="(ch, i) in name.givenName" :key="i"><ruby v-if="isChinese(ch)" class="text-teal-warm">{{ ch }}<rp>(</rp><rt class="text-xs text-warm-gray font-sans">{{ getPinyin(ch) }}</rt><rp>)</rp></ruby><span v-else class="text-teal-warm">{{ ch }}</span></template>
        </h3>
      </div>
      <button
        @click="copyName"
        class="shrink-0 px-3 py-1.5 text-xs font-bold text-teal-warm border border-teal-warm/20 rounded-full bg-cream/80 hover:-translate-y-0.5 hover:shadow-lg transition"
      >{{ copied ? '已复制' : '复制' }}</button>
    </div>

    <p class="text-base text-warm-brown leading-relaxed mt-2" v-html="highlightedSentence"></p>

    <div class="mt-auto pt-3 border-t border-stone-300/40 flex justify-between items-center gap-2 text-xs text-warm-gray">
      <div class="min-w-0 flex flex-col gap-0.5">
        <span class="truncate">{{ sourceInfo.main }}</span>
        <span v-if="sourceInfo.author" class="text-[11px] text-stone-400">{{ sourceInfo.author }}</span>
      </div>
      <button
        v-if="name.poemId"
        @click="openPoem"
        class="shrink-0 flex items-center gap-1 px-2.5 py-1 rounded-full bg-teal-mist text-teal-warm font-bold hover:-translate-y-0.5 hover:shadow transition cursor-pointer"
      >查看全诗 <span class="text-[10px]">›</span></button>
    </div>
    <PoemDetailModal v-if="showPoem && poemData" :poem="poemData" @close="showPoem = false" />
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { isChinese, getPinyin } from '../composables/usePinyin'
import { getPoem } from '../api'
import PoemDetailModal from './PoemDetailModal.vue'

const props = defineProps({
  name: { type: Object, required: true },
  index: { type: Number, default: 0 }
})
const emit = defineEmits(['detail'])

const copied = ref(false)
const poemData = ref(null)
const showPoem = ref(false)

watch(() => props.name.poemId, () => {
  poemData.value = null
  showPoem.value = false
})

async function openPoem() {
  if (!props.name.poemId) return
  if (poemData.value) { showPoem.value = true; return }
  const { data } = await getPoem(props.name.poemId)
  poemData.value = data
  showPoem.value = true
}

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

// sourceNote 格式为「书名·诗名 作者 XXX」，按「 作者 」拆成两行；
// 无 sourceNote 时退回显示诗句，此时没有作者行
const sourceInfo = computed(() => {
  const note = props.name.sourceNote || props.name.sources?.[0] || '未知'
  const sep = ' 作者 '
  const idx = note.indexOf(sep)
  if (idx >= 0) {
    return { main: '出处：' + note.slice(0, idx), author: '作者 ' + note.slice(idx + sep.length) }
  }
  return { main: '出处：' + note, author: '' }
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
