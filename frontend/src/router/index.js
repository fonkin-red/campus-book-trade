import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/book/:id',
    name: 'BookDetail',
    component: () => import('@/views/BookDetail.vue')
  },
  {
    path: '/publish',
    name: 'BookPublish',
    meta: { requiresAuth: true },
    component: () => import('@/views/BookPublish.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    meta: { requiresAuth: true },
    component: () => import('@/views/Cart.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    meta: { requiresAuth: true },
    component: () => import('@/views/Orders.vue')
  },
  {
    path: '/user',
    name: 'UserCenter',
    meta: { requiresAuth: true },
    component: () => import('@/views/UserCenter.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    meta: { requiresAuth: true, requiresAdmin: true },
    component: () => import('@/views/admin/Dashboard.vue'),
    children: [
      {
        path: '',
        redirect: '/admin/orders'
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue')
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/Categories.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue')
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: () => import('@/views/admin/Announcements.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (to.meta.requiresAuth && !token) {
    ElMessage.warning('请先登录')
    next('/login')
  } else if (to.meta.requiresAdmin && user.role !== 1) {
    ElMessage.warning('权限不足')
    next('/')
  } else {
    next()
  }
})

export default router
