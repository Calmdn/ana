<template>
  <div class="time-efficiency-analysis">
    <!-- 页面标题和控制器 -->
    <div class="page-header">
      <h2>时间效率分析</h2>
      <div class="header-controls">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleDateChange"
          :shortcuts="dateShortcuts"
        />
        <el-button type="primary" @click="loadData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 数据概览卡片 -->
    <div class="overview-cards">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-icon avg-time">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.avgDeliveryTime }}分钟</div>
                <div class="card-label">平均配送时间</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-icon total-deliveries">
                <el-icon><Box /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.totalDeliveries }}</div>
                <div class="card-label">总配送量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-icon fast-rate">
                <el-icon><CircleCheckFilled /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.fastDeliveryRate }}%</div>
                <div class="card-label">快速配送率</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-icon slow-rate">
                <el-icon><WarningFilled /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.slowDeliveryRate }}%</div>
                <div class="card-label">慢速配送率</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-icon fast-count">
                <el-icon><Lightning /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.fastDeliveries }}</div>
                <div class="card-label">快速配送数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="overview-card">
            <div class="card-content">
              <div class="card-icon slow-count">
                <el-icon><Timer /></el-icon>
              </div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.slowDeliveries }}</div>
                <div class="card-label">慢速配送数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="20">
        <!-- 效率趋势图 -->
        <el-col :span="12">
          <el-card title="效率趋势分析" shadow="hover">
            <div id="trendChart" style="height: 400px;"></div>
          </el-card>
        </el-col>
        <!-- 效率分布图 -->
        <el-col :span="12">
          <el-card title="效率分布统计" shadow="hover">
            <div id="distributionChart" style="height: 400px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 数据表格区域 -->
    <div class="tables-section">
      <el-row :gutter="20">
        <!-- 城市效率排行 -->
        <el-col :span="24">
          <el-card title="城市效率排行对比" shadow="hover">
            <el-table :data="rankingData" v-loading="loading" stripe>
              <el-table-column type="index" label="排名" width="80" align="center" />
              <el-table-column prop="city" label="城市" width="120" align="center">
                <template #default="{ row }">
                  <el-tag 
                    :type="row.city === currentCity ? 'primary' : 'info'"
                    :effect="row.city === currentCity ? 'dark' : 'plain'"
                  >
                    {{ row.city }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="avgDeliveryTime" label="平均配送时间(分钟)" align="center" sortable />
              <el-table-column prop="totalDeliveries" label="总配送量" align="center" sortable />
              <el-table-column prop="recordCount" label="记录数" align="center" />
              <el-table-column prop="fastDeliveryRate" label="快速配送率" align="center" sortable>
                <template #default="{ row }">
                  <el-tag :type="getPerformanceType(row.fastDeliveryRate)">
                    {{ row.fastDeliveryRate }}%
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="slowDeliveryRate" label="慢速配送率" align="center" sortable>
                <template #default="{ row }">
                  <el-tag :type="getPerformanceType(100 - row.slowDeliveryRate)">
                    {{ row.slowDeliveryRate }}%
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 快速配送时段分析 -->
        <el-col :span="12">
          <el-card title="快速配送时段分析" shadow="hover">
            <div style="margin-bottom: 15px;">
              <span style="color: #666; font-size: 12px;">
                快速配送率阈值: 
                <el-input-number 
                  v-model="fastThreshold" 
                  :min="0" 
                  :max="1" 
                  :step="0.1" 
                  :precision="1"
                  size="small"
                  style="width: 100px; margin: 0 5px;"
                  @change="loadAnalysisData"
                />
                ({{ Math.round(fastThreshold * 100) }}%)
              </span>
            </div>
            <el-table :data="fastAnalysisData" v-loading="loading" stripe size="small">
              <el-table-column prop="timeSlot" label="时段" width="180" />
              <el-table-column prop="avgDeliveryTime" label="平均时间(分钟)" align="center" />
              <el-table-column prop="fastRate" label="快速配送率" align="center">
                <template #default="{ row }">
                  <el-tag type="success">{{ row.fastRate }}%</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="deliveryCount" label="配送量" align="center" />
            </el-table>
          </el-card>
        </el-col>
        
        <!-- 慢速配送时段分析 -->
        <el-col :span="12">
          <el-card title="慢速配送时段分析" shadow="hover">
            <div style="margin-bottom: 15px;">
              <span style="color: #666; font-size: 12px;">
                慢速配送率阈值: 
                <el-input-number 
                  v-model="slowThreshold" 
                  :min="0" 
                  :max="1" 
                  :step="0.1" 
                  :precision="1"
                  size="small"
                  style="width: 100px; margin: 0 5px;"
                  @change="loadAnalysisData"
                />
                ({{ Math.round(slowThreshold * 100) }}%)
              </span>
            </div>
            <el-table :data="slowAnalysisData" v-loading="loading" stripe size="small">
              <el-table-column prop="timeSlot" label="时段" width="180" />
              <el-table-column prop="avgDeliveryTime" label="平均时间(分钟)" align="center" />
              <el-table-column prop="slowRate" label="慢速配送率" align="center">
                <template #default="{ row }">
                  <el-tag type="warning">{{ row.slowRate }}%</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="deliveryCount" label="配送量" align="center" />
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Clock, Box, CircleCheckFilled, WarningFilled, Lightning, Timer, Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { timeEfficiencyApi } from '@/api/timeEfficiency'
import { useDashboardStore } from '@/stores/dashboard'

const dashboardStore = useDashboardStore()

// 响应式数据
const loading = ref(false)
const dateRange = ref([])
const fastThreshold = ref(0.7) // 快速配送率阈值 70%
const slowThreshold = ref(0.3) // 慢速配送率阈值 30%

// 数据状态
const overviewData = reactive({
  avgDeliveryTime: 0,
  totalDeliveries: 0,
  fastDeliveries: 0,
  slowDeliveries: 0,
  fastDeliveryRate: 0,
  slowDeliveryRate: 0
})

const trendData = ref([])
const distributionData = ref([])
const rankingData = ref([])
const fastAnalysisData = ref([])
const slowAnalysisData = ref([])

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
let trendChart = null
let distributionChart = null

// 方法定义
const getPerformanceType = (rate) => {
  if (rate >= 80) return 'success'
  if (rate >= 60) return 'primary'
  if (rate >= 40) return 'warning'
  return 'danger'
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

    console.log(`开始加载数据 - 城市: ${city}, 日期范围: ${startDate} 至 ${endDate}`)

    // 并行加载所有数据
    await Promise.all([
      loadSummaryData(city, startDate),
      loadTrendData(city, startDate, endDate),
      loadDistributionData(city, startDate),
      loadRankingData(startDate),
      loadAnalysisData()
    ])

    ElMessage.success('数据加载完成')
  } catch (error) {
    console.error('数据加载失败:', error)
    ElMessage.error('数据加载失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 汇总数据处理
const loadSummaryData = async (city, startDate) => {
  try {
    const response = await timeEfficiencyApi.getSummaryStats(city, startDate)
    console.log('汇总数据响应:', response)

    // 修复：直接检查数据，不检查success字段
    if (response && typeof response === 'object' && !Array.isArray(response)) {
      Object.assign(overviewData, {
        avgDeliveryTime: Math.round(response.avg_delivery_time || 0),
        totalDeliveries: response.total_deliveries || 0,
        fastDeliveries: Math.round((response.total_deliveries || 0) * (response.avg_fast_rate || 0)),
        slowDeliveries: Math.round((response.total_deliveries || 0) * (response.avg_slow_rate || 0)),
        fastDeliveryRate: Math.round((response.avg_fast_rate || 0) * 100),
        slowDeliveryRate: Math.round((response.avg_slow_rate || 0) * 100)
      })
      console.log('汇总数据处理完成:', overviewData)
    } else {
      console.warn('⚠汇总数据格式异常:', response)
    }
  } catch (error) {
    console.error('汇总数据加载失败:', error)
  }
}

// 趋势数据处理
const loadTrendData = async (city, startDate, endDate) => {
  try {
    const days = Math.ceil((new Date(endDate) - new Date(startDate)) / (1000 * 60 * 60 * 24)) + 1
    const response = await timeEfficiencyApi.getEfficiencyTrend(city, days)
    console.log('趋势数据响应:', response)

    // 检查是否为数组
    if (Array.isArray(response) && response.length > 0) {
      const aggregatedData = {}

      response.forEach(item => {
        let dateKey = ''
        
        if (Array.isArray(item.date) && item.date.length >= 3) {
          const [year, month, day] = item.date
          dateKey = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
        } else if (typeof item.date === 'string') {
          dateKey = item.date
        } else {
          console.warn('未知日期格式:', item.date)
          return
        }

        if (dateKey < startDate || dateKey > endDate) {
          return
        }

        if (!aggregatedData[dateKey]) {
          aggregatedData[dateKey] = {
            date: dateKey,
            totalDeliveries: 0,
            totalDeliveryTime: 0,
            fastDeliveries: 0,
            slowDeliveries: 0
          }
        }

        const aggData = aggregatedData[dateKey]
        aggData.totalDeliveries += item.totalDeliveries || 0
        aggData.totalDeliveryTime += (item.avgDeliveryTime || 0) * (item.totalDeliveries || 0)
        aggData.fastDeliveries += item.fastDeliveries || 0
        aggData.slowDeliveries += item.slowDeliveries || 0
      })

      trendData.value = Object.values(aggregatedData)
        .map(item => ({
          date: item.date,
          displayDate: item.date.split('-').slice(1).join('-'),
          avgDeliveryTime: Math.round(item.totalDeliveryTime / item.totalDeliveries) || 0,
          fastDeliveryRate: Math.round((item.fastDeliveries / item.totalDeliveries) * 100) || 0,
          slowDeliveryRate: Math.round((item.slowDeliveries / item.totalDeliveries) * 100) || 0,
          totalDeliveries: item.totalDeliveries
        }))
        .sort((a, b) => a.date.localeCompare(b.date))

      console.log('趋势数据处理完成:', trendData.value.length, '天')
      
      nextTick(() => {
        updateTrendChart()
      })
    } else {
      console.warn('趋势数据格式异常:', response)
      trendData.value = []
    }
  } catch (error) {
    console.error('趋势数据加载失败:', error)
    trendData.value = []
  }
}

// 分布数据处理
const loadDistributionData = async (city, startDate) => {
  try {
    const response = await timeEfficiencyApi.getDistributionStats(city, startDate)
    console.log('分布数据响应:', response)

    // 修复：直接检查是否为数组
    if (Array.isArray(response) && response.length > 0) {
      distributionData.value = response.map(item => ({
        name: item.efficiency_level || item.category || '未知',
        value: item.count || 0,
        percentage: item.percentage || 0
      }))
      console.log('分布数据处理完成:', distributionData.value)

      nextTick(() => {
        updateDistributionChart()
      })
    } else {
      console.warn('分布数据格式异常:', response)
      distributionData.value = []
    }
  } catch (error) {
    console.error('分布数据加载失败:', error)
    distributionData.value = []
  }
}

// 排行数据处理
const loadRankingData = async (startDate) => {
  try {
    const cities = ['shanghai', 'jilin', 'hangzhou', 'yantai', 'chongqing']
    const response = await timeEfficiencyApi.getEfficiencyRanking(cities, startDate, 10)
    console.log('排行数据响应:', response)

    // 修复：直接检查是否为数组
    if (Array.isArray(response) && response.length > 0) {
      rankingData.value = response
        .map(item => ({
          city: item.city || '未知',
          avgDeliveryTime: Math.round((item.avg_delivery_time || 0) * 100) / 100,
          totalDeliveries: item.total_deliveries || 0,
          recordCount: item.record_count || 0,
          fastDeliveryRate: Math.round((item.avg_fast_rate || 0) * 100),
          slowDeliveryRate: Math.round((item.avg_slow_rate || 0) * 100)
        }))
        .sort((a, b) => b.fastDeliveryRate - a.fastDeliveryRate)

      console.log('排行数据处理完成:', rankingData.value)
    } else {
      console.warn('排行数据格式异常:', response)
      rankingData.value = []
    }
  } catch (error) {
    console.error('排行数据加载失败:', error)
    rankingData.value = []
  }
}

// 加载分析数据
// 修改分析数据处理的字段映射
const loadAnalysisData = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) return

  const [startDate] = dateRange.value
  const city = currentCity.value

  try {
    // 加载快速配送分析
    const fastResponse = await timeEfficiencyApi.getFastDeliveryAnalysis(
      city, 
      fastThreshold.value, 
      startDate, 
      10
    )
    
    console.log('快速配送分析响应:', fastResponse)
    console.log('快速配送第一条数据:', fastResponse[0])
    
    // 修复：使用正确的驼峰字段名
    if (Array.isArray(fastResponse) && fastResponse.length > 0) {
      fastAnalysisData.value = fastResponse.map(item => ({
        timeSlot: `${item.city}-${item.date}-${String(item.hour || 0).padStart(2, '0')}:00`,
        avgDeliveryTime: Math.round(item.avgDeliveryTime || 0),
        fastRate: Math.round((item.fastDeliveryRate || 0) * 100),
        deliveryCount: item.totalDeliveries || 0,
        fastCount: item.fastDeliveries || 0
      }))
    } else {
      console.warn('快速配送分析数据格式异常:', fastResponse)
      fastAnalysisData.value = []
    }

    // 加载慢速配送分析
    const slowResponse = await timeEfficiencyApi.getSlowDeliveryAnalysis(
      city, 
      slowThreshold.value, 
      startDate, 
      10
    )
    
    console.log('慢速配送分析响应:', slowResponse)
    console.log('慢速配送第一条数据:', slowResponse[0])
    
    // 修复：使用正确的驼峰字段名
    if (Array.isArray(slowResponse) && slowResponse.length > 0) {
      slowAnalysisData.value = slowResponse.map(item => ({
        timeSlot: `${item.city}-${item.date}-${String(item.hour || 0).padStart(2, '0')}:00`,
        avgDeliveryTime: Math.round(item.avgDeliveryTime || 0), // 驼峰格式
        slowRate: Math.round((item.slowDeliveryRate || 0) * 100), // 驼峰格式，已经是百分比
        deliveryCount: item.totalDeliveries || 0, // 驼峰格式
        slowCount: item.slowDeliveries || 0 // 驼峰格式
      }))
    } else {
      console.warn('慢速配送分析数据格式异常:', slowResponse)
      slowAnalysisData.value = []
    }

    console.log('分析数据加载完成 - 快速:', fastAnalysisData.value.length, '慢速:', slowAnalysisData.value.length)
    console.log('快速配送处理后数据:', fastAnalysisData.value.slice(0, 2))
    console.log('慢速配送处理后数据:', slowAnalysisData.value.slice(0, 2))
  } catch (error) {
    console.error('分析数据加载失败:', error)
    fastAnalysisData.value = []
    slowAnalysisData.value = []
  }
}

// 图表更新函数
const updateTrendChart = () => {
  if (!trendChart) {
    trendChart = echarts.init(document.getElementById('trendChart'))
  }

  if (trendData.value.length === 0) {
    trendChart.setOption({
      title: {
        text: '暂无趋势数据',
        left: 'center',
        top: 'center',
        textStyle: { color: '#999', fontSize: 16 }
      }
    })
    return
  }

  const option = {
    title: {
      text: `效率趋势分析 (${trendData.value.length}天)`,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const dataIndex = params[0].dataIndex
        const item = trendData.value[dataIndex]
        return `
          <div style="text-align: left;">
            <strong>${item.date}</strong><br/>
            平均配送时间：${params[0].value}分钟<br/>
            快速配送率：${params[1].value}%<br/>
            慢速配送率：${params[2].value}%<br/>
            总配送量：${item.totalDeliveries}单
          </div>
        `
      }
    },
    legend: {
      data: ['平均配送时间', '快速配送率', '慢速配送率'],
      top: 30
    },
    xAxis: {
      type: 'category',
      data: trendData.value.map(item => item.displayDate),
      axisLabel: {
        rotate: trendData.value.length > 15 ? 45 : 0
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '配送时间(分钟)',
        position: 'left'
      },
      {
        type: 'value',
        name: '配送率(%)',
        position: 'right',
        min: 0,
        max: 100
      }
    ],
    series: [
      {
        name: '平均配送时间',
        type: 'line',
        data: trendData.value.map(item => item.avgDeliveryTime),
        smooth: true,
        itemStyle: { color: '#409EFF' },
        lineStyle: { width: 2 }
      },
      {
        name: '快速配送率',
        type: 'line',
        yAxisIndex: 1,
        data: trendData.value.map(item => item.fastDeliveryRate),
        smooth: true,
        itemStyle: { color: '#67C23A' },
        lineStyle: { width: 2 }
      },
      {
        name: '慢速配送率',
        type: 'line',
        yAxisIndex: 1,
        data: trendData.value.map(item => item.slowDeliveryRate),
        smooth: true,
        itemStyle: { color: '#F56C6C' },
        lineStyle: { width: 2 }
      }
    ]
  }

  trendChart.setOption(option)
}

