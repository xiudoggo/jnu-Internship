<template>
  <div class="product-manage">
    <div class="page-toolbar">
      <h3>商品管理</h3>
      <el-button type="primary" @click="openDialog(null)">
        <el-icon><Plus /></el-icon> 新增商品
      </el-button>
    </div>

    <!-- 商品表格 -->
    <el-table :data="products" border stripe v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <el-image :src="row.coverImage" style="width:50px;height:50px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="200" show-overflow-tooltip />
      <el-table-column label="价格" width="120">
        <template #default="{ row }">
          <span class="price">¥{{ row.price }}</span>
          <span v-if="row.originalPrice > row.price" class="original-price">¥{{ row.originalPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column label="标签" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.isHot" type="danger" size="small">热门</el-tag>
          <el-tag v-if="row.isNew" type="success" size="small" style="margin-left:4px">新品</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="info" size="small">已下架</el-tag>
          <el-tag v-else type="success" size="small">已上架</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">
            <el-icon><Edit /></el-icon> 编辑
          </el-button>
          <el-button
            v-if="row.status !== 0"
            size="small" type="warning"
            @click="handleToggleStatus(row.id)"
          >下架</el-button>
          <el-button
            v-else
            size="small" type="success"
            @click="handleToggleStatus(row.id)"
          >上架</el-button>
          <el-popconfirm title="确定删除该商品？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @change="fetchProducts"
      />
    </div>

    <!-- 新增/编辑 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingProduct ? '编辑商品' : '新增商品'"
      width="640px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="80px" ref="formRef">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" placeholder="商品名称" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="价格" required>
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价">
              <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分类ID">
              <el-input-number v-model="form.categoryId" :min="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存">
              <el-input-number v-model="form.stock" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="封面图 URL" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="商品描述" />
        </el-form-item>
        <el-form-item label="标签">
          <el-checkbox v-model="form.isHot">热门</el-checkbox>
          <el-checkbox v-model="form.isNew" style="margin-left:12px">新品</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const products = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const editingProduct = ref(null)
const form = reactive({
  name: '', price: null, originalPrice: null, categoryId: null,
  stock: 0, coverImage: '',
  description: '', isHot: false, isNew: false
})

async function fetchProducts() {
  loading.value = true
  try {
    const res = await axios.get(`/api/admin/product/list/${page.value}/${pageSize.value}`)
    if (res.data.code === 200) {
      products.value = res.data.data.list
      total.value = res.data.data.total
    }
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function openDialog(product) {
  editingProduct.value = product
  if (product) {
    Object.assign(form, {
      name: product.name, price: product.price, originalPrice: product.originalPrice,
      categoryId: product.categoryId,
      stock: product.stock, coverImage: product.coverImage,
      description: product.description, isHot: !!product.isHot, isNew: !!product.isNew
    })
  } else {
    Object.assign(form, { name: '', price: null, originalPrice: null, categoryId: null, stock: 0, coverImage: '', description: '', isHot: false, isNew: false })
  }
  dialogVisible.value = true
}

async function handleSave() {
  if (!form.name || form.price == null) {
    ElMessage.warning('请填写名称和价格')
    return
  }
  try {
    if (editingProduct.value) {
      await axios.put(`/api/admin/product/${editingProduct.value.id}`, form)
      // 本地更新
      const idx = products.value.findIndex(p => p.id === editingProduct.value.id)
      if (idx !== -1) {
        Object.assign(products.value[idx], form)
      }
      ElMessage.success('更新成功')
    } else {
      const res = await axios.post('/api/admin/product', form)
      if (res.data.code === 200 && res.data.data) {
        products.value.unshift(res.data.data)
        total.value++
      }
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function handleToggleStatus(id) {
  try {
    const res = await axios.put(`/api/admin/product/${id}/toggle-status`)
    if (res.data.code === 200) {
      const item = products.value.find(p => p.id === id)
      if (item) item.status = parseInt(res.data.data.status)
      ElMessage.success(res.data.data.label)
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function handleDelete(id) {
  try {
    await axios.delete(`/api/admin/product/${id}`)
    products.value = products.value.filter(p => p.id !== id)
    total.value--
    ElMessage.success('已删除')
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => fetchProducts())
</script>

<style lang="scss" scoped>
.product-manage {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  min-height: calc(100vh - 160px);
}
.page-toolbar {
  @include flex-between;
  margin-bottom: 20px;
  h3 { font-size: 18px; font-weight: 600; margin: 0; }
}
.pagination-wrap {
  margin-top: 20px;
  @include flex-center;
}
.price { color: $primary-color; font-weight: 600; }
.original-price { text-decoration: line-through; color: $text-secondary; font-size: 12px; margin-left: 6px; }
</style>
