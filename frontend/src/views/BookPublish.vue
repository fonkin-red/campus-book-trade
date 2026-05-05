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
              <input ref="fileInputRef" type="file" accept="image/*" hidden @change="handleFileChange" />
              <el-button @click="fileInputRef.click()" :loading="uploading">上传封面</el-button>
              <el-progress v-if="uploading" :percentage="uploadProgress" :stroke-width="6"
                style="width:200px;margin-left:12px" />
              <div v-if="form.coverImage" class="preview-wrap">
                <el-image :src="form.coverImage" class="preview"
                  :preview-src-list="[form.coverImage]" fit="cover" hide-on-click-modal
                  preview-teleported />
                <el-button class="preview-del" type="danger" size="small" circle
                  @click="removeCoverImage">&times;</el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="附加图片">
              <input ref="multiFileInputRef" type="file" accept="image/*" multiple hidden
                @change="handleMultiFileChange" />
              <el-button @click="multiFileInputRef.click()" :loading="multiUploading">上传附加图片</el-button>
              <el-progress v-if="multiUploading" :percentage="multiUploadProgress" :stroke-width="6"
                style="width:200px;margin-left:12px" />
              <div v-if="form.additionalImages.length" class="multi-preview-gallery">
                <div v-for="(img, idx) in form.additionalImages" :key="idx" class="multi-preview-item">
                  <el-image :src="img" fit="cover" class="multi-preview-img"
                    :preview-src-list="[img]" preview-teleported hide-on-click-modal />
                  <el-button class="multi-del-btn" type="danger" size="small" circle
                    :loading="deletingIndex === idx" @click="removeAdditionalImage(idx)">&times;</el-button>
                </div>
              </div>
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
import { getCategories, publishBook, uploadFile, deleteFile } from '@/api'

const router = useRouter()
const formRef = ref()
const fileInputRef = ref()
const multiFileInputRef = ref()
const submitting = ref(false)
const uploading = ref(false)
const uploadProgress = ref(0)
const multiUploading = ref(false)
const multiUploadProgress = ref(0)
const deletingIndex = ref(null)
const categories = ref([])

const condMap = { 1: '全新', 2: '九五新', 3: '九成新', 4: '八成新', 5: '较旧' }

const form = reactive({
  title: '', author: '', isbn: '', categoryId: null,
  originalPrice: null, price: null, condition: 1,
  description: '', coverImage: '', additionalImages: []
})

const rules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const handleFileChange = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }
  uploading.value = true
  uploadProgress.value = 0
  try {
    const res = await uploadFile(file, (pct) => { uploadProgress.value = pct })
    form.coverImage = res.data
    ElMessage.success('封面上传成功')
  } catch (err) {
    const msg = err?.response?.data?.message || '上传失败'
    ElMessage.error(msg)
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}

const removeCoverImage = async () => {
  const url = form.coverImage
  form.coverImage = ''
  if (url) {
    try { await deleteFile(url) } catch { /* 服务端清理失败不阻塞 */ }
  }
}

const handleMultiFileChange = async (e) => {
  const files = Array.from(e.target.files || [])
  if (!files.length) return
  multiUploading.value = true
  multiUploadProgress.value = 0
  let completed = 0
  for (const file of files) {
    if (!file.type.startsWith('image/')) {
      ElMessage.warning(`跳过非图片文件: ${file.name}`)
      completed++
      continue
    }
    try {
      const res = await uploadFile(file, (pct) => {
        multiUploadProgress.value = Math.round(((completed + pct / 100) / files.length) * 100)
      })
      form.additionalImages.push(res.data)
    } catch (err) {
      ElMessage.error(`上传失败: ${file.name}`)
    } finally {
      completed++
    }
  }
  multiUploading.value = false
  e.target.value = ''
}

const removeAdditionalImage = async (idx) => {
  const url = form.additionalImages[idx]
  deletingIndex.value = idx
  try {
    await deleteFile(url)
    form.additionalImages.splice(idx, 1)
    ElMessage.success('已删除')
  } catch {
    form.additionalImages.splice(idx, 1)
    ElMessage.warning('已从列表移除')
  } finally {
    deletingIndex.value = null
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const payload = { ...form, images: form.additionalImages.join(',') }
    delete payload.additionalImages
    await publishBook(payload)
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
.preview-wrap { display: inline-block; position: relative; margin-top: 8px; }
.preview { width: 120px; height: 160px; object-fit: cover; border-radius: 6px; display: block; }
.preview-del { position: absolute; top: -8px; right: -8px; width: 22px; height: 22px; min-width: unset; padding: 0; font-size: 14px; line-height: 22px; }
.multi-preview-gallery { display: flex; flex-wrap: wrap; gap: 12px; margin-top: 8px; }
.multi-preview-item { position: relative; width: 100px; height: 100px; border-radius: 6px; overflow: hidden; border: 1px solid #ebeef5; }
.multi-preview-img { width: 100%; height: 100%; display: block; }
.multi-del-btn { position: absolute; top: 2px; right: 2px; width: 22px; height: 22px; min-width: unset; padding: 0; font-size: 14px; line-height: 22px; opacity: 0.9; }
</style>
