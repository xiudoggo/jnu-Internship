<template>
  <div class="favorite-page">
    <div class="page-container">
      <h2 class="page-title">我的收藏</h2>

      <div v-if="list.length > 0" class="product-grid">
        <div v-for="item in list" :key="item.id" class="fav-item">
          <div class="fav-card" @click="$router.push('/product/' + item.productId)">
            <img :src="item.image" class="fav-img" />
            <span class="fav-name">{{ item.name }}</span>
            <span class="fav-price">&yen;{{ Number(item.price).toFixed(2) }}</span>
          </div>
          <el-button type="danger" link size="small" @click.stop="removeFav(item)">
            <el-icon><Delete /></el-icon> 取消收藏
          </el-button>
        </div>
      </div>

      <el-empty v-else description="还没有收藏任何商品">
        <router-link to="/products">
          <el-button type="primary">去逛逛</el-button>
        </router-link>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const list = ref([])

onMounted(async () => {
  try {
    const res = await axios.get('/api/favorite/list')
    if (res.data.code === 200) list.value = res.data.data
  } catch { /* */ }
})

async function removeFav(item) {
  try {
    const res = await axios.post('/api/favorite/toggle', {
      productId: item.productId,
      name: item.name,
      image: item.image,
      price: item.price
    })
    if (res.data.code === 200 && !res.data.data.isFavorited) {
      list.value = list.value.filter(f => f.productId !== item.productId)
    }
  } catch { /* */ }
}
</script>

<style lang="scss" scoped>
.page-container { @include page-container; }
.page-title { font-size: $font-size-xxl; font-weight: 600; margin-bottom: $spacing-lg; }
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-md;
}
.fav-item {
  background: $bg-white;
  border-radius: $radius-md;
  padding: $spacing-md;
  text-align: center;
}
.fav-card {
  cursor: pointer;
  @include card-hover;
}
.fav-img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: $radius-sm;
  margin-bottom: $spacing-sm;
}
.fav-name {
  @include text-ellipsis(1);
  display: block;
  font-size: $font-size-sm;
  margin-bottom: $spacing-xs;
}
.fav-price {
  color: $price-color;
  font-size: $font-size-md;
  font-weight: 600;
}
@media (max-width: 1024px) { .product-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .product-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
