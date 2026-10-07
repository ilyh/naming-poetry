import axios from 'axios'

function getSessionId() {
  let sid = localStorage.getItem('sessionId')
  if (!sid) {
    sid = 'web-' + Date.now() + '-' + Math.random().toString(36).slice(2, 10)
    localStorage.setItem('sessionId', sid)
  }
  return sid
}

const api = axios.create({ baseURL: '/api' })
const ADMIN_TOKEN_KEY = 'adminToken'

function isAdminRequest(url = '') {
  return url.startsWith('/admin/') || url.startsWith('/name/admin/')
}

api.interceptors.request.use(config => {
  const sessionId = getSessionId()
  if (isAdminRequest(config.url) && config.url !== '/admin/auth') {
    const token = sessionStorage.getItem(ADMIN_TOKEN_KEY)
    if (token) config.headers['X-Admin-Token'] = token
  }
  if (config.method === 'post' && config.data) {
    config.data.sessionId = sessionId
  } else if (config.method === 'get') {
    config.params = { ...config.params, sessionId }
  }
  return config
})

api.interceptors.response.use(response => response, error => {
  const url = error.config?.url || ''
  if (error.response?.status === 401 && isAdminRequest(url) && url !== '/admin/auth') {
    sessionStorage.removeItem(ADMIN_TOKEN_KEY)
    window.dispatchEvent(new Event('admin-session-expired'))
  }
  return Promise.reject(error)
})

export function generateRandom(params) {
  return api.post('/name/random', params)
}

export function generateKeyword(params) {
  return api.post('/name/keyword', params)
}

export function generateTheme(params) {
  return api.post('/name/theme', params)
}

export function getHistory(page = 0, size = 20) {
  return api.get('/name/history', { params: { page, size } })
}

export function getStats() {
  return api.get('/name/stats')
}

export function loginAdmin(password) {
  return api.post('/admin/auth', { password })
}

export function verifyAdminSession() {
  return api.get('/admin/session')
}

export function logoutAdmin() {
  return api.post('/admin/logout')
}

export function getBlacklist() {
  return api.get('/admin/blacklist')
}

export function updateBlacklist(chars) {
  return api.post('/admin/blacklist', { chars })
}

export function reloadBlacklist() {
  return api.post('/admin/blacklist/reload')
}

export function getPhraseBlacklist() {
  return api.get('/admin/phrase-blacklist')
}

export function updatePhraseBlacklist(phrases) {
  return api.post('/admin/phrase-blacklist', { phrases })
}

export function reloadPhraseBlacklist() {
  return api.post('/admin/phrase-blacklist/reload')
}

export function getPoem(id) {
  return api.get(`/poem/${id}`)
}
