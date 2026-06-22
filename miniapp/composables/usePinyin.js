import { pinyin } from 'pinyin-pro'

const CHINESE_RE = /[一-鿿]/

export function isChinese(char) {
  return CHINESE_RE.test(char)
}

export function getPinyin(char) {
  if (!isChinese(char)) return char
  return pinyin(char, { toneType: 'none', type: 'string' })
}

export function getPinyinArray(text) {
  return text.split('').map(ch => ({
    char: ch,
    pinyin: isChinese(ch) ? getPinyin(ch) : null
  }))
}
