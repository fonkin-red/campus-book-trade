import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home'
    // component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login'
    // component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register'
    // component: () => import('@/views/Register.vue')
  },
  {
    path: '/book/:id',
    name: 'BookDetail'
    // component: () => import('@/views/BookDetail.vue')
  },
  {
    path: '/publish',
    name: 'BookPublish',
    meta: { requiresAuth: true }
    // component: () => import('@/views/BookPublish.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    meta: { requiresAuth: true }
    // component: () => import('@/views/Cart.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    meta: { requiresAuth: true }
    // component: () => import('@/views/Orders.vue')
  },
  {
    path: '/user',
    name: 'UserCenter',
    meta: { requiresAuth: true }
    // component: () => import('@/views/UserCenter.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    meta: { requiresAuth: true, requiresAdmin: true },
    // component: () => import('@/views/admin/Dashboard.vue'),
    children: [
      {
        path: 'users',
        name: 'AdminUsers'
        // component: () => import('@/views/admin/Users.vue')
      },
      {
        path: 'categories',
        name: 'AdminCategories'
        // component: () => import('@/views/admin/Categories.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders'
        // component: () => import('@/views/admin/Orders.vue')
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements'
        // component: () => import('@/views/admin/Announcements.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// TODO: 组员补充路由守卫（登录校验、管理员权限校验）

export default router
