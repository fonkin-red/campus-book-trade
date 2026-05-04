<template>
  <div class="page-container">
    <el-card>
      <template #header><h2>发布图书</h2></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="书名" prop="title">
              <el-input v-model="form.title" placeholder="请输入书名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者">
              <el-input v-model="form.author" placeholder="请输入作者" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="ISBN">
              <el-input v-model="form.isbn" placeholder="选填" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择" style="width:100%">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="原价">
              <el-input-number v-model="form.originalPrice" :precision="2" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="售价" prop="price">
              <el-input-number v-model="form.price" :precision="2" :min="0.01" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="新旧程度">
              <el-select v-model="form.condition" style="width:100%">
                <el-option v-for="(label, val) in condMap" :key="val" :label="label" :value="Number(val)" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述">
              <el-input v-model="form.description" type="textarea" :rows="4"
                placeholder="描述这本书的状况、亮点..." />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="封面">
              <el-upload :action="uploadUrl" :headers="uploadHeaders" :on-success="onUploadSucc"
                :show-file-list="false" accept="image/*">
                <el-button>上传封面</el-button>
              </el-upload>
              <img v-if="form.coverImage" :src="form.coverImage" class="preview" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">发 布</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCategories, publishBook } from '@/api'
import { getToken } from '@/utils/auth'

const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const categories = ref([])

const condMap = { 1: '全新', 2: '九五新', 3: '九成新', 4: '八成新', 5: '较旧' }
const uploadUrl = '/api/upload'
const uploadHeaders = { Authorization: `Bearer ${getToken()}` }

const form = reactive({
  title: '', author: '', isbn: '', categoryId: null,
  originalPrice: null, price: null, condition: 1,
  description: '', coverImage: ''
})

const rules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const onUploadSucc = (res) => { form.coverImage = res.data }

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await publishBook(form)
    ElMessage.success('发布成功')
    router.push('/')
  } finally { submitting.value = false }
}

onMounted(async () => {
  const res = await getCategories()
  categories.value = res.data || []
})
</script>

<style scoped>
.preview { width: 120px; height: 160px; object-fit: cover; border-radius: 6px; margin-top: 8px; }
</style>
