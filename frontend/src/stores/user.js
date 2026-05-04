import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, removeToken, setUser, removeUser } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value.role === 1)
  const displayName = computed(() => user.value.nickname || user.value.username || '')

  function loginSuccess(data) {
    token.value = data.token
    user.value = data.user
    setToken(data.token)
    setUser(data.user)
  }

  function logout() {
    token.value = ''
    user.value = {}
    removeToken()
    removeUser()
  }

  function updateUserInfo(info) {
    user.value = { ...user.value, ...info }
    setUser(user.value)
  }

  return { token, user, isLoggedIn, isAdmin, displayName, loginSuccess, logout, updateUserInfo }
})
