<template>
  <div class="page-container">
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

    <div v-if="books.length" class="book-grid">
      <BookCard v-for="book in books" :key="book.id" :book="book" />
    </div>
    <el-empty v-else description="暂无图书" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getBookList, getCategories, searchBooks } from '@/api'
import BookCard from '@/components/BookCard.vue'

const keyword = ref('')
const categoryId = ref(null)
const books = ref([])
const categories = ref([])

const fetchBooks = async () => {
  const res = await getBookList({ categoryId: categoryId.value || undefined })
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
})
</script>

<style scoped>
.search-bar { display: flex; gap: 12px; margin-bottom: 24px; flex-wrap: wrap; }
.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}
</style>
