<template>
  <div class="user-manage">
    <div class="page-toolbar">
      <h3>用户管理</h3>
      <div class="toolbar-right">
        <el-button type="primary" @click="openCreateDialog">
          <el-icon><Plus /></el-icon> 新增用户
        </el-button>
        <el-button @click="fetchUsers">
          <el-icon><RefreshRight /></el-icon> 刷新
        </el-button>
      </div>
    </div>

    <el-table :data="users" border stripe v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="头像" width="70">
        <template #default="{ row }">
          <el-avatar :size="36" :src="row.avatar" />
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="昵称" width="100" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
      <el-table-column label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 1 ? 'danger' : 'info'" size="small">
            {{ row.role === 1 ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" @click="openPwdDialog(row)">密码</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingUser ? '编辑用户' : '新增用户'" width="480px" :close-on-click-modal="false">
      <el-form :model="form" label-width="80px">
        <el-form-item label="昵称" required>
          <el-input v-model="form.nickname" placeholder="昵称" />
        </el-form-item>
        <el-form-item label="手机号" required>
          <el-input v-model="form.phone" placeholder="手机号" />
        </el-form-item>
        <el-form-item v-if="!editingUser" label="密码" required>
          <el-input v-model="form.password" placeholder="密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="邮箱" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width:100%">
            <el-option :value="0" label="普通用户" />
            <el-option :value="1" label="管理员" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="pwdVisible" title="修改密码" width="400px" :close-on-click-modal="false">
      <el-form :model="pwdForm" label-width="80px">
        <p style="margin-bottom:12px;color:#666">
          用户：<b>{{ pwdTarget?.nickname }}</b>（{{ pwdTarget?.phone }}）
        </p>
        <el-form-item label="新密码" required>
          <el-input v-model="pwdForm.password" placeholder="请输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePwdSave">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const users = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editingUser = ref(null)
const form = reactive({ nickname: '', phone: '', password: '', email: '', role: 0 })

const pwdVisible = ref(false)
const pwdTarget = ref(null)
const pwdForm = reactive({ password: '' })

async function fetchUsers() {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/user/list')
    if (res.data.code === 200) users.value = res.data.data
  } catch (e) {
    ElMessage.error('加载失败')
  } finally { loading.value = false }
}

function openCreateDialog() {
  editingUser.value = null
  Object.assign(form, { nickname: '', phone: '', password: '', email: '', role: 0 })
  dialogVisible.value = true
}

function openEditDialog(user) {
  editingUser.value = user
  Object.assign(form, { nickname: user.nickname || '', phone: user.phone || '', password: '', email: user.email || '', role: user.role || 0 })
  dialogVisible.value = true
}

async function handleSave() {
  if (!form.nickname || !form.phone) { ElMessage.warning('昵称和手机号不能为空'); return }
  if (!editingUser.value && !form.password) { ElMessage.warning('密码不能为空'); return }
  try {
    if (editingUser.value) {
      await axios.put(`/api/admin/user/${editingUser.value.id}`, form)
      // 本地更新
      const idx = users.value.findIndex(u => u.id === editingUser.value.id)
      if (idx !== -1) Object.assign(users.value[idx], form)
      ElMessage.success('更新成功')
    } else {
      const res = await axios.post('/api/admin/user', form)
      if (res.data.code === 200 && res.data.data) {
        users.value.unshift(res.data.data)
      }
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

function openPwdDialog(user) {
  pwdTarget.value = user
  pwdForm.password = ''
  pwdVisible.value = true
}

async function handlePwdSave() {
  if (!pwdForm.password) { ElMessage.warning('密码不能为空'); return }
  try {
    await axios.put(`/api/admin/user/${pwdTarget.value.id}/password`, { password: pwdForm.password })
    ElMessage.success('密码已更新')
    pwdVisible.value = false
  } catch (e) {
    ElMessage.error('修改失败')
  }
}

async function handleDelete(id) {
  try {
    await axios.delete(`/api/admin/user/${id}`)
    users.value = users.value.filter(u => u.id !== id)
    ElMessage.success('已删除')
  } catch (e) { ElMessage.error('删除失败') }
}

onMounted(() => fetchUsers())
</script>

<style lang="scss" scoped>
.user-manage {
  background: #fff; border-radius: 4px; padding: 20px; min-height: calc(100vh - 160px);
}
.page-toolbar {
  @include flex-between; margin-bottom: 20px;
  h3 { font-size: 18px; font-weight: 600; margin: 0; }
  .toolbar-right { @include flex-center; gap: 10px; }
}
</style>
