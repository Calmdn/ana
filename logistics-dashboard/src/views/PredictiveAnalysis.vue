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
import { predictiveApi } from '@/api/predictiveAnalysis'
import { DataAnalysis, TrendCharts, Promotion, Warning, Cpu } from '@element-plus/icons-vue'
import LineChart from '@/components/charts/LineChart.vue'
import BarChart from '@/components/charts/BarChart.vue'
import PieChart from '@/components/charts/PieChart.vue'
import { ElMessage } from 'element-plus'

const dashboardStore = useDashboardStore()

const loading = ref(false)
const selectedModel = ref('demand')
const forecastDays = ref(7)

// 预测结果数据
const predictionAccuracy = ref(85)
const predictedOrders = ref(0)
const riskLevel = ref('低')
const modelPerformance = ref(92)

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

// 清理后的数据加载函数
const loadPredictiveData = async () => {
  loading.value = true
  try {
    const city = dashboardStore.selectedCity
    const today = new Date().toISOString().split('T')[0]
    
    console.log('开始加载预测数据，城市:', city)

    // 1. 获取需求预测数据
    try {
      const trendData = await predictiveApi.getDemandForecast(city, forecastDays.value)
      console.log('趋势数据:', trendData)
      
      if (trendData && Array.isArray(trendData) && trendData.length > 0) {
        const validData = trendData.filter(item => 
          item && (item.dsDate || item.date) && 
          (item.dsDate !== '' && item.date !== '')
        )
        
        console.log('过滤后的有效数据:', validData.length, '条')
        
        if (validData.length > 0) {
          demandForecastData.value = validData.map(item => ({
            period: item.dsDate || item.date,
            predicted: item.orderVolume || item.predictedValue || 0,
            actual: item.actualValue || 0,
            confidence: item.confidence || 0,
            hour: item.dsHour || item.hour || 0
          }))
          
          // 计算预测订单量
          const totalPredicted = demandForecastData.value.reduce((sum, item) => sum + (item.predicted || 0), 0)
          predictedOrders.value = Math.round(totalPredicted)
          
          console.log('处理后的需求预测数据:', demandForecastData.value.slice(0, 3))
        } else {
          console.warn('没有有效的日期数据（ds_date都为空）')
          demandForecastData.value = []
          predictedOrders.value = 0
        }
      } else {
        console.warn('趋势数据为空')
        demandForecastData.value = []
        predictedOrders.value = 0
      }
    } catch (error) {
      console.warn('趋势数据获取失败:', error.message)
      demandForecastData.value = []
      predictedOrders.value = 0
    }

    // 2. 获取最新预测数据
    try {
      const latestData = await predictiveApi.getLatestPrediction(city, null, 24)
      console.log('最新预测数据:', latestData)
      
      if (latestData && Array.isArray(latestData) && latestData.length > 0) {
        // 过滤掉 ds_date 为空的数据
        const validLatestData = latestData.filter(item => 
          item && (item.dsDate || item.date) && 
          (item.dsDate !== '' && item.date !== '')
        )
        
        console.log('过滤后的最新数据:', validLatestData.length, '条')
        
        if (validLatestData.length > 0) {
          const latest = validLatestData[0]
          if (latest.orderVolume || latest.predictedValue) {
            predictedOrders.value = latest.orderVolume || latest.predictedValue
          }
          if (latest.confidence) {
            predictionAccuracy.value = Math.round(latest.confidence)
          }
        }
      } else {
        console.warn('最新预测数据为空')
      }
    } catch (error) {
      console.warn('最新数据获取失败:', error.message)
    }

    // 3. 获取小时分布数据
    try {
      const hourlyData = await predictiveApi.getHourlyDistribution(city, today)
      console.log('小时分布数据:', hourlyData)
      
      if (hourlyData && Array.isArray(hourlyData) && hourlyData.length > 0) {
        // 即使 ds_date 为空，但如果有 hour 数据也可以使用
        const validHourlyData = hourlyData.filter(item => 
          item && (item.hour !== undefined || item.dsHour !== undefined)
        )
        
        console.log('过滤后的小时数据:', validHourlyData.length, '条')
        
        if (validHourlyData.length > 0) {
          anomalyData.value = validHourlyData.map(item => ({
            date: `${item.hour || item.dsHour}:00`,
            score: item.anomalyScore || 0,
            hour: item.hour || item.dsHour,
            orderVolume: item.orderVolume || 0
          }))
        } else {
          console.warn('没有有效的小时数据')
          anomalyData.value = []
        }
      } else {
        console.warn('小时分布数据为空')
        anomalyData.value = []
      }
    } catch (error) {
      console.warn('小时分布获取失败:', error.message)
      anomalyData.value = []
    }

    // 4. 获取容量分析数据
    try {
      const capacityData = await predictiveApi.getCapacityAnalysis(city)
      console.log('容量分析数据:', capacityData)
      
      if (capacityData && Array.isArray(capacityData) && capacityData.length > 0) {
        const validCapacityData = capacityData.filter(item => 
          item && (item.orderVolume !== undefined && item.orderVolume !== null)
        )
        
        console.log('过滤后的容量数据:', validCapacityData.length, '条')
        
        if (validCapacityData.length > 0) {
          const totalOrders = validCapacityData.reduce((sum, item) => sum + (item.orderVolume || 0), 0)
          const totalCouriers = validCapacityData.reduce((sum, item) => sum + (item.courierCount || item.activeCouriers || 0), 0)
          const utilization = totalCouriers > 0 ? totalOrders / totalCouriers : 0
          
          console.log('容量分析结果:', { totalOrders, totalCouriers, utilization })
          
          if (utilization > 8) {
            riskLevel.value = '高'
            riskData.value = [
              { name: '高风险', value: 60 },
              { name: '中风险', value: 30 },
              { name: '低风险', value: 10 }
            ]
          } else if (utilization > 5) {
            riskLevel.value = '中'
            riskData.value = [
              { name: '高风险', value: 20 },
              { name: '中风险', value: 50 },
              { name: '低风险', value: 30 }
            ]
          } else {
            riskLevel.value = '低'
            riskData.value = [
              { name: '高风险', value: 10 },
              { name: '中风险', value: 20 },
              { name: '低风险', value: 70 }
            ]
          }
        } else {
          console.warn('没有有效的容量数据')
          riskLevel.value = '未知'
          riskData.value = []
        }
      } else {
        console.warn('容量数据为空')
        riskLevel.value = '未知'
        riskData.value = []
      }
    } catch (error) {
      console.warn('容量数据获取失败:', error.message)
      riskLevel.value = '未知'
      riskData.value = []
    }

    // 5. 获取汇总统计数据
    try {
      const summaryData = await predictiveApi.getSummaryStats(city, today)
      console.log('汇总统计数据:', summaryData)
      
      if (summaryData) {
        if (summaryData.accuracy || summaryData.predictionAccuracy) {
          predictionAccuracy.value = Math.round(summaryData.accuracy || summaryData.predictionAccuracy)
        }
        if (summaryData.modelPerformance) {
          modelPerformance.value = Math.round(summaryData.modelPerformance)
        }
      }
    } catch (error) {
      console.warn('汇总统计获取失败:', error.message)
    }

    // 6. 获取自定义预测数据
    try {
      const customData = await predictiveApi.getCustomPrediction(
        city, 
        'order_volume', 
        new Date(Date.now() - 10 * 24 * 60 * 60 * 1000).toISOString().split('T')[0], 
        today
      )
      console.log('自定义预测数据:', customData)
      
      if (customData && Array.isArray(customData) && customData.length > 0) {
        // 过滤掉 ds_date 为空的数据
        const validCustomData = customData.filter(item => 
          item && (item.dsDate || item.date) && 
          (item.dsDate !== '' && item.date !== '')
        )
        
        if (validCustomData.length > 0) {
          predictionHistory.value = validCustomData.map(item => ({
            date: item.dsDate || item.date,
            model: item.model || predictionConfig.value.model.toUpperCase(),
            predictedValue: Math.round(item.predictedValue || 0),
            actualValue: Math.round(item.actualValue || 0),
            accuracy: Math.round(item.accuracy || 0),
            error: Math.round(item.error || Math.abs((item.predictedValue || 0) - (item.actualValue || 0))),
            confidence: item.confidence || predictionConfig.value.confidenceInterval
          }))
        } else {
          console.warn('没有有效的自定义预测数据')
          predictionHistory.value = []
        }
      } else {
        console.warn('自定义预测数据为空')
        predictionHistory.value = []
      }
    } catch (error) {
      console.warn('自定义预测数据获取失败:', error.message)
      predictionHistory.value = []
    }

    // 7. 获取记录统计数据
    try {
      const countData = await predictiveApi.getCount(city, 'order_volume')
      console.log('记录统计:', countData)
      
      if (countData && typeof countData === 'number') {
        console.log(`${city} 城市共有 ${countData} 条预测记录`)
      }
    } catch (error) {
      console.warn('记录统计获取失败:', error.message)
    }

    // 输出最终状态
    console.log('预测分析数据加载完成')
    console.log('最终数据状态:', {
      demandForecastData: demandForecastData.value.length,
      anomalyData: anomalyData.value.length,
      riskData: riskData.value.length,
      predictionHistory: predictionHistory.value.length,
      predictedOrders: predictedOrders.value,
      predictionAccuracy: predictionAccuracy.value,
      riskLevel: riskLevel.value
    })

    const hasValidData = demandForecastData.value.length > 0 || 
                        anomalyData.value.length > 0 || 
                        predictionHistory.value.length > 0

    if (!hasValidData) {
      ElMessage.warning('当前城市暂无有效的预测数据')
    } else {
      ElMessage.success(`预测数据加载完成`)
    }
    
  } catch (error) {
    console.error('预测分析数据加载失败:', error)
    ElMessage.error('加载预测分析数据失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 清理后的图表配置
const demandForecastOptions = computed(() => {
  console.log('生成图表配置，数据长度:', demandForecastData.value.length)
  
  if (!demandForecastData.value || demandForecastData.value.length === 0) {
    return {
      title: { text: '需求预测趋势 (暂无数据)' },
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: []
      },
      yAxis: { type: 'value', name: '订单量' },
      graphic: {
        type: 'text',
        left: 'center',
        top: 'middle',
        style: {
          text: '暂无有效的预测数据\n请检查数据库中的 ds_date 字段',
          fontSize: 16,
          fill: '#999'
        }
      },
      series: []
    }
  }

  return {
    title: { text: `需求预测趋势 (${demandForecastData.value.length}条记录)` },
    tooltip: { 
      trigger: 'axis',
      formatter: function(params) {
        let result = params[0].name + '<br/>'
        params.forEach(param => {
          result += `${param.seriesName}: ${param.value}<br/>`
        })
        return result
      }
    },
    legend: { data: ['预测值'] },
    xAxis: {
      type: 'category',
      data: demandForecastData.value.map(item => item.period)
    },
    yAxis: { type: 'value', name: '订单量' },
    series: [
      {
        name: '预测值',
        type: 'line',
        data: demandForecastData.value.map(item => item.predicted),
        itemStyle: { color: '#67C23A' },
        lineStyle: { type: 'dashed', width: 2 },
        smooth: true
      }
    ]
  }
})

// 清理后的异常检测图表配置
const anomalyDetectionOptions = computed(() => {
  if (!anomalyData.value || anomalyData.value.length === 0) {
    return {
      title: { text: '异常检测结果 (暂无数据)' },
      xAxis: {
        type: 'category',
        data: []
      },
      yAxis: { type: 'value', name: '异常分数' },
      graphic: {
        type: 'text',
        left: 'center',
        top: 'middle',
        style: {
          text: '暂无异常检测数据',
          fontSize: 16,
          fill: '#999'
        }
      },
      series: []
    }
  }

  return {
    title: { text: `异常检测结果 (${anomalyData.value.length}条记录)` },
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
  }
})

// 清理后的风险分布图表配置
const riskDistributionOptions = computed(() => {
  if (!riskData.value || riskData.value.length === 0) {
    return {
      title: { text: '风险分布 (暂无数据)' },
      tooltip: { trigger: 'item' },
      graphic: {
        type: 'text',
        left: 'center',
        top: 'middle',
        style: {
          text: '暂无风险评估数据',
          fontSize: 16,
          fill: '#999'
        }
      },
      series: []
    }
  }

  return {
    title: { text: '风险分布' },
    tooltip: { trigger: 'item' },
    series: [{
      name: '风险等级',
      type: 'pie',
      radius: '60%',
      data: riskData.value,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
})

// 清理后的其他方法
const resetConfig = () => {
  predictionConfig.value = {
    model: 'arima',
    confidenceInterval: 95,
    dataWindow: 30
  }
  ElMessage.info('配置已重置')
}

const exportPrediction = () => {
  if (demandForecastData.value.length === 0 && 
      anomalyData.value.length === 0 && 
      predictionHistory.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }

  const data = {
    demandForecast: demandForecastData.value,
    anomalyDetection: anomalyData.value,
    riskAssessment: riskData.value,
    predictionHistory: predictionHistory.value,
    config: predictionConfig.value,
    exportTime: new Date().toISOString()
  }
  
  const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `prediction_results_${dashboardStore.selectedCity}_${new Date().toISOString().split('T')[0]}.json`
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('预测结果已导出')
}
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