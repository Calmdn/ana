<template>
  <div class="dashboard">
    <el-row :gutter="20" class="summary-cards">
      <el-col :span="6" v-for="(card, index) in summaryCards" :key="index">
        <el-card class="summary-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon" :style="{ background: card.color }">
              <el-icon :size="24"><component :is="card.icon" /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">{{ card.title }}</div>
              <div class="card-value">{{ card.value }}</div>
              <div class="card-trend" :class="card.trend > 0 ? 'up' : 'down'">
                {{ card.trend > 0 ? '↑' : '↓' }} {{ Math.abs(card.trend) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card title="订单量趋势" shadow="hover">
          <LineChart :data="orderTrendData" :options="orderTrendOptions" />
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card title="配送效率分布" shadow="hover">
          <PieChart :data="efficiencyData" :options="efficiencyOptions" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="8">
        <el-card title="实时告警" shadow="hover">
          <div class="alert-list">
            <div 
              v-for="alert in recentAlerts" 
              :key="alert.id"
              class="alert-item"
              :class="alert.anomalySeverity.toLowerCase()"
            >
              <div class="alert-type">{{ alert.anomalyType }}</div>
              <div class="alert-city">{{ alert.city }}</div>
              <div class="alert-time">{{ formatTime(alert.createdAt) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card title="城市配送热力图" shadow="hover">
          <HeatmapChart :data="heatmapData" :options="heatmapOptions" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { TrendCharts, Warning, Timer, Location } from '@element-plus/icons-vue'
import LineChart from '@/components/charts/LineChart.vue'
import PieChart from '@/components/charts/PieChart.vue'
import HeatmapChart from '@/components/charts/HeatmapChart.vue'
import { kpiApi } from '@/api/kpi'
import { alertsApi } from '@/api/alerts'
import { useDashboardStore } from '@/stores/dashboard'

const dashboardStore = useDashboardStore()

const summaryCards = ref([
  {
    title: '今日订单',
    value: '0',
    trend: 0,
    color: '#409EFF',
    icon: TrendCharts
  },
  {
    title: '活跃配送员',
    value: '0',
    trend: 0,
    color: '#67C23A',
    icon: Location
  },
  {
    title: '平均配送时间',
    value: '0min',
    trend: 0,
    color: '#E6A23C',
    icon: Timer
  },
  {
    title: '未解决告警',
    value: '0',
    trend: 0,
    color: '#F56C6C',
    icon: Warning
  }
])

const orderTrendData = ref([])
const efficiencyData = ref([])
const heatmapData = ref([])
const recentAlerts = ref([])

const orderTrendOptions = {
  title: { text: '' },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [{
    name: '订单量',
    type: 'line',
    data: [],
    smooth: true
  }]
}

const efficiencyOptions = {
  title: { text: '' },
  tooltip: { trigger: 'item' },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [{
    name: '配送效率',
    type: 'pie',
    radius: '50%',
    data: []
  }]
}

const heatmapOptions = {
  title: { text: '' },
  tooltip: { position: 'top' },
  grid: {
    height: '50%',
    top: '10%'
  },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: {
    type: 'category',
    data: []
  },
  visualMap: {
    min: 0,
    max: 1000,
    calculable: true,
    orient: 'horizontal',
    left: 'center',
    bottom: '15%'
  },
  series: [{
    name: '配送量',
    type: 'heatmap',
    data: []
  }]
}

const formatTime = (timeStr) => {
  return new Date(timeStr).toLocaleTimeString('zh-CN')
}

const loadDashboardData = async () => {
  try {
    // 加载今日KPI数据
    const todayKpi = await kpiApi.getTodayKpi(dashboardStore.selectedCity)
    if (todayKpi && todayKpi.length > 0) {
      const latestKpi = todayKpi[todayKpi.length - 1]
      summaryCards.value[0].value = latestKpi.totalOrders
      summaryCards.value[1].value = latestKpi.activeCouriers
    }
    
    // 加载最近告警
    const alerts = await alertsApi.getRecentAlerts(10)
    recentAlerts.value = alerts || []
    summaryCards.value[3].value = recentAlerts.value.filter(alert => !alert.isResolved).length
    
    // 加载订单趋势数据
    const recentKpi = await kpiApi.getRecentKpi(dashboardStore.selectedCity, 7)
    if (recentKpi && recentKpi.length > 0) {
      const dates = recentKpi.map(item => item.date)
      const orders = recentKpi.map(item => item.totalOrders)
      
      orderTrendOptions.xAxis.data = dates
      orderTrendOptions.series[0].data = orders
      orderTrendData.value = recentKpi
    }
    
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.summary-cards {
  margin-bottom: 20px;
}

.summary-card {
  height: 120px;
}

.card-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
}

.card-info {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.card-trend {
  font-size: 12px;
}

.card-trend.up {
  color: #67C23A;
}

.card-trend.down {
  color: #F56C6C;
}

.chart-row {
  margin-bottom: 20px;
}

.alert-list {
  max-height: 300px;
  overflow-y: auto;
}

.alert-item {
  padding: 8px;
  border-left: 4px solid;
  margin-bottom: 8px;
  border-radius: 4px;
  background: #f9f9f9;
}

.alert-item.high {
  border-left-color: #F56C6C;
}

.alert-item.medium {
  border-left-color: #E6A23C;
}

.alert-item.low {
  border-left-color: #67C23A;
}

.alert-type {
  font-weight: bold;
  font-size: 14px;
}

.alert-city {
  font-size: 12px;
  color: #666;
}

.alert-time {
  font-size: 11px;
  color: #999;
}
</style>