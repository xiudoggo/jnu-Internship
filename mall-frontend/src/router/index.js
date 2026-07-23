import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/HomeView.vue'),
        meta: { title: '潮购 - 首页' }
      },
      {
        path: 'products',
        name: 'ProductList',
        component: () => import('@/views/ProductListView.vue'),
        meta: { title: '商品列表' }
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/ProductDetailView.vue'),
        meta: { title: '商品详情' }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/CartView.vue'),
        meta: { title: '购物车', requiresAuth: true }
      },
      {
        path: 'order/confirm',
        name: 'OrderConfirm',
        component: () => import('@/views/OrderConfirmView.vue'),
        meta: { title: '确认订单', requiresAuth: true }
      },
      {
        path: 'order/list',
        name: 'OrderList',
        component: () => import('@/views/OrderListView.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        component: () => import('@/views/OrderDetailView.vue'),
        meta: { title: '订单详情', requiresAuth: true }
      },
      {
        path: 'search',
        name: 'Search',
        component: () => import('@/views/SearchView.vue'),
        meta: { title: '搜索' }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('@/views/FavoriteView.vue'),
        meta: { title: '我的收藏', requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterView.vue'),
    meta: { title: '注册' }
  },
  // ==================== 后台管理 ====================
  {
    path: '/admin',
    component: () => import('@/components/AdminLayout.vue'),
    meta: { requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '数据统计 - 后台' }
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/ProductManage.vue'),
        meta: { title: '商品管理 - 后台' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理 - 后台' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/OrderManage.vue'),
        meta: { title: '订单管理 - 后台' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 登录守卫 + 管理员守卫（Token 由 HttpOnly Cookie 管理，前端通过 userInfo 判断登录状态）
router.beforeEach((to, from, next) => {
  let userInfo = null
  try {
    userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
  } catch (e) { /* ignore */ }

  const isLoggedIn = !!userInfo

  // 需要登录
  if (to.meta.requiresAuth && !isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  // 需要管理员权限
  if (to.meta.requiresAdmin) {
    if (!isLoggedIn) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
    if (userInfo.role !== 1) {
      next({ name: 'Home' })
      return
    }
  }

  next()
  document.title = to.meta.title || '潮购'
})

export default router
