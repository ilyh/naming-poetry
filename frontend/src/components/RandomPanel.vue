<template>
  <div>
    <div class="flex justify-center gap-3 mb-6">
      <button @click="generate" :disabled="loading"
        class="px-8 py-2.5 rounded-full font-bold text-sm tracking-wider transition"
        style="background:linear-gradient(135deg,#1f5e55,#2f7f73); color:#fffdf8; box-shadow:0 20px 34px -24px rgba(31,94,85,0.72)">
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
    <div v-else class="flex flex-col items-center justify-center min-h-[23rem] rounded-2xl border border-dashed border-teal-warm/20 text-center" style="background:linear-gradient(180deg, rgba(255,253,248,0.88), rgba(239,228,209,0.56))">
      <p class="text-amber-warm text-xs font-bold tracking-widest">尚未生成</p>
      <h3 class="font-serif-name text-2xl text-warm-brown mt-3">点击上方按钮开始生成</h3>
      <p class="text-sm text-warm-gray mt-2 max-w-md">系统会从古诗文中抽取适合入名的字词，并为每个候选附上原诗句出处。</p>
    </div>
    <NameDetailModal v-if="detailName" :name="detailName" @close="detailName = null" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { generateRandom } from '../api/index.js'
import NameCard from './NameCard.vue'
import NameDetailModal from './NameDetailModal.vue'

const props = defineProps({
  surname: { type: String, required: true },
  length: { type: Number, default: 2 },
  sources: { type: Array, default: () => [] }
})

const names = ref([])
const loading = ref(false)
const detailName = ref(null)

async function generate() {
  loading.value = true
  try {
    const { data } = await generateRandom({
      surname: props.surname,
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
