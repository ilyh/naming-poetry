<template>
  <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50" @click.self="$emit('close')">
    <div class="bg-cream rounded-2xl shadow-xl max-w-lg w-full mx-4 p-6 max-h-[80vh] overflow-y-auto">
      <div class="text-center mb-6">
        <h3 class="font-serif-name text-2xl text-warm-brown mb-1">{{ poem.title }}</h3>
        <p class="text-sm text-warm-gray">
          <span v-if="poem.dynasty">〔{{ poem.dynasty }}〕</span>
          <span v-if="poem.author">{{ poem.author }}</span>
        </p>
        <p v-if="poem.source" class="text-xs text-warm-gray/60 mt-1">{{ sourceLabel }}</p>
      </div>

      <div class="rounded-xl p-5 mb-4 whitespace-pre-line text-base text-warm-brown leading-loose font-serif-name" style="background:#F8F6F2; border:1px solid rgba(0,0,0,0.05)">
        {{ poem.content }}
      </div>

      <button @click="$emit('close')" class="w-full py-2.5 rounded-full text-sm font-bold text-teal-warm border border-teal-warm/20 bg-cream/80 hover:-translate-y-0.5 transition">
        关闭
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const SOURCE_NAMES = {
  shijing: '诗经', chuci: '楚辞', tang: '唐诗', song: '宋词',
  yuefu: '乐府诗集', gushi: '古诗', cifu: '著名辞赋'
}

const props = defineProps({
  poem: { type: Object, required: true }
})
defineEmits(['close'])

const sourceLabel = computed(() => SOURCE_NAMES[props.poem.source] || props.poem.source)
</script>
