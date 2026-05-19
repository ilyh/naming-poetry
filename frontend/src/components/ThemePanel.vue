<template>
  <div>
    <div class="flex flex-wrap justify-center gap-2 mb-4">
      <button
        v-for="theme in allThemes"
        :key="theme"
        @click="toggleTheme(theme)"
        :class="[
          'px-3 py-1.5 rounded-full text-sm transition font-medium',
          selectedThemes.includes(theme)
            ? 'text-white'
            : 'text-warm-gray border border-stone-300/40 bg-cream/80 hover:-translate-y-0.5'
        ]"
        :style="selectedThemes.includes(theme) ? { background:'#11554F' } : {}"
      >{{ theme }}</button>
    </div>
    <div class="flex justify-center gap-3 mb-6">
      <button @click="generate" :disabled="loading || selectedThemes.length === 0"
        class="px-6 py-2.5 rounded-full font-bold text-sm tracking-wider transition"
        style="background:#11554F; color:#FFFFFF">
        {{ loading ? '翻检诗卷中…' : '生成 6 个名字' }}
      </button>
      <button v-if="names.length > 0" @click="generate" :disabled="loading"
        class="px-5 py-2.5 rounded-full font-bold text-sm tracking-wider text-teal-warm border border-teal-warm/20 bg-cream/80 hover:-translate-y-0.5 transition">
        换一组
      </button>
    </div>
    <div v-if="names.length > 0" class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <NameCard v-for="(name, i) in names" :key="i" :name="name" :index="i" @detail="detailName = name" />
    </div>
    <div v-else class="flex flex-col items-center justify-center min-h-[23rem] rounded-2xl border border-dashed border-stone-300 text-center bg-cream">
      <p class="text-amber-warm text-xs font-bold tracking-widest">尚未生成</p>
      <h3 class="font-serif-name text-2xl text-warm-brown mt-3">选择意境标签，点击生成</h3>
      <p class="text-sm text-warm-gray mt-2 max-w-md">系统会从匹配意境的诗句中提取名字候选。</p>
    </div>
    <NameDetailModal v-if="detailName" :name="detailName" @close="detailName = null" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { generateTheme } from '../api/index.js'
import NameCard from './NameCard.vue'
import NameDetailModal from './NameDetailModal.vue'

const props = defineProps({
  surname: { type: String, required: true },
  length: { type: Number, default: 2 },
  sources: { type: Array, default: () => [] }
})

const allThemes = ['山水', '豪迈', '婉约', '清雅', '离别', '田园', '爱情', '志向']
const selectedThemes = ref([])
const names = ref([])
const loading = ref(false)
const detailName = ref(null)

function toggleTheme(theme) {
  const idx = selectedThemes.value.indexOf(theme)
  if (idx >= 0) selectedThemes.value.splice(idx, 1)
  else selectedThemes.value.push(theme)
}

async function generate() {
  if (selectedThemes.value.length === 0) return
  loading.value = true
  try {
    const { data } = await generateTheme({
      surname: props.surname,
      themes: selectedThemes.value,
      count: 6,
      length: props.length,
      sources: props.sources.length > 0 ? props.sources : null
    })
    names.value = data.names || []
  } catch (e) {
    console.error('Generate failed', e)
  } finally {
    loading.value = false
  }
}
</script>
