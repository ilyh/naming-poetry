import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

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
