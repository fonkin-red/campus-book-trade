<template>
  <div class="page-container" v-loading="loading">
    <el-row :gutter="30" v-if="book">
      <el-col :span="10">
        <div class="cover-box">
          <img :src="book.coverImage || defaultCover" :alt="book.title" />
        </div>
      </el-col>
      <el-col :span="14">
        <h1 class="title">{{ book.title }}</h1>
        <p class="author" v-if="book.author">作者：{{ book.author }}</p>
        <p v-if="book.isbn">ISBN：{{ book.isbn }}</p>
        <div class="price-row">
          <span class="price">&yen;{{ book.price }}</span>
          <span class="original" v-if="book.originalPrice">&yen;{{ book.originalPrice }}</span>
          <el-tag>{{ condMap[book.condition] }}</el-tag>
        </div>
        <p class="desc" v-if="book.description">{{ book.description }}</p>
        <div class="actions">
          <el-button type="primary" size="large" @click="buyNow">立即购买</el-button>
          <el-button size="large" @click="addCart">加入购物车</el-button>
          <el-button size="large" @click="toggleFav" :type="isFav ? 'warning' : ''">
            {{ isFav ? '已收藏' : '收藏' }}
          </el-button>
        </div>
        <p class="meta">浏览量：{{ book.viewCount }} | 发布于：{{ book.createTime }}</p>
      </el-col>
    </el-row>
    <el-empty v-else description="图书不存在或已下架" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getBookDetail, addToCart, addFavorite, removeFavorite } from '@/api'

const route = useRoute()
const router = useRouter()
const book = ref(null)
const loading = ref(true)
const isFav = ref(false)

const defaultCover = 'data:image/svg+xml,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="300" height="400" fill="#dcdfe6"><rect width="300" height="400"/><text x="150" y="200" text-anchor="middle" fill="#909399" font-size="18">暂无封面</text></svg>'
)
const condMap = { 1: '全新', 2: '九五新', 3: '九成新', 4: '八成新', 5: '较旧' }

onMounted(async () => {
  try {
    const res = await getBookDetail(route.params.id)
    book.value = res.data
  } finally { loading.value = false }
})

const buyNow = async () => {
  if (!localStorage.getItem('token')) return router.push('/login')
  await addToCart({ bookId: book.value.id, quantity: 1 })
  router.push('/cart')
}
const addCart = async () => {
  if (!localStorage.getItem('token')) return router.push('/login')
  await addToCart({ bookId: book.value.id, quantity: 1 })
  ElMessage.success('已加入购物车')
}
const toggleFav = async () => {
  if (!localStorage.getItem('token')) return router.push('/login')
  if (isFav.value) {
    await removeFavorite(book.value.id)
    isFav.value = false
  } else {
    await addFavorite(book.value.id)
    isFav.value = true
  }
}
</script>

<style scoped>
.cover-box { border-radius: 12px; overflow: hidden; background: #f0f2f5; box-shadow: 0 2px 12px rgba(102, 126, 234, 0.08); }
.cover-box img { width: 100%; display: block; }
.title { font-size: 22px; margin-bottom: 12px; }
.author { color: #606266; margin-bottom: 6px; }
.price-row { display: flex; align-items: center; gap: 12px; margin: 20px 0; }
.price { font-size: 28px; color: #f56c6c; font-weight: bold; }
.original { font-size: 14px; color: #c0c4cc; text-decoration: line-through; }
.desc { margin: 20px 0; padding: 16px; background: #fafafa; border-radius: 10px; line-height: 1.6; }
.actions { display: flex; gap: 12px; margin: 24px 0; }
.meta { font-size: 12px; color: #c0c4cc; }
</style>
