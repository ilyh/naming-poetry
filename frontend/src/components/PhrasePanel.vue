<template>
  <div class="paper-card rounded-2xl p-6">
    <h3 class="font-serif-name text-lg text-warm-brown mb-1">词组黑名单</h3>
    <p class="text-sm text-warm-gray/60 mb-4">拦截不适合做人名的多字组合（如"白痴""小人"），单字仍可与其他字组合出好名字。</p>

    <textarea
      v-model="localPhrases"
      class="w-full h-32 p-3 rounded-lg border text-sm text-warm-brown font-mono resize-y focus:outline-none focus:ring-1 focus:ring-teal-warm/30"
      style="background:#FAF8F5; border-color:#EBE8E3"
      placeholder="输入不良词组，用逗号分隔，如：无心,白痴,如花"
    ></textarea>

    <div class="flex items-center justify-between mt-3 mb-5">
      <span class="text-sm text-warm-gray">词组数：<b class="text-teal-warm">{{ phraseCount }}</b></span>
      <button
        @click="save"
        :disabled="loading || !localPhrases.trim()"
        class="px-4 py-2 rounded-lg text-sm font-bold text-white transition"
        :class="loading || !localPhrases.trim() ? 'bg-stone-400 cursor-not-allowed' : 'bg-teal-warm hover:bg-teal-light'"
      >
        {{ loading ? '保存中...' : '保存并热更新' }}
      </button>
    </div>

    <div class="flex flex-wrap gap-2 mb-4">
      <span
        v-for="p in phraseList"
        :key="p"
        class="inline-flex items-center gap-1 px-3 py-1 rounded-lg text-sm char-tag"
      >
        {{ p }}
        <button
          @click="remove(p)"
          class="text-warm-gray/50 hover:text-red-600 transition"
          title="移除"
        >&times;</button>
      </span>
      <p v-if="phraseList.length === 0" class="text-sm text-warm-gray/50 py-2">词组黑名单为空</p>
    </div>

    <div class="flex gap-2">
      <input
        v-model="addPhrase"
        type="text"
        maxlength="4"
        placeholder="添加词组（2-4字）"
        class="flex-1 px-3 py-2 rounded-lg border text-sm text-warm-brown focus:outline-none focus:ring-1 focus:ring-teal-warm/30"
        style="background:#FAF8F5; border-color:#EBE8E3"
        @keyup.enter="addOne"
      />
      <button
        @click="addOne"
        class="px-3 py-2 rounded-lg text-sm font-bold bg-teal-warm text-white hover:bg-teal-light transition"
      >添加</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { getPhraseBlacklist, updatePhraseBlacklist } from '../api'

const props = defineProps({
  toast: { type: Function, default: () => {} },
})

const localPhrases = ref('')
const addPhrase = ref('')
const loading = ref(false)

const phraseList = computed(() => {
  if (!localPhrases.value) return []
  return localPhrases.value.split(',').map(s => s.trim()).filter(Boolean)
})

const phraseCount = computed(() => phraseList.value.length)

async function load() {
  try {
    const res = await getPhraseBlacklist()
    localPhrases.value = res.data.phrases || ''
  } catch {
    props.toast('加载词组黑名单失败')
  }
}

async function save(next) {
  loading.value = true
  try {
    await updatePhraseBlacklist(next != null ? next : localPhrases.value)
    await load()
    props.toast('词组黑名单已热更新')
  } catch {
    props.toast('保存失败，请重试')
  } finally {
    loading.value = false
  }
}

async function remove(p) {
  const arr = phraseList.value.slice()
  const next = arr.filter(s => s !== p).join(',')
  localPhrases.value = next
  await save(next)
}

async function addOne() {
  const v = addPhrase.value.trim()
  if (!v) return
  if (v.length < 2) {
    props.toast('词组至少 2 个字')
    return
  }
  if (phraseList.value.includes(v)) {
    props.toast('该词组已在黑名单中')
    return
  }
  const next = localPhrases.value ? localPhrases.value + ',' + v : v
  localPhrases.value = next
  addPhrase.value = ''
  await save(next)
}

onMounted(load)
</script>

<style scoped>
.char-tag {
  background: #FAF8F5;
  border: 1px solid rgba(0, 0, 0, 0.06);
  color: #1A1A1A;
}
</style>
