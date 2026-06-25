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

api.interceptors.request.use(config => {
  const sessionId = getSessionId()
  if (config.method === 'post' && config.data) {
    config.data.sessionId = sessionId
  } else if (config.method === 'get') {
    config.params = { ...config.params, sessionId }
  }
  return config
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
