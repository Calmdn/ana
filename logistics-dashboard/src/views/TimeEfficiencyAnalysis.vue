<template>
  <div class="time-efficiency-analysis">
    <div class="page-header">
      <h2>时间效率分析</h2>
      <div class="header-controls">
        <DateRangePicker v-model="dateRange" @change="handleDateChange" />
        <el-button type="primary" @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 效率指标概览 -->
    <el-row :gutter="20" class="metrics-overview">
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon" style="background: #409EFF;">
              <el-icon :size="24"><Timer /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-title">平均配送时间</div>
              <div class="metric-value">{{ overviewData.avgDeliveryTime || 0 }}分钟</div>
              <div class="metric-trend" :class="overviewData.deliveryTimeTrend > 0 ? 'down' : 'up'">
                {{ overviewData.deliveryTimeTrend > 0 ? '↑' : '↓' }} 
                {{ Math.abs(overviewData.deliveryTimeTrend || 0) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon" style="background: #67C23A;">
              <el-icon :size="24"><CircleCheck /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-title">准时送达率</div>
              <div class="metric-value">{{ overviewData.onTimeRate || 0 }}%</div>
              <div class="metric-trend" :class="overviewData.onTimeRateTrend > 0 ? 'up' : 'down'">
                {{ overviewData.onTimeRateTrend > 0 ? '↑' : '↓' }} 
                {{ Math.abs(overviewData.onTimeRateTrend || 0) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon" style="background: #E6A23C;">
              <el-icon :size="24"><Clock /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-title">延迟订单数</div>
              <div class="metric-value">{{ overviewData.delayedOrders || 0 }}</div>
              <div class="metric-trend" :class="overviewData.delayedOrdersTrend > 0 ? 'down' : 'up'">
                {{ overviewData.delayedOrdersTrend > 0 ? '↑' : '↓' }} 
                {{ Math.abs(overviewData.delayedOrdersTrend || 0) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon" style="background: #F56C6C;">
              <el-icon :size="24"><TrendCharts /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-title">效率指数</div>
              <div class="metric-value">{{ overviewData.efficiencyIndex || 0 }}</div>
              <div class="metric-trend" :class="overviewData.efficiencyIndexTrend > 0 ? 'up' : 'down'">
                {{ overviewData.efficiencyIndexTrend > 0 ? '↑' : '↓' }} 
                {{ Math.abs(overviewData.efficiencyIndexTrend || 0) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表分析区域 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :span="12">
        <el-card title="配送时间分布" shadow="hover">
          <BarChart :options="deliveryTimeDistributionOptions" height="350px" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="时段效率分析" shadow="hover">
          <LineChart :options="hourlyEfficiencyOptions" height="350px" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-section">
      <el-col :span="24">
        <el-card title="效率趋势分析" shadow="hover">
          <LineChart :options="efficiencyTrendOptions" height="400px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 配送员效率排名 -->
    <el-card title="配送员效率排名" class="ranking-section">
      <el-table :data="courierRanking" v-loading="loading" stripe>
        <el-table-column type="index" label="排名" width="80" />
        <el-table-column prop="courierId" label="配送员ID" width="120" />
        <el-table-column prop="courierName" label="姓名" width="100" />
        <el-table-column prop="totalOrders" label="配送订单数" align="center" />
        <el-table-column prop="avgDeliveryTime" label="平均配送时间(分钟)" align="center" />
        <el-table-column prop="onTimeRate" label="准时率(%)" align="center" />
        <el-table-column prop="efficiencyScore" label="效率评分" align="center">
          <template #default="{ row }">
            <el-tag :type="getEfficiencyTagType(row.efficiencyScore)">
              {{ row.efficiencyScore }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 区域效率对比 -->
    <el-card title="区域效率对比" class="regional-section">
      <BarChart :options="regionalEfficiencyOptions" height="350px" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { timeEfficiencyApi } from '@/api/timeEfficiency'
import { Timer, CircleCheck, Clock, TrendCharts, Refresh } from '@element-plus/icons-vue'
import LineChart from '@/components/charts/LineChart.vue'
import BarChart from '@/components/charts/BarChart.vue'
import DateRangePicker from '@/components/common/DateRangePicker.vue'

const dashboardStore = useDashboardStore()

const loading = ref(false)
const dateRange = ref([])
const overviewData = ref({})
const deliveryTimeData = ref([])
const hourlyEfficiencyData = ref([])
const efficiencyTrendData = ref([])
const courierRanking = ref([])
const regionalData = ref([])

const deliveryTimeDistributionOptions = computed(() => ({
  title: { text: '配送时间分布' },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: ['0-30分钟', '30-60分钟', '60-90分钟', '90-120分钟', '120分钟以上']
  },
  yAxis: { type: 'value', name: '订单数量' },
  series: [{
    name: '订单数量',
    type: 'bar',
    data: deliveryTimeData.value,
    itemStyle: { color: '#409EFF' }
  }]
}))

const hourlyEfficiencyOptions = computed(() => ({
  title: { text: '时段效率分析' },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: Array.from({ length: 24 }, (_, i) => `${i}:00`)
  },
  yAxis: { type: 'value', name: '平均配送时间(分钟)' },
  series: [{
    name: '平均配送时间',
    type: 'line',
    data: hourlyEfficiencyData.value,
    smooth: true,
    itemStyle: { color: '#67C23A' }
  }]
}))

const efficiencyTrendOptions = computed(() => ({
  title: { text: '效率趋势分析' },
  tooltip: { trigger: 'axis' },
  legend: { data: ['平均配送时间', '准时率', '效率指数'] },
  xAxis: {
    type: 'category',
    data: efficiencyTrendData.value.map(item => item.date)
  },
  yAxis: [
    { type: 'value', name: '时间(分钟)' },
    { type: 'value', name: '比率(%)', position: 'right' }
  ],
  series: [
    {
      name: '平均配送时间',
      type: 'line',
      data: efficiencyTrendData.value.map(item => item.avgDeliveryTime),
      smooth: true
    },
    {
      name: '准时率',
      type: 'line',
      yAxisIndex: 1,
      data: efficiencyTrendData.value.map(item => item.onTimeRate),
      smooth: true
    },
    {
      name: '效率指数',
      type: 'line',
      yAxisIndex: 1,
      data: efficiencyTrendData.value.map(item => item.efficiencyIndex),
      smooth: true
    }
  ]
}))

const regionalEfficiencyOptions = computed(() => ({
  title: { text: '区域效率对比' },
  tooltip: { trigger: 'axis' },
  legend: { data: ['平均配送时间', '准时率'] },
  xAxis: {
    type: 'category',
    data: regionalData.value.map(item => item.region)
  },
  yAxis: [
    { type: 'value', name: '时间(分钟)' },
    { type: 'value', name: '比率(%)', position: 'right' }
  ],
  series: [
    {
      name: '平均配送时间',
      type: 'bar',
      data: regionalData.value.map(item => item.avgDeliveryTime)
    },
    {
      name: '准时率',
      type: 'line',
      yAxisIndex: 1,
      data: regionalData.value.map(item => item.onTimeRate)
    }
  ]
}))

const getEfficiencyTagType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'warning'
  return 'danger'
}

const loadTimeEfficiencyData = async () => {
  loading.value = true
  try {
    const city = dashboardStore.selectedCity
    
    // 获取概览数据
    const overview = await timeEfficiencyApi.getOverview(city, dateRange.value)
    overviewData.value = overview || {}
    
    // 获取配送时间分布
    const distribution = await timeEfficiencyApi.getDeliveryTimeDistribution(city, new Date().toISOString().split('T')[0])
    deliveryTimeData.value = distribution || []
    
    // 获取时段效率
    const hourly = await timeEfficiencyApi.getHourlyEfficiency(city, new Date().toISOString().split('T')[0])
    hourlyEfficiencyData.value = hourly || []
    
    // 获取效率趋势
    const trend = await timeEfficiencyApi.getEfficiencyTrend(city, 30)
    efficiencyTrendData.value = trend || []
    
    // 获取配送员排名
    const ranking = await timeEfficiencyApi.getCourierEfficiencyRanking(city, 20)
    courierRanking.value = ranking || []
    
    // 获取区域效率
    const regional = await timeEfficiencyApi.getRegionalEfficiency(city)
    regionalData.value = regional || []
    
  } catch (error) {
    console.error('Failed to load time efficiency data:', error)
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadTimeEfficiencyData()
}

const handleDateChange = (dates) => {
  dateRange.value = dates
  loadTimeEfficiencyData()
}

onMounted(() => {
  loadTimeEfficiencyData()
})
</script>

<style scoped>
.time-efficiency-analysis {
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

.metrics-overview {
  margin-bottom: 20px;
}

.metric-card {
  height: 120px;
}

.metric-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.metric-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
}

.metric-info {
  flex: 1;
}

.metric-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.metric-trend {
  font-size: 12px;
}

.metric-trend.up {
  color: #67C23A;
}

.metric-trend.down {
  color: #F56C6C;
}

.charts-section {
  margin-bottom: 20px;
}

.ranking-section,
.regional-section {
  margin-bottom: 20px;
}
</style>