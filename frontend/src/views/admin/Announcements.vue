<template>
  <div v-loading="loading" class="announcement-manage">
    <div class="page-header">
      <h2>公告管理</h2>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon>发布公告
      </el-button>
    </div>

    <!-- 搜索过滤 -->
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="标题">
          <el-input
            v-model="filterForm.title"
            placeholder="搜索标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="置顶">
          <el-select
            v-model="filterForm.isPinned"
            placeholder="全部"
            clearable
            style="width: 120px"
          >
            <el-option label="置顶" :value="1" />
            <el-option label="非置顶" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table :data="filteredAnnouncements" stripe border style="margin-top: 16px">
      <el-table-column label="ID" prop="id" width="80" align="center" />
      <el-table-column label="标题" prop="title" min-width="180" show-overflow-tooltip />
      <el-table-column label="内容" prop="content" min-width="250" show-overflow-tooltip>
        <template #default="{ row }">
          {{ truncateText(row.content, 40) }}
        </template>
      </el-table-column>
      <el-table-column label="置顶" width="80" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.isPinned === 1" type="warning" size="small">置顶</el-tag>
          <span v-else class="text-gray">-</span>
        </template>
      </el-table-column>
      <el-table-column label="发布者" prop="publisherName" width="100" align="center" />
      <el-table-column label="发布时间" width="170" align="center">
        <template #default="{ row }">
          {{ row.createTime || row.publishTime || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" text @click="openDialog(row)">
            <el-icon><Edit /></el-icon>编辑
          </el-button>
          <el-popconfirm
            title="确定删除该公告吗？"
            confirm-button-text="确定"
            cancel-button-text="取消"
            @confirm="handleDelete(row.id)"
          >
            <template #reference>
              <el-button size="small" type="danger" text>
                <el-icon><Delete /></el-icon>删除
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!filteredAnnouncements.length && !loading" description="暂无公告" />

    <!-- 发布/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editing.id ? '编辑公告' : '发布公告'"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入公告标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch
            v-model="form.isPinned"
            :active-value="1"
            :inactive-value="0"
            active-text="是"
            inactive-text="否"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ editing.id ? '更新' : '发布' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getAnnouncements,
  publishAnnouncement,
  updateAnnouncement,
  deleteAnnouncement
} from '@/api/announcement'

// ========== 数据 ==========
const announcements = ref([])
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)

const editing = ref({})  // 当前编辑的公告对象

// 筛选表单
const filterForm = reactive({
  title: '',
  isPinned: null
})

// 公告表单
const form = reactive({
  title: '',
  content: '',
  isPinned: 0
})

// 表单校验规则
const formRules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { min: 5, max: 500, message: '内容长度在 5 到 500 个字符', trigger: 'blur' }
  ]
}

// ========== 计算属性 ==========

// 筛选后的公告列表
const filteredAnnouncements = computed(() => {
  let list = announcements.value

  // 按标题搜索
  if (filterForm.title) {
    const keyword = filterForm.title.toLowerCase()
    list = list.filter(item => item.title && item.title.toLowerCase().includes(keyword))
  }

  // 按置顶状态筛选
  if (filterForm.isPinned !== null && filterForm.isPinned !== '') {
    list = list.filter(item => item.isPinned === filterForm.isPinned)
  }

  // 置顶的排前面，再按时间倒序
  return [...list].sort((a, b) => {
    if (a.isPinned !== b.isPinned) return (b.isPinned || 0) - (a.isPinned || 0)
    const timeA = a.createTime || a.publishTime || ''
    const timeB = b.createTime || b.publishTime || ''
    return timeB.localeCompare(timeA)
  })
})

// ========== 工具函数 ==========

// 截断文本
const truncateText = (text, maxLen = 40) => {
  if (!text) return ''
  return text.length > maxLen ? text.slice(0, maxLen) + '...' : text
}

// 重置筛选
const resetFilter = () => {
  filterForm.title = ''
  filterForm.isPinned = null
}

// ========== 接口操作 ==========

// 加载公告列表
const load = async () => {
  loading.value = true
  try {
    const res = await getAnnouncements()
    // 兼容不同后端返回格式
    announcements.value = res?.data || res || []
  } catch (error) {
    console.error('加载公告失败', error)
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
  // 清除校验
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    const payload = {
      title: form.title,
      content: form.content,
      isPinned: form.isPinned
    }
    if (editing.value.id) {
      await updateAnnouncement(editing.value.id, payload)
      ElMessage.success('公告已更新')
    } else {
      await publishAnnouncement(payload)
      ElMessage.success('公告已发布')
    }
    dialogVisible.value = false
    load()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 删除公告
const handleDelete = async (id) => {
  try {
    await deleteAnnouncement(id)
    ElMessage.success('公告已删除')
    load()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(load)
</script>

<style scoped>
.announcement-manage {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
}

.filter-card {
  margin-bottom: 0;
}

.text-gray {
  color: #c0c4cc;
}
</style>