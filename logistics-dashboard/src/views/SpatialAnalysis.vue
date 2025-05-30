<template>
  <div class="spatial-analysis">
    <div class="page-header">
      <h2>空间分析</h2>
      <div class="header-controls">
        <el-select v-model="selectedMetric" placeholder="选择指标" @change="handleMetricChange">
          <el-option label="订单密度" value="orders" />
          <el-option label="配送时间" value="delivery_time" />
          <el-option label="配送员分布" value="couriers" />
        </el-select>
        <el-button type="primary" @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 空间统计概览 -->
    <el-row :gutter="20" class="spatial-overview">
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: #409EFF;">
              <el-icon :size="24"><Location /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-title">覆盖区域</div>
              <div class="overview-value">{{ spatialStats.totalAreas || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: #67C23A;">
              <el-icon :size="24"><MapLocation /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-title">热点区域</div>
              <div class="overview-value">{{ spatialStats.hotspots || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: #E6A23C;">
              <el-icon :size="24"><Position /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-title">平均密度</div>
              <div class="overview-value">{{ spatialStats.avgDensity || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="overview-content">
            <div class="overview-icon" style="background: #F56C6C;">
              <el-icon :size="24"><Guide /></el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-title">配送路径</div>
              <div class="overview-value">{{ spatialStats.totalRoutes || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热力图和统计图表 -->
    <el-row :gutter="20" class="main-content">
      <el-col :span="16">
        <el-card title="配送热力图" shadow="hover" class="heatmap-card">
          <HeatmapChart :options="heatmapOptions" height="500px" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card title="区域配送统计" shadow="hover" class="stats-card">
          <PieChart :options="regionalStatsOptions" height="250px" />
          <div class="stats-details">
            <div class="stats-item" v-for="item in regionalStats" :key="item.region">
              <span class="stats-label">{{ item.region }}:</span>
              <span class="stats-value">{{ item.orders }} 单</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 路径优化和交通分析 -->
    <el-row :gutter="20" class="analysis-section">
      <el-col :span="12">
        <el-card title="路径优化建议" shadow="hover">
          <div class="optimization-content">
            <div class="optimization-header">
              <el-input 
                v-model="routeStart" 
                placeholder="起点地址" 
                style="margin-right: 10px;"
              />
              <el-input 
                v-model="routeEnd" 
                placeholder="终点地址" 
                style="margin-right: 10px;"
              />
              <el-button type="primary" @click="getRouteOptimization">
                获取优化建议
              </el-button>
            </div>
            <div v-if="routeOptimization" class="optimization-result">
              <h4>优化建议：</h4>
              <p><strong>推荐路径：</strong>{{ routeOptimization.recommendedRoute }}</p>
              <p><strong>预计时间：</strong>{{ routeOptimization.estimatedTime }} 分钟</p>
              <p><strong>距离：</strong>{{ routeOptimization.distance }} 公里</p>
              <p><strong>节省时间：</strong>{{ routeOptimization.timeSaved }} 分钟</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="交通拥堵分析" shadow="hover">
          <BarChart :options="trafficAnalysisOptions" height="300px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 配送密度分析 -->
    <el-card title="配送密度时段分析" class="density-analysis">
      <LineChart :options="densityAnalysisOptions" height="350px" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { spatialAnalysisApi } from '@/api/spatialAnalysis'
import { Location, MapLocation, Position, Guide, Refresh } from '@element-plus/icons-vue'
import HeatmapChart from '@/components/charts/HeatmapChart.vue'
import PieChart from '@/components/charts/PieChart.vue'
import BarChart from '@/components/charts/BarChart.vue'
import LineChart from '@/components/charts/LineChart.vue'
import { ElMessage } from 'element-plus'

const dashboardStore = useDashboardStore()

const loading = ref(false)
const selectedMetric = ref('orders')
const routeStart = ref('')
const routeEnd = ref('')

const spatialStats = ref({})
const heatmapData = ref([])
const regionalStats = ref([])
const routeOptimization = ref(null)
const trafficData = ref([])
const densityData = ref([])

const heatmapOptions = computed(() => ({
  title: { text: '配送热力图' },
  tooltip: {
    position: 'top',
    formatter: function (params) {
      return `区域: ${params.name}<br/>数值: ${params.value[2]}`
    }
  },
  visualMap: {
    min: 0,
    max: Math.max(...(heatmapData.value.map(item => item[2]) || [100])),
    calculable: true,
    orient: 'horizontal',
    left: 'center',
    bottom: '15%',
    inRange: {
      color: ['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8', 
              '#ffffcc', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026']
    }
  },
  xAxis: {
    type: 'category',
    data: Array.from({ length: 10 }, (_, i) => `经度${i}`)
  },
  yAxis: {
    type: 'category',
    data: Array.from({ length: 10 }, (_, i) => `纬度${i}`)
  },
  series: [{
    name: selectedMetric.value,
    type: 'heatmap',
    data: heatmapData.value,
    label: { show: true }
  }]
}))

const regionalStatsOptions = computed(() => ({
  title: { text: '区域分布' },
  tooltip: { trigger: 'item' },
  series: [{
    name: '配送量',
    type: 'pie',
    radius: '70%',
    data: regionalStats.value.map(item => ({
      name: item.region,
      value: item.orders
    })),
    emphasis: {
      itemStyle: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)'
      }
    }
  }]
}))

const trafficAnalysisOptions = computed(() => ({
  title: { text: '交通拥堵分析' },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: ['早高峰', '上午', '中午', '下午', '晚高峰', '夜间']
  },
  yAxis: { type: 'value', name: '拥堵指数' },
  series: [{
    name: '拥堵指数',
    type: 'bar',
    data: trafficData.value,
    itemStyle: {
      color: function(params) {
        const colors = ['#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452']
        return colors[params.dataIndex % colors.length]
      }
    }
  }]
}))

const densityAnalysisOptions = computed(() => ({
  title: { text: '配送密度时段分析' },
  tooltip: { trigger: 'axis' },
  legend: { data: ['订单密度', '配送员密度', '平均响应时间'] },
  xAxis: {
    type: 'category',
    data: Array.from({ length: 24 }, (_, i) => `${i}:00`)
  },
  yAxis: [
    { type: 'value', name: '密度' },
    { type: 'value', name: '时间(分钟)', position: 'right' }
  ],
  series: [
    {
      name: '订单密度',
      type: 'line',
      data: densityData.value.map(item => item.orderDensity),
      smooth: true
    },
    {
      name: '配送员密度',
      type: 'line',
      data: densityData.value.map(item => item.courierDensity),
      smooth: true
    },
    {
      name: '平均响应时间',
      type: 'line',
      yAxisIndex: 1,
      data: densityData.value.map(item => item.responseTime),
      smooth: true
    }
  ]
}))

const loadSpatialData = async () => {
  loading.value = true
  try {
    const city = dashboardStore.selectedCity
    const today = new Date().toISOString().split('T')[0]
    
    // 获取热力图数据
    const heatmap = await spatialAnalysisApi.getHeatmapData(city, today, selectedMetric.value)
    if (heatmap && heatmap.data) {
      heatmapData.value = heatmap.data
    }
    
    // 获取区域统计
    const regional = await spatialAnalysisApi.getRegionalStats(city)
    regionalStats.value = regional || []
    
    // 获取交通分析
    const traffic = await spatialAnalysisApi.getTrafficAnalysis(city, [
      new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
      new Date().toISOString()
    ])
    trafficData.value = traffic || []
    
    // 获取配送密度分析
    const density = await spatialAnalysisApi.getDeliveryDensityAnalysis(city, today)
    densityData.value = density || []
    
    // 计算统计数据
    spatialStats.value = {
      totalAreas: regionalStats.value.length,
      hotspots: regionalStats.value.filter(item => item.orders > 100).length,
      avgDensity: Math.round(regionalStats.value.reduce((sum, item) => sum + item.orders, 0) / regionalStats.value.length),
      totalRoutes: Math.floor(Math.random() * 100) + 50 // 模拟数据
    }
    
  } catch (error) {
    console.error('Failed to load spatial data:', error)
    ElMessage.error('加载空间分析数据失败')
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadSpatialData()
}

const handleMetricChange = () => {
  loadSpatialData()
}

const getRouteOptimization = async () => {
  if (!routeStart.value || !routeEnd.value) {
    ElMessage.warning('请输入起点和终点地址')
    return
  }
  
  try {
    const city = dashboardStore.selectedCity
    const result = await spatialAnalysisApi.getRouteOptimization(city, routeStart.value, routeEnd.value)
    routeOptimization.value = result
    ElMessage.success('获取路径优化建议成功')
  } catch (error) {
    console.error('Failed to get route optimization:', error)
    ElMessage.error('获取路径优化建议失败')
  }
}

onMounted(() => {
  loadSpatialData()
})
</script>

<style scoped>
.spatial-analysis {
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

.spatial-overview {
  margin-bottom: 20px;
}

.overview-card {
  height: 120px;
}

.overview-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.overview-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
}

.overview-info {
  flex: 1;
}

.overview-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.overview-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.main-content {
  margin-bottom: 20px;
}

.heatmap-card {
  height: 600px;
}

.stats-card {
  height: 600px;
}

.stats-details {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.stats-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  padding: 5px 0;
}

.stats-label {
  color: #666;
}

.stats-value {
  font-weight: bold;
  color: #333;
}

.analysis-section {
  margin-bottom: 20px;
}

.optimization-content {
  padding: 10px 0;
}

.optimization-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.optimization-result {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
}

.optimization-result h4 {
  margin-bottom: 10px;
  color: #333;
}

.optimization-result p {
  margin-bottom: 8px;
  color: #666;
}

.density-analysis {
  margin-bottom: 20px;
}
</style>