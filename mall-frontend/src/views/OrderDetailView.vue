<template>
  <div class="order-detail-page">
    <div class="page-container" v-if="order">
      <div class="detail-header">
        <router-link to="/order/list" class="back-link">&larr; 返回订单列表</router-link>
        <h2 class="page-title">订单详情</h2>
        <div class="order-status-bar">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <el-tag :type="statusType(order.status)">{{ order.statusText }}</el-tag>
        </div>
      </div>

      <!-- 物流步骤 -->
      <div class="section" v-if="order.status >= 0">
        <el-steps :active="order.status" align-center finish-status="success">
          <el-step title="提交订单" :description="order.createTime" />
          <el-step title="支付" :description="order.payTime || '待支付'" />
          <el-step title="发货" :description="order.deliveryTime || '待发货'" />
          <el-step title="完成" :description="order.completeTime || '待完成'" />
        </el-steps>
      </div>

      <!-- 收货信息 -->
      <div class="section">
        <h3 class="section-title">
          <el-icon><Location /></el-icon> 收货信息
        </h3>
        <div class="info-grid">
          <span>收件人：{{ order.receiverName }}</span>
          <span>手机号：{{ order.receiverPhone }}</span>
          <span class="address-full">地址：{{ order.receiverAddress }}</span>
        </div>
      </div>

      <!-- 商品清单 -->
      <div class="section">
        <h3 class="section-title">
          <el-icon><Goods /></el-icon> 商品清单
        </h3>
        <el-table :data="order.items" style="width: 100%">
          <el-table-column label="商品" min-width="300">
            <template #default="{ row }">
              <div class="goods-info" @click="$router.push('/product/' + row.productId)">
                <img :src="row.image" class="goods-img" />
                <span>{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="120" align="center">
            <template #default="{ row }">&yen;{{ Number(row.price || 0).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="数量" width="100" align="center">
            <template #default="{ row }">x{{ row.quantity }}</template>
          </el-table-column>
          <el-table-column label="小计" width="120" align="center">
            <template #default="{ row }">&yen;{{ (Number(row.price || 0) * Number(row.quantity || 0)).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 价格明细 -->
      <div class="section price-section">
        <div class="price-row">
          <span>商品总额</span>
          <span>&yen;{{ order.totalAmount || order.payAmount }}</span>
        </div>
        <div class="price-row">
          <span>运费</span>
          <span class="free">免运费</span>
        </div>
        <div class="price-row total">
          <span>实付金额</span>
          <span class="total-amount">&yen;{{ order.payAmount || order.totalAmount }}</span>
        </div>
      </div>

      <!-- 操作 -->
      <div class="actions" v-if="order.status === 0 || order.status === 1">
        <el-button v-if="order.status === 0" type="danger" size="large" @click="handlePay">
          立即支付
        </el-button>
        <el-button v-if="order.status === 0" size="large" @click="handleCancel">取消订单</el-button>
      </div>
    </div>

    <div class="page-container" v-else>
      <el-skeleton :rows="5" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const order = ref(null)

const statusColors = { 0: 'warning', 1: 'primary', 2: 'success', 3: '', 4: 'info' }
function statusType(s) { return statusColors[s] || '' }

onMounted(async () => {
  try {
    const res = await axios.get('/api/order/' + route.params.id)
    if (res.data.code === 200) order.value = res.data.data
  } catch { /* */ }
})

async function handlePay() {
  const res = await axios.put('/api/order/' + order.value.id + '/pay')
  if (res.data.code === 200) {
    order.value.status = 1
    ElMessage.success('支付成功')
  }
}

async function handleCancel() {
  await ElMessageBox.confirm('确定取消该订单？', '提示', { type: 'warning' })
  const res = await axios.put('/api/order/' + order.value.id + '/cancel')
  if (res.data.code === 200) {
    order.value.status = 4
    ElMessage.success('已取消')
  }
}
</script>

<style lang="scss" scoped>
.page-container { @include page-container; }
.detail-header {
  margin-bottom: $spacing-lg;
}
.back-link {
  color: $text-secondary;
  font-size: $font-size-sm;
  &:hover { color: $primary-color; }
}
.page-title { font-size: $font-size-xxl; font-weight: 600; margin: $spacing-sm 0; }
.order-status-bar {
  @include flex-center;
  gap: $spacing-md;
  font-size: $font-size-sm;
}
.section {
  background: $bg-white;
  border-radius: $radius-md;
  padding: $spacing-lg;
  margin-bottom: $spacing-md;
}
.section-title {
  @include flex-center;
  gap: $spacing-sm;
  font-size: $font-size-md;
  font-weight: 600;
  margin-bottom: $spacing-md;
  padding-bottom: $spacing-sm;
  border-bottom: 1px solid $border-light;
}
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $spacing-sm;
  font-size: $font-size-base;
  color: $text-regular;
}
.address-full { grid-column: 1 / -1; }
.goods-info {
  @include flex-center;
  gap: $spacing-md;
  cursor: pointer;
}
.goods-img {
  width: 60px; height: 60px; object-fit: cover; border-radius: $radius-sm;
}
.price-section {
  text-align: right;
}
.price-row {
  @include flex-between;
  padding: $spacing-xs 0;
  font-size: $font-size-base;
  color: $text-regular;
}
.free { color: $success-color; }
.total {
  border-top: 1px solid $border-light;
  margin-top: $spacing-sm;
  padding-top: $spacing-md;
  font-size: $font-size-lg;
  font-weight: 600;
}
.total-amount { color: $price-color; font-size: $font-size-xxl; font-weight: 700; }
.actions {
  @include flex-center;
  gap: $spacing-md;
  justify-content: flex-end;
  margin-top: $spacing-lg;
}
</style>
