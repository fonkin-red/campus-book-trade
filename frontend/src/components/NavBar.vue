<template>
  <div class="navbar-wrapper">
    <div class="navbar-inner">
      <div class="nav-left">
        <div class="logo" @click="router.push('/')">
          <span>校园二手图书</span>
        </div>
        <el-menu :default-active="route.path" mode="horizontal" router :ellipsis="false" class="nav-menu">
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item v-if="userStore.isLoggedIn" index="/cart">购物车</el-menu-item>
          <el-menu-item v-if="userStore.isLoggedIn" index="/orders">我的订单</el-menu-item>
          <el-menu-item v-if="userStore.isAdmin" index="/admin/orders">后台管理</el-menu-item>
        </el-menu>
      </div>
      <div class="nav-right">
        <el-menu :default-active="route.path" mode="horizontal" router :ellipsis="false" class="nav-menu">
          <template v-if="userStore.isLoggedIn">
            <el-sub-menu index="user">
              <template #title>
                <el-icon><User /></el-icon>
                <span>{{ userStore.displayName }}</span>
              </template>
              <el-menu-item index="/user">个人中心</el-menu-item>
              <el-menu-item index="/publish">发布图书</el-menu-item>
              <el-menu-item @click="handleLogout">退出登录</el-menu-item>
            </el-sub-menu>
          </template>
          <template v-else>
            <el-menu-item index="/login">登录</el-menu-item>
            <el-menu-item index="/register">注册</el-menu-item>
          </template>
        </el-menu>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar-wrapper {
  position: sticky;
  top: 0;
  z-index: 100;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.navbar-inner {
  display: flex;
  justify-content: space-between;
  padding: 0 40px;
}

.nav-left, .nav-right {
  display: flex;
  align-items: center;
}

.nav-menu {
  background: transparent !important;
  border-bottom: none !important;
}

.nav-menu :deep(.el-menu-item),
.nav-menu :deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.9) !important;
  font-weight: 500;
  border-radius: 8px !important;
  margin: 4px 2px;
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.15) !important;
  color: #fff !important;
}

.nav-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.2) !important;
  color: #fff !important;
  border-bottom-color: transparent !important;
}

.nav-menu :deep(.el-sub-menu.is-active .el-sub-menu__title) {
  border-bottom-color: transparent !important;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  cursor: pointer;
  padding: 0 20px 0 0;
  user-select: none;
}
</style>
