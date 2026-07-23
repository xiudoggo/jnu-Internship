<template>
  <div class="product-list-page">
    <div class="page-container">
      <!-- 面包屑 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentCategory">{{ currentCategory }}</el-breadcrumb-item>
        <el-breadcrumb-item>全部商品</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="list-layout">
        <!-- 左侧分类 -->
        <aside class="sidebar">
          <h3 class="sidebar-title">商品分类</h3>
          <ul class="category-list">
            <li
              :class="{ active: !route.query.categoryId }"
              @click="selectCategory(null)"
            >全部</li>
            <template v-for="cat in categories" :key="cat.id">
              <li class="root-cat">{{ cat.name }}</li>
              <li
                v-for="child in cat.children"
                :key="child.id"
                :class="{ active: String(route.query.categoryId) === String(child.id) }"
                @click="selectCategory(child.id)"
                class="child-cat"
              >{{ child.name }}</li>
            </template>
          </ul>
        </aside>

        <!-- 右侧商品区 -->
        <div class="content">
          <!-- 筛选 + 排序 -->
          <div class="filter-bar">
            <el-radio-group v-model="sortType" size="small" @change="onSortChange">
              <el-radio-button value="default">综合</el-radio-button>
              <el-radio-button value="sales">销量</el-radio-button>
              <el-radio-button value="price-asc">价格↑</el-radio-button>
              <el-radio-button value="price-desc">价格↓</el-radio-button>
            </el-radio-group>
            <span class="total-count">共 {{ total }} 件商品</span>
          </div>

          <!-- 商品网格 -->
          <div v-if="list.length > 0" class="product-grid">
            <ProductCard v-for="item in list" :key="item.id" :product="item" />
          </div>
          <el-empty v-else-if="!loading" description="暂无商品" />

          <!-- 分页 -->
          <div class="pagination-wrap" v-if="total > pageSize">
            <el-pagination
              v-model:current-page="page"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next"
              background
              @current-change="fetchData"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import ProductCard from '@/components/ProductCard.vue'

const route = useRoute()
const router = useRouter()

const list = ref([])
const categories = ref([])
const total = ref(0)
const loading = ref(true)
const page = ref(1)
const pageSize = 12
const sortType = ref('default')

const currentCategory = computed(() => {
  const cid = route.query.categoryId
  if (!cid) return ''
  for (const cat of categories.value) {
    if (String(cat.id) === String(cid)) return cat.name
    if (cat.children) {
      const child = cat.children.find(c => String(c.id) === String(cid))
      if (child) return child.name
    }
  }
  return ''
})

function selectCategory(cid) {
  const query = { ...route.query }
  if (cid) {
    query.categoryId = cid
  } else {
    delete query.categoryId
  }
  router.push({ query })
}

function onSortChange() {
  page.value = 1
  fetchData()
}

watch(() => route.query.categoryId, () => {
  page.value = 1
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    const cid = route.query.categoryId
    const pageNum = page.value
    const sort = sortType.value

    // 根据参数构建 RESTful URL
    let url
    if (cid) {
      url = sort !== 'default'
        ? `/api/product/list/category/${cid}/${pageNum}/${pageSize}/${sort}`
        : `/api/product/list/category/${cid}/${pageNum}/${pageSize}`
    } else {
      url = sort !== 'default'
        ? `/api/product/list/${pageNum}/${pageSize}/${sort}`
        : `/api/product/list/${pageNum}/${pageSize}`
    }

    const res = await axios.get(url)
    if (res.data.code === 200) {
      list.value = res.data.data.list
      total.value = res.data.data.total
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  // 获取分类
  try {
    const res = await axios.get('/api/category/tree')
    if (res.data.code === 200) categories.value = res.data.data
  } catch { /* */ }
  fetchData()
})
</script>

<style lang="scss" scoped>
.page-container { @include page-container; }
.breadcrumb { margin-bottom: $spacing-md; }
.list-layout {
  display: flex;
  gap: $spacing-lg;
}
.sidebar {
  width: 180px;
  flex-shrink: 0;
  background: $bg-white;
  border-radius: $radius-md;
  padding: $spacing-md;
  height: fit-content;
}
.sidebar-title {
  font-size: $font-size-md;
  font-weight: 600;
  margin-bottom: $spacing-sm;
  padding-bottom: $spacing-sm;
  border-bottom: 1px solid $border-light;
}
.category-list {
  li {
    padding: $spacing-sm $spacing-md;
    cursor: pointer;
    border-radius: $radius-sm;
    font-size: $font-size-sm;
    color: $text-regular;
    &:hover { background: $bg-light; color: $primary-color; }
    &.active {
      background: lighten($primary-color, 42%);
      color: $primary-color;
      font-weight: 600;
    }
    &.root-cat {
      font-weight: 600;
      color: $text-primary;
      cursor: default;
      padding-top: 10px;
      &:hover { background: transparent; color: $text-primary; }
    }
    &.child-cat {
      padding-left: 28px;
    }
  }
}
.content { flex: 1; }
.filter-bar {
  @include flex-between;
  background: $bg-white;
  padding: $spacing-md;
  border-radius: $radius-md;
  margin-bottom: $spacing-md;
}
.total-count {
  font-size: $font-size-sm;
  color: $text-secondary;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-md;
  margin-bottom: $spacing-lg;
}
.pagination-wrap {
  @include flex-center;
  margin-top: $spacing-lg;
}
@media (max-width: 1024px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .sidebar { display: none; }
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
