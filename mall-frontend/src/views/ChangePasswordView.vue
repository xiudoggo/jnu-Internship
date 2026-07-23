<template>
  <div class="change-pwd-page">
    <div class="page-card">
      <h2>修改密码</h2>

      <!-- ============ 步骤 1：验证原密码 ============ -->
      <template v-if="step === 1">
        <p class="subtitle">请先输入原密码进行身份验证</p>
        <el-form ref="formRef1" :model="form1" :rules="rules1" label-width="0" size="large">
          <el-form-item prop="oldPassword">
            <el-input
              v-model="form1.oldPassword" type="password" show-password
              placeholder="请输入原密码" :prefix-icon="Lock"
              @keyup.enter="handleVerify"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="submit-btn" :loading="verifying" @click="handleVerify">
              验证身份
            </el-button>
          </el-form-item>
        </el-form>
      </template>

      <!-- ============ 步骤 2：设置新密码 ============ -->
      <template v-else>
        <p class="subtitle">
          <el-icon class="verified-icon"><CircleCheckFilled /></el-icon>
          身份验证通过，请设置新密码
        </p>
        <el-form ref="formRef2" :model="form2" :rules="rules2" label-width="0" size="large">
          <el-form-item prop="newPassword">
            <el-input
              v-model="form2.newPassword" type="password" show-password
              placeholder="请输入新密码（至少6位）" :prefix-icon="Key"
            />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form2.confirmPassword" type="password" show-password
              placeholder="请再次输入新密码" :prefix-icon="CircleCheck"
              @keyup.enter="handleSubmit"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="submit-btn" :loading="submitting" @click="handleSubmit">
              确认修改
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-button class="submit-btn" @click="step = 1">返回上一步</el-button>
          </el-form-item>
        </el-form>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, Key, CircleCheck, CircleCheckFilled } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

const step = ref(1)
const verifying = ref(false)
const submitting = ref(false)

const formRef1 = ref(null)
const formRef2 = ref(null)

const form1 = reactive({ oldPassword: '' })
const form2 = reactive({ newPassword: '', confirmPassword: '' })

const rules1 = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ]
}

const validateConfirm = (_rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入新密码'))
  } else if (value !== form2.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
  } else {
    callback()
  }
}

const rules2 = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码至少6位', trigger: 'blur' },
    { validator: (_rule, value, callback) => {
      if (value && value === form1.oldPassword) {
        callback(new Error('新密码不能与原密码相同'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

// 步骤 1：验证原密码
async function handleVerify() {
  const valid = await formRef1.value.validate().catch(() => false)
  if (!valid) return

  verifying.value = true
  try {
    const res = await axios.post('/api/user/password/verify', {
      oldPassword: form1.oldPassword
    })
    if (res.data.code === 200) {
      step.value = 2
    } else {
      ElMessage.error(res.data.message || '原密码错误')
    }
  } catch {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    verifying.value = false
  }
}

// 步骤 2：提交新密码
async function handleSubmit() {
  const valid = await formRef2.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const res = await axios.put('/api/user/password', {
      oldPassword: form1.oldPassword,
      newPassword: form2.newPassword
    })
    if (res.data.code === 200) {
      // 清除本地存储中该用户的已保存密码
      clearSavedPassword()
      ElMessage.success('密码修改成功，请重新登录')
      localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      ElMessage.error(res.data.message || '修改失败')
    }
  } catch {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 清除 loginHistory 中对应用户的已存密码
function clearSavedPassword() {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
    if (!userInfo) return
    const history = JSON.parse(localStorage.getItem('loginHistory') || '[]')
    const idx = history.findIndex(u => u.phone === userInfo.phone)
    if (idx !== -1) {
      delete history[idx].password
      localStorage.setItem('loginHistory', JSON.stringify(history))
    }
  } catch { /* ignore */ }
}
</script>

<style lang="scss" scoped>
.change-pwd-page {
  min-height: calc(100vh - #{$header-height} - 200px);
  @include flex-center;
  padding: $spacing-xl;
}
.page-card {
  width: 420px;
  padding: $spacing-xl * 2;
  background: $bg-white;
  border-radius: $radius-lg;
  box-shadow: $shadow-card;
}
h2 {
  font-size: $font-size-xl;
  text-align: center;
  margin-bottom: $spacing-sm;
}
.subtitle {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  text-align: center;
  color: $text-secondary;
  font-size: $font-size-sm;
  margin-bottom: $spacing-xl;
}
.verified-icon {
  color: #67c23a;
  font-size: 18px;
}
.submit-btn {
  width: 100%;
}
@media (max-width: 480px) {
  .page-card { width: 90%; padding: $spacing-xl; }
}
</style>
