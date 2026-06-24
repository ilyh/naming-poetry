const HIGHLIGHT_STYLE = 'color:#11554F;font-weight:600;'
const FADE_STYLE = 'opacity:0.45;'
const PUNCT_RE = /[，。！？；：、]/

function escapeText(ch) {
  return ch.replace(/[<>&]/g, (c) => ({ '<': '&lt;', '>': '&gt;', '&': '&amp;' }[c]))
}

export function buildHighlightNodes(sentence, highlightChars) {
  const chars = highlightChars ? highlightChars.split('') : []
  const nodes = [{ type: 'text', text: '「' }]
  for (const ch of sentence) {
    if (chars.includes(ch)) {
      nodes.push({
        name: 'span',
        attrs: { style: HIGHLIGHT_STYLE },
        children: [{ type: 'text', text: escapeText(ch) }]
      })
    } else {
      nodes.push({ type: 'text', text: escapeText(ch) })
    }
  }
  nodes.push({ type: 'text', text: '」' })
  return nodes
}

export function buildPunctuationFadedNodes(text) {
  const nodes = []
  for (const ch of text) {
    if (PUNCT_RE.test(ch)) {
      nodes.push({
        name: 'span',
        attrs: { style: FADE_STYLE },
        children: [{ type: 'text', text: ch }]
      })
    } else {
      nodes.push({ type: 'text', text: escapeText(ch) })
    }
  }
  return nodes
}
