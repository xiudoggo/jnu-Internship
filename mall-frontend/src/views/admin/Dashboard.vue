<template>
  <div class="dashboard" v-loading="loading">
    <!-- 今日概览卡片 -->
    <el-row :gutter="20" class="overview-row">
      <el-col :xs="24" :sm="8" v-for="card in overviewCards" :key="card.label">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-card-content">
            <div class="stat-icon" :style="{ background: card.bg }">
              <el-icon :size="28">
                <component :is="card.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">{{ card.label }}</div>
              <div class="stat-value">{{ card.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域：第一行 -->
    <el-row :gutter="20">
      <!-- 近7日订单趋势（折线图，双Y轴） -->
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">近7日订单趋势</div>
          </template>
          <div v-if="orderTrendData.length" class="chart-container">
            <v-chart :option="orderTrendOption" autoresize />
          </div>
          <el-empty v-else description="暂无订单趋势数据" />
        </el-card>
      </el-col>

      <!-- 商品销量 Top10（柱状图） -->
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">商品销量 Top10</div>
          </template>
          <div v-if="topProductsData.length" class="chart-container">
            <v-chart :option="topProductsOption" autoresize />
          </div>
          <el-empty v-else description="暂无商品销量数据" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域：第二行 -->
    <el-row :gutter="20" class="chart-row-second">
      <!-- 订单状态分布（饼图） -->
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">订单状态分布</div>
          </template>
          <div v-if="orderStatusData.length" class="chart-container">
            <v-chart :option="orderStatusOption" autoresize />
          </div>
          <el-empty v-else description="暂无订单状态数据" />
        </el-card>
      </el-col>

      <!-- 近7日用户增长趋势（折线图） -->
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">近7日用户增长趋势</div>
          </template>
          <div v-if="userTrendData.length" class="chart-container">
            <v-chart :option="userTrendOption" autoresize />
          </div>
          <el-empty v-else description="暂无用户增长数据" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Tickets, Goods, User } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'

// 注册 ECharts 必要组件（按需引入）
use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// ==================== 状态 ====================
const loading = ref(false)

// 今日概览
const overview = reactive({
  todayOrders: 0,
  todaySales: 0,
  todayUsers: 0
})

// 概览卡片配置（使用 computed 以便响应式更新）
const overviewCards = computed(() => [
  {
    label: '今日订单数',
    value: overview.todayOrders,
    icon: Tickets,
    bg: 'linear-gradient(135deg, #667eea, #764ba2)'
  },
  {
    label: '今日销售额',
    value: '¥' + (overview.todaySales || 0).toFixed(2),
    icon: Goods,
    bg: 'linear-gradient(135deg, #f093fb, #f5576c)'
  },
  {
    label: '今日新增用户',
    value: overview.todayUsers,
    icon: User,
    bg: 'linear-gradient(135deg, #4facfe, #00f2fe)'
  }
])

// 订单趋势
const orderTrendData = ref([])
const orderTrendOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'cross' }
  },
  legend: {
    data: ['订单数', '销售额'],
    bottom: 0
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '12%',
    top: '8%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: orderTrendData.value.map(item => item.orderDate)
  },
  yAxis: [
    {
      type: 'value',
      name: '订单数',
      minInterval: 1
    },
    {
      type: 'value',
      name: '销售额(¥)',
      axisLabel: { formatter: '¥{value}' }
    }
  ],
  series: [
    {
      name: '订单数',
      type: 'line',
      smooth: true,
      data: orderTrendData.value.map(item => item.orderCount),
      itemStyle: { color: '#409EFF' }
    },
    {
      name: '销售额',
      type: 'line',
      yAxisIndex: 1,
      smooth: true,
      data: orderTrendData.value.map(item => item.totalAmount),
      itemStyle: { color: '#67c23a' }
    }
  ]
}))

// 商品销量 Top10
const topProductsData = ref([])
const topProductsOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' }
  },
  grid: {
    left: '3%',
    right: '8%',
    bottom: '3%',
    top: '8%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: topProductsData.value.map(item => item.name),
    axisLabel: {
      rotate: 30,
      fontSize: 11
    }
  },
  yAxis: {
    type: 'value',
    name: '销量',
    minInterval: 1
  },
  series: [
    {
      type: 'bar',
      data: topProductsData.value.map(item => item.sales),
      itemStyle: {
        color: '#409EFF',
        borderRadius: [4, 4, 0, 0]
      }
    }
  ]
}))

