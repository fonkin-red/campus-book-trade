<template>
  <div v-loading="loading">
    <h2>公告管理</h2>
    <el-button type="primary" style="margin-bottom:16px" @click="openDialog()">发布公告</el-button>
    <el-table :data="announcements" stripe>
      <el-table-column label="ID" prop="id" width="60" />
      <el-table-column label="标题" prop="title" />
      <el-table-column label="置顶" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.isPinned === 1" type="warning">置顶</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="del(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editing.id ? '编辑公告' : '发布公告'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
        <el-form-item label="置顶"><el-switch v-model="form.isPinned" :active-value="1" :inactive-value="0" /></el-form-item>
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
import { getAnnouncements, publishAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api'
import { ElMessage } from 'element-plus'

const announcements = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = ref({})
const form = reactive({ title: '', content: '', isPinned: 0 })

const load = async () => {
  loading.value = true
  try {
    const res = await getAnnouncements()
    announcements.value = res.data || []
  } finally { loading.value = false }
}
onMounted(load)

const openDialog = (row) => {
  editing.value = row || {}
  form.title = row?.title || ''
  form.content = row?.content || ''
  form.isPinned = row?.isPinned || 0
  dialogVisible.value = true
}

const save = async () => {
  if (editing.value.id) {
    await updateAnnouncement(editing.value.id, { ...form })
    ElMessage.success('已更新')
  } else {
    await publishAnnouncement({ ...form })
    ElMessage.success('已发布')
  }
  dialogVisible.value = false
  load()
}

const del = async (id) => {
  await deleteAnnouncement(id)
  ElMessage.success('已删除')
  load()
}
</script>