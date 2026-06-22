const BASE_URL = 'https://www.xiaochanmao.top'

function request(url, options = {}) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        ...options.header
      },
      success(res) {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve({ data: res.data })
        } else {
          reject(new Error(`Request failed with status ${res.statusCode}`))
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
