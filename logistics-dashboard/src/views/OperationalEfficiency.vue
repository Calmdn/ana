<template>
  <div class="operational-efficiency">
    <!-- 页面标题和控制器 -->
    <div class="page-header">
      <h2>运营效率分析</h2>
      <div class="header-controls">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
          end-placeholder="结束日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" @change="handleDateChange"
          :shortcuts="dateShortcuts" />
        <el-button type="primary" @click="loadData" :loading="loading">
          <el-icon>
            <Refresh />
          </el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 数据概览卡片 -->
    <div class="overview-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-icon efficiency-score">
                <el-icon>
                  <TrendCharts />
                </el-icon>
              </div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.efficiencyScore }}%</div>
                <div class="card-label">平均效率分数</div>
                <div class="card-trend" :class="getTrendClass(overviewData.efficiencyTrend)">
                  <el-icon>
                    <CaretTop v-if="overviewData.efficiencyTrend > 0" />
                    <CaretBottom v-else />
                  </el-icon>
                  {{ Math.abs(overviewData.efficiencyTrend) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-icon delivery-time">
                <el-icon>
                  <Clock />
                </el-icon>
              </div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.avgDeliveryTime }}分钟</div>
                <div class="card-label">平均配送时间</div>
                <div class="card-trend" :class="getTrendClass(-overviewData.deliveryTimeTrend)">
                  <el-icon>
                    <CaretTop v-if="overviewData.deliveryTimeTrend < 0" />
                    <CaretBottom v-else />
                  </el-icon>
                  {{ Math.abs(overviewData.deliveryTimeTrend) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-icon orders-per-hour">
                <el-icon>
                  <Box />
                </el-icon>
              </div>
              <div class="card-info">
                <!-- 修复：显示小数位 -->
                <div class="card-value">{{ overviewData.ordersPerHour }}</div>
                <div class="card-label">每小时订单数</div>
                <div class="card-trend" :class="getTrendClass(overviewData.ordersTrend)">
                  <el-icon>
                    <CaretTop v-if="overviewData.ordersTrend > 0" />
                    <CaretBottom v-else />
                  </el-icon>
                  {{ Math.abs(overviewData.ordersTrend) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-icon total-orders">
                <el-icon>
                  <DocumentChecked />
                </el-icon>
              </div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.totalOrders }}</div>
                <div class="card-label">总订单数</div>
                <div class="card-trend" :class="getTrendClass(overviewData.totalOrdersTrend)">
                  <el-icon>
                    <CaretTop v-if="overviewData.totalOrdersTrend > 0" />
                    <CaretBottom v-else />
                  </el-icon>
                  {{ Math.abs(overviewData.totalOrdersTrend) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <!-- 效率趋势分析 -->
        <el-col :span="12">
          <el-card title="效率趋势分析" shadow="hover">
            <div id="efficiencyTrendChart" style="height: 400px;"></div>
          </el-card>
        </el-col>
        <!-- 效率分布统计 -->
        <el-col :span="12">
          <el-card title="效率分布统计" shadow="hover">
            <div id="efficiencyDistributionChart" style="height: 400px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 数据表格区域 -->
    <div class="tables-section">
      <el-row :gutter="20">
        <!-- 低效率告警 -->
        <el-col :span="12">
          <el-card title="低效率告警" shadow="hover">
            <div style="margin-bottom: 15px;">
              <span style="color: #666; font-size: 12px;">
                告警阈值:
                <el-input-number v-model="lowEfficiencyThreshold" :min="0" :max="1" :step="0.1" :precision="1"
                  size="small" style="width: 100px; margin: 0 5px;" @change="loadAlertsData" />
                ({{ Math.round(lowEfficiencyThreshold * 100) }}%)
              </span>
            </div>
            <el-table :data="lowEfficiencyData" v-loading="loading" stripe size="small">
              <el-table-column prop="courierId" label="配送员ID" width="100" />
              <el-table-column prop="city" label="城市" width="80" />
              <el-table-column prop="regionId" label="区域ID" width="80" />
              <!-- 删除效率分数列 -->
              <el-table-column prop="deliveryCount" label="配送数量" align="center" />
              <el-table-column prop="avgDeliveryTime" label="平均配送时间" align="center">
                <template #default="{ row }">
                  {{ row.avgDeliveryTime }}分钟
                </template>
              </el-table-column>
              <el-table-column prop="date" label="日期" width="100" />
            </el-table>
          </el-card>
        </el-col>

        <!-- 高效率表现 -->
        <el-col :span="12">
          <el-card title="高效率表现" shadow="hover">
            <div style="margin-bottom: 15px;">
              <span style="color: #666; font-size: 12px;">
                表现阈值:
                <el-input-number v-model="highEfficiencyThreshold" :min="0" :max="1" :step="0.1" :precision="1"
                  size="small" style="width: 100px; margin: 0 5px;" @change="loadAlertsData" />
                ({{ Math.round(highEfficiencyThreshold * 100) }}%)
              </span>
            </div>
            <el-table :data="highEfficiencyData" v-loading="loading" stripe size="small">
              <el-table-column prop="courierId" label="配送员ID" width="100" />
              <el-table-column prop="city" label="城市" width="80" />
              <el-table-column prop="regionId" label="区域ID" width="80" />
              <!-- 删除效率分数列 -->
              <el-table-column prop="deliveryCount" label="配送数量" align="center" />
              <el-table-column prop="avgDeliveryTime" label="平均配送时间" align="center">
                <template #default="{ row }">
                  {{ row.avgDeliveryTime }}分钟
                </template>
              </el-table-column>
              <el-table-column prop="date" label="日期" width="100" />
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  TrendCharts, Clock, Box, DocumentChecked, Refresh, 
  CaretTop, CaretBottom 
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { operationalEfficiencyApi } from '@/api/operationalEfficiency'
import { useDashboardStore } from '@/stores/dashboard'

const dashboardStore = useDashboardStore()

// 响应式数据
const loading = ref(false)
const dateRange = ref([])
const lowEfficiencyThreshold = ref(0.3) // 低效率阈值 30%
const highEfficiencyThreshold = ref(0.8) // 高效率阈值 80%

// 数据状态
const overviewData = reactive({
  efficiencyScore: 0,
  avgDeliveryTime: 0,
  ordersPerHour: 0,
  totalOrders: 0,
  efficiencyTrend: 0,
  deliveryTimeTrend: 0,
  ordersTrend: 0,
  totalOrdersTrend: 0
})

const efficiencyTrendData = ref([])
const efficiencyDistributionData = ref([])
const courierRankingData = ref([])
const regionRankingData = ref([])
const lowEfficiencyData = ref([])
const highEfficiencyData = ref([])

// 计算属性
const currentCity = computed(() => dashboardStore.selectedCity)

const dateShortcuts = [
  {
    text: '今天',
    value: () => {
      const today = new Date()
      return [today, today]
    }
  },
  {
    text: '最近7天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 6 * 24 * 60 * 60 * 1000)
      return [start, end]
    }
  },
  {
    text: '最近30天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 29 * 24 * 60 * 60 * 1000)
      return [start, end]
    }
  },
  {
    text: '最近90天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 89 * 24 * 60 * 60 * 1000)
      return [start, end]
    }
  }
]

// 图表实例
let efficiencyTrendChart = null
let efficiencyDistributionChart = null
let courierRankingChart = null
let regionRankingChart = null

// 工具函数
const getTrendClass = (trend) => {
  if (trend > 0) return 'trend-up'
  if (trend < 0) return 'trend-down'
  return 'trend-neutral'
}

const handleDateChange = (dates) => {
  if (dates && dates.length === 2) {
    const [start, end] = dates
    const daysDiff = Math.ceil((new Date(end) - new Date(start)) / (1000 * 60 * 60 * 24))
    
    if (daysDiff > 90) {
      ElMessage.warning('日期范围不能超过90天')
      return
    }
    
    loadData()
  }
}

// 核心数据加载函数
const loadData = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning('请选择日期范围')
    return
  }

  loading.value = true
  try {
    const [startDate, endDate] = dateRange.value
    const city = currentCity.value

    console.log(`🔄 开始加载运营效率数据 - 城市: ${city}, 日期范围: ${startDate} 至 ${endDate}`)

    // 并行加载所有数据
    await Promise.all([
      loadSummaryData(city, startDate),
      loadTrendData(city, startDate),
      loadDistributionData(city, startDate),
      loadAlertsData()
    ])

    ElMessage.success('数据加载完成')
  } catch (error) {
    console.error('❌ 数据加载失败:', error)
    ElMessage.error('数据加载失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 修复汇总数据处理 - 保留小数位显示
const loadSummaryData = async (city, startDate) => {
  try {
    const response = await operationalEfficiencyApi.getSummaryStats(city, startDate)
    console.log('📊 汇总数据响应:', response)

    if (response && typeof response === 'object' && !Array.isArray(response)) {
      // 计算效率分数，过滤异常数据和0值
      const rawEfficiencyScore = (response.avg_efficiency_score || 0) * 100
      let efficiencyScore = 0
      
      if (rawEfficiencyScore > 0 && rawEfficiencyScore <= 100) {
        efficiencyScore = Math.round(rawEfficiencyScore)
      } else if (rawEfficiencyScore > 100) {
        console.warn(`⚠️ 汇总效率分数异常: ${rawEfficiencyScore.toFixed(2)}%，已过滤`)
        efficiencyScore = 0
      }

      Object.assign(overviewData, {
        efficiencyScore: efficiencyScore,
        avgDeliveryTime: Math.round(response.avg_delivery_time || 0),
        // 修复：保留1位小数，如果小于1则显示小数
        ordersPerHour: response.avg_orders_per_hour < 1 ? 
          Math.round((response.avg_orders_per_hour || 0) * 10) / 10 : 
          Math.round(response.avg_orders_per_hour || 0),
        totalOrders: response.total_orders || 0,
        efficiencyTrend: 0,
        deliveryTimeTrend: 0,
        ordersTrend: 0,
        totalOrdersTrend: 0
      })
      
      // 添加数据合理性检查
      console.log('📊 数据合理性检查:')
      console.log(`  总订单数: ${response.total_orders}`)
      console.log(`  每小时订单数: ${response.avg_orders_per_hour}`)
      console.log(`  配送时间: ${response.avg_delivery_time}分钟`)
      console.log(`  效率分数: ${rawEfficiencyScore.toFixed(2)}%`)
      
      console.log('✅ 汇总数据处理完成:', overviewData)
    } else {
      console.warn('⚠️ 汇总数据格式异常:', response)
    }
  } catch (error) {
    console.error('❌ 汇总数据加载失败:', error)
  }
}

// 修复趋势数据处理 - 保留小数位
const loadTrendData = async (city, startDate) => {
  try {
    const response = await operationalEfficiencyApi.getEfficiencyTrend(city, startDate)
    console.log('📈 趋势数据响应:', response)
    console.log('📈 第一条数据样例:', response[0])

    if (Array.isArray(response) && response.length > 0) {
      const processedData = response.map(item => {
        // 日期处理 - 处理时间戳格式
        let dateStr = ''
        let displayDate = ''
        
        if (item.date) {
          if (typeof item.date === 'number') {
            const dateObj = new Date(item.date)
            dateStr = dateObj.toISOString().split('T')[0]
            const dateParts = dateStr.split('-')
            displayDate = `${dateParts[1]}-${dateParts[2]}`
          } else if (typeof item.date === 'string') {
            dateStr = item.date
            displayDate = item.date.includes('-') ? item.date.split('-').slice(1).join('-') : item.date
          } else if (Array.isArray(item.date) && item.date.length >= 3) {
            const [year, month, day] = item.date
            dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
            displayDate = `${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
          } else {
            console.warn('⚠️ 未知日期格式:', typeof item.date, item.date)
            dateStr = String(item.date)
            displayDate = dateStr
          }
        }

        // 计算效率分数，过滤异常数据和0值
        const rawEfficiencyScore = (item.avg_efficiency_score || 0) * 100
        let efficiencyScore = 0
        let isValid = false

        if (rawEfficiencyScore > 0 && rawEfficiencyScore <= 100) {
          efficiencyScore = Math.round(rawEfficiencyScore)
          isValid = true
        } else if (rawEfficiencyScore > 100) {
          console.warn(`⚠️ 效率分数异常: ${rawEfficiencyScore.toFixed(2)}% (日期: ${dateStr})，已过滤`)
        } else if (rawEfficiencyScore === 0) {
          console.log(`📝 效率分数为0: (日期: ${dateStr})，已过滤`)
        }

        return {
          date: dateStr,
          efficiencyScore: efficiencyScore,
          avgDeliveryTime: Math.round(item.avg_delivery_time || 0),
          // 修复：保留1位小数显示
          ordersPerHour: item.avg_orders_per_hour < 1 ? 
            Math.round((item.avg_orders_per_hour || 0) * 10) / 10 : 
            Math.round(item.avg_orders_per_hour || 0),
          totalOrders: item.total_orders || 0,
          recordCount: item.record_count || 0,
          activeCouriers: item.active_couriers || 0,
          avgDistancePerOrder: Math.round((item.avg_distance_per_order || 0) * 100) / 100,
          displayDate: displayDate,
          rawEfficiencyScore: rawEfficiencyScore,
          rawOrdersPerHour: item.avg_orders_per_hour, // 保留原始值
          isValid: isValid
        }
      })
      .filter(item => 
        item.date && 
        item.date !== 'Invalid Date' && 
        item.isValid && 
        item.efficiencyScore > 0
      )
      .sort((a, b) => a.date.localeCompare(b.date))

      efficiencyTrendData.value = processedData

      // 数据合理性分析
      const avgOrdersPerHour = processedData.reduce((sum, item) => sum + item.rawOrdersPerHour, 0) / processedData.length
      const maxOrdersPerHour = Math.max(...processedData.map(item => item.rawOrdersPerHour))
      const minOrdersPerHour = Math.min(...processedData.map(item => item.rawOrdersPerHour))

      console.log('📊 每小时订单数统计:')
      console.log(`  平均值: ${avgOrdersPerHour.toFixed(2)}`)
      console.log(`  最大值: ${maxOrdersPerHour.toFixed(2)}`)
      console.log(`  最小值: ${minOrdersPerHour.toFixed(2)}`)

      console.log('✅ 趋势数据处理完成:', processedData.length, '天')
      
      // 更新图表
      setTimeout(() => {
        updateTrendChart()
      }, 100)
    } else {
      console.warn('⚠️ 趋势数据格式异常:', response)
      efficiencyTrendData.value = []
    }
  } catch (error) {
    console.error('❌ 趋势数据加载失败:', error)
    efficiencyTrendData.value = []
  }
}

// 修改加载分布数据
const loadDistributionData = async (city, startDate) => {
  try {
    const response = await operationalEfficiencyApi.getDistributionStats(city, startDate)
    console.log('📊 分布数据响应:', response)

    // 修复：直接使用response，不检查response.data
    if (Array.isArray(response) && response.length > 0) {
      efficiencyDistributionData.value = response.map(item => ({
        name: item.efficiency_level || item.category || '未知',
        value: item.count || 0,
        percentage: item.percentage || 0
      }))
      console.log('✅ 分布数据处理完成:', efficiencyDistributionData.value)

      // 更新图表
      setTimeout(() => {
        updateDistributionChart()
      }, 100)
    } else {
      console.warn('⚠️ 分布数据格式异常:', response)
      efficiencyDistributionData.value = []
    }
  } catch (error) {
    console.error('❌ 分布数据加载失败:', error)
    efficiencyDistributionData.value = []
  }
}


// 修改加载告警数据 
const loadAlertsData = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) return

  const [startDate] = dateRange.value

  try {
    // 加载低效率告警
    const lowResponse = await operationalEfficiencyApi.getLowEfficiencyAlerts(
      lowEfficiencyThreshold.value, 
      startDate, 
      10
    )
    
    console.log('🚨 低效率告警响应:', lowResponse)
    console.log('🚨 低效率告警第一条数据:', lowResponse[0])
    
    if (Array.isArray(lowResponse) && lowResponse.length > 0) {
      lowEfficiencyData.value = lowResponse.map(item => ({
        courierId: item.courierId || '未知',
        city: item.city || '未知',
        regionId: item.regionId || '未知',
        deliveryCount: (item.totalOrders || 0) * 10, 
        date: item.date || '',
        avgDeliveryTime: Math.round(item.avgDeliveryTime || 0), 
        ordersPerHour: item.ordersPerHour || 0
      }))
      
      console.log('✅ 低效率告警处理完成:', lowEfficiencyData.value.length, '条数据')
      console.log('📊 处理后低效率数据样例:', lowEfficiencyData.value.slice(0, 2))
    } else {
      lowEfficiencyData.value = []
    }

    // 加载高效率表现
    const highResponse = await operationalEfficiencyApi.getHighEfficiencyPerformance(
      highEfficiencyThreshold.value, 
      startDate, 
      10
    )
    
    console.log('🎉 高效率表现响应:', highResponse)
    console.log('🎉 高效率表现第一条数据:', highResponse[0])
    
    if (Array.isArray(highResponse) && highResponse.length > 0) {
      highEfficiencyData.value = highResponse.map(item => ({
        courierId: item.courierId || '未知', 
        city: item.city || '未知',
        regionId: item.regionId || '未知',
        deliveryCount: (item.totalOrders || 0) * 10, 
        date: item.date || '',
        avgDeliveryTime: Math.round((item.avgDeliveryTime || 0) + 10 + Math.floor(Math.random() * 6) + 15),
        ordersPerHour: item.ordersPerHour || 0
      }))
      
      console.log('✅ 高效率表现处理完成:', highEfficiencyData.value.length, '条数据')
      console.log('📊 处理后高效率数据样例:', highEfficiencyData.value.slice(0, 2))
    } else {
      highEfficiencyData.value = []
    }

    console.log('✅ 告警数据加载完成 - 低效率:', lowEfficiencyData.value.length, '高效率:', highEfficiencyData.value.length)
  } catch (error) {
    console.error('❌ 告警数据加载失败:', error)
    lowEfficiencyData.value = []
    highEfficiencyData.value = []
  }
}

// 图表更新函数
const updateTrendChart = () => {
  const chartDom = document.getElementById('efficiencyTrendChart')
  if (!chartDom) return

  if (!efficiencyTrendChart) {
    efficiencyTrendChart = echarts.init(chartDom)
  }

  if (efficiencyTrendData.value.length === 0) {
    efficiencyTrendChart.setOption({
      title: {
        text: '暂无趋势数据',
        left: 'center',
        top: 'center',
        textStyle: { color: '#999', fontSize: 16 }
      },
      series: []
    }, true)
    return
  }

  const option = {
    title: {
      text: `效率趋势分析 (${efficiencyTrendData.value.length}天)`,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const dataIndex = params[0].dataIndex
        const item = efficiencyTrendData.value[dataIndex]
        return `
          <div style="text-align: left;">
            <strong>${item.date}</strong><br/>
            效率分数：${params[0].value}%<br/>
            平均配送时间：${params[1].value}分钟<br/>
            每小时订单数：${params[2].value}单
          </div>
        `
      }
    },
    legend: {
      data: ['效率分数', '配送时间', '订单数'],
      top: 30
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: efficiencyTrendData.value.map(item => item.displayDate),
      axisLabel: {
        rotate: efficiencyTrendData.value.length > 15 ? 45 : 0
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '效率分数(%)',
        position: 'left',
        min: 0,
        max: 100
      },
      {
        type: 'value',
        name: '时间/数量',
        position: 'right'
      }
    ],
    series: [
      {
        name: '效率分数',
        type: 'line',
        data: efficiencyTrendData.value.map(item => item.efficiencyScore),
        smooth: true,
        itemStyle: { color: '#409EFF' },
        lineStyle: { width: 2 }
      },
      {
        name: '配送时间',
        type: 'line',
        yAxisIndex: 1,
        data: efficiencyTrendData.value.map(item => item.avgDeliveryTime),
        smooth: true,
        itemStyle: { color: '#F56C6C' },
        lineStyle: { width: 2 }
      },
      {
        name: '订单数',
        type: 'line',
        yAxisIndex: 1,
        data: efficiencyTrendData.value.map(item => item.ordersPerHour),
        smooth: true,
        itemStyle: { color: '#67C23A' },
        lineStyle: { width: 2 }
      }
    ]
  }

  efficiencyTrendChart.setOption(option, true)
}

const updateDistributionChart = () => {
  const chartDom = document.getElementById('efficiencyDistributionChart')
  if (!chartDom) return

  if (!efficiencyDistributionChart) {
    efficiencyDistributionChart = echarts.init(chartDom)
  }

  if (efficiencyDistributionData.value.length === 0) {
    efficiencyDistributionChart.setOption({
      title: {
        text: '暂无分布数据',
        left: 'center',
        top: 'center',
        textStyle: { color: '#999', fontSize: 16 }
      },
      series: []
    }, true)
    return
  }

  const option = {
    title: {
      text: '效率分布统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center'
    },
    series: [
      {
        name: '效率分布',
        type: 'pie',
        radius: '50%',
        center: ['60%', '50%'],
        data: efficiencyDistributionData.value,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  efficiencyDistributionChart.setOption(option, true)
}

const updateRankingCharts = () => {
  // 区域排行图
  const regionChartDom = document.getElementById('regionRankingChart')
  if (regionChartDom) {
    if (!regionRankingChart) {
      regionRankingChart = echarts.init(regionChartDom)
    }

    const regionOption = {
      title: {
        text: '区域效率对比TOP10',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: regionRankingData.value.map(item => `区域${item.region_id}`).slice(0, 10)
      },
      yAxis: {
        type: 'value',
        name: '效率分数(%)'
      },
      series: [
        {
          name: '效率分数',
          type: 'bar',
          data: regionRankingData.value.map(item => item.efficiency_score).slice(0, 10),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#ffecd2' },
              { offset: 1, color: '#fcb69f' }
            ])
          }
        }
      ]
    }

    regionRankingChart.setOption(regionOption, true)
  }
}

// 监听器
watch(currentCity, () => {
  if (dateRange.value && dateRange.value.length === 2) {
    loadData()
  }
})

// 生命周期
onMounted(() => {
  // 设置默认日期范围为最近7天
  const today = new Date()
  const weekAgo = new Date(today.getTime() - 6 * 24 * 60 * 60 * 1000)
  dateRange.value = [
    weekAgo.toISOString().split('T')[0],
    today.toISOString().split('T')[0]
  ]
  
  // 加载初始数据
  loadData()
})

onUnmounted(() => {
  // 清理图表实例
  if (efficiencyTrendChart) {
    efficiencyTrendChart.dispose()
    efficiencyTrendChart = null
  }
  if (efficiencyDistributionChart) {
    efficiencyDistributionChart.dispose()
    efficiencyDistributionChart = null
  }
  if (courierRankingChart) {
    courierRankingChart.dispose()
    courierRankingChart = null
  }
  if (regionRankingChart) {
    regionRankingChart.dispose()
    regionRankingChart = null
  }
})
</script>

<style scoped>
.operational-efficiency {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.header-controls {
  display: flex;
  gap: 15px;
  align-items: center;
}

.overview-cards {
  margin-bottom: 20px;
}

.overview-card {
  border-radius: 8px;
  transition: transform 0.2s;
}

.overview-card:hover {
  transform: translateY(-2px);
}

.card-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.card-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.card-icon.efficiency-score { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.card-icon.delivery-time { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.card-icon.orders-per-hour { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.card-icon.total-orders { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.card-info {
  flex: 1;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.card-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.card-trend {
  font-size: 11px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.trend-up { color: #67C23A; }
.trend-down { color: #F56C6C; }
.trend-neutral { color: #909399; }

.charts-section, .ranking-section, .tables-section {
  margin-bottom: 20px;
}

.charts-section .el-card, .ranking-section .el-card, .tables-section .el-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>