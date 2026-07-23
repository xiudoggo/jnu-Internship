<template>
  <div class="admin-layout">
    <!-- 左侧菜单 -->
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <router-link to="/" class="sidebar-logo">
          <span class="logo-icon">&#x1f525;</span>
          <span class="logo-text">潮购后台</span>
        </router-link>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        class="sidebar-menu"
      >
        <el-menu-item index="/admin/products">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><Tickets /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <router-link to="/" class="back-link">
          <el-icon><Back /></el-icon>
          <span>返回前台</span>
        </router-link>
      </div>
    </aside>

    <!-- 右侧内容 -->
    <section class="admin-main">
      <header class="admin-header">
        <div class="header-left">
          <span class="header-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <span class="admin-name">{{ userStore.userInfo?.nickname || '管理员' }}</span>
          <el-button type="danger" size="small" plain @click="userStore.logout()">退出</el-button>
        </div>
      </header>
      <div class="admin-content">
        <router-view />
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '后台管理')
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}
.admin-sidebar {
  position: fixed;
  top: 0;
  left: 0;
  width: 220px;
  height: 100vh;
  overflow-y: auto;
  background: #304156;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  z-index: 100;
  .sidebar-header {
    height: 60px;
    @include flex-center;
    .sidebar-logo {
      @include flex-center;
      gap: 6px;
      .logo-icon { font-size: 22px; }
      .logo-text { font-size: 18px; font-weight: 700; color: #fff; }
    }
  }
  .sidebar-menu {
    flex: 1;
    border-right: none;
  }
  .sidebar-footer {
    padding: 12px 20px;
    border-top: 1px solid rgba(255,255,255,0.1);
    .back-link {
      @include flex-center;
      gap: 6px;
      color: #bfcbd9;
      font-size: $font-size-sm;
      &:hover { color: #fff; }
    }
  }
}
.admin-main {
  margin-left: 220px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  overflow: hidden;
}
.admin-header {
  position: sticky;
  top: 0;
  z-index: 99;
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  @include flex-between;
  padding: 0 $spacing-xl;
  flex-shrink: 0;
  .header-title {
    font-size: $font-size-lg;
    font-weight: 600;
    color: $text-primary;
  }
  .header-right {
    @include flex-center;
    gap: $spacing-md;
    .admin-name { color: $text-regular; }
  }
}
.admin-content {
  flex: 1;
  padding: $spacing-xl;
}
</style>
