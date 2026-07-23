<template>
  <header class="nav-header">
    <div class="header-inner">
      <!-- Logo -->
      <router-link to="/" class="logo">
        <span class="logo-icon">&#x1f525;</span>
        <span class="logo-text">潮购</span>
      </router-link>

      <!-- 搜索 -->
      <div class="search-area">
        <SearchBox />
      </div>

      <!-- 右侧操作 -->
      <div class="header-actions">
        <router-link to="/cart" class="cart-btn">
          <el-badge :value="cartStore.totalCount" :hidden="cartStore.totalCount === 0" :max="99">
            <el-icon :size="24"><ShoppingCart /></el-icon>
          </el-badge>
          <span class="action-text">购物车</span>
        </router-link>
        <router-link to="/order/list" class="order-btn">
          <el-icon :size="22"><Tickets /></el-icon>
          <span class="action-text">我的订单</span>
        </router-link>
        <router-link to="/favorites" class="fav-btn">
          <el-icon :size="22"><Star /></el-icon>
          <span class="action-text">收藏</span>
        </router-link>

        <!-- 用户 -->
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <span class="user-area">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
              <span class="nickname">{{ userStore.userInfo?.nickname || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="userStore.userInfo?.role === 1" @click="$router.push('/admin')">
                  <el-icon><Setting /></el-icon> 后台管理
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/change-password')">
                  <el-icon><Edit /></el-icon> 修改密码
                </el-dropdown-item>
                <el-dropdown-item @click="userStore.logout()">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="login-btn">
            <el-button type="primary" size="small">登录</el-button>
          </router-link>
          <router-link to="/register" class="register-btn">
            <el-button size="small">注册</el-button>
          </router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import SearchBox from './SearchBox.vue'

const userStore = useUserStore()
const cartStore = useCartStore()

onMounted(() => {
  if (userStore.isLoggedIn) {
    cartStore.fetchCart()
  }
})
</script>

<style lang="scss" scoped>
.nav-header {
  height: $header-height;
  background: $bg-white;
  box-shadow: $shadow-header;
  position: sticky;
  top: 0;
  z-index: 1000;
}
.header-inner {
  @include page-container;
  @include flex-between;
  height: 100%;
  gap: $spacing-lg;
}
.logo {
  @include flex-center;
  gap: 6px;
  flex-shrink: 0;
  .logo-icon { font-size: 24px; }
  .logo-text {
    font-size: $font-size-xl;
    font-weight: 700;
    color: $primary-color;
  }
}
.search-area {
  flex: 1;
  max-width: 500px;
}
.header-actions {
  @include flex-center;
  gap: $spacing-md;
  flex-shrink: 0;
  a {
    @include flex-center;
    flex-direction: column;
    gap: 2px;
    color: $text-regular;
    font-size: $font-size-xs;
    &:hover { color: $primary-color; }
  }
}
.user-area {
  @include flex-center;
  gap: 6px;
  cursor: pointer;
  .nickname { font-size: $font-size-sm; }
}
@media (max-width: 768px) {
  .search-area { display: none; }
  .order-btn, .fav-btn { display: none; }
}
</style>
