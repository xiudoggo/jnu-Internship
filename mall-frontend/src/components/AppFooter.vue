<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <p>&copy; 2025 潮购 ChaoGou — 品质生活，潮流之选</p>
      <p class="footer-links">
        <span>关于我们</span>
        <span class="divider">|</span>
        <span>帮助中心</span>
        <span class="divider">|</span>
        <span>隐私政策</span>
      </p>
    </div>
  </footer>
</template>

<style lang="scss" scoped>
.app-footer {
  height: $footer-height;
  background: $text-primary;
  color: #999;
  @include flex-center;
}
.footer-inner {
  text-align: center;
  font-size: $font-size-xs;
  line-height: 1.8;
}
.footer-links {
  .divider { margin: 0 $spacing-sm; }
  span { cursor: pointer; &:hover { color: #fff; } }
}
</style>
