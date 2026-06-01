<template>
  <Teleport to="body">
    <div class="modal-overlay" @click="handleBackdrop" ref="backdrop">
      <div class="modal-panel">
        <button @click="emit('close')" class="close-btn">&times;</button>
        <div class="modal-scroll">
          <PoemContent :poem="poem" />
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref } from 'vue'
import PoemContent from './PoemContent.vue'

defineProps({
  poem: { type: Object, required: true }
})
const emit = defineEmits(['close'])

const backdrop = ref(null)
function handleBackdrop(e) {
  if (e.target === backdrop.value) emit('close')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
  padding: 1rem;
}

.modal-panel {
  position: relative;
  border-radius: 1.25rem;
  width: 100%;
  max-width: 32rem;
  max-height: 85vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.05);
}

.modal-scroll {
  overflow-y: auto;
  scrollbar-width: none;          /* Firefox */
}
.modal-scroll::-webkit-scrollbar {
  display: none;                   /* Chrome / Safari / Edge */
}

.close-btn {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  z-index: 10;
  width: 2rem;
  height: 2rem;
  border-radius: 9999px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  line-height: 1;
  color: #999;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}
.close-btn:hover {
  color: #333;
  background: rgba(0, 0, 0, 0.06);
}
</style>
