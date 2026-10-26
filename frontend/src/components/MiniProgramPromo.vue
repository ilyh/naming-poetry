<template>
  <section id="mini-program-promo" class="mx-auto max-w-6xl px-6 py-4 md:py-6" aria-labelledby="mini-program-title">
    <div class="relative overflow-hidden rounded-2xl border border-teal-warm/15 bg-[#eef6f3] px-6 py-7 shadow-[0_4px_18px_rgba(17,85,79,0.06)] md:px-10 md:py-8">
      <div class="pointer-events-none absolute -right-16 -top-20 h-48 w-48 rounded-full border border-teal-warm/10"></div>
      <div class="pointer-events-none absolute -bottom-24 right-16 h-40 w-40 rounded-full border border-teal-warm/10"></div>

      <div class="relative grid items-center gap-8 md:grid-cols-[1fr_15rem]">
        <div>
          <div class="mb-3 inline-flex items-center gap-2 text-xs tracking-[0.18em] text-teal-warm">
            <svg viewBox="0 0 24 24" class="h-4 w-4" fill="none" aria-hidden="true">
              <path d="M7.2 18.2c-2.4 0-4.4-1.5-4.4-3.5 0-1.4 1-2.7 2.5-3.2-.1-.3-.1-.6-.1-.9 0-2.2 2.2-4 4.9-4 .6 0 1.2.1 1.7.2C12.6 5.2 14.4 4 16.6 4c2.9 0 5.2 1.8 5.2 4.2 0 .5-.1.9-.3 1.3 1.5.6 2.5 1.8 2.5 3.3 0 2-2 3.5-4.4 3.5H13l-3.4 3v-3.1c-.7.1-1.5.2-2.4.2Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
              <path d="M8.2 12.5h.01M12 12.5h.01" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" />
            </svg>
            <span>微信小程序</span>
          </div>

          <h2 id="mini-program-title" class="font-serif-name text-2xl text-warm-brown md:text-3xl">
            把取名灵感，带进微信
          </h2>
          <p class="mt-3 max-w-xl text-sm leading-7 text-warm-gray md:text-base">
            打开微信搜索「诗语择名」，随时继续挑选有出处的好名字，让诗词灵感陪你走得更远。
          </p>

          <div class="mt-5 flex flex-wrap items-center gap-3">
            <button
              type="button"
              class="inline-flex items-center gap-2 rounded-full bg-teal-warm px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-teal-light focus:outline-none focus:ring-2 focus:ring-teal-warm/30 focus:ring-offset-2"
              @click="openGuide"
            >
              查看进入方式
              <svg viewBox="0 0 16 16" class="h-4 w-4" fill="none" aria-hidden="true">
                <path d="M3 8h9M8.5 4.5 12 8l-3.5 3.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
            </button>
            <button
              type="button"
              class="rounded-full border border-teal-warm/25 bg-white/70 px-5 py-2.5 text-sm text-teal-warm transition hover:border-teal-warm/45 hover:bg-white focus:outline-none focus:ring-2 focus:ring-teal-warm/20 focus:ring-offset-2"
              @click="copyMiniProgramName"
            >
              复制小程序名
            </button>
          </div>
          <p v-if="copyMessage" role="status" class="mt-3 text-xs text-teal-warm">{{ copyMessage }}</p>
        </div>

        <div class="relative rounded-xl border border-teal-warm/10 bg-white/75 px-5 py-6 text-center">
          <div v-if="miniProgramCodeUrl && !codeLoadFailed" class="mx-auto h-32 w-32 rounded-xl bg-white p-1 shadow-sm">
            <img
              :src="miniProgramCodeUrl"
              alt="诗语择名微信小程序码"
              class="h-full w-full rounded-lg object-contain"
              @error="codeLoadFailed = true"
            />
          </div>
          <div v-else class="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-teal-warm text-white shadow-sm">
            <svg viewBox="0 0 24 24" class="h-8 w-8" fill="none" aria-hidden="true">
              <path d="M7.2 18.2c-2.4 0-4.4-1.5-4.4-3.5 0-1.4 1-2.7 2.5-3.2-.1-.3-.1-.6-.1-.9 0-2.2 2.2-4 4.9-4 .6 0 1.2.1 1.7.2C12.6 5.2 14.4 4 16.6 4c2.9 0 5.2 1.8 5.2 4.2 0 .5-.1.9-.3 1.3 1.5.6 2.5 1.8 2.5 3.3 0 2-2 3.5-4.4 3.5H13l-3.4 3v-3.1c-.7.1-1.5.2-2.4.2Z" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round" />
              <path d="M8.2 12.5h.01M12 12.5h.01" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" />
            </svg>
          </div>
          <p class="mt-4 text-xs tracking-[0.16em] text-warm-gray/70">{{ miniProgramCodeUrl && !codeLoadFailed ? '扫码进入' : '微信搜索' }}</p>
          <p class="mt-1 font-serif-name text-lg text-warm-brown">诗语择名</p>
          <p class="mt-2 text-xs text-warm-gray/65">发现更多取名灵感</p>
        </div>
      </div>
    </div>
  </section>

  <Teleport to="body">
    <transition name="mini-program-modal">
      <div
        v-if="guideVisible"
        class="fixed inset-0 z-50 flex items-center justify-center bg-warm-brown/35 px-4 py-6 backdrop-blur-[2px]"
        role="presentation"
        @click.self="closeGuide"
      >
        <section
          ref="dialogRef"
          class="max-h-[calc(100vh-3rem)] w-full max-w-lg overflow-y-auto rounded-2xl bg-white p-6 shadow-2xl outline-none md:p-8"
          role="dialog"
          aria-modal="true"
          aria-labelledby="mini-program-dialog-title"
          tabindex="-1"
        >
          <div class="flex items-start justify-between gap-4">
            <div>
              <p class="text-xs tracking-[0.18em] text-teal-warm">WECHAT MINI PROGRAM</p>
              <h2 id="mini-program-dialog-title" class="mt-2 font-serif-name text-2xl text-warm-brown">进入诗语择名</h2>
            </div>
            <button
              type="button"
              class="rounded-full p-2 text-warm-gray/60 transition hover:bg-cream-dark hover:text-warm-brown focus:outline-none focus:ring-2 focus:ring-teal-warm/20"
              aria-label="关闭进入指引"
              @click="closeGuide"
            >
              <svg viewBox="0 0 20 20" class="h-5 w-5" fill="none" aria-hidden="true">
                <path d="m5 5 10 10M15 5 5 15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" />
              </svg>
            </button>
          </div>

          <div class="mt-6 grid gap-6 sm:grid-cols-[10rem_1fr] sm:items-center">
            <div class="flex min-h-40 items-center justify-center rounded-xl border border-stone-200 bg-cream-dark p-4 text-center">
              <img
                v-if="miniProgramCodeUrl && !codeLoadFailed"
                :src="miniProgramCodeUrl"
                alt="诗语择名微信小程序码"
                class="h-32 w-32 rounded-lg bg-white object-contain"
                @error="codeLoadFailed = true"
              />
              <div v-else class="flex flex-col items-center text-warm-gray">
                <svg viewBox="0 0 48 48" class="h-12 w-12 text-teal-warm" fill="none" aria-hidden="true">
                  <rect x="7" y="7" width="13" height="13" rx="1.5" stroke="currentColor" stroke-width="2" />
                  <rect x="28" y="7" width="13" height="13" rx="1.5" stroke="currentColor" stroke-width="2" />
                  <rect x="7" y="28" width="13" height="13" rx="1.5" stroke="currentColor" stroke-width="2" />
                  <path d="M30 28h3v3h-3zM37 28h4v4h-4zM28 36h4v5h-4zM35 36h3v3h-3zM40 38h2v3h-2z" fill="currentColor" />
                </svg>
                <span class="mt-2 text-xs">微信中搜索即可</span>
              </div>
            </div>

            <div>
              <p class="text-sm leading-7 text-warm-gray">打开微信，搜索下面的小程序名称：</p>
              <button
                type="button"
                class="mt-3 inline-flex items-center gap-2 rounded-lg border border-teal-warm/20 bg-teal-warm/5 px-3 py-2 font-serif-name text-lg text-teal-warm transition hover:bg-teal-warm/10 focus:outline-none focus:ring-2 focus:ring-teal-warm/20"
                @click="copyMiniProgramName"
              >
                诗语择名
                <svg viewBox="0 0 20 20" class="h-4 w-4" fill="none" aria-hidden="true">
                  <rect x="7" y="6" width="9" height="10" rx="1.5" stroke="currentColor" stroke-width="1.4" />
                  <path d="M13 6V4.5A1.5 1.5 0 0 0 11.5 3h-7A1.5 1.5 0 0 0 3 4.5v8A1.5 1.5 0 0 0 4.5 14H7" stroke="currentColor" stroke-width="1.4" />
                </svg>
              </button>
              <p v-if="copyMessage" role="status" class="mt-2 text-xs text-teal-warm">{{ copyMessage }}</p>
            </div>
          </div>

          <ol class="mt-7 grid gap-3 border-t border-stone-200 pt-5 text-sm text-warm-gray sm:grid-cols-3">
            <li class="flex gap-2">
              <span class="font-serif-name text-lg text-teal-warm">一</span>
              <span class="leading-6">打开微信<br />进入搜索</span>
            </li>
            <li class="flex gap-2">
              <span class="font-serif-name text-lg text-teal-warm">二</span>
              <span class="leading-6">搜索「诗语择名」</span>
            </li>
            <li class="flex gap-2">
              <span class="font-serif-name text-lg text-teal-warm">三</span>
              <span class="leading-6">点击进入小程序</span>
            </li>
          </ol>

          <button
            type="button"
            class="mt-7 w-full rounded-xl bg-warm-brown px-4 py-3 text-sm font-semibold text-white transition hover:bg-warm-brown/80 focus:outline-none focus:ring-2 focus:ring-warm-brown/25 focus:ring-offset-2"
            @click="closeGuide"
          >
            知道了
          </button>
        </section>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { nextTick, onBeforeUnmount, ref, watch } from 'vue'

