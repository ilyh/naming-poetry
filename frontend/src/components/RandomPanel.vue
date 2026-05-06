<template>
  <div>
    <div class="flex justify-center mb-6">
      <button @click="generate" :disabled="loading"
        class="px-8 py-2 bg-amber-600 text-white rounded-lg hover:bg-amber-700 disabled:opacity-50 transition">
        {{ loading ? '生成中...' : '生成名字' }}
      </button>
    </div>
    <div v-if="names.length > 0" class="grid grid-cols-2 md:grid-cols-3 gap-4">
      <NameCard v-for="(name, i) in names" :key="i" :name="name" @detail="detailName = name" />
    </div>
    <div v-else class="text-center text-stone-400 py-12">点击上方按钮开始生成</div>
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