const updateDistributionChart = () => {
  if (!distributionChart) {
    distributionChart = echarts.init(document.getElementById('distributionChart'))
  }

  if (distributionData.value.length === 0) {
    distributionChart.setOption({
      title: {
        text: '暂无分布数据',
        left: 'center',
        top: 'center',
        textStyle: { color: '#999', fontSize: 16 }
      }
    })
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
        data: distributionData.value,
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

  distributionChart.setOption(option)
}

// 修改watch逻辑，处理初始加载
watch(currentCity, (newCity, oldCity) => {
  console.log(`城市变化: ${oldCity} -> ${newCity}`)
  
  if (newCity && newCity !== '' && newCity !== 'undefined') {
    if (dateRange.value && dateRange.value.length === 2) {
      console.log('城市变化触发数据加载')
      loadData()
    }
  }
}, { immediate: true }) // 添加immediate: true确保初始值也触发

// 简化onMounted
onMounted(() => {
  // 只设置默认日期范围
  const today = new Date()
  const weekAgo = new Date(today.getTime() - 6 * 24 * 60 * 60 * 1000)
  dateRange.value = [
    weekAgo.toISOString().split('T')[0],
    today.toISOString().split('T')[0]
  ]
  
  // 数据加载由watch(currentCity)处理
  console.log('默认日期已设置，等待城市状态触发数据加载...')
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
</script>

<style scoped>
.time-efficiency-analysis {
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

.card-icon.avg-time { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.card-icon.total-deliveries { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.card-icon.fast-rate { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.card-icon.slow-rate { background: linear-gradient(135deg, #fad0c4 0%, #ffd1ff 100%); }
.card-icon.fast-count { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); }
.card-icon.slow-count { background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%); }

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
}

.charts-section, .tables-section {
  margin-bottom: 20px;
}

.charts-section .el-card, .tables-section .el-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>