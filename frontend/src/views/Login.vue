<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2>登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password
            @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width:100%">登 录</el-button>
        </el-form-item>
      </el-form>
      <p class="switch">还没有账号？<router-link to="/register">立即注册</router-link></p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login(form)
    userStore.loginSuccess(res.data)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  display: flex; justify-content: center; align-items: center;
  min-height: calc(100vh - 60px);
  background: linear-gradient(135deg, #e8ecf4 0%, #dfe3f0 30%, #e4e6f2 70%, #eceff7 100%);
  position: relative;
  overflow: hidden;
}
.auth-page::before {
  content: '';
  position: absolute;
  top: -30%;
  right: -10%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(102,126,234,0.08) 0%, transparent 70%);
  border-radius: 50%;
}
.auth-page::after {
  content: '';
  position: absolute;
  bottom: -20%;
  left: -5%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(118,75,162,0.06) 0%, transparent 70%);
  border-radius: 50%;
}
.auth-card {
  width: 420px;
  border-radius: 16px !important;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.1) !important;
  backdrop-filter: blur(4px);
  position: relative;
  z-index: 1;
}
.auth-card h2 {
  text-align: center;
  margin-bottom: 24px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.switch { text-align: center; font-size: 13px; color: #8890b8; }
.switch a { color: #667eea; font-weight: 500; }
</style>
