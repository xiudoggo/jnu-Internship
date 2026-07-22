<template>
  <div class="banner-swiper">
    <el-carousel :interval="4000" arrow="hover" height="400px">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <router-link :to="item.linkUrl || '/'" class="banner-link">
          <img :src="item.imageUrl" :alt="item.title" class="banner-img" />
        </router-link>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const banners = ref([])

onMounted(async () => {
  try {
    const res = await axios.get('/api/banner/list')
    if (res.data.code === 200) banners.value = res.data.data
  } catch { /* 使用默认数据 */ }
})
</script>

<style lang="scss" scoped>
.banner-swiper {
  margin-bottom: $spacing-lg;
  border-radius: $radius-lg;
  overflow: hidden;
}
.banner-link {
  display: block;
  width: 100%;
  height: 100%;
}
.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
