<template>
  <div class="page-container">
    <h2>我的收藏</h2>

    <el-empty v-if="!loading && !favorites.length" description="还没有收藏图书，去逛逛吧" />

    <div v-else class="favorites-grid">
      <div v-for="item in favorites" :key="item.id" class="favorite-card" @click="$router.push(`/book/${item.bookId}`)">
        <el-image
          v-if="item.coverImage"
          :src="item.coverImage"
          class="favorite-cover"
          fit="cover"
        />
        <div v-else class="favorite-cover placeholder">
          <span>暂无封面</span>
        </div>
        <div class="favorite-info">
          <div class="favorite-title">{{ item.title || '未知书名' }}</div>
          <div class="favorite-price">¥{{ (item.price || 0).toFixed(2) }}</div>
          <div class="favorite-meta">
            <el-tag :type="item.status === 1 ? 'success' : 'info'" size="small">
              {{ item.status === 1 ? '在售' : '已下架' }}
            </el-tag>
          </div>
        </div>
        <el-button
          class="unfavorite-btn"
          type="danger"
          size="small"
          circle
          @click.stop="handleRemove(item.bookId)"
        >
          ✕
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFavorites, removeFavorite } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const favorites = ref([])

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavorites()
    favorites.value = res.data || []
  } catch {
    ElMessage.error('加载收藏失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => loadFavorites())

const handleRemove = async (bookId) => {
  try {
    await ElMessageBox.confirm('确定取消收藏？', '提示', { type: 'warning' })
    await removeFavorite(bookId)
    ElMessage.success('已取消收藏')
    await loadFavorites()
  } catch {
    // 取消操作
  }
}
</script>

<style scoped>
.page-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 20px;
}

h2 {
  margin-bottom: 24px;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.favorite-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  transition: transform 0.2s, box-shadow 0.2s;
}

.favorite-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.favorite-cover {
  width: 100%;
  aspect-ratio: 3/4;
  object-fit: cover;
  display: block;
}

.favorite-cover.placeholder {
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 13px;
}

.favorite-info {
  padding: 12px;
}

.favorite-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.favorite-price {
  color: #e74c3c;
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 6px;
}

.favorite-meta {
  display: flex;
  align-items: center;
}

.unfavorite-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

.favorite-card:hover .unfavorite-btn {
  opacity: 1;
}
</style>
