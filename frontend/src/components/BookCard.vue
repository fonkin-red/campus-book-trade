<template>
  <div class="book-card" @click="goDetail">
    <div class="cover">
      <img :src="book.coverImage || defaultCover" :alt="book.title" loading="lazy" decoding="async" />
      <span v-if="book.condition" class="tag">{{ condMap[book.condition] }}</span>
    </div>
    <div class="info">
      <h3 class="title">{{ book.title }}</h3>
      <p class="author" v-if="book.author">{{ book.author }}</p>
      <div class="bottom">
        <span class="price">&yen;{{ book.price }}</span>
        <span v-if="book.originalPrice" class="original">&yen;{{ book.originalPrice }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({ book: { type: Object, required: true } })
const router = useRouter()

const defaultCover = 'data:image/svg+xml,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="200" height="260"><defs><linearGradient id="g" x1="0%" y1="0%" x2="100%" y2="100%"><stop offset="0%" stop-color="#d5dbef"/><stop offset="100%" stop-color="#c8cde8"/></linearGradient></defs><rect width="200" height="260" fill="url(#g)"/><text x="100" y="130" text-anchor="middle" fill="#8890b8" font-size="16">暂无封面</text></svg>'
)
const condMap = { 1: '全新', 2: '九五新', 3: '九成新', 4: '八成新', 5: '较旧' }
const goDetail = () => router.push(`/book/${props.book.id}`)
</script>

<style scoped>
.book-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
  box-shadow: var(--shadow-sm);
  border: 1px solid rgba(132, 153, 160, 0.22);
}
.book-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
  border-color: rgba(47, 111, 95, 0.30);
}
.cover {
  position: relative;
  width: 100%;
  aspect-ratio: 4 / 5;
  overflow: hidden;
  background: #eef3f4;
}
.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.book-card:hover .cover img {
  transform: scale(1.05);
}
.tag {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(31, 41, 51, 0.78);
  color: #fff;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 999px;
  font-weight: 500;
  backdrop-filter: blur(6px);
}
.info {
  padding: 12px 13px 14px;
}
.title {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-main);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 6px;
}
.author {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 10px;
}
.bottom {
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.price {
  font-size: 20px;
  font-weight: 700;
  color: var(--danger);
}
.original {
  font-size: 12px;
  color: #98a2b3;
  text-decoration: line-through;
}
</style>
