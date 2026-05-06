<template>
  <div>
    <div class="flex justify-center gap-3 mb-6">
      <input v-model="keyword" maxlength="1" placeholder="输入偏好字，如：清"
        class="w-24 text-center border-b-2 border-teal-warm/30 focus:border-teal-warm outline-none py-1 bg-transparent text-lg text-warm-brown placeholder:text-warm-gray/50" />
      <button @click="generate" :disabled="loading || !keyword"
        class="px-6 py-2.5 rounded-full font-bold text-sm tracking-wider transition"
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
      <h3 class="font-serif-name text-2xl text-warm-brown mt-3">输入一个偏好字，点击生成</h3>
      <p class="text-sm text-warm-gray mt-2 max-w-md">包含该字的诗句中的两个字符将组成你的专属名字。</p>
    </div>
    <NameDetailModal v-if="detailName" :name="detailName" @close="detailName = null" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { generateKeyword } from '../api/index.js'
import NameCard from './NameCard.vue'
import NameDetailModal from './NameDetailModal.vue'

const props = defineProps({
  surname: { type: String, required: true },
  length: { type: Number, default: 2 },
  sources: { type: Array, default: () => [] }
})

const keyword = ref('')
const names = ref([])
const loading = ref(false)
const detailName = ref(null)

async function generate() {
  if (!keyword.value) return
  loading.value = true
  try {
    const { data } = await generateKeyword({
      surname: props.surname,
      keyword: keyword.value,
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
