<template>
  <div class="order-list-page">
    <div class="page-container">
      <h2 class="page-title">我的订单</h2>

      <!-- 状态 Tab -->
      <div class="status-tabs">
        <el-radio-group v-model="statusFilter" size="default" @change="filterOrders">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="0">待支付</el-radio-button>
          <el-radio-button value="1">已支付</el-radio-button>
          <el-radio-button value="2">已发货</el-radio-button>
          <el-radio-button value="3">已完成</el-radio-button>
          <el-radio-button value="4">已取消</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 订单列表 -->
      <div v-if="filteredOrders.length > 0" class="order-list">
        <div v-for="order in filteredOrders" :key="order.id" class="order-card">
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span class="order-time">{{ order.createTime }}</span>
            <el-tag :type="statusType(order.status)" size="small">{{ order.statusText }}</el-tag>
          </div>
          <div class="order-body">
            <div class="order-items">
              <div
                v-for="item in order.items"
                :key="item.id"
                class="order-item"
                @click="$router.push('/product/' + item.productId)"
              >
                <img :src="item.image" class="item-img" />
                <span class="item-name">{{ item.name }}</span>
                <span class="item-price">&yen;{{ Number(item.price || 0).toFixed(2) }}</span>
                <span class="item-qty">x{{ item.quantity }}</span>
                <span class="item-subtotal">&yen;{{ (Number(item.price || 0) * Number(item.quantity || 0)).toFixed(2) }}</span>
              </div>
            </div>
            <div class="order-total">
              共 {{ order.items?.length || 0 }} 件，实付：
              <span class="total-amount">&yen;{{ order.payAmount || order.totalAmount }}</span>
            </div>
          </div>
          <div class="order-actions">
            <router-link :to="'/order/' + order.id">
              <el-button size="small">查看详情</el-button>
            </router-link>
            <el-button
              v-if="order.status === 0"
              type="danger" size="small" @click="handlePay(order)"
            >立即支付</el-button>
            <el-button
              v-if="order.status === 0"
              size="small" @click="handleCancel(order)"
            >取消订单</el-button>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无订单">
        <router-link to="/products">
          <el-button type="primary">去逛逛</el-button>
        </router-link>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const orders = ref([])
const statusFilter = ref('')

const filteredOrders = computed(() => {
  if (statusFilter.value === '') return orders.value
  return orders.value.filter(o => o.status === parseInt(statusFilter.value))
})

const statusColors = { 0: 'warning', 1: 'primary', 2: 'success', 3: '', 4: 'info' }
function statusType(status) {
  return statusColors[status] || ''
}

onMounted(async () => {
  try {
    const res = await axios.get('/api/order/list')
    if (res.data.code === 200) orders.value = res.data.data.list || []
  } catch { /* */ }
})

async function handlePay(order) {
  try {
    const res = await axios.put('/api/order/' + order.id + '/pay')
    if (res.data.code === 200) {
      order.status = 1
      ElMessage.success('支付成功')
    }
  } catch { ElMessage.error('支付失败') }
}

async function handleCancel(order) {
  await ElMessageBox.confirm('确定取消该订单？', '提示', { type: 'warning' })
  try {
    const res = await axios.put('/api/order/' + order.id + '/cancel')
    if (res.data.code === 200) {
      order.status = 4
      ElMessage.success('已取消')
    }
  } catch { /* 用户取消操作 */ }
}
</script>

<style lang="scss" scoped>
.page-container { @include page-container; }
.page-title { font-size: $font-size-xxl; font-weight: 600; margin-bottom: $spacing-lg; }
.status-tabs { margin-bottom: $spacing-lg; }
.order-card {
  background: $bg-white;
  border-radius: $radius-md;
  margin-bottom: $spacing-md;
  padding: $spacing-md $spacing-lg;
}
.order-header {
  @include flex-between;
  padding-bottom: $spacing-md;
  border-bottom: 1px solid $border-light;
  font-size: $font-size-sm;
}
.order-no { color: $text-primary; }
.order-time { color: $text-secondary; margin-left: auto; margin-right: $spacing-md; }
.order-body {
  padding: $spacing-md 0;
  border-bottom: 1px solid $border-light;
}
.order-item {
  @include flex-center;
  gap: $spacing-md;
  padding: $spacing-sm 0;
  cursor: pointer;
}
.item-img {
  width: 60px; height: 60px; object-fit: cover; border-radius: $radius-sm;
}
.item-name { flex: 1; }
.item-price { color: $text-regular; }
.item-qty { color: $text-secondary; }
.order-total {
  text-align: right;
  margin-top: $spacing-sm;
  font-size: $font-size-base;
  color: $text-primary;
}
.total-amount { color: $price-color; font-size: $font-size-lg; font-weight: 700; }
.order-actions {
  @include flex-center;
  gap: $spacing-sm;
  justify-content: flex-end;
  padding-top: $spacing-md;
}
</style>
