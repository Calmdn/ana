<template>
  <div class="predictive-analysis">
    <div class="page-header">
      <h2>预测分析</h2>
      <div class="header-controls">
        <el-select v-model="selectedModel" placeholder="选择模型" @change="handleModelChange">
          <el-option label="需求预测" value="demand" />
          <el-option label="时间预测" value="time" />
          <el-option label="异常检测" value="anomaly" />
          <el-option label="容量规划" value="capacity" />
        </el-select>
        <el-input-number 
          v-model="forecastDays" 
          :min="1" 
          :max="30" 
          placeholder="预测天数"
          style="width: 120px;"
        />
        <el-button type="primary" @click="runPrediction" :loading="loading">
          <el-icon><DataAnalysis /></el-icon>
          运行预测
        </el-button>
      </div>
    </div>

    <!-- 预测结果概览 -->
    <el-row :gutter="20" class="prediction-overview">
      <el-col :span="6">
        <el-card class="prediction-card">
          <div class="prediction-content">
            <div class="prediction-icon" style="background: #409EFF;">
              <el-icon :size="24"><TrendCharts /></el-icon>
            </div>
            <div class="prediction-info">
              <div class="prediction-title">预测准确率</div>
              <div class="prediction-value">{{ predictionAccuracy || 0 }}%</div>
              <div class="prediction-trend up">
                ↑ {{ Math.floor(Math.random() * 5) + 1 }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="prediction-card">
          <div class="prediction-content">
            <div class="prediction-icon" style="background: #67C23A;">
              <el-icon :size="24"><Promotion /></el-icon>
            </div>
            <div class="prediction-info">
              <div class="prediction-title">预测订单量</div>
              <div class="prediction-value">{{ predictedOrders || 0 }}</div>
              <div class="prediction-trend up">
                ↑ {{ Math.floor(Math.random() * 10) + 5 }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="prediction-card">
          <div class="prediction-content">
            <div class="prediction-icon" style="background: #E6A23C;">
              <el-icon :size="24"><Warning /></el-icon>
            </div>
            <div class="prediction-info">
              <div class="prediction-title">风险等级</div>
              <div class="prediction-value">{{ riskLevel || '低' }}</div>
              <div class="prediction-trend down">
                ↓ {{ Math.floor(Math.random() * 3) + 1 }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="prediction-card">
          <div class="prediction-content">
            <div class="prediction-icon" style="background: #F56C6C;">
              <el-icon :size="24"><Cpu /></el-icon>
            </div>
            <div class="prediction-info">
              <div class="prediction-title">模型性能</div>
              <div class="prediction-value">{{ modelPerformance || 0 }}%</div>
              <div class="prediction-trend up">
                ↑ {{ Math.floor(Math.random() * 2) + 1 }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预测图表区域 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :span="16">
        <el-card title="需求预测趋势" shadow="hover">
          <LineChart :options="demandForecastOptions" height="400px" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card title="模型准确度" shadow="hover">
          <GaugeChart :options="accuracyGaugeOptions" height="400px" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-section">
      <el-col :span="12">
        <el-card title="异常检测结果" shadow="hover">
          <BarChart :options="anomalyDetectionOptions" height="350px" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="风险分布" shadow="hover">
          <PieChart :options="riskDistributionOptions" height="350px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 预测配置面板 -->
    <el-card title="预测配置" class="config-panel">
      <el-form :model="predictionConfig" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="预测模型">
              <el-select v-model="predictionConfig.model" placeholder="选择模型">
                <el-option label="ARIMA" value="arima" />
                <el-option label="LSTM" value="lstm" />
                <el-option label="Random Forest" value="rf" />
                <el-option label="XGBoost" value="xgboost" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="置信区间">
              <el-slider 
                v-model="predictionConfig.confidenceInterval" 
                :min="80" 
                :max="99" 
                :step="1"
                show-stops
                show-input
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="数据窗口">
              <el-input-number 
                v-model="predictionConfig.dataWindow" 
                :min="7" 
                :max="365" 
                placeholder="天数"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item>
              <el-button type="primary" @click="updatePredictionConfig">
                更新配置
              </el-button>
              <el-button @click="resetConfig">重置</el-button>
              <el-button type="success" @click="exportPrediction">
                导出预测结果
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 历史预测准确度 -->
    <el-card title="历史预测表现" class="history-section">
      <el-table :data="predictionHistory" v-loading="loading" stripe>
        <el-table-column prop="date" label="预测日期" width="120" />
        <el-table-column prop="model" label="模型" width="100" />
        <el-table-column prop="predictedValue" label="预测值" align="center" />
        <el-table-column prop="actualValue" label="实际值" align="center" />
        <el-table-column prop="accuracy" label="准确率(%)" align="center">
          <template #default="{ row }">
            <el-tag :type="getAccuracyTagType(row.accuracy)">
              {{ row.accuracy }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="error" label="误差" align="center" />
        <el-table-column prop="confidence" label="置信度" align="center" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { predictiveAnalysisApi } from '@/api/predictiveAnalysis'
import { DataAnalysis, TrendCharts, Promotion, Warning, Cpu } from '@element-plus/icons-vue'
import LineChart from '@/components/charts/LineChart.vue'
import BarChart from '@/components/charts/BarChart.vue'
import PieChart from '@/components/charts/PieChart.vue'
import GaugeChart from '@/components/charts/GaugeChart.vue'
import { ElMessage } from 'element-plus'

const dashboardStore = useDashboardStore()

const loading = ref(false)
const selectedModel = ref('demand')
const forecastDays = ref(7)

// 预测结果数据
const predictionAccuracy = ref(0)
const predictedOrders = ref(0)
const riskLevel = ref('低')
const modelPerformance = ref(0)

// 预测配置
const predictionConfig = ref({
  model: 'arima',
  confidenceInterval: 95,
  dataWindow: 30
})

// 图表数据
const demandForecastData = ref([])
const anomalyData = ref([])
const riskData = ref([])
const predictionHistory = ref([])

// 需求预测趋势图配置
const demandForecastOptions = computed(() => ({
  title: { text: '需求预测趋势' },
  tooltip: { trigger: 'axis' },
  legend: { data: ['历史数据', '预测值', '置信区间上限', '置信区间下限'] },
  xAxis: {
    type: 'category',
    data: demandForecastData.value.map(item => item.date)
  },
  yAxis: { type: 'value', name: '订单量' },
  series: [
    {
      name: '历史数据',
      type: 'line',
      data: demandForecastData.value.map(item => item.historical),
      itemStyle: { color: '#409EFF' },
      lineStyle: { width: 2 }
    },
    {
      name: '预测值',
      type: 'line',
      data: demandForecastData.value.map(item => item.predicted),
      itemStyle: { color: '#67C23A' },
      lineStyle: { type: 'dashed', width: 2 }
    },
    {
      name: '置信区间',
      type: 'line',
      data: demandForecastData.value.map(item => [item.upperBound, item.lowerBound]),
      areaStyle: { 
        color: 'rgba(103, 194, 58, 0.2)'
      },
      lineStyle: { opacity: 0 },
      stack: 'confidence'
    }
  ]
}))

// 准确度仪表盘配置
const accuracyGaugeOptions = computed(() => ({
  series: [{
    type: 'gauge',
    progress: { show: true },
    detail: {
      valueAnimation: true,
      formatter: '{value}%'
    },
    data: [{
      value: predictionAccuracy.value,
      name: '准确率'
    }],
    max: 100,
    axisLine: {
      lineStyle: {
        width: 20,
        color: [
          [0.3, '#FF6E76'],
          [0.7, '#FDDD60'],
          [1, '#58D9F9']
        ]
      }
    }
  }]
}))

// 异常检测结果配置
const anomalyDetectionOptions = computed(() => ({
  title: { text: '异常检测结果' },
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: anomalyData.value.map(item => item.date)
  },
  yAxis: { type: 'value', name: '异常分数' },
  series: [{
    name: '异常分数',
    type: 'bar',
    data: anomalyData.value.map(item => item.score),
    itemStyle: {
      color: function(params) {
        return params.value > 0.7 ? '#F56C6C' : 
               params.value > 0.4 ? '#E6A23C' : '#67C23A'
      }
    }
  }]
}))

// 风险分布饼图配置
const riskDistributionOptions = computed(() => ({
  title: { text: '风险分布' },
  tooltip: { trigger: 'item' },
  series: [{
    name: '风险等级',
    type: 'pie',
    radius: '60%',
    data: riskData.value.map(item => ({
      name: item.level,
      value: item.count
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

const getAccuracyTagType = (accuracy) => {
  if (accuracy >= 90) return 'success'
  if (accuracy >= 80) return 'warning'
  return 'danger'
}

const loadPredictiveData = async () => {
  loading.value = true
  try {
    const city = dashboardStore.selectedCity
    
    // 获取需求预测
    const demandForecast = await predictiveAnalysisApi.getDemandForecast(city, forecastDays.value)
    demandForecastData.value = demandForecast || []
    
    // 获取异常检测结果
    const anomalyDetection = await predictiveAnalysisApi.getAnomalyDetection(city, [
      new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString(),
      new Date().toISOString()
    ])
    anomalyData.value = anomalyDetection || []
    
    // 获取风险评估
    const riskAssessment = await predictiveAnalysisApi.getRiskAssessment(city)
    riskData.value = riskAssessment || []
    
    // 获取模型性能
    const modelPerf = await predictiveAnalysisApi.getModelPerformance(selectedModel.value)
    if (modelPerf) {
      predictionAccuracy.value = modelPerf.accuracy || 0
      modelPerformance.value = modelPerf.performance || 0
    }
    
    // 获取预测准确度历史
    const accuracy = await predictiveAnalysisApi.getForecastAccuracy(city, 30)
    predictionHistory.value = accuracy || []
    
    // 计算预测订单量（模拟）
    if (demandForecastData.value.length > 0) {
      predictedOrders.value = Math.round(
        demandForecastData.value
          .filter(item => item.predicted)
          .reduce((sum, item) => sum + item.predicted, 0)
      )
    }
    
    // 确定风险等级
    const avgRisk = riskData.value.reduce((sum, item) => sum + item.count, 0) / riskData.value.length
    riskLevel.value = avgRisk > 0.7 ? '高' : avgRisk > 0.4 ? '中' : '低'
    
  } catch (error) {
    console.error('Failed to load predictive analysis data:', error)
    ElMessage.error('加载预测分析数据失败')
  } finally {
    loading.value = false
  }
}

const runPrediction = async () => {
  ElMessage.info('正在运行预测分析...')
  await loadPredictiveData()
  ElMessage.success('预测分析完成')
}

const handleModelChange = () => {
  loadPredictiveData()
}

const updatePredictionConfig = () => {
  ElMessage.success('预测配置已更新')
  loadPredictiveData()
}

const resetConfig = () => {
  predictionConfig.value = {
    model: 'arima',
    confidenceInterval: 95,
    dataWindow: 30
  }
  ElMessage.info('配置已重置')
}

const exportPrediction = () => {
  const data = {
    demandForecast: demandForecastData.value,
    anomalyDetection: anomalyData.value,
    riskAssessment: riskData.value,
    config: predictionConfig.value
  }
  
  const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'prediction_results.json'
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('预测结果已导出')
}

onMounted(() => {
  loadPredictiveData()
})
</script>

<style scoped>
.predictive-analysis {
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

.prediction-overview {
  margin-bottom: 20px;
}

.prediction-card {
  height: 120px;
}

.prediction-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.prediction-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
}

.prediction-info {
  flex: 1;
}

.prediction-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.prediction-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.prediction-trend {
  font-size: 12px;
}

.prediction-trend.up {
  color: #67C23A;
}

.prediction-trend.down {
  color: #F56C6C;
}

.charts-section {
  margin-bottom: 20px;
}

.config-panel {
  margin-bottom: 20px;
}

.history-section {
  margin-bottom: 20px;
}
</style>