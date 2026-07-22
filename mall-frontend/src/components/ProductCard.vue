<template>
  <div class="product-card" @click="goDetail">
    <div class="card-img">
      <img :src="product.coverImage || product.image" :alt="product.name" loading="lazy" />
      <!-- 标签 -->
      <div class="card-tags">
        <span v-if="product.isHot" class="tag tag-hot">热卖</span>
        <span v-if="product.isNew" class="tag tag-new">新品</span>
      </div>
    </div>
    <div class="card-info">
      <h3 class="card-name">{{ product.name }}</h3>
      <div class="card-price">
        <span class="price-current">&yen;{{ Number(product.price).toFixed(2) }}</span>
        <span class="price-original" v-if="Number(product.originalPrice) > Number(product.price)">
          &yen;{{ Number(product.originalPrice).toFixed(2) }}
        </span>
      </div>
      <div class="card-meta">
        <span>已售 {{ product.sales || 0 }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  product: { type: Object, required: true }
})
const router = useRouter()

function goDetail() {
  router.push('/product/' + props.product.id)
}
</script>

<style lang="scss" scoped>
.product-card {
  background: $bg-white;
  border-radius: $radius-md;
  overflow: hidden;
  @include card-hover;
}
.card-img {
  position: relative;
  width: 100%;
  padding-top: 100%; // 1:1 正方形
  background: $bg-light;
  overflow: hidden;
  img {
    position: absolute;
    top: 0; left: 0;
    width: 100%; height: 100%;
    object-fit: cover;
  }
}
.card-tags {
  position: absolute;
  top: $spacing-sm; left: $spacing-sm;
  display: flex; gap: 4px;
  .tag {
    padding: 2px 8px;
    font-size: $font-size-xs;
    color: #fff;
    border-radius: $radius-sm;
  }
  .tag-hot { background: $danger-color; }
  .tag-new { background: $primary-color; }
}
.card-info {
  padding: $spacing-sm $spacing-md $spacing-md;
}
.card-name {
  @include text-ellipsis(2);
  font-size: $font-size-base;
  font-weight: 500;
  margin-bottom: $spacing-xs;
  color: $text-primary;
}
.card-price {
  @include flex-center;
  justify-content: flex-start;
  gap: $spacing-sm;
  margin-bottom: $spacing-xs;
  .price-current {
    color: $price-color;
    font-size: $font-size-lg;
    font-weight: 700;
  }
  .price-original {
    color: $text-placeholder;
    font-size: $font-size-xs;
    text-decoration: line-through;
  }
}
.card-meta {
  font-size: $font-size-xs;
  color: $text-secondary;
}
</style>
