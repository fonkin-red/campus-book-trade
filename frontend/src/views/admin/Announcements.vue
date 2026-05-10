<template>
  <div v-loading="loading">
    <div class="page-header">
      <h3>公告管理</h3>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>发布公告
      </el-button>
    </div>

    <el-table :data="announcements" stripe border style="margin-top: 16px">
      <el-table-column label="ID" prop="id" width="80" align="center" />
      <el-table-column label="标题" prop="title" min-width="180" show-overflow-tooltip />
      <el-table-column label="内容" prop="content" min-width="250" show-overflow-tooltip />
      <el-table-column label="置顶" width="80" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.isPinned === 1" type="warning" size="small">置顶</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" prop="createTime" width="170" />
      <el-table-column label="操作" width="200" align="center">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="del(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 发布/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editing.id ? '编辑公告' : '发布公告'"
      width="600px"
      destroy-on-close
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch
            v-model="form.isPinned"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
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
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getAnnouncements,
  publishAnnouncement,
  updateAnnouncement,
  deleteAnnouncement
} from '@/api/index'

const announcements = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = ref({})
const form = reactive({ title: '', content: '', isPinned: 0 })

// 加载公告列表
const load = async () => {
  loading.value = true
  try {
    const res = await getAnnouncements()
    announcements.value = res?.data || res || []
  } catch (error) {
    console.error('加载失败', error)
  } finally {
    loading.value = false
  }
}

// 打开对话框
const openDialog = (row) => {
  if (row?.id) {
    editing.value = row
    form.title = row.title || ''
    form.content = row.content || ''
    form.isPinned = row.isPinned ?? 0
  } else {
    editing.value = {}
    form.title = ''
    form.content = ''
    form.isPinned = 0
  }
  dialogVisible.value = true
}

// 保存公告
const save = async () => {
  try {
    const payload = { ...form }
    if (editing.value.id) {
      await updateAnnouncement(editing.value.id, payload)
      ElMessage.success('已更新')
    } else {
      await publishAnnouncement(payload)
      ElMessage.success('已发布')
    }
    dialogVisible.value = false
    load()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 删除公告
const del = async (id) => {
  try {
    await deleteAnnouncement(id)
    ElMessage.success('已删除')
    load()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(load)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.page-header h3 {
  margin: 0;
}
</style>