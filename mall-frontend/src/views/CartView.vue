<template>
  <div class="cart-page">
    <div class="page-container">
      <h2 class="page-title">我的购物车</h2>

      <!-- 购物车表格 -->
      <div class="cart-table-wrap" v-if="cartStore.items.length > 0">
        <el-table :data="cartStore.items" style="width: 100%">
          <!-- 选择列 -->
          <el-table-column width="50">
            <template #default="{ row }">
              <el-checkbox :model-value="row.selected" @change="cartStore.toggleSelect(row.productId)" />
            </template>
          </el-table-column>
          <!-- 商品 -->
          <el-table-column label="商品" min-width="360">
            <template #default="{ row }">
              <div class="goods-info" @click="$router.push('/product/' + row.productId)">
                <img :src="row.image" class="goods-img" />
                <span class="goods-name">{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <!-- 单价 -->
          <el-table-column label="单价" width="120" align="center">
            <template #default="{ row }">
              <span class="goods-price">&yen;{{ Number(row.price).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <!-- 数量 -->
          <el-table-column label="数量" width="150" align="center">
            <template #default="{ row }">
              <el-input-number
                :model-value="row.quantity"
                :min="1" :max="99"
                size="small"
                @change="(v) => cartStore.updateQuantity(row.productId, v)"
              />
            </template>
          </el-table-column>
          <!-- 小计 -->
          <el-table-column label="小计" width="120" align="center">
            <template #default="{ row }">
              <span class="subtotal">&yen;{{ (row.price * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <!-- 操作 -->
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row }">
              <el-button type="danger" link @click="cartStore.removeItem(row.productId)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 空购物车 -->
      <el-empty v-else description="购物车是空的">
        <router-link to="/products">
          <el-button type="primary">去逛逛</el-button>
        </router-link>
      </el-empty>

      <!-- 底部结算栏 -->
      <div class="cart-footer" v-if="cartStore.items.length > 0">
        <div class="footer-left">
          <el-checkbox
            :model-value="cartStore.isAllSelected"
            :indeterminate="cartStore.totalCount > 0 && !cartStore.isAllSelected"
            @change="cartStore.toggleSelectAll($event)"
          >全选</el-checkbox>
          <el-button link @click="clearSelect">删除选中</el-button>
        </div>
        <div class="footer-right">
          <span class="total-label">
            已选 <em>{{ cartStore.selectedItems.length }}</em> 件，合计：
          </span>
          <span class="total-price">&yen;{{ cartStore.totalPrice.toFixed(2) }}</span>
          <el-button
            type="danger" size="large"
            :disabled="cartStore.selectedItems.length === 0"
            @click="goConfirm"
          >
            去结算
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'

const cartStore = useCartStore()
const userStore = useUserStore()
const router = useRouter()

function goConfirm() {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login', query: { redirect: '/cart' } })
    return
  }
  router.push('/order/confirm')
}

function clearSelect() {
  cartStore.clearSelected()
  ElMessage.success('已删除选中商品')
}
</script>

<style lang="scss" scoped>
.page-container { @include page-container; }
.page-title {
  font-size: $font-size-xxl;
  font-weight: 600;
  margin-bottom: $spacing-lg;
}
.cart-table-wrap {
  background: $bg-white;
  border-radius: $radius-md;
  padding: $spacing-md;
}
.goods-info {
  @include flex-center;
  gap: $spacing-md;
  cursor: pointer;
}
.goods-img {
  width: 80px; height: 80px;
  object-fit: cover;
  border-radius: $radius-sm;
}
.goods-name {
  @include text-ellipsis(2);
  flex: 1;
}
.goods-price, .subtotal {
  color: $price-color;
  font-weight: 600;
}
.cart-footer {
  @include flex-between;
  position: sticky;
  bottom: 0;
  background: $bg-white;
  padding: $spacing-md $spacing-lg;
  margin-top: $spacing-md;
  border-radius: $radius-md;
  box-shadow: 0 -2px 8px rgba(0,0,0,.06);
}
.footer-left {
  @include flex-center;
  gap: $spacing-md;
}
.footer-right {
  @include flex-center;
  gap: $spacing-md;
}
.total-label {
  font-size: $font-size-base;
  em { color: $price-color; font-style: normal; font-weight: 600; }
}
.total-price {
  font-size: $font-size-title;
  font-weight: 700;
  color: $price-color;
}
</style>
