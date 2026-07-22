<template>
  <div class="search-box">
    <el-input
      v-model="keyword"
      placeholder="搜索潮购商品"
      clearable
      @keyup.enter="search"
      @clear="keyword = ''"
    >
      <template #append>
        <el-button type="primary" @click="search" :icon="Search">搜索</el-button>
      </template>
    </el-input>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const keyword = ref(route.query.q || '')

function search() {
  if (keyword.value.trim()) {
    router.push({ path: '/search', query: { q: keyword.value.trim() } })
  }
}
</script>

<style lang="scss" scoped>
.search-box {
  width: 100%;
}
</style>
