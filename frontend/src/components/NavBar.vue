<template>
  <div class="navbar-wrapper">
    <div class="navbar-inner">
      <div class="nav-left">
        <div class="logo" @click="router.push('/')">
          <span class="logo-mark">书</span>
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
  background: rgba(255, 255, 255, 0.94);
  border-bottom: 1px solid rgba(132, 153, 160, 0.22);
  backdrop-filter: blur(10px);
}

.navbar-inner {
  display: flex;
  justify-content: space-between;
  width: min(1180px, calc(100% - 40px));
  height: 60px;
  margin: 0 auto;
}

.nav-left, .nav-right {
  display: flex;
  align-items: center;
}

.nav-menu {
  background: transparent !important;
  border-bottom: none !important;
  height: 60px;
}

.nav-menu :deep(.el-menu-item),
.nav-menu :deep(.el-sub-menu__title) {
  height: 52px;
  color: #475467 !important;
  font-weight: 600;
  border-radius: 8px !important;
  margin: 4px 2px;
}

.nav-menu :deep(.el-menu-item:hover),
.nav-menu :deep(.el-sub-menu__title:hover) {
  background: #e7f3ef !important;
  color: #24584c !important;
}

.nav-menu :deep(.el-menu-item.is-active) {
  background: #e7f3ef !important;
  color: #24584c !important;
  border-bottom-color: transparent !important;
}

.nav-menu :deep(.el-sub-menu.is-active .el-sub-menu__title) {
  border-bottom-color: transparent !important;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 800;
  color: #1f2933;
  cursor: pointer;
  padding: 0 20px 0 0;
  user-select: none;
}

.logo-mark {
  display: inline-grid;
  place-items: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  color: #fff;
  font-size: 16px;
  background: #2f6f5f;
  box-shadow: 0 8px 20px rgba(47, 111, 95, 0.18);
}

@media (max-width: 760px) {
  .navbar-inner {
    width: calc(100% - 24px);
  }
  .logo span:last-child {
    display: none;
  }
}
</style>
