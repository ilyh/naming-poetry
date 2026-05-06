<template>
  <div>
    <div class="flex justify-center gap-4 mb-6">
      <input v-model="keyword" maxlength="1" placeholder="输入偏好字，如：清"
        class="w-24 text-center border-b-2 border-stone-300 focus:border-amber-500 outline-none py-1 bg-transparent text-lg" />
      <button @click="generate" :disabled="loading || !keyword"
        class="px-6 py-2 bg-amber-600 text-white rounded-lg hover:bg-amber-700 disabled:opacity-50 transition">
        {{ loading ? '生成中...' : '生成名字' }}
      </button>
    </div>
    <div v-if="names.length > 0" class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <NameCard v-for="(name, i) in names" :key="i" :name="name" @detail="detailName = name" />
    </div>
    <div v-else class="text-center text-stone-400 py-12">输入一个字，点击生成</div>
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
