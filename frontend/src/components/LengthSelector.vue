<template>
  <div class="flex items-center gap-3">
    <span class="text-warm-gray text-sm font-bold tracking-wider">名字字数</span>
    <button
      v-for="n in [1, 2]"
      :key="n"
      @click="$emit('update:modelValue', n)"
      :class="[
        'px-3 py-1 rounded-full text-sm font-medium transition',
        modelValue === n
          ? 'text-white'
          : 'text-warm-gray border border-stone-300/40 bg-cream/80 hover:-translate-y-0.5'
      ]"
      :style="modelValue === n ? { background: '#11554F' } : {}"
    >{{ n }}字</button>
  </div>
</template>

<script setup>
defineProps({ modelValue: { type: Number, default: 2 } })
defineEmits(['update:modelValue'])
</script>
