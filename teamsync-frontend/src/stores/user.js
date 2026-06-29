import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import { login as loginApi, register as registerApi } from '../api/auth'
import { getCurrentUser } from '../api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const theme = ref(localStorage.getItem('theme') || 'light')

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isDark = computed(() => theme.value === 'dark')

  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
  }

  watch(theme, (newTheme) => {
    localStorage.setItem('theme', newTheme)
    if (newTheme === 'dark') {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }, { immediate: true })

  async function login(form) {
    const res = await loginApi(form)
    token.value = res.data.token
    user.value = res.data.user
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    return res
  }

  async function register(form) {
    return await registerApi(form)
  }

  async function fetchUser() {
    const res = await getCurrentUser()
    user.value = res.data
    localStorage.setItem('user', JSON.stringify(res.data))
    return res.data
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, theme, isLoggedIn, isAdmin, isDark, toggleTheme, login, register, fetchUser, logout }
})
