<template>
  <div class="operational-efficiency">
    <div class="page-header">
      <h2>运营效率分析</h2>
      <div class="header-controls">
        <DateRangePicker v-model="dateRange" @change="handleDateChange" />
        <el-select v-model="selectedWarehouse" placeholder="选择仓库" clearable>
          <el-option label="全部仓库" value="" />
          <el-option label="北京仓库" value="BJ001" />
          <el-option label="上海仓库" value="SH001" />
          <el-option label="广州仓库" value="GZ001" />
        </el-select>
        <el-button type="primary" @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 运营指标概览 -->
    <el-row :gutter="20" class="metrics-overview">
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon" style="background: #409EFF;">
              <el-icon :size="24"><Operation /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-title">整体效率指数</div>
              <div class="metric-value">{{ overviewData.overallEfficiency || 0 }}%</div>
              <div class="metric-trend" :class="overviewData.efficiencyTrend > 0 ? 'up' : 'down'">
                {{ overviewData.efficiencyTrend > 0 ? '↑' : '↓' }} 
                {{ Math.abs(overviewData.efficiencyTrend || 0) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon" style="background: #67C23A;">
              <el-icon :size="24"><Truck /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-title">车辆利用率</div>
              <div class="metric-value">{{ overviewData.vehicleUtilization || 0 }}%</div>
              <div class="metric-trend" :class="overviewData.vehicleTrend > 0 ? 'up' : 'down'">
                {{ overviewData.vehicleTrend > 0 ? '↑' : '↓' }} 
                {{ Math.abs(overviewData.vehicleTrend || 0) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon" style="background: #E6A23C;">
              <el-icon :size="24"><Box /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-title">仓库处理效率</div>
              <div class="metric-value">{{ overviewData.warehouseEfficiency || 0 }}%</div>
              <div class="metric-trend" :class="overviewData.warehouseTrend > 0 ? 'up' : 'down'">
                {{ overviewData.warehouseTrend > 0 ? '↑' : '↓' }} 
                {{ Math.abs(overviewData.warehouseTrend || 0) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-content">
            <div class="metric-icon" style="background: #F56C6C;">
              <el-icon :size="24"><Money /></el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-title">成本效益比</div>
              <div class="metric-value">{{ overviewData.costEfficiency || 0 }}</div>
              <div class="metric-trend" :class="overviewData.costTrend > 0 ? 'up' : 'down'">
                {{ overviewData.costTrend > 0 ? '↑' : '↓' }} 
                {{ Math.abs(overviewData.costTrend || 0) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 效率分析图表 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :span="12">
        <el-card title="配送员效率分析" shadow="hover">
          <BarChart :options="courierEfficiencyOptions" height="350px" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="车辆利用率趋势" shadow="hover">
          <LineChart :options="vehicleUtilizationOptions" height="350px" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-section">
      <el-col :span="8">
        <el-card title="订单处理效率" shadow="hover">
          <GaugeChart :options="orderProcessingGaugeOptions" height="300px" />
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card title="成本分析" shadow="hover">
          <LineChart :options="costAnalysisOptions" height="300px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 运营优化建议 -->
    <el-card title="运营优化建议" class="optimization-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="suggestions-list">
            <div 
              v-for="(suggestion, index) in optimizationSuggestions" 
              :key="index"
              class="suggestion-item"
              :class="suggestion.priority"
            >
              <div class="suggestion-header">
                <el-icon>
                  <component :is="getSuggestionIcon(suggestion.type)" />
                </el-icon>
                <span class="suggestion-title">{{ suggestion.title }}</span>
                <el-tag :type="getPriorityTagType(suggestion.priority)" size="small">
                  {{ getPriorityLabel(suggestion.priority) }}
                </el-tag>
              </div>
              <div class="suggestion-content">{{ suggestion.description }}</div>
              <div class="suggestion-impact">
                <span>预期收益: {{ suggestion.expectedBenefit }}</span>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="optimization-charts">
            <PieChart :options="optimizationImpactOptions" height="300px" />
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 服务质量指标 -->
    <el-card title="服务质量指标" class="quality-section">
      <el-table :data="serviceQualityMetrics" v-loading="loading" stripe>
        <el-table-column prop="metric" label="指标" width="150" />
        <el-table-column prop="currentValue" label="当前值" align="center" />
        <el-table-column prop="target" label="目标值" align="center" />
        <el-table-column prop="completion" label="完成度" align="center">
          <template #default="{ row }">
            <el-progress 
              :percentage="row.completion" 
              :color="getProgressColor(row.completion)"
              :stroke-width="8"
            />
          </template>
        </el-table-column>
        <el-table-column prop="trend" label="趋势" align="center">
          <template #default="{ row }">
            <el-tag :type="row.trend > 0 ? 'success' : 'danger'" size="small">
              {{ row.trend > 0 ? '↑' : '↓' }} {{ Math.abs(row.trend) }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastUpdate" label="更新时间" width="150" />
      </el-table>
    </el-card>

    <!-- 容量与需求分析 -->
    <el-card title="容量与需求分析" class="capacity-section">
      <LineChart :options="capacityDemandOptions" height="400px" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { operationalEfficiencyApi } from '@/api/operationalEfficiency'
import { Operation, Truck, Box, Money, Refresh, Setting, TrendCharts, Warning } from '@element-plus/icons-vue'
import LineChart from '@/components/charts/LineChart.vue'
import BarChart from '@/components/charts/BarChart.vue'
import PieChart from '@/components/charts/PieChart.vue'
import GaugeChart from '@/components/charts/GaugeChart.vue'
import DateRangePicker from '@/components/common/DateRangePicker.vue'

const dashboardStore = useDashboardStore()

const loading = ref(false)
const dateRange = ref([])
const selectedWarehouse = ref('')

// 概览数据
const overviewData = ref({})
const courierEfficiencyData = ref([])
const vehicleUtilizationData = ref([])
const orderProcessingData = ref({})
const costAnalysisData = ref([])
const optimizationSuggestions = ref([])
const serviceQualityMetrics = ref([])
const capacityDemandData = ref([])

// 配送员效率分析图配置
const courierEfficiencyOptions = computed(() => ({
  title: { text: '配送员效率分析' },
  tooltip: { trigger: 'axis' },
  legend: { data: ['效率分数', '订单完成率'] },
  xAxis: {
    type: 'category',
    data: courierEfficiencyData.value.map(item => item.courierName)
  },
  yAxis: [
    { type: 'value', name: '效率分数' },
    { type: 'value', name: '完成率(%)', position: 'right' }
  ],
  series: [
    {
      name: '效率分数',
      type: 'bar',
      data: courierEfficiencyData.value.map(item => item.efficiencyScore),
      itemStyle: { color: '#409EFF' }
    },
    {
      name: '订单完成率',
      type: 'line',
      yAxisIndex: 1,
      data: courierEfficiencyData.value.map(item => item.completionRate),
      itemStyle: { color: '#67C23A' }
    }
  ]
}))

// 车辆利用率趋势图配置
const vehicleUtilizationOptions = computed(() => ({
  title: { text: '车辆利用率趋势' },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: vehicleUtilizationData.value.map(item => item.date)
  },
  yAxis: { type: 'value', name: '利用率(%)' },
  series: [{
    name: '车辆利用率',
    type: 'line',
    data: vehicleUtilizationData.value.map(item => item.utilization),
    smooth: true,
    areaStyle: { opacity: 0.3 },
    itemStyle: { color: '#E6A23C' }
  }]
}))

// 订单处理效率仪表盘配置
const orderProcessingGaugeOptions = computed(() => ({
  series: [{
    type: 'gauge',
    progress: { show: true },
    detail: {
      valueAnimation: true,
      formatter: '{value}%'
    },
    data: [{
      value: orderProcessingData.value.efficiency || 0,
      name: '处理效率'
    }],
    max: 100
  }]
}))

// 成本分析图配置
const costAnalysisOptions = computed(() => ({
  title: { text: '成本分析' },
  tooltip: { trigger: 'axis' },
  legend: { data: ['运输成本', '人工成本', '仓储成本', '其他成本'] },
  xAxis: {
    type: 'category',
    data: costAnalysisData.value.map(item => item.date)
  },
  yAxis: { type: 'value', name: '成本(元)' },
  series: [
    {
      name: '运输成本',
      type: 'line',
      stack: 'cost',
      data: costAnalysisData.value.map(item => item.transportCost)
    },
    {
      name: '人工成本',
      type: 'line',
      stack: 'cost',
      data: costAnalysisData.value.map(item => item.laborCost)
    },
    {
      name: '仓储成本',
      type: 'line',
      stack: 'cost',
      data: costAnalysisData.value.map(item => item.storageCost)
    },
    {
      name: '其他成本',
      type: 'line',
      stack: 'cost',
      data: costAnalysisData.value.map(item => item.otherCost)
    }
  ]
}))

// 优化影响分析饼图配置
const optimizationImpactOptions = computed(() => ({
  title: { text: '优化影响分析' },
  tooltip: { trigger: 'item' },
  series: [{
    name: '优化类型',
    type: 'pie',
    radius: '60%',
    data: [
      { name: '路线优化', value: 35 },
      { name: '资源配置', value: 25 },
      { name: '流程改进', value: 20 },
      { name: '技术升级', value: 20 }
    ]
  }]
}))

// 容量需求分析图配置
const capacityDemandOptions = computed(() => ({
  title: { text: '容量与需求分析' },
  tooltip: { trigger: 'axis' },
  legend: { data: ['当前容量', '需求量', '容量利用率'] },
  xAxis: {
    type: 'category',
    data: capacityDemandData.value.map(item => item.date)
  },
  yAxis: [
    { type: 'value', name: '数量' },
    { type: 'value', name: '利用率(%)', position: 'right' }
  ],
  series: [
    {
      name: '当前容量',
      type: 'bar',
      data: capacityDemandData.value.map(item => item.capacity)
    },
    {
      name: '需求量',
      type: 'bar',
      data: capacityDemandData.value.map(item => item.demand)
    },
    {
      name: '容量利用率',
      type: 'line',
      yAxisIndex: 1,
      data: capacityDemandData.value.map(item => item.utilizationRate)
    }
  ]
}))

const getSuggestionIcon = (type) => {
  const iconMap = {
    route: 'TrendCharts',
    resource: 'Setting',
    process: 'Operation',
    technology: 'Cpu'
  }
  return iconMap[type] || 'Setting'
}

const getPriorityTagType = (priority) => {
  const typeMap = {
    high: 'danger',
    medium: 'warning',
    low: 'success'
  }
  return typeMap[priority] || 'info'
}

const getPriorityLabel = (priority) => {
  const labelMap = {
    high: '高优先级',
    medium: '中优先级',
    low: '低优先级'
  }
  return labelMap[priority] || priority
}

const getProgressColor = (percentage) => {
  if (percentage >= 90) return '#67C23A'
  if (percentage >= 70) return '#E6A23C'
  return '#F56C6C'
}

const loadOperationalData = async () => {
  loading.value = true
  try {
    const city = dashboardStore.selectedCity
    
    // 获取运营效率概览
    const overview = await operationalEfficiencyApi.getOverview(city)
    overviewData.value = overview || {}
    
    // 获取配送员效率分析
    const courierEfficiency = await operationalEfficiencyApi.getCourierEfficiency(city, dateRange.value)
    courierEfficiencyData.value = courierEfficiency || []
    
    // 获取车辆利用率
    const vehicleUtilization = await operationalEfficiencyApi.getVehicleUtilization(city, new Date().toISOString().split('T')[0])
    vehicleUtilizationData.value = vehicleUtilization || []
    
    // 获取订单处理效率
    const orderProcessing = await operationalEfficiencyApi.getOrderProcessingEfficiency(city, dateRange.value)
    orderProcessingData.value = orderProcessing || {}
    
    // 获取成本分析
    const costAnalysis = await operationalEfficiencyApi.getCostAnalysis(city, 'month')
    costAnalysisData.value = costAnalysis || []
    
    // 获取优化建议
    const optimization = await operationalEfficiencyApi.getOptimizationSuggestions(city)
    optimizationSuggestions.value = optimization || []
    
    // 获取服务质量指标
    const serviceQuality = await operationalEfficiencyApi.getServiceQualityMetrics(city, dateRange.value)
    serviceQualityMetrics.value = serviceQuality || []
    
    // 获取容量需求分析
    const capacityDemand = await operationalEfficiencyApi.getCapacityDemandAnalysis(city, 'week')
    capacityDemandData.value = capacityDemand || []
    
  } catch (error) {
    console.error('Failed to load operational efficiency data:', error)
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadOperationalData()
}

const handleDateChange = (dates) => {
  dateRange.value = dates
  loadOperationalData()
}

onMounted(() => {
  loadOperationalData()
})
</script>

<style scoped>
.operational-efficiency {
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

.optimization-section,
.quality-section,
.capacity-section {
  margin-bottom: 20px;
}

.suggestions-list {
  max-height: 400px;
  overflow-y: auto;
}

.suggestion-item {
  padding: 15px;
  margin-bottom: 10px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fafafa;
}

.suggestion-item.high {
  border-left: 4px solid #F56C6C;
}

.suggestion-item.medium {
  border-left: 4px solid #E6A23C;
}

.suggestion-item.low {
  border-left: 4px solid #67C23A;
}

.suggestion-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.suggestion-title {
  font-weight: bold;
  flex: 1;
}

.suggestion-content {
  color: #666;
  margin-bottom: 8px;
  line-height: 1.5;
}

.suggestion-impact {
  font-size: 12px;
  color: #999;
}
</style>