<template>
  <div class="search-page">
    <div class="page-container">
      <p class="search-hint" v-if="route.query.q">
        搜索 "<em>{{ route.query.q }}</em>" ，共找到 <em>{{ total }}</em> 件商品
      </p>

      <!-- 商品网格 -->
      <div v-if="list.length > 0" class="product-grid">
        <ProductCard v-for="item in list" :key="item.id" :product="item" />
      </div>
      <el-empty v-else-if="!loading" description="未找到相关商品">
        <p style="color: #999; font-size: 13px">试试其他关键词吧</p>
      </el-empty>

      <!-- 分页 -->
      <div class="pagination-wrap" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          background
          @current-change="fetchResults"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import ProductCard from '@/components/ProductCard.vue'

const route = useRoute()

const list = ref([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const pageSize = 12

async function fetchResults() {
  const q = route.query.q
  if (!q) { list.value = []; total.value = 0; return }
  loading.value = true
  try {
    const res = await axios.get('/api/search', {
      params: { q, page: page.value, pageSize }
    })
    if (res.data.code === 200) {
      list.value = res.data.data.list
      total.value = res.data.data.total
    }
  } finally {
    loading.value = false
  }
}

watch(() => route.query.q, (q) => {
  page.value = 1
  fetchResults()
}, { immediate: true })
</script>

<style lang="scss" scoped>
.page-container { @include page-container; }
.search-hint {
  margin-bottom: $spacing-lg;
  font-size: $font-size-sm;
  color: $text-secondary;
  em { color: $primary-color; font-style: normal; font-weight: 600; }
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-md;
  margin-bottom: $spacing-lg;
}
.pagination-wrap {
  @include flex-center;
  margin-top: $spacing-lg;
}
@media (max-width: 1024px) {
  .product-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
