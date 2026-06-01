<template>
  <div class="poem-card">
    <!-- 标题与作者 -->
    <header class="poem-header">
      <h2 class="poem-title">{{ poem.title }}</h2>
      <p class="poem-meta">
        <span v-if="poem.dynasty">〔{{ poem.dynasty }}〕</span>
        <span v-if="poem.author">{{ poem.author }}</span>
      </p>
    </header>

    <!-- 分隔线 -->
    <div class="poem-divider">
      <span class="poem-divider-line"></span>
      <span class="poem-divider-dot">◇</span>
      <span class="poem-divider-line"></span>
    </div>

    <!-- 正文 -->
    <div :class="['poem-body', isCi ? 'poem-body--ci' : 'poem-body--shi']">
      <p v-for="(line, i) in formattedLines" :key="i" class="poem-line" v-html="line" />
    </div>

    <!-- 底部分隔 -->
    <div class="poem-divider poem-divider--end">
      <span class="poem-divider-line"></span>
      <span class="poem-divider-dot">·</span>
      <span class="poem-divider-line"></span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  poem: { type: Object, required: true }
})

const isCi = computed(() => props.poem.source === 'song')

function fadePunctuation(text) {
  return text.replace(/([，。！？；：、])/g, '<span class="poem-punc">$1</span>')
}

const formattedLines = computed(() => {
  const content = props.poem.content || ''
  const normalized = content.replace(/\n+/g, '。').replace(/。{2,}/g, '。')
  const parts = normalized
    .split(/(?<=[。！？；])/)
    .map(s => s.trim())
    .filter(Boolean)
    .map(fadePunctuation)

  return parts
})
</script>

<style scoped>
.poem-card {
  background: #fbfaf7;
  border-radius: 1.25rem;
  padding: 2.5rem 2rem;
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.06),
    0 2px 8px rgba(0, 0, 0, 0.03);
}

/* ── 标题与作者 ── */
.poem-header {
  text-align: center;
  margin-bottom: 1.25rem;
}

.poem-title {
  font-family: "STKaiti", "Kaiti SC", "Source Han Serif SC", "Noto Serif SC", "Songti SC", "SimSun", serif;
  font-size: 1.65rem;
  font-weight: 700;
  color: #1a1a1a;
  letter-spacing: 0.15em;
  margin: 0 0 0.4rem;
  line-height: 1.4;
}

.poem-meta {
  font-size: 0.85rem;
  color: #888;
  letter-spacing: 0.08em;
  margin: 0;
}

/* ── 分隔线 ── */
.poem-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  margin-bottom: 1.75rem;
}
.poem-divider--end {
  margin-top: 1.75rem;
  margin-bottom: 0;
}
.poem-divider-line {
  width: 3rem;
  height: 1px;
  background: #d4d0ca;
}
.poem-divider-dot {
  font-size: 0.7rem;
  color: #b0a89a;
}

/* ── 正文 ── */
.poem-body {
  font-family: "STKaiti", "Kaiti SC", "Source Han Serif SC", "Noto Serif SC", "Songti SC", "SimSun", serif;
  color: #2c2c2c;
}

.poem-body--shi {
  text-align: center;
}
.poem-body--shi .poem-line {
  font-size: 1.15rem;
  line-height: 2.4;
  letter-spacing: 0.25em;
  white-space: nowrap;
  overflow-x: auto;
}

.poem-body--ci {
  text-align: center;
}
.poem-body--ci .poem-line {
  display: inline-block;
  text-align: left;
  font-size: 1.1rem;
  line-height: 2.2;
  letter-spacing: 0.15em;
  white-space: nowrap;
}

/* ── 标点淡化 ── */
.poem-punc {
  opacity: 0.45;
}

/* ── 响应式 ── */
@media (max-width: 640px) {
  .poem-card {
    padding: 1.75rem 1.25rem;
  }
  .poem-title {
    font-size: 1.35rem;
    letter-spacing: 0.1em;
  }
  .poem-meta {
    font-size: 0.8rem;
  }
  .poem-body--shi .poem-line {
    font-size: 1rem;
    line-height: 2.2;
    letter-spacing: 0.15em;
  }
  .poem-body--ci .poem-line {
    font-size: 0.95rem;
    line-height: 2;
    letter-spacing: 0.1em;
  }
}
</style>
