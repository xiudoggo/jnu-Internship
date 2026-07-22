<template>
  <div class="home-page">
    <div class="page-container">
      <!-- 轮播 Banner -->
      <BannerSwiper />

      <!-- 分类导航 -->
      <CategoryNav />

      <!-- 热门推荐 -->
      <section class="product-section">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon :size="22"><TrendCharts /></el-icon>
            热门推荐
          </h2>
          <router-link to="/products?sort=sales" class="more-link">查看更多 →</router-link>
        </div>
        <div class="product-grid">
          <ProductCard v-for="item in hotList" :key="item.id" :product="item" />
        </div>
        <el-skeleton v-if="hotLoading" :rows="2" animated />
      </section>

      <!-- 新品上架 -->
      <section class="product-section">
        <div class="section-header">
          <h2 class="section-title">
            <el-icon :size="22"><Present /></el-icon>
            新品上架
          </h2>
          <router-link to="/products?sort=new" class="more-link">查看更多 →</router-link>
        </div>
        <div class="product-grid">
          <ProductCard v-for="item in newList" :key="item.id" :product="item" />
        </div>
        <el-skeleton v-if="newLoading" :rows="2" animated />
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import BannerSwiper from '@/components/BannerSwiper.vue'
import CategoryNav from '@/components/CategoryNav.vue'
import ProductCard from '@/components/ProductCard.vue'

const hotList = ref([])
const newList = ref([])
const hotLoading = ref(true)
const newLoading = ref(true)

onMounted(async () => {
  // 并行请求
  const [hotRes, newRes] = await Promise.allSettled([
    axios.get('/api/product/hot'),
    axios.get('/api/product/new')
  ])

  if (hotRes.status === 'fulfilled' && hotRes.value.data.code === 200) {
    hotList.value = hotRes.value.data.data
  }
  hotLoading.value = false

  if (newRes.status === 'fulfilled' && newRes.value.data.code === 200) {
    newList.value = newRes.value.data.data
  }
  newLoading.value = false
})
</script>

<style lang="scss" scoped>
.page-container {
  @include page-container;
}
.product-section {
  margin-bottom: $spacing-xl;
}
.section-header {
  @include flex-between;
  margin-bottom: $spacing-md;
}
.section-title {
  @include flex-center;
  gap: $spacing-sm;
  font-size: $font-size-lg;
  font-weight: 600;
  color: $text-primary;
  .el-icon { color: $primary-color; }
}
.more-link {
  font-size: $font-size-sm;
  color: $text-secondary;
  &:hover { color: $primary-color; }
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-md;
}
@media (max-width: 1024px) {
  .product-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 480px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); gap: $spacing-sm; }
}
</style>
