<template>
  <div class="category-nav">
    <template v-for="cat in categories" :key="cat.id">
      <div class="cat-group">
        <span class="root-name">{{ cat.name }}</span>
        <div class="child-list">
          <span
            v-for="child in cat.children"
            :key="child.id"
            class="child-item"
            @click="$router.push({ path: '/products', query: { categoryId: child.id } })"
          >{{ child.name }}</span>
        </div>
      </div>
    </template>
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
  display: flex;
  gap: $spacing-lg;
  padding: $spacing-lg;
  margin-bottom: $spacing-lg;
  background: $bg-white;
  border-radius: $radius-md;
  flex-wrap: wrap;
}
.cat-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.root-name {
  font-weight: 600;
  font-size: $font-size-sm;
  color: $text-primary;
  padding-bottom: 4px;
  border-bottom: 2px solid $primary-color;
}
.child-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.child-item {
  font-size: $font-size-xs;
  color: $text-regular;
  cursor: pointer;
  padding: 2px 10px;
  border-radius: 12px;
  background: $bg-light;
  transition: all 0.2s;
  &:hover {
    color: $primary-color;
    background: lighten($primary-color, 40%);
  }
}
@media (max-width: 768px) {
  .category-nav { gap: $spacing-md; padding: $spacing-md; }
}
</style>
