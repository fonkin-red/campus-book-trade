<template>
  <div class="page-container" v-loading="loading">
    <el-row :gutter="30" v-if="book">
      <el-col :span="10">
        <div class="cover-box">
          <div class="carousel-container" v-if="allImages.length">
            <el-image :src="currentImage" fit="contain" class="carousel-image"
              :preview-src-list="allImages" :initial-index="currentIndex"
              hide-on-click-modal preview-teleported />
            <div class="carousel-arrow carousel-arrow-left" @click="prevImage">
              <el-icon><ArrowLeft /></el-icon>
            </div>
            <div class="carousel-arrow carousel-arrow-right" @click="nextImage">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
          <img v-else :src="defaultCover" :alt="book.title" class="fallback-img" />
          <div class="carousel-indicator">{{ currentIndex + 1 }} / {{ allImages.length || 1 }}</div>
        </div>
      </el-col>
      <el-col :span="14">
        <section class="detail-panel">
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
        </section>
      </el-col>
    </el-row>
    <el-empty v-else description="图书不存在或已下架" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { getBookDetail, addToCart, checkFavorite, addFavorite, removeFavorite } from '@/api'

const route = useRoute()
const router = useRouter()
const book = ref(null)
const loading = ref(true)
const isFav = ref(false)
const currentIndex = ref(0)

const allImages = computed(() => {
  if (!book.value) return []
  const list = [book.value.coverImage]
  if (book.value.images) {
    list.push(...book.value.images.split(',').filter(Boolean))
  }
  return list.filter(Boolean)
})

const currentImage = computed(() => allImages.value[currentIndex.value])

const prevImage = () => {
  const len = allImages.value.length
  if (!len) return
  currentIndex.value = (currentIndex.value - 1 + len) % len
}

const nextImage = () => {
  const len = allImages.value.length
  if (!len) return
  currentIndex.value = (currentIndex.value + 1) % len
}

const defaultCover = 'data:image/svg+xml,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="300" height="400" fill="#dcdfe6"><rect width="300" height="400"/><text x="150" y="200" text-anchor="middle" fill="#909399" font-size="18">暂无封面</text></svg>'
)
const condMap = { 1: '全新', 2: '九五新', 3: '九成新', 4: '八成新', 5: '较旧' }

onMounted(async () => {
  try {
    const res = await getBookDetail(route.params.id)
    book.value = res.data
    currentIndex.value = 0
    if (localStorage.getItem('token')) {
      const favRes = await checkFavorite(route.params.id)
      isFav.value = favRes.data === true
    }
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
  try {
    if (isFav.value) {
      await removeFavorite(book.value.id)
      isFav.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(book.value.id)
      isFav.value = true
      ElMessage.success('收藏成功')
    }
  } catch {
    // 收藏状态不变，错误消息由拦截器统一提示
  }
}
</script>

<style scoped>
.cover-box { border-radius: 8px; overflow: hidden; background: #fff; box-shadow: var(--shadow-sm); border: 1px solid rgba(132, 153, 160, 0.22); }
.carousel-container { position: relative; width: 100%; aspect-ratio: 3 / 4; display: flex; align-items: center; justify-content: center; background: #f6f8f9; }
.carousel-image { max-width: 100%; max-height: 100%; }
.carousel-arrow { position: absolute; top: 50%; transform: translateY(-50%); width: 36px; height: 36px; border-radius: 50%; background: rgba(31, 41, 51, 0.56); color: #fff; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: background 0.2s; z-index: 2; font-size: 18px; user-select: none; }
.carousel-arrow:hover { background: rgba(31, 41, 51, 0.78); }
.carousel-arrow-left { left: 8px; }
.carousel-arrow-right { right: 8px; }
.carousel-indicator { text-align: center; padding: 10px 0 8px; font-size: 13px; color: var(--text-muted); font-weight: 600; }
.fallback-img { width: 100%; display: block; }
.detail-panel { min-height: 100%; padding: 22px; background: #fff; border: 1px solid rgba(132, 153, 160, 0.22); border-radius: 8px; box-shadow: var(--shadow-sm); }
.title { font-size: 24px; line-height: 1.35; margin: 0 0 12px; color: var(--text-main); }
.author { color: var(--text-muted); margin-bottom: 6px; }
.price-row { display: flex; align-items: center; gap: 12px; margin: 20px 0; }
.price { font-size: 30px; color: var(--danger); font-weight: 800; }
.original { font-size: 14px; color: #98a2b3; text-decoration: line-through; }
.desc { margin: 20px 0; padding: 16px; background: #f6f8f9; border-radius: 8px; line-height: 1.7; color: #334155; }
.actions { display: flex; gap: 12px; margin: 24px 0; }
.meta { font-size: 12px; color: #98a2b3; }
</style>
