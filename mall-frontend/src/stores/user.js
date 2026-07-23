import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import router from '@/router'
import axios from 'axios'
import { useCartStore } from '@/stores/cart'

export const useUserStore = defineStore('user', () => {
  // Token 由后端 HttpOnly Cookie 管理，前端只保存 userInfo 用于 UI 展示和路由守卫
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!userInfo.value)

  async function login(phone, password) {
    const res = await axios.post('/api/user/login', { phone, password })
    if (res.data.code === 200) {
      userInfo.value = res.data.data.userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
    return res.data
  }

  async function register(form) {
    const res = await axios.post('/api/user/register', form)
    return res.data
  }

  async function fetchUserInfo() {
    const res = await axios.get('/api/user/info')
    if (res.data.code === 200) {
      userInfo.value = res.data.data
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
    return res.data
  }

  async function logout() {
    // 调用后端接口清除 Cookie
    await axios.post('/api/user/logout')
    userInfo.value = null
    localStorage.removeItem('userInfo')
    // 清空购物车状态
    useCartStore().clearCart()
    // 跳转到首页
    router.push('/')
  }

  return { userInfo, isLoggedIn, login, register, fetchUserInfo, logout }
})
