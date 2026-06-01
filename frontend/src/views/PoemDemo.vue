<template>
  <div class="demo-page">
    <h1 class="demo-title">诗词排版预览</h1>

    <div class="demo-grid">
      <div>
        <p class="demo-label">诗（五言/七言）· 两句一行 · 居中</p>
        <PoemContent :poem="sampleShi" />
      </div>
      <div>
        <p class="demo-label">词（长短句）· 逐句换行 · 居中+左对齐</p>
        <PoemContent :poem="sampleCi" />
      </div>
    </div>
  </div>
</template>

<script setup>
import PoemContent from '../components/PoemContent.vue'

const sampleShi = {
  title: '关雎', dynasty: '先秦', author: '佚名', source: 'shijing',
  content: '关关雎鸠，在河之洲。窈窕淑女，君子好逑。参差荇菜，左右流之。窈窕淑女，寤寐求之。求之不得，寤寐思服。悠哉悠哉，辗转反侧。参差荇菜，左右采之。窈窕淑女，琴瑟友之。参差荇菜，左右芼之。窈窕淑女，钟鼓乐之。'
}

const sampleCi = {
  title: '水调歌头·明月几时有', dynasty: '宋', author: '苏轼', source: 'song',
  content: '明月几时有？把酒问青天。不知天上宫阙，今夕是何年。我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。'
}
</script>

<style scoped>
.demo-page {
  min-height: 100vh;
  background: #f5f3ef;
  padding: 3rem 1.5rem;
}

.demo-title {
  text-align: center;
  font-size: 0.85rem;
  color: #999;
  letter-spacing: 0.3em;
  margin-bottom: 2.5rem;
}

.demo-grid {
  max-width: 40rem;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 2.5rem;
}

.demo-label {
  font-size: 0.75rem;
  color: #aaa;
  letter-spacing: 0.1em;
  margin-bottom: 0.75rem;
  text-align: center;
}
</style>
