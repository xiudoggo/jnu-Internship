<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-logo">
          <span class="logo-icon">&#x1f525;</span>
          <span class="logo-text">潮购</span>
        </div>
        <h2>欢迎回来</h2>
        <p class="subtitle">登录您的潮购账号</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <!-- 历史用户自动补全 -->
        <el-form-item prop="phone">
          <el-autocomplete
            v-model="form.phone"
            :fetch-suggestions="queryHistory"
            :trigger-on-focus="true"
            :placeholder="historyUsers.length ? '输入手机号或点击选择历史账号' : '请输入手机号'"
            :prefix-icon="Phone"
            clearable
            @select="onSelectHistory"
            @keyup.enter="handleLogin"
          >
            <template #default="{ item }">
              <div class="history-item">
                <el-avatar :size="28" :src="item.avatar" />
                <div class="history-info">
                  <span class="history-nickname">{{ item.nickname }}</span>
                  <span class="history-phone">{{ item.phone }}</span>
                </div>
                <el-tag v-if="item.hasPassword" size="small" type="success" effect="plain">已存密码</el-tag>
              </div>
            </template>
          </el-autocomplete>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password" type="password" show-password
            placeholder="请输入密码" :prefix-icon="Lock"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <!-- 记住密码 -->
        <el-form-item class="remember-row">
          <el-checkbox v-model="form.rememberPassword">记住密码</el-checkbox>
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Phone, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

const HISTORY_KEY = 'loginHistory'
const MAX_HISTORY = 5

const formRef = ref(null)
const loading = ref(false)
const form = reactive({
  phone: '',
  password: '',
  rememberPassword: false
})

const historyUsers = ref([])

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

// 加载历史用户
function loadHistory() {
  try {
    historyUsers.value = JSON.parse(localStorage.getItem(HISTORY_KEY) || '[]')
  } catch {
    historyUsers.value = []
  }
}

// 保存/更新历史用户
function saveHistory(phone, nickname, avatar, password) {
  const list = historyUsers.value.filter(u => u.phone !== phone)
  const entry = { phone, nickname, avatar }
  if (password) {
    // base64 编码存储（仅用于本地回填，非安全加密）
    entry.password = btoa(encodeURIComponent(password))
  }
  list.unshift(entry)
  // 最多保留 MAX_HISTORY 条
  historyUsers.value = list.slice(0, MAX_HISTORY)
  localStorage.setItem(HISTORY_KEY, JSON.stringify(historyUsers.value))
}

// autocomplete 查询回调
function queryHistory(queryString, cb) {
  const results = historyUsers.value
    .filter(u => u.phone.includes(queryString) || u.nickname.includes(queryString))
    .map(u => ({
      ...u,
      value: u.phone,
      hasPassword: !!u.password
    }))
  cb(results)
}

// 选择历史用户
function onSelectHistory(item) {
  form.phone = item.phone
  // 清除手机号字段的校验提示
  formRef.value?.clearValidate('phone')
  if (item.password) {
    try {
      form.password = decodeURIComponent(atob(item.password))
    } catch {
      form.password = ''
    }
    form.rememberPassword = true
  } else {
    form.password = ''
    form.rememberPassword = false
  }
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await userStore.login(form.phone, form.password)
    if (res.code === 200) {
      // 保存登录历史
      const userInfo = res.data.userInfo
      const savePwd = form.rememberPassword ? form.password : null
      saveHistory(form.phone, userInfo.nickname, userInfo.avatar, savePwd)

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

onMounted(() => {
  loadHistory()
  // 如果有历史用户，默认填充最近一个（不填密码，需用户手动输入或选择）
  if (historyUsers.value.length > 0) {
    const last = historyUsers.value[0]
    form.phone = last.phone
  }
})
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

// 历史用户下拉项
.history-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 0;
  .history-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 2px;
    .history-nickname {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }
    .history-phone {
      font-size: 12px;
      color: #909399;
    }
  }
}

// 记住密码行
.remember-row {
  margin-bottom: 4px;
}

@media (max-width: 480px) {
  .auth-card { width: 90%; padding: $spacing-xl; }
}
</style>
