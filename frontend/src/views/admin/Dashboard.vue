<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <div class="admin-sidebar" :class="{ collapsed: isCollapse }">
      <div class="logo-area">
        <span class="logo-text" v-if="!isCollapse">管理后台</span>
        <span class="logo-text-small" v-else>后台</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        router
        :collapse="isCollapse"
        background-color="#ffffff"
        text-color="#475467"
        active-text-color="#24584c"
      >
        <el-menu-item index="/admin/orders">
          <el-icon><Document /></el-icon>
          <template #title>订单管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Grid /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/announcements">
          <el-icon><Bell /></el-icon>
          <template #title>公告管理</template>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 右侧区域 -->
    <div class="admin-right">
      <!-- 顶部 Header（仅保留标题和折叠按钮） -->
      <div class="admin-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse" :size="20">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span class="header-title">{{ currentPageTitle }}</span>
        </div>
      </div>

      <!-- 子路由渲染区域 -->
      <div class="admin-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  Document, User, Grid, Bell,
  Fold, Expand
} from '@element-plus/icons-vue'

const route = useRoute()

const isCollapse = ref(false)

// 侧边栏当前激活菜单
const activeMenu = computed(() => {
  const path = route.path
  // 精确匹配子路由路径
  if (path.startsWith('/admin/orders')) return '/admin/orders'
  if (path.startsWith('/admin/users')) return '/admin/users'
  if (path.startsWith('/admin/categories')) return '/admin/categories'
  if (path.startsWith('/admin/announcements')) return '/admin/announcements'
  return path
})

// 当前页面标题
const currentPageTitle = computed(() => {
  const map = {
    '/admin/orders': '订单管理',
    '/admin/users': '用户管理',
    '/admin/categories': '分类管理',
    '/admin/announcements': '公告管理'
  }
  // 遍历找到匹配的
  for (const [key, value] of Object.entries(map)) {
    if (route.path.startsWith(key)) return value
  }
  return '管理后台'
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: calc(100vh - 60px);
}

/* 侧边栏 */
.admin-sidebar {
  width: 220px;
  background: #fff;
  border-right: 1px solid rgba(132, 153, 160, 0.22);
  transition: width 0.3s;
  flex-shrink: 0;
}
.admin-sidebar.collapsed {
  width: 64px;
}

.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(132, 153, 160, 0.22);
}
.logo-text {
  color: #1f2933;
  font-size: 18px;
  font-weight: bold;
}
.logo-text-small {
  color: #1f2933;
  font-size: 14px;
  font-weight: bold;
}

/* 右侧 */
.admin-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部栏 */
.admin-header {
  height: 50px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid rgba(132, 153, 160, 0.22);
  box-shadow: none;
  flex-shrink: 0;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.header-title {
  font-size: 16px;
  font-weight: 500;
}
.collapse-btn {
  cursor: pointer;
  color: #666;
}
.collapse-btn:hover {
  color: #2f6f5f;
}

/* 内容区 */
.admin-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f6f8f9;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item) {
  margin: 4px 10px;
  border-radius: 8px;
}

:deep(.el-menu-item.is-active) {
  background: #e7f3ef;
}
</style>
