<template>
  <div class="glass-card rounded-2xl p-5">
    <h3 class="font-serif-name text-lg text-warm-brown mb-3">批量编辑</h3>
    <textarea
      v-model="localChars"
      class="w-full h-40 p-3 rounded-lg border text-sm text-warm-brown font-mono resize-y focus:outline-none focus:ring-1 focus:ring-teal-warm/30"
      style="background:rgba(255,255,255,0.6); border-color:rgba(89,72,56,0.16)"
      placeholder="输入黑名单字符，用逗号分隔"
    ></textarea>
    <div class="flex items-center justify-between mt-3">
      <span class="text-sm text-warm-gray">字符数：<b class="text-teal-warm">{{ charCount }}</b></span>
      <button
        @click="$emit('save', localChars)"
        :disabled="loading || !localChars.trim()"
        class="px-4 py-2 rounded-lg text-sm font-bold text-white transition"
        :class="loading || !localChars.trim() ? 'bg-stone-400 cursor-not-allowed' : 'bg-teal-warm hover:bg-teal-light'"
      >
        {{ loading ? '保存中...' : '保存并热更新' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  chars: { type: String, default: '' },
  charCount: { type: Number, default: 0 },
  loading: { type: Boolean, default: false },
})

defineEmits(['save'])

const localChars = ref(props.chars)
watch(() => props.chars, (v) => { localChars.value = v })
</script>
