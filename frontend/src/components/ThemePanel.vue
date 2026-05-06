<template>
  <div>
    <div class="flex flex-wrap justify-center gap-2 mb-6">
      <button
        v-for="theme in allThemes"
        :key="theme"
        @click="toggleTheme(theme)"
        :class="[
          'px-3 py-1 rounded-full text-sm transition',
          selectedThemes.includes(theme)
            ? 'bg-amber-600 text-white'
            : 'bg-stone-200 text-stone-600 hover:bg-stone-300'
        ]"
      >{{ theme }}</button>
    </div>
    <div class="flex justify-center mb-6">
      <button @click="generate" :disabled="loading || selectedThemes.length === 0"
        class="px-8 py-2 bg-amber-600 text-white rounded-lg hover:bg-amber-700 disabled:opacity-50 transition">
        {{ loading ? '生成中...' : '生成名字' }}
      </button>
    </div>
    <div v-if="names.length > 0" class="grid grid-cols-2 md:grid-cols-3 gap-4">
      <NameCard v-for="(name, i) in names" :key="i" :name="name" @detail="detailName = name" />
    </div>
    <div v-else class="text-center text-stone-400 py-12">选择意境标签，点击生成</div>
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
