<template>
  <div class="order-manage">
    <div class="page-toolbar">
      <h3>订单管理</h3>
      <div class="toolbar-right">
        <el-radio-group v-model="statusFilter" size="small" @change="fetchOrders">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button :value="0">待支付</el-radio-button>
          <el-radio-button :value="1">已支付</el-radio-button>
          <el-radio-button :value="2">已发货</el-radio-button>
          <el-radio-button :value="3">已完成</el-radio-button>
          <el-radio-button :value="4">已取消</el-radio-button>
        </el-radio-group>
        <el-button type="primary" size="small" @click="openCreateDialog">
          <el-icon><Plus /></el-icon> 新增订单
        </el-button>
      </div>
    </div>

    <el-table :data="filteredOrders" border stripe v-loading="loading" style="width:100%">
      <el-table-column type="expand">
        <template #default="{ row }">
          <div class="expand-detail">
            <h4>订单商品</h4>
            <el-table :data="row._items || []" size="small" border>
              <el-table-column label="图片" width="70">
                <template #default="{ row: item }">
                  <el-image :src="item.image" style="width:40px;height:40px" fit="cover" />
                </template>
              </el-table-column>
              <el-table-column prop="name" label="商品名" />
              <el-table-column label="单价" width="100">
                <template #default="{ row: item }">¥{{ item.price }}</template>
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="80" />
              <el-table-column label="小计" width="100">
                <template #default="{ row: item }">¥{{ (item.price * item.quantity).toFixed(2) }}</template>
              </el-table-column>
            </el-table>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" width="55" />
      <el-table-column prop="orderNo" label="订单号" width="195" show-overflow-tooltip />
      <el-table-column label="用户ID" width="80">
        <template #default="{ row }">
          <span>#{{ row.userId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="下单用户" width="110">
        <template #default="{ row }">
          <span v-if="row.userNickname">{{ row.userNickname }}</span>
          <span v-else class="text-muted">用户#{{ row.userId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="金额" width="90">
        <template #default="{ row }">¥{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="130">
        <template #default="{ row }">
          <el-select v-model="row.status" size="small" @change="(val) => handleStatusChange(row, val)" style="width:110px">
            <el-option :value="0" label="待支付" />
            <el-option :value="1" label="已支付" />
            <el-option :value="2" label="已发货" />
            <el-option :value="3" label="已完成" />
            <el-option :value="4" label="已取消" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column prop="receiverName" label="收货人" width="80" />
      <el-table-column prop="receiverAddress" label="收货地址" min-width="140" show-overflow-tooltip />
      <el-table-column prop="createTime" label="下单时间" width="160" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增 / 编辑 弹窗（含商品项编辑） -->
    <el-dialog v-model="dialogVisible" :title="editingOrder ? '编辑订单' : '新增订单'" width="700px" :close-on-click-modal="false">
      <el-form :model="form" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户ID">
              <el-input-number v-model="form.userId" :min="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单金额">
              <el-input-number v-model="form.totalAmount" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option :value="0" label="待支付" />
            <el-option :value="1" label="已支付" />
            <el-option :value="2" label="已发货" />
            <el-option :value="3" label="已完成" />
            <el-option :value="4" label="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item label="收货人">
          <el-input v-model="form.receiverName" placeholder="收货人姓名" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.receiverPhone" placeholder="收货人电话" />
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input v-model="form.receiverAddress" placeholder="收货地址" />
        </el-form-item>

        <!-- 商品项编辑：从已有商品库搜索选择 -->
        <el-divider content-position="left">订单商品</el-divider>
        <div class="items-editor">
          <div v-for="(item, idx) in form.items" :key="idx" class="item-row">
            <el-select
              v-model="item.productId"
              filterable
              remote
              reserve-keyword
              :remote-method="(kw) => searchProducts(kw)"
              placeholder="搜索商品"
              size="small"
              style="width:220px"
              @change="(val) => onProductSelect(idx, val)"
              @focus="searchProducts('')"
              clearable
            >
              <el-option
                v-for="p in searchResults"
                :key="p.id"
                :label="p.name + ' (¥' + p.price + ')'"
                :value="p.id"
              />
            </el-select>
            <el-input-number v-model="item.quantity" :min="1" size="small" style="width:80px" placeholder="数量" />
            <span v-if="item.price" class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            <el-button size="small" type="danger" @click="form.items.splice(idx, 1)">删除</el-button>
          </div>
          <el-button size="small" @click="form.items.push({ productId: null, name: '', price: 0, quantity: 1, image: '' })">
            <el-icon><Plus /></el-icon> 添加商品
          </el-button>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const orders = ref([])
