<template>
  <div v-loading="loading">
    <h2>分类管理</h2>
    <el-button type="primary" style="margin-bottom:16px" @click="openDialog()">添加分类</el-button>
    <el-table :data="categories" stripe>
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="名称" prop="name" />
      <el-table-column label="排序" prop="sortOrder" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="del(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing.id ? '编辑分类' : '添加分类'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCategories, addCategory, updateCategory, deleteCategory } from '@/api'
import { ElMessage } from 'element-plus'

const categories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = ref({})
const form = reactive({ name: '', sortOrder: 0 })

const load = async () => {
  loading.value = true
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } finally { loading.value = false }
}
onMounted(load)

const openDialog = (row) => {
  editing.value = row || {}
  form.name = row?.name || ''
  form.sortOrder = row?.sortOrder || 0
  dialogVisible.value = true
}

const save = async () => {
  if (editing.value.id) {
    await updateCategory(editing.value.id, { ...form })
    ElMessage.success('已更新')
  } else {
    await addCategory({ ...form })
    ElMessage.success('已添加')
  }
  dialogVisible.value = false
  load()
}

const del = async (id) => {
  await deleteCategory(id)
  ElMessage.success('已删除')
  load()
}
</script>