const miniProgramCodeUrl = (import.meta.env.VITE_MINI_PROGRAM_CODE || '').trim()
const guideVisible = ref(false)
const codeLoadFailed = ref(false)
const copyMessage = ref('')
const dialogRef = ref(null)
let copyTimer = null
let previousBodyOverflow = ''

function openGuide() {
  codeLoadFailed.value = false
  guideVisible.value = true
}

function closeGuide() {
  guideVisible.value = false
}

function handleEscape(event) {
  if (event.key === 'Escape') closeGuide()
}

async function copyMiniProgramName() {
  const text = '诗语择名'
  let copied = false

  try {
    if (navigator.clipboard?.writeText) {
      await navigator.clipboard.writeText(text)
      copied = true
    }
  } catch {
    // Fall back to a temporary textarea for browsers without clipboard permission.
  }

  if (!copied) {
    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.setAttribute('readonly', '')
    textarea.style.position = 'fixed'
    textarea.style.opacity = '0'
    document.body.appendChild(textarea)
    textarea.select()
    try {
      copied = document.execCommand('copy')
    } catch {
      copied = false
    }
    textarea.remove()
  }

  copyMessage.value = copied ? '已复制，可在微信中粘贴搜索' : '请手动搜索：诗语择名'
  clearTimeout(copyTimer)
  copyTimer = setTimeout(() => { copyMessage.value = '' }, 2800)
}

watch(guideVisible, async (visible) => {
  if (visible) {
    previousBodyOverflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    window.addEventListener('keydown', handleEscape)
    await nextTick()
    dialogRef.value?.focus()
  } else {
    document.body.style.overflow = previousBodyOverflow
    window.removeEventListener('keydown', handleEscape)
  }
})

onBeforeUnmount(() => {
  clearTimeout(copyTimer)
  document.body.style.overflow = previousBodyOverflow
  window.removeEventListener('keydown', handleEscape)
})
</script>

<style scoped>
.mini-program-modal-enter-active,
.mini-program-modal-leave-active {
  transition: opacity 180ms ease;
}

.mini-program-modal-enter-active section,
.mini-program-modal-leave-active section {
  transition: transform 180ms ease, opacity 180ms ease;
}

.mini-program-modal-enter-from,
.mini-program-modal-leave-to {
  opacity: 0;
}

.mini-program-modal-enter-from section,
.mini-program-modal-leave-to section {
  opacity: 0;
  transform: translateY(10px) scale(0.98);
}
</style>