const loading = ref(false)
const statusFilter = ref('')
const dialogVisible = ref(false)
const editingOrder = ref(null)
const searchResults = ref([])
const form = ref({
  userId: 1, totalAmount: 0, status: 0,
  receiverName: '', receiverPhone: '', receiverAddress: '',
  items: []
})

const statusTextMap = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消' }

const filteredOrders = computed(() => {
  if (statusFilter.value === '') return orders.value
  return orders.value.filter(o => o.status === statusFilter.value)
})

async function searchProducts(keyword) {
  try {
    const kw = keyword ? encodeURIComponent(keyword.trim()) : '__all__'
    const res = await axios.get(`/api/admin/product/search/${kw}`)
    if (res.data.code === 200) searchResults.value = res.data.data
  } catch (e) { searchResults.value = [] }
}

function onProductSelect(idx, productId) {
  const product = searchResults.value.find(p => p.id === productId)
  if (product) {
    form.value.items[idx].name = product.name
    form.value.items[idx].price = product.price
    form.value.items[idx].image = product.coverImage || ''
  }
}

async function fetchOrders() {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/order/list')
    if (res.data.code === 200) {
      orders.value = res.data.data.map(o => {
        o._items = o.items || []
        return o
      })
    }
  } catch (e) {
    ElMessage.error('加载失败')
  } finally { loading.value = false }
}

async function handleStatusChange(row, val) {
  try {
    await axios.put(`/api/admin/order/${row.id}/status`, { status: val })
    row.statusText = statusTextMap[val]
    ElMessage.success('状态已更新')
  } catch (e) { ElMessage.error('更新失败') }
}

function openCreateDialog() {
  editingOrder.value = null
  form.value = { userId: 1, totalAmount: 0, status: 0, receiverName: '', receiverPhone: '', receiverAddress: '', items: [] }
  dialogVisible.value = true
}

function openEditDialog(order) {
  editingOrder.value = order
  form.value = {
    userId: order.userId,
    totalAmount: order.totalAmount,
    status: order.status,
    receiverName: order.receiverName || '',
    receiverPhone: order.receiverPhone || '',
    receiverAddress: order.receiverAddress || '',
    items: JSON.parse(JSON.stringify(order._items || order.items || []))
  }
  dialogVisible.value = true
}

async function handleSave() {
  try {
    const payload = { ...form.value, items: form.value.items }
    if (editingOrder.value) {
      await axios.put(`/api/admin/order/${editingOrder.value.id}`, payload)
      // 本地更新
      const idx = orders.value.findIndex(o => o.id === editingOrder.value.id)
      if (idx !== -1) {
        orders.value[idx] = {
          ...orders.value[idx],
          ...payload,
          _items: JSON.parse(JSON.stringify(payload.items)),
          statusText: statusTextMap[payload.status]
        }
      }
      ElMessage.success('更新成功')
    } else {
      const res = await axios.post('/api/admin/order', payload)
      if (res.data.code === 200) {
        // 本地插入新订单
        const newOrder = {
          id: res.data.data.orderId,
          orderNo: res.data.data.orderNo,
          ...payload,
          _items: JSON.parse(JSON.stringify(payload.items)),
          statusText: statusTextMap[payload.status],
          createTime: new Date().toISOString()
        }
        orders.value.unshift(newOrder)
      }
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
  } catch (e) { ElMessage.error('操作失败') }
}

async function handleDelete(id) {
  try {
    await axios.delete(`/api/admin/order/${id}`)
    orders.value = orders.value.filter(o => o.id !== id)
    ElMessage.success('已删除')
  } catch (e) { ElMessage.error('删除失败') }
}

onMounted(() => fetchOrders())
</script>

<style lang="scss" scoped>
.order-manage {
  background: #fff; border-radius: 4px; padding: 20px; min-height: calc(100vh - 160px);
}
.page-toolbar {
  @include flex-between; margin-bottom: 20px; flex-wrap: wrap; gap: 12px;
  h3 { font-size: 18px; font-weight: 600; margin: 0; }
  .toolbar-right { @include flex-center; gap: 12px; }
}
.expand-detail {
  padding: 12px 20px;
  h4 { margin: 0 0 10px 0; font-size: 14px; color: $text-regular; }
}
.text-muted { color: $text-secondary; font-size: 12px; }
.items-editor {
  .item-row {
    display: flex; gap: 6px; align-items: center; margin-bottom: 8px;
  }
  .item-price { color: $primary-color; font-weight: 600; font-size: 13px; white-space: nowrap; }
}
</style>
