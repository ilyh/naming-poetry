<template>
  <div class="flex items-center gap-3">
    <span class="text-stone-600 text-sm">名字字数</span>
    <button
      v-for="n in [1, 2]"
      :key="n"
      @click="$emit('update:modelValue', n)"
      :class="[
        'px-3 py-1 rounded text-sm transition',
        modelValue === n ? 'bg-amber-600 text-white' : 'bg-stone-200 text-stone-600 hover:bg-stone-300'
      ]"
    >{{ n }}字</button>
  </div>
</template>

<script setup>
defineProps({ modelValue: { type: Number, default: 2 } })
defineEmits(['update:modelValue'])
</script>
