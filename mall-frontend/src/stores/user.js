import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)

  async function login(phone, password) {
    const res = await axios.post('/api/user/login', { phone, password })
    if (res.data.code === 200) {
      token.value = res.data.data.token
      userInfo.value = res.data.data.userInfo
      localStorage.setItem('token', token.value)
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

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, isLoggedIn, login, register, fetchUserInfo, logout }
})
