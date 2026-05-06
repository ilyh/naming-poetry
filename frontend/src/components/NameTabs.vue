<template>
  <div class="flex border-b border-stone-200">
    <button
      v-for="tab in tabs"
      :key="tab.key"
      @click="$emit('update:modelValue', tab.key)"
      :class="[
        'px-4 py-2 text-sm transition border-b-2 -mb-[2px]',
        modelValue === tab.key
          ? 'border-amber-600 text-amber-700 font-medium'
          : 'border-transparent text-stone-500 hover:text-stone-700'
      ]"
    >{{ tab.label }}</button>
  </div>
</template>

<script setup>
defineProps({ modelValue: { type: String, default: 'random' } })
defineEmits(['update:modelValue'])

const tabs = [
  { key: 'random', label: '随机生成' },
  { key: 'keyword', label: '关键词筛选' },
  { key: 'theme', label: '主题意境' },
]
</script>
