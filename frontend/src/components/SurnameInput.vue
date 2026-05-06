<template>
  <div class="flex items-center gap-3">
    <label class="text-stone-600 text-sm whitespace-nowrap">姓氏</label>
    <input
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      maxlength="1"
      placeholder="李"
      class="w-16 text-center text-lg border-b-2 border-stone-300 focus:border-amber-500 outline-none py-1 bg-transparent"
    />
  </div>
</template>

<script setup>
defineProps({ modelValue: { type: String, default: '' } })
defineEmits(['update:modelValue'])
</script>
