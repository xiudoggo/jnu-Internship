<template>
  <div class="category-nav">
    <div
      v-for="cat in categories"
      :key="cat.id"
      class="category-item"
      @click="$router.push({ path: '/products', query: { categoryId: cat.id } })"
    >
      <el-icon :size="28"><component :is="cat.icon" /></el-icon>
      <span class="cat-name">{{ cat.name }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const categories = ref([])

onMounted(async () => {
  try {
    const res = await axios.get('/api/category/tree')
    if (res.data.code === 200) categories.value = res.data.data
  } catch { /* fallback */ }
})
</script>

<style lang="scss" scoped>
.category-nav {
  @include flex-center;
  gap: $spacing-xl;
  padding: $spacing-lg 0;
  margin-bottom: $spacing-lg;
  background: $bg-white;
  border-radius: $radius-md;
  flex-wrap: wrap;
}
.category-item {
  @include flex-center;
  flex-direction: column;
  gap: $spacing-xs;
  cursor: pointer;
  padding: $spacing-sm $spacing-md;
  border-radius: $radius-md;
  transition: all 0.3s;
  color: $text-regular;
  &:hover {
    background: lighten($primary-color, 40%);
    color: $primary-color;
  }
}
.cat-name {
  font-size: $font-size-sm;
}
@media (max-width: 768px) {
  .category-nav { gap: $spacing-md; }
}
</style>
