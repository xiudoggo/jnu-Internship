<template>
  <div class="detail-page">
    <div class="page-container" v-if="product">
      <!-- 面包屑 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/products', query: { categoryId: product.categoryId } }">
          {{ product.categoryName }}
        </el-breadcrumb-item>
        <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 商品主体 -->
      <div class="detail-main">
        <!-- 左侧图片 -->
        <div class="gallery">
          <el-carousel :interval="4000" height="420px" arrow="hover" trigger="click">
            <el-carousel-item v-for="(img, idx) in product.images" :key="idx">
              <img :src="img" class="gallery-img" />
            </el-carousel-item>
          </el-carousel>
        </div>

        <!-- 右侧信息 -->
        <div class="info">
          <h1 class="product-name">{{ product.name }}</h1>
          <p class="product-desc">{{ product.description }}</p>

          <div class="price-area">
            <span class="price-current">&yen;{{ Number(product.price).toFixed(2) }}</span>
            <span class="price-original" v-if="Number(product.originalPrice) > Number(product.price)">
              &yen;{{ Number(product.originalPrice).toFixed(2) }}
            </span>
          </div>

          <div class="meta-row">
            <span>销量：{{ product.sales }}</span>
            <span>库存：{{ product.stock }}</span>
            <span>评分：
              <el-rate v-model="product.rating" disabled show-score text-color="#ff9900" />
            </span>
          </div>

          <!-- 数量选择 -->
          <div class="quantity-row">
            <span class="label">数量：</span>
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="product.stock"
              size="large"
            />
          </div>

          <!-- 操作按钮 -->
          <div class="action-row">
            <el-button type="primary" size="large" class="btn-cart" @click="addToCart">
              <el-icon><ShoppingCart /></el-icon> 加入购物车
            </el-button>
            <el-button type="danger" size="large" class="btn-buy" @click="buyNow">
              立即购买
            </el-button>
            <el-button size="large" circle class="btn-fav" @click="toggleFav">
              <el-icon :size="20" :color="isFav ? '#e4393c' : ''">
                <StarFilled v-if="isFav" /><Star v-else />
              </el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <!-- Tab：详情 / 评论 -->
      <div class="detail-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="商品详情" name="detail">
            <div class="detail-content">
              <div v-for="(img, idx) in product.images" :key="idx" class="detail-img-wrap">
                <img :src="img" class="detail-img" />
              </div>
              <p class="detail-text">{{ product.description }}</p>
            </div>
          </el-tab-pane>
          <el-tab-pane label="商品评论" name="reviews">
            <div class="review-list" v-if="reviews.length > 0">
              <div v-for="rv in reviews" :key="rv.id" class="review-item">
                <div class="review-header">
                  <img :src="rv.avatar" class="review-avatar" />
                  <span class="review-nickname">{{ rv.nickname }}</span>
                  <el-rate v-model="rv.star" disabled size="small" />
                  <span class="review-time">{{ rv.createTime }}</span>
                </div>
                <p class="review-content">{{ rv.content }}</p>
              </div>
            </div>
            <el-empty v-else description="暂无评论" />
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 加载态 -->
    <div class="page-container" v-else>
      <el-skeleton :rows="8" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const product = ref(null)
const reviews = ref([])
const quantity = ref(1)
const isFav = ref(false)
const activeTab = ref('detail')

onMounted(async () => {
  const id = route.params.id
  try {
    const res = await axios.get('/api/product/' + id)
    if (res.data.code === 200) {
      const p = res.data.data
      // images 从 MySQL 返回的是 JSON 字符串，需解析为数组
      if (typeof p.images === 'string') {
        try { p.images = JSON.parse(p.images) } catch (e) { p.images = [] }
      }
      // 确保价格是数字
      p.price = Number(p.price)
      p.originalPrice = Number(p.originalPrice)
      product.value = p
    }
  } catch {
    ElMessage.error('商品不存在')
  }
  // 加载评论
  try {
    const rvRes = await axios.get('/api/product/' + id + '/reviews')
    if (rvRes.data.code === 200) reviews.value = rvRes.data.data
  } catch { /* */ }
})

function addToCart() {
  if (!product.value) return
  cartStore.addToCart({
    id: product.value.id,
    name: product.value.name,
    coverImage: product.value.coverImage,
    price: product.value.price
  }, quantity.value)
  ElMessage.success('已加入购物车')
}

function buyNow() {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  addToCart()
  router.push('/cart')
}

function toggleFav() {
  isFav.value = !isFav.value
  ElMessage.success(isFav.value ? '已收藏' : '已取消收藏')
  if (product.value) {
    axios.post('/api/favorite/toggle', {
      productId: product.value.id,
      name: product.value.name,
      image: product.value.coverImage,
      price: product.value.price
    }).catch(() => {})
  }
}
</script>

<style lang="scss" scoped>
.page-container { @include page-container; }
.breadcrumb { margin-bottom: $spacing-md; }
.detail-main {
  display: flex;
  gap: $spacing-xl;
  background: $bg-white;
  padding: $spacing-xl;
  border-radius: $radius-lg;
  margin-bottom: $spacing-lg;
}
.gallery {
  width: 420px;
  flex-shrink: 0;
  .gallery-img { width: 100%; height: 100%; object-fit: cover; border-radius: $radius-md; }
}
.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}
.product-name {
  font-size: $font-size-xl;
  font-weight: 600;
}
.product-desc {
  color: $text-secondary;
  font-size: $font-size-sm;
}
.price-area {
  background: lighten($price-color, 48%);
  padding: $spacing-md;
  border-radius: $radius-md;
  .price-current {
    color: $price-color;
    font-size: $font-size-title;
    font-weight: 700;
  }
  .price-original {
    color: $text-placeholder;
    text-decoration: line-through;
    margin-left: $spacing-md;
    font-size: $font-size-base;
  }
}
.meta-row {
  @include flex-center;
  gap: $spacing-lg;
  font-size: $font-size-sm;
  color: $text-secondary;
}
.quantity-row {
  @include flex-center;
  gap: $spacing-md;
  .label { font-size: $font-size-base; color: $text-regular; }
}
.action-row {
  display: flex;
  gap: $spacing-md;
  margin-top: auto;
}
.btn-cart, .btn-buy {
  width: 160px;
  height: 48px;
  font-size: $font-size-md;
}
.detail-tabs {
  background: $bg-white;
  padding: $spacing-lg;
  border-radius: $radius-lg;
}
.detail-content {
  max-width: 800px;
  margin: 0 auto;
}
.detail-img-wrap {
  margin-bottom: $spacing-md;
}
.detail-img {
  width: 100%;
  border-radius: $radius-sm;
}
.review-item {
  padding: $spacing-md 0;
  border-bottom: 1px solid $border-light;
}
.review-header {
  @include flex-center;
  gap: $spacing-sm;
  margin-bottom: $spacing-sm;
}
.review-avatar {
  width: 32px; height: 32px;
  border-radius: 50%;
  object-fit: cover;
}
.review-nickname { font-size: $font-size-sm; font-weight: 500; }
.review-time { color: $text-placeholder; font-size: $font-size-xs; margin-left: auto; }
.review-content { font-size: $font-size-base; color: $text-regular; }

@media (max-width: 768px) {
  .detail-main { flex-direction: column; }
  .gallery { width: 100%; }
  .action-row { flex-wrap: wrap; }
}
</style>
