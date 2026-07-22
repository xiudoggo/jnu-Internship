<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <router-link to="/" class="auth-logo">
          <span class="logo-icon">&#x1f525;</span>
          <span class="logo-text">潮购</span>
        </router-link>
        <h2>欢迎回来</h2>
        <p class="subtitle">登录您的潮购账号</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" :prefix-icon="Phone" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password" type="password" show-password
            placeholder="请输入密码" :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Phone, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

const formRef = ref(null)
const loading = ref(false)
const form = reactive({ phone: '13800000001', password: '123456' })

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await userStore.login(form.phone, form.password)
    if (res.code === 200) {
      ElMessage.success('登录成功')
      await cartStore.fetchCart()
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.auth-page {
  min-height: 100vh;
  @include flex-center;
  background: linear-gradient(135deg, lighten($primary-color, 35%) 0%, #fff 100%);
}
.auth-card {
  width: 400px;
  padding: $spacing-xl * 2;
  background: $bg-white;
  border-radius: $radius-lg;
  box-shadow: $shadow-card;
}
.auth-header {
  text-align: center;
  margin-bottom: $spacing-xl;
}
.auth-logo {
  @include flex-center;
  gap: 6px;
  margin-bottom: $spacing-md;
  .logo-icon { font-size: 32px; }
  .logo-text { font-size: $font-size-xxl; font-weight: 700; color: $primary-color; }
}
h2 {
  font-size: $font-size-xl;
  margin-bottom: $spacing-sm;
}
.subtitle {
  color: $text-secondary;
  font-size: $font-size-sm;
}
.submit-btn {
  width: 100%;
}
.auth-footer {
  text-align: center;
  font-size: $font-size-sm;
  color: $text-secondary;
  a { color: $primary-color; }
}
@media (max-width: 480px) {
  .auth-card { width: 90%; padding: $spacing-xl; }
}
</style>
