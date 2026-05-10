<template>
  <div class="page-container">
    <h2>个人中心</h2>

    <!-- 快捷入口 -->
    <el-row :gutter="24" style="margin-bottom:20px">
      <el-col :span="6">
        <el-card shadow="hover" class="quick-entry" @click="$router.push('/cart')">
          <el-icon :size="24"><ShoppingCart /></el-icon>
          <span>购物车</span>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="quick-entry" @click="$router.push('/orders')">
          <el-icon :size="24"><Document /></el-icon>
          <span>我的订单</span>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="quick-entry" @click="$router.push('/favorites')">
          <el-icon :size="24"><Star /></el-icon>
          <span>我的收藏</span>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" style="margin-top:20px">
      <el-col :span="8">
        <el-card>
          <template #header>个人信息</template>
          <el-form :model="profile" label-width="80px">
            <el-form-item label="用户名">{{ profile.username || '-' }}</el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="profile.nickname" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="profile.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profile.email" />
            </el-form-item>
            <el-button type="primary" @click="save" :loading="saving">保存</el-button>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>我发布的图书</span>
            <el-button type="primary" size="small" style="float:right" @click="$router.push('/publish')">发布新书</el-button>
          </template>
          <el-table :data="myBooks" v-loading="bookLoading">
            <el-table-column label="书名" prop="title" min-width="200" />
            <el-table-column label="售价" width="100">
              <template #default="{ row }">&yen;{{ row.price }}</template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">
                  {{ row.status === 1 ? '在售' : row.status === 2 ? '已售出' : '已下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="浏览量" prop="viewCount" width="80" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" @click="$router.push(`/book/${row.id}`)">查看</el-button>
                <el-popconfirm title="确定删除？" @confirm="delBook(row.id)">
                  <template #reference>
                    <el-button size="small" type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Document, Star } from '@element-plus/icons-vue'
import { getUserInfo, updateUser, getBookList, deleteBook as delBookApi } from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const profile = reactive({ id: null, username: '', nickname: '', phone: '', email: '' })
const myBooks = ref([])
const saving = ref(false)
const bookLoading = ref(true)

onMounted(async () => {
  try {
    const res = await getUserInfo()
    Object.assign(profile, res.data)
  } catch {}
  try {
    const sellerId = profile.id || userStore.user.id
    if (!sellerId) return
    const res = await getBookList({ sellerId })
    myBooks.value = res.data || []
  } finally { bookLoading.value = false }
})

const save = async () => {
  saving.value = true
  try {
    await updateUser({ nickname: profile.nickname, phone: profile.phone, email: profile.email })
    userStore.updateUserInfo(profile)
    ElMessage.success('保存成功')
  } finally { saving.value = false }
}

const delBook = async (id) => {
  await delBookApi(id)
  myBooks.value = myBooks.value.filter(b => b.id !== id)
  ElMessage.success('已删除')
}
</script>

<style scoped>
.quick-entry {
  cursor: pointer;
  text-align: center;
  transition: transform 0.2s;
}
.quick-entry:hover {
  transform: translateY(-3px);
}
.quick-entry .el-icon {
  display: block;
  margin: 0 auto 8px;
}
.quick-entry span {
  font-size: 14px;
  color: #303133;
}
</style>
