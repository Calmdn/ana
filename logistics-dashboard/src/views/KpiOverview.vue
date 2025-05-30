<template>
  <div class="kpi-overview">
    <div class="page-header">
      <h2>KPI监控</h2>
      <div class="header-controls">
        <DateRangePicker 
          v-model="dateRange" 
          @change="handleDateChange"
          :show-shortcuts="true"
        />
        <el-button type="primary" @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- KPI 指标卡片 -->
    <el-row :gutter="20" class="kpi-cards">
      <el-col :span="6" v-for="(kpi, index) in kpiMetrics" :key="index">
        <el-card class="kpi-card" shadow="hover">
          <div class="kpi-content">
            <div class="kpi-icon" :style="{ background: kpi.color }">
              <el-icon :size="24">
                <component :is="kpi.icon" />
              </el-icon>
            </div>
            <div class="kpi-info">
              <div class="kpi-title">{{ kpi.title }}</div>
              <div class="kpi-value">{{ kpi.value }}{{ kpi.unit }}</div>
              <div class="kpi-trend" :class="kpi.trend > 0 ? 'up' : 'down'">
                <el-icon>
                  <component :is="kpi.trend > 0 ? 'TrendCharts' : 'Bottom'" />
                </el-icon>
                {{ Math.abs(kpi.trend) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- KPI 趋势图表 -->
    <el-row :gutter="20" class="chart-section">
      <el-col :span="24">
        <el-card title="KPI趋势分析" shadow="hover">
          <LineChart :options="trendChartOptions" height="400px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细数据表格 -->
    <el-card title="历史数据" class="data-table-card">
      <el-table :data="kpiHistory" v-loading="loading" stripe>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="totalOrders" label="订单总数" align="center" />
        <el-table-column prop="activeCouriers" label="活跃配送员" align="center" />
        <el-table-column prop="avgDeliveryTime" label="平均配送时间(分钟)" align="center" />
        <el-table-column prop="deliverySuccessRate" label="配送成功率(%)" align="center" />
        <el-table-column prop="customerSatisfaction" label="客户满意度" align="center" />
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { useKpiApi } from '@/composables/useApi'
import { Refresh, TrendCharts, Bottom, Timer, User, Document, Star } from '@element-plus/icons-vue'
import LineChart from '@/components/charts/LineChart.vue'
import DateRangePicker from '@/components/common/DateRangePicker.vue'

const dashboardStore = useDashboardStore()
const { getTodayKpi, getKpiTrend } = useKpiApi()

const loading = ref(false)
const dateRange = ref([])
const kpiHistory = ref([])
const pagination = ref({
  current: 1,
  pageSize: 20,
  total: 0
})

const kpiMetrics = ref([
  {
    title: '今日订单',
    value: 0,
    unit: '个',
    trend: 0,
    color: '#409EFF',
    icon: Document
  },
  {
    title: '活跃配送员',
    value: 0,
    unit: '人',
    trend: 0,
    color: '#67C23A',
    icon: User
  },
  {
    title: '平均配送时间',
    value: 0,
    unit: '分钟',
    trend: 0,
    color: '#E6A23C',
    icon: Timer
  },
  {
    title: '客户满意度',
    value: 0,
    unit: '分',
    trend: 0,
    color: '#F56C6C',
    icon: Star
  }
])

const trendChartOptions = computed(() => ({
  title: { text: 'KPI趋势分析' },
  tooltip: { trigger: 'axis' },
  legend: { data: ['订单量', '配送员数量', '平均配送时间'] },
  xAxis: {
    type: 'category',
    data: kpiHistory.value.map(item => item.date)
  },
  yAxis: [
    { type: 'value', name: '数量' },
    { type: 'value', name: '时间(分钟)', position: 'right' }
  ],
  series: [
    {
      name: '订单量',
      type: 'line',
      data: kpiHistory.value.map(item => item.totalOrders),
      smooth: true
    },
    {
      name: '配送员数量',
      type: 'line',
      data: kpiHistory.value.map(item => item.activeCouriers),
      smooth: true
    },
    {
      name: '平均配送时间',
      type: 'line',
      yAxisIndex: 1,
      data: kpiHistory.value.map(item => item.avgDeliveryTime),
      smooth: true
    }
  ]
}))

const loadKpiData = async () => {
  loading.value = true
  try {
    // 获取今日KPI
    const todayData = await getTodayKpi(dashboardStore.selectedCity)
    if (todayData) {
      kpiMetrics.value[0].value = todayData.totalOrders || 0
      kpiMetrics.value[1].value = todayData.activeCouriers || 0
      kpiMetrics.value[2].value = todayData.avgDeliveryTime || 0
      kpiMetrics.value[3].value = todayData.customerSatisfaction || 0
    }

    // 获取趋势数据
    const trendData = await getKpiTrend(dashboardStore.selectedCity, 30)
    kpiHistory.value = trendData || []
    pagination.value.total = kpiHistory.value.length

  } catch (error) {
    console.error('Failed to load KPI data:', error)
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadKpiData()
}

const handleDateChange = (dates) => {
  console.log('Date range changed:', dates)
  // 根据日期范围重新加载数据
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.current = 1
}

const handleCurrentChange = (page) => {
  pagination.value.current = page
}

onMounted(() => {
  loadKpiData()
})
</script>

<style scoped>
.kpi-overview {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-controls {
  display: flex;
  gap: 12px;
  align-items: center;
}

.kpi-cards {
  margin-bottom: 20px;
}

.kpi-card {
  height: 120px;
}

.kpi-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.kpi-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
}

.kpi-info {
  flex: 1;
}

.kpi-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.kpi-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.kpi-trend {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.kpi-trend.up {
  color: #67C23A;
}

.kpi-trend.down {
  color: #F56C6C;
}

.chart-section {
  margin-bottom: 20px;
}

.data-table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>