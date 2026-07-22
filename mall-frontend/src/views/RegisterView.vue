<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <router-link to="/" class="auth-logo">
          <span class="logo-icon">&#x1f525;</span>
          <span class="logo-text">潮购</span>
        </router-link>
        <h2>创建账号</h2>
        <p class="subtitle">加入潮购，发现潮流好物</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" :prefix-icon="Phone" />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password" type="password" show-password
            placeholder="请设置密码（至少6位）" :prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword" type="password" show-password
            placeholder="请确认密码" :prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleRegister">
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Phone, User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({
  phone: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度 2-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await userStore.register({
      phone: form.phone,
      nickname: form.nickname,
      password: form.password
    })
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.message || '注册失败')
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
h2 { font-size: $font-size-xl; margin-bottom: $spacing-sm; }
.subtitle { color: $text-secondary; font-size: $font-size-sm; }
.submit-btn { width: 100%; }
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
