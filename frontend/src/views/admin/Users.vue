<template>
  <div v-loading="loading">
    <h2>用户管理</h2>
    <el-table :data="users" stripe>
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="用户名" prop="username" />
      <el-table-column label="昵称" prop="nickname" />
      <el-table-column label="手机号" prop="phone" />
      <el-table-column label="角色" width="100">
        <template #default="{ row }">{{ row.role === 1 ? '管理员' : '用户' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" type="danger" size="small" @click="toggleStatus(row.id, 0)">禁用</el-button>
          <el-button v-else type="success" size="small" @click="toggleStatus(row.id, 1)">启用</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminUsers, updateUserStatus } from '@/api'
import { ElMessage } from 'element-plus'

const users = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getAdminUsers()
    users.value = res.data || []
  } finally { loading.value = false }
})

const toggleStatus = async (id, status) => {
  await updateUserStatus(id, status)
  ElMessage.success(status === 1 ? '已启用' : '已禁用')
  const user = users.value.find(u => u.id === id)
  if (user) user.status = status
}
</script>