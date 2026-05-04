<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2>注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="至少6位" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="选填" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width:100%">注 册</el-button>
        </el-form-item>
      </el-form>
      <p class="switch">已有账号？<router-link to="/login">去登录</router-link></p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', confirmPassword: '', phone: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value, cb) => value !== form.password ? cb(new Error('两次密码不一致')) : cb(), trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await register({ username: form.username, password: form.password, phone: form.phone })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally { loading.value = false }
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