// 订单状态分布
const orderStatusData = ref([])
const statusColorMap = {
  '待付款': '#e6a23c',
  '待发货': '#409EFF',
  '已发货': '#67c23a',
  '已完成': '#909399',
  '已取消': '#f56c6c'
}
const orderStatusOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} 单 ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 0,
    top: 'center'
  },
  series: [
    {
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['55%', '50%'],
      avoidLabelOverlap: false,
      data: orderStatusData.value.map(item => ({
        name: item.label,
        value: item.count
      })),
      itemStyle: {
        borderRadius: 4,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}: {d}%'
      },
      emphasis: {
        label: {
          fontSize: 16,
          fontWeight: 'bold'
        }
      }
    }
  ]
}))

// 用户增长趋势
const userTrendData = ref([])
const userTrendOption = computed(() => ({
  tooltip: {
    trigger: 'axis'
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    top: '8%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: userTrendData.value.map(item => item.regDate)
  },
  yAxis: {
    type: 'value',
    name: '新增用户',
    minInterval: 1
  },
  series: [
    {
      name: '新增用户',
      type: 'line',
      smooth: true,
      data: userTrendData.value.map(item => item.userCount),
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(103, 194, 58, 0.35)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.02)' }
          ]
        }
      },
      itemStyle: { color: '#67c23a' },
      lineStyle: { width: 2 }
    }
  ]
}))

// ==================== 数据请求 ====================
async function fetchAllData() {
  loading.value = true
  try {
    const [
      overviewRes,
      orderTrendRes,
      topProductsRes,
      orderStatusRes,
      userTrendRes
    ] = await Promise.all([
      axios.get('/api/admin/stat/today-overview'),
      axios.get('/api/admin/stat/order-trend'),
      axios.get('/api/admin/stat/top-products'),
      axios.get('/api/admin/stat/order-status'),
      axios.get('/api/admin/stat/user-trend')
    ])

    // 今日概览（后端返回 orderCount / totalSales / newUserCount）
    if (overviewRes.data.code === 200 && overviewRes.data.data) {
      const d = overviewRes.data.data
      overview.todayOrders = d.orderCount ?? 0
      overview.todaySales = d.totalSales ?? 0
      overview.todayUsers = d.newUserCount ?? 0
    }

    // 订单趋势
    if (orderTrendRes.data.code === 200 && orderTrendRes.data.data) {
      orderTrendData.value = orderTrendRes.data.data
    }

    // 商品销量 Top10
    if (topProductsRes.data.code === 200 && topProductsRes.data.data) {
      topProductsData.value = topProductsRes.data.data
    }

    // 订单状态分布
    if (orderStatusRes.data.code === 200 && orderStatusRes.data.data) {
      orderStatusData.value = orderStatusRes.data.data
    }

    // 用户增长趋势
    if (userTrendRes.data.code === 200 && userTrendRes.data.data) {
      userTrendData.value = userTrendRes.data.data
    }
  } catch (e) {
    ElMessage.error('数据加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchAllData()
})
</script>

<style lang="scss" scoped>
.dashboard {
  min-height: calc(100vh - 160px);
}

/* ======== 概览卡片行 ======== */
.overview-row {
  margin-bottom: 20px;
}

.stat-card {
  :deep(.el-card__body) {
    padding: 20px 24px;
  }
}
.stat-card-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  @include flex-center;
  color: #fff;
  flex-shrink: 0;
}
.stat-info {
  flex: 1;
  min-width: 0;
  .stat-label {
    font-size: $font-size-sm;
    color: $text-secondary;
    margin-bottom: 4px;
  }
  .stat-value {
    font-size: $font-size-xxl;
    font-weight: 700;
    color: $text-primary;
  }
}

/* ======== 图表卡片 ======== */
.chart-row-second {
  margin-top: 20px;
}

.chart-card {
  margin-bottom: 0;
  .chart-header {
    font-size: $font-size-md;
    font-weight: 600;
    color: $text-primary;
  }
  :deep(.el-card__body) {
    padding: 12px 16px 16px;
  }
}

.chart-container {
  width: 100%;
  height: 380px;
}

/* ======== 响应式 ======== */
@media (max-width: 768px) {
  .chart-container {
    height: 320px;
  }
  .stat-card-content {
    gap: 12px;
  }
  .stat-icon {
    width: 44px;
    height: 44px;
    border-radius: 10px;
  }
  .stat-value {
    font-size: $font-size-xl !important;
  }
}
</style>
