<template>
  <div class="page-container">
    <section class="home-toolbar">
      <div>
        <h1>校园图书集市</h1>
        <p>按分类快速筛选，找到正在出售的教材、考研资料和闲置书。</p>
      </div>
      <el-button type="primary" @click="$router.push('/publish')">发布图书</el-button>
    </section>

    <!-- ========== 公告区域 ========== -->
    <div class="announcement-bar" v-if="announcements.length" @click.stop>
      <el-icon class="announce-icon"><Bell /></el-icon>
      <div class="announce-text" v-if="announcements.length === 1">
        <span v-if="announcements[0].isPinned === 1" style="color:#e6a23c;font-weight:bold;">【置顶】</span>
        <span>{{ announcements[0].title }}：{{ announcements[0].content }}</span>
      </div>
      <el-carousel
        v-else
        height="40px"
        direction="vertical"
        :autoplay="true"
        :interval="4000"
        indicator-position="none"
        class="announce-carousel"
        @click.stop
      >
        <el-carousel-item v-for="item in announcements" :key="item.id">
          <span v-if="item.isPinned === 1" style="color:#e6a23c;font-weight:bold;">【置顶】</span>
          <span>{{ item.title }}：{{ item.content }}</span>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索书名或作者..." clearable style="width:300px"
        @keyup.enter="handleSearch">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-select v-model="categoryId" placeholder="全部分类" clearable style="width:160px" @change="fetchBooks">
        <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- 图书列表 -->
    <div v-if="books.length" class="book-grid">
      <BookCard v-for="book in books" :key="book.id" :book="book" />
    </div>
    <el-empty v-else description="暂无图书" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getBookList, getCategories, searchBooks } from '@/api/index'
import { getPublicAnnouncements } from '@/api/index'
import BookCard from '@/components/BookCard.vue'
import { Bell, Search } from '@element-plus/icons-vue'

const keyword = ref('')
const categoryId = ref(null)
const books = ref([])
const categories = ref([])
const announcements = ref([])

const fetchBooks = async () => {
  const res = await getBookList({ categoryId: categoryId.value || undefined, page: 1, pageSize: 24 })
  books.value = res.data || []
}

const handleSearch = async () => {
  if (keyword.value.trim()) {
    const res = await searchBooks(keyword.value.trim())
    books.value = res.data || []
  } else {
    fetchBooks()
  }
}

onMounted(async () => {
  fetchBooks()
  const catRes = await getCategories()
  categories.value = catRes.data || []
  try {
    const annRes = await getPublicAnnouncements()
    announcements.value = annRes?.data || annRes || []
  } catch {}
})
</script>

<style scoped>
/* 公告 */
.announcement-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff8ed;
  border: 1px solid #f6d7a8;
  border-radius: 8px;
  padding: 10px 16px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm);
}
.announce-icon {
  font-size: 20px;
  color: #e6a23c;
  flex-shrink: 0;
}
.announce-carousel {
  flex: 1;
}
.announce-text {
  flex: 1;
  font-size: 14px;
}

.home-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
  padding: 18px 20px;
  background: #fff;
  border: 1px solid rgba(132, 153, 160, 0.22);
  border-radius: 8px;
  box-shadow: var(--shadow-sm);
}

.home-toolbar h1 {
  margin: 0;
  font-size: 24px;
  line-height: 1.3;
}

.home-toolbar p {
  margin: 4px 0 0;
  color: var(--text-muted);
  font-size: 14px;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
  padding: 14px;
  background: #fff;
  border: 1px solid rgba(132, 153, 160, 0.22);
  border-radius: 8px;
  box-shadow: var(--shadow-sm);
}

/* 图书网格 */
.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
  gap: 18px;
}

@media (max-width: 720px) {
  .home-toolbar {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
