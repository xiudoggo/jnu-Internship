<template>
  <div class="app-layout">
    <NavHeader />
    <main class="main-content">
      <router-view />
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import NavHeader from './NavHeader.vue'
import AppFooter from './AppFooter.vue'
</script>

<style lang="scss" scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.main-content {
  flex: 1;
  min-height: calc(100vh - $header-height - $footer-height);
}
</style>
