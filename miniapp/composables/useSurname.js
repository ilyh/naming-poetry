// 姓氏归一化：去掉空白，按码点截到 4 字（复姓最长 2 字，留出余量）
// 按码点截断而非按 UTF-16 单元，避免把代理对截成半个字符
export function normalizeSurname(value) {
  return Array.from((value || '').replace(/\s/g, '')).slice(0, 4).join('')
}
