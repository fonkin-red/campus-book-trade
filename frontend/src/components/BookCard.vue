<template>
  <div class="book-card" @click="goDetail">
    <div class="cover">
      <img :src="book.coverImage || defaultCover" :alt="book.title" />
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
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
  box-shadow: 0 2px 12px rgba(102, 126, 234, 0.06);
  border: 1px solid rgba(102, 126, 234, 0.05);
}
.book-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.15);
  border-color: rgba(102, 126, 234, 0.15);
}
.cover {
  position: relative;
  width: 100%;
  height: 210px;
  overflow: hidden;
  background: linear-gradient(135deg, #eef0f8 0%, #e4e7f2 100%);
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
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(118, 75, 162, 0.3);
}
.info {
  padding: 14px;
}
.title {
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 6px;
}
.author {
  font-size: 12px;
  color: #8890b8;
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
  background: linear-gradient(135deg, #f56565, #ed64a6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.original {
  font-size: 12px;
  color: #b0b6d1;
  text-decoration: line-through;
}
</style>
