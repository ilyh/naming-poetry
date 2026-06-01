import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'

const routes = [
  { path: '/', name: 'home', component: HomePage },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('../views/AdminPage.vue'),
  },
  {
    path: '/demo/poem',
    name: 'poem-demo',
    component: () => import('../views/PoemDemo.vue'),
  },
]

export default createRouter({
  history: createWebHistory(),
  routes,
})
