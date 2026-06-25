const BASE_URL = 'https://www.xiaochanmao.top'

export { BASE_URL }

function getSessionId() {
  let sid = uni.getStorageSync('sessionId')
  if (!sid) {
    sid = 'mp-' + Date.now() + '-' + Math.random().toString(36).slice(2, 10)
    uni.setStorageSync('sessionId', sid)
  }
  return sid
}

function request(url, options = {}) {
  const sessionId = getSessionId()
  const method = (options.method || 'GET').toUpperCase()
  if (method === 'POST' && options.data) {
    options.data.sessionId = sessionId
  } else {
    const sep = url.includes('?') ? '&' : '?'
    url = url + sep + 'sessionId=' + encodeURIComponent(sessionId)
  }
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method,
      data: options.data,
      timeout: options.timeout || 15000,
      header: {
        'Content-Type': 'application/json',
        ...options.header
      },
      success(res) {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve({ data: res.data })
        } else {
          const msg = (res.data && res.data.message) || `Request failed with status ${res.statusCode}`
          reject(new Error(msg))
        }
      },
      fail(err) {
        reject(err)
      }
    })
  })
}

export function generateRandom(params) {
  return request('/api/name/random', { method: 'POST', data: params })
}

export function generateKeyword(params) {
  return request('/api/name/keyword', { method: 'POST', data: params })
}

export function generateTheme(params) {
  return request('/api/name/theme', { method: 'POST', data: params })
}

export function getHistory(page = 0, size = 20) {
  return request(`/api/name/history?page=${page}&size=${size}`)
}

export function getStats() {
  return request('/api/name/stats')
}

export function getPoem(id) {
  return request(`/api/poem/${id}`)
}
