<template>
  <div class="order-confirm-page">
    <div class="page-container">
      <h2 class="page-title">确认订单</h2>

      <el-steps :active="0" align-center class="steps" finish-status="success">
        <el-step title="确认订单" />
        <el-step title="支付" />
        <el-step title="完成" />
      </el-steps>

      <!-- 收货地址 -->
      <div class="section">
        <h3 class="section-title">收货地址</h3>
        <el-form ref="formRef" :model="address" :rules="addrRules" label-width="80px">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="收件人" prop="name">
                <el-input v-model="address.name" placeholder="请输入收件人姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="address.phone" placeholder="请输入手机号" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="省份" prop="province">
                <el-input v-model="address.province" placeholder="省份" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="城市" prop="city">
                <el-input v-model="address.city" placeholder="城市" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="区县" prop="district">
                <el-input v-model="address.district" placeholder="区县" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="详细地址" prop="detail">
            <el-input v-model="address.detail" placeholder="街道、门牌号等" />
          </el-form-item>
        </el-form>
      </div>

      <!-- 商品清单 -->
      <div class="section">
        <h3 class="section-title">商品清单</h3>
        <el-table :data="cartStore.selectedItems" style="width: 100%">
          <el-table-column label="商品" min-width="300">
            <template #default="{ row }">
              <div class="goods-info">
                <img :src="row.image" class="goods-img" />
                <span>{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="120" align="center">
            <template #default="{ row }">&yen;{{ Number(row.price).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="数量" width="100" align="center">
            <template #default="{ row }">x{{ row.quantity }}</template>
          </el-table-column>
          <el-table-column label="小计" width="120" align="center">
            <template #default="{ row }">&yen;{{ (Number(row.price) * row.quantity).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 价格汇总 -->
      <div class="price-summary">
        <div class="summary-row">
          <span>商品总额</span>
          <span>&yen;{{ cartStore.totalPrice.toFixed(2) }}</span>
        </div>
        <div class="summary-row">
          <span>运费</span>
          <span class="free">免运费</span>
        </div>
        <div class="summary-row total-row">
          <span>应付总额</span>
          <span class="total-amount">&yen;{{ cartStore.totalPrice.toFixed(2) }}</span>
        </div>
      </div>

      <div class="submit-row">
        <router-link to="/cart" class="back-link">&larr; 返回购物车</router-link>
        <el-button type="danger" size="large" :loading="submitting" @click="submitOrder">
          提交订单
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useCartStore } from '@/stores/cart'

const cartStore = useCartStore()
const router = useRouter()
const submitting = ref(false)
const formRef = ref(null)

const address = reactive({
  name: '张三',
  phone: '13800001111',
  province: '广东省',
  city: '深圳市',
  district: '南山区',
  detail: '科技园路1号创新大厦1201室'
})

const addrRules = {
  name: [{ required: true, message: '请输入收件人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

async function submitOrder() {
  if (cartStore.selectedItems.length === 0) {
    ElMessage.warning('请选择要购买的商品')
    return
  }
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const res = await axios.post('/api/order/create', {
      address: { ...address },
      items: cartStore.selectedItems,
      totalAmount: cartStore.totalPrice.toFixed(2)
    })
    if (res.data.code === 200) {
      cartStore.clearSelected()
      ElMessage.success('下单成功！')
      router.push('/order/' + res.data.data.orderId)
    }
  } catch {
    ElMessage.error('下单失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.page-container { @include page-container; }
.page-title { font-size: $font-size-xxl; font-weight: 600; margin-bottom: $spacing-lg; }
.steps { margin-bottom: $spacing-xl; }
.section {
  background: $bg-white;
  border-radius: $radius-md;
  padding: $spacing-lg;
  margin-bottom: $spacing-md;
}
.section-title {
  font-size: $font-size-md;
  font-weight: 600;
  margin-bottom: $spacing-md;
  padding-bottom: $spacing-sm;
  border-bottom: 1px solid $border-light;
}
.goods-info {
  @include flex-center; gap: $spacing-md;
}
.goods-img {
  width: 60px; height: 60px; object-fit: cover; border-radius: $radius-sm;
}
.price-summary {
  background: $bg-white;
  border-radius: $radius-md;
  padding: $spacing-lg;
  margin-bottom: $spacing-md;
}
.summary-row {
  @include flex-between;
  padding: $spacing-sm 0;
  font-size: $font-size-base;
  color: $text-regular;
}
.free { color: $success-color; }
.total-row {
  border-top: 1px solid $border-light;
  margin-top: $spacing-sm;
  padding-top: $spacing-md;
  font-size: $font-size-lg;
  font-weight: 600;
}
.total-amount { color: $price-color; font-size: $font-size-xxl; }
.submit-row {
  @include flex-between;
}
.back-link {
  color: $text-secondary;
  &:hover { color: $primary-color; }
}
</style>
