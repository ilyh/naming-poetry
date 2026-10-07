import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'

document.querySelector('#toggle-history')?.addEventListener('click', () => {
  window.dispatchEvent(new Event('naming-poetry:toggle-history'))
})

if (window.location.pathname !== '/') {
  document.querySelectorAll('[data-home-only]').forEach((element) => element.remove())
}

createApp(App).use(router).mount('#app')
