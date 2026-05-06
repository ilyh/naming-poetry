<template>
  <div class="flex items-center gap-3">
    <label class="text-warm-gray text-sm font-bold tracking-wider whitespace-nowrap">姓氏</label>
    <input
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      maxlength="4"
      placeholder="李"
      class="w-20 text-center text-lg border-b-2 border-teal-warm/20 focus:border-teal-warm outline-none py-1 bg-transparent text-warm-brown placeholder:text-warm-gray/40"
    />
    <span class="text-xs text-warm-gray">支持复姓</span>
  </div>
</template>

<script setup>
defineProps({ modelValue: { type: String, default: '' } })
defineEmits(['update:modelValue'])
</script>
