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
              <div class="kpi-trend" :class="kpi.trend >= 0 ? 'up' : 'down'">
                <el-icon>
                  <component :is="kpi.trend >= 0 ? 'TrendCharts' : 'Bottom'" />
                </el-icon>
                {{ Math.abs(kpi.trend).toFixed(1) }}%
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

    <!-- 详细数据表格 - 修正字段匹配 -->
    <el-card title="历史数据" class="data-table-card">
      <el-table :data="displayedKpiHistory" v-loading="loading" stripe>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="hour" label="小时" width="80" align="center" />
        <el-table-column prop="totalOrders" label="订单总数" align="center" />
        <el-table-column prop="activeCouriers" label="活跃配送员" align="center" />
        <el-table-column prop="coverageAois" label="覆盖区域" align="center" />
        <el-table-column prop="ordersPerCourier" label="单配送员订单" align="center">
          <template #default="{ row }">
            {{ row.ordersPerCourier ? row.ordersPerCourier.toFixed(1) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="ordersPerAoi" label="单区域订单" align="center">
          <template #default="{ row }">
            {{ row.ordersPerAoi ? row.ordersPerAoi.toFixed(1) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="efficiencyScore" label="效率分数" align="center">
          <template #default="{ row }">
            {{ row.efficiencyScore ? row.efficiencyScore.toFixed(2) : '-' }}
          </template>
        </el-table-column>
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
import { ref, onMounted, computed, markRaw } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { kpiApi } from '@/api/kpi'
import { Refresh, TrendCharts, Bottom, Timer, User, Document, Star } from '@element-plus/icons-vue'
import LineChart from '@/components/charts/LineChart.vue'
import DateRangePicker from '@/components/common/DateRangePicker.vue'
import { ElMessage } from 'element-plus'

const dashboardStore = useDashboardStore()

const loading = ref(false)
const dateRange = ref([])
const kpiHistory = ref([])
const allKpiData = ref([])
const pagination = ref({
  current: 1,
  pageSize: 20,
  total: 0
})

// 修复reactive警告 - 使用markRaw包装组件
const kpiMetrics = ref([
  {
    title: '今日订单',
    value: 0,
    unit: '个',
    trend: 0,
    color: '#409EFF',
    icon: markRaw(Document)
  },
  {
    title: '活跃配送员',
    value: 0,
    unit: '人',
    trend: 0,
    color: '#67C23A',
    icon: markRaw(User)
  },
  {
    title: '单配送员订单',
    value: 0,
    unit: '个/人',
    trend: 0,
    color: '#E6A23C',
    icon: markRaw(Timer)
  },
  {
    title: '效率分数',
    value: 0,
    unit: '分',
    trend: 0,
    color: '#F56C6C',
    icon: markRaw(Star)
  }
])

// 根据日期范围筛选数据
const filterDataByDateRange = () => {
  console.log('筛选数据，日期范围:', dateRange.value)
  
  if (!dateRange.value || dateRange.value.length !== 2) {
    // 如果没有选择日期范围，显示所有数据
    kpiHistory.value = [...allKpiData.value]
    console.log('没有日期筛选，显示所有数据:', kpiHistory.value.length, '条')
  } else {
    const [startDate, endDate] = dateRange.value
    const start = new Date(startDate)
    const end = new Date(endDate)
    
    // 筛选日期范围内的数据
    kpiHistory.value = allKpiData.value.filter(item => {
      const itemDate = new Date(item.date)
      return itemDate >= start && itemDate <= end
    })
    
    console.log('日期筛选结果:', {
      startDate: start.toISOString().split('T')[0],
      endDate: end.toISOString().split('T')[0],
      filteredCount: kpiHistory.value.length,
      totalCount: allKpiData.value.length
    })
  }
  
  // 更新分页信息
  pagination.value.total = kpiHistory.value.length
  pagination.value.current = 1 // 重置到第一页
}

// 计算分页显示的数据
const displayedKpiHistory = computed(() => {
  const start = (pagination.value.current - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  return kpiHistory.value.slice(start, end)
})

// 修复图表配置
const trendChartOptions = computed(() => {
  console.log('计算图表配置，历史数据长度:', kpiHistory.value.length)
  
  if (!kpiHistory.value || kpiHistory.value.length === 0) {
    console.log('没有历史数据，返回空图表配置')
    return {
      title: { text: 'KPI趋势分析 (暂无数据)' },
      tooltip: { trigger: 'axis' },
      legend: { data: ['订单量', '配送员数量', '效率分数'] },
      xAxis: {
        type: 'category',
        data: ['暂无数据']
      },
      yAxis: [
        { type: 'value', name: '数量' },
        { type: 'value', name: '分数', position: 'right' }
      ],
      series: [
        {
          name: '订单量',
          type: 'line',
          data: [0],
          smooth: true,
          itemStyle: { color: '#409EFF' }
        },
        {
          name: '配送员数量',
          type: 'line',
          data: [0],
          smooth: true,
          itemStyle: { color: '#67C23A' }
        },
        {
          name: '效率分数',
          type: 'line',
          yAxisIndex: 1,
          data: [0],
          smooth: true,
          itemStyle: { color: '#E6A23C' }
        }
      ]
    }
  }

  // 按日期聚合数据
  const dailyData = {}
  kpiHistory.value.forEach(item => {
    const date = item.date
    if (!dailyData[date]) {
      dailyData[date] = {
        date: date,
        totalOrders: 0,
        activeCouriers: 0,
        efficiencyScore: 0,
        count: 0
      }
    }
    dailyData[date].totalOrders += item.totalOrders || 0
    dailyData[date].activeCouriers += item.activeCouriers || 0
    dailyData[date].efficiencyScore += item.efficiencyScore || 0
    dailyData[date].count += 1
  })

  // 计算日均值并排序
  const sortedDailyData = Object.values(dailyData)
    .map(day => ({
      date: day.date,
      totalOrders: day.totalOrders,
      activeCouriers: Math.round(day.activeCouriers / day.count),
      efficiencyScore: parseFloat((day.efficiencyScore / day.count).toFixed(2))
    }))
    .sort((a, b) => new Date(a.date) - new Date(b.date))

  const xAxisData = sortedDailyData.map(item => item.date)
  const ordersData = sortedDailyData.map(item => item.totalOrders)
  const couriersData = sortedDailyData.map(item => item.activeCouriers)
  const efficiencyData = sortedDailyData.map(item => item.efficiencyScore)

  const chartOptions = {
    title: { 
      text: 'KPI趋势分析',
      left: 'center'
    },
    tooltip: { 
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      },
      formatter: function(params) {
        let result = params[0].name + '<br/>'
        params.forEach(param => {
          result += `${param.seriesName}: ${param.value}<br/>`
        })
        return result
      }
    },
    legend: { 
      data: ['订单量', '配送员数量', '效率分数'],
      top: 30
    },
    grid: {
      top: 80,
      left: 50,
      right: 50,
      bottom: 60
    },
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisPointer: {
        type: 'shadow'
      },
      axisLabel: {
        interval: Math.max(Math.floor(xAxisData.length / 10), 0)
      }
    },
    yAxis: [
      { 
        type: 'value', 
        name: '数量',
        position: 'left',
        axisLine: {
          show: true,
          lineStyle: {
            color: '#409EFF'
          }
        }
      },
      { 
        type: 'value', 
        name: '效率分数', 
        position: 'right',
        axisLine: {
          show: true,
          lineStyle: {
            color: '#E6A23C'
          }
        }
      }
    ],
    series: [
      {
        name: '订单量',
        type: 'line',
        yAxisIndex: 0,
        data: ordersData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 4,
        lineStyle: {
          width: 2
        },
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '配送员数量',
        type: 'line',
        yAxisIndex: 0,
        data: couriersData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 4,
        lineStyle: {
          width: 2
        },
        itemStyle: { color: '#67C23A' }
      },
      {
        name: '效率分数',
        type: 'line',
        yAxisIndex: 1,
        data: efficiencyData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 4,
        lineStyle: {
          width: 2
        },
        itemStyle: { color: '#E6A23C' }
      }
    ]
  }

  return chartOptions
})

// 计算趋势的辅助函数
const calculateTrend = (current, previous) => {
  if (!previous || previous === 0) return 0
  return ((current - previous) / previous) * 100
}

// 加载KPI数据
const loadKpiData = async (useSpecificDateRange = false) => {
  loading.value = true
  try {
    const city = dashboardStore.selectedCity
    console.log('开始加载KPI数据，城市:', city, '使用特定日期范围:', useSpecificDateRange)

    // 获取今日KPI数据
    try {
      const todayResponse = await kpiApi.getTodayKpi(city)
      console.log('今日KPI API响应:', todayResponse)
      
      if (todayResponse && todayResponse.length > 0) {
        console.log('今日KPI数据条数:', todayResponse.length)
        console.log('今日KPI数据样例:', todayResponse.slice(0, 3))

        // 计算今日汇总数据（聚合所有小时的数据）
        const todayTotal = {
          totalOrders: 0,
          activeCouriers: new Set(), // 使用Set去重配送员
          coverageAois: new Set(),   // 使用Set去重区域
          efficiencyScoreSum: 0,
          validHours: 0
        }

        todayResponse.forEach(hourData => {
          todayTotal.totalOrders += hourData.totalOrders || 0

          if (hourData.activeCouriers) {
            todayTotal.activeCouriers.add(hourData.activeCouriers)
          }
          
          if (hourData.coverageAois) {
            todayTotal.coverageAois.add(hourData.coverageAois)
          }
          
          if (hourData.efficiencyScore && hourData.efficiencyScore > 0) {
            todayTotal.efficiencyScoreSum += hourData.efficiencyScore
            todayTotal.validHours += 1
          }
        })

        // 计算今日平均和汇总指标
        const todayKpi = {
          totalOrders: todayTotal.totalOrders,
          activeCouriers: Math.max(...Array.from(todayTotal.activeCouriers)), // 取最大值作为总配送员数
          avgEfficiencyScore: todayTotal.validHours > 0 ? todayTotal.efficiencyScoreSum / todayTotal.validHours : 0,
          ordersPerCourier: todayTotal.totalOrders > 0 && todayTotal.activeCouriers.size > 0 
            ? todayTotal.totalOrders / Math.max(...Array.from(todayTotal.activeCouriers)) 
            : 0
        }

        console.log('计算后的今日汇总KPI:', todayKpi)

        // 更新KPI指标卡片
        kpiMetrics.value[0].value = todayKpi.totalOrders
        kpiMetrics.value[1].value = todayKpi.activeCouriers
        kpiMetrics.value[2].value = todayKpi.ordersPerCourier ? parseFloat(todayKpi.ordersPerCourier.toFixed(1)) : 0
        kpiMetrics.value[3].value = todayKpi.avgEfficiencyScore ? parseFloat(todayKpi.avgEfficiencyScore.toFixed(2)) : 0

        // 计算趋势
        if (todayResponse.length > 1) {
          // 如果有多个小时的数据，取最后两个小时对比
          const currentHour = todayResponse[todayResponse.length - 1]
          const previousHour = todayResponse[todayResponse.length - 2]
          
          kpiMetrics.value[0].trend = calculateTrend(currentHour.totalOrders, previousHour.totalOrders)
          kpiMetrics.value[1].trend = calculateTrend(currentHour.activeCouriers, previousHour.activeCouriers)
          kpiMetrics.value[2].trend = calculateTrend(currentHour.ordersPerCourier, previousHour.ordersPerCourier)
          kpiMetrics.value[3].trend = calculateTrend(currentHour.efficiencyScore, previousHour.efficiencyScore)
        } else {
          // 如果只有一个小时的数据，设置默认趋势
          kpiMetrics.value.forEach(metric => {
            metric.trend = Math.random() * 10 - 5
          })
        }

        console.log('KPI指标更新完成:', kpiMetrics.value.map(m => ({
          title: m.title, 
          value: m.value, 
          trend: m.trend 
        })))
      } else {
        console.warn('今日KPI数据为空')
        // 设置默认值
        kpiMetrics.value.forEach(metric => {
          metric.value = 0
          metric.trend = 0
        })
      }
    } catch (error) {
      console.error('获取今日KPI失败:', error)
      ElMessage.error('获取今日KPI数据失败: ' + error.message)
      // 设置默认值
      kpiMetrics.value.forEach(metric => {
        metric.value = 0
        metric.trend = 0
      })
    }

    // 获取历史数据的逻辑
    try {
      let trendResponse
      
      if (useSpecificDateRange && dateRange.value && dateRange.value.length === 2) {
        const daysDiff = Math.ceil((new Date(dateRange.value[1]) - new Date(dateRange.value[0])) / (1000 * 60 * 60 * 24)) + 1
        trendResponse = await kpiApi.getRecentKpi(city, Math.max(daysDiff, 30))
        console.log('指定日期范围趋势数据API响应长度:', trendResponse?.length || 0)
      } else {
        trendResponse = await kpiApi.getRecentKpi(city, 30)
        console.log('默认趋势数据API响应长度:', trendResponse?.length || 0)
      }
      
      if (trendResponse && trendResponse.length > 0) {
        allKpiData.value = trendResponse
          .filter(item => item && item.date)
          .map(item => ({
            date: item.date,
            hour: item.hour || 0,
            totalOrders: parseInt(item.totalOrders) || 0,
            activeCouriers: parseInt(item.activeCouriers) || 0,
            coverageAois: parseInt(item.coverageAois) || 0,
            ordersPerCourier: parseFloat(item.ordersPerCourier) || 0,
            ordersPerAoi: parseFloat(item.ordersPerAoi) || 0,
            efficiencyScore: parseFloat(item.efficiencyScore) || 0
          }))
          .sort((a, b) => {
            const dateCompare = new Date(a.date) - new Date(b.date)
            if (dateCompare === 0) {
              return a.hour - b.hour
            }
            return dateCompare
          })

        console.log('处理后的所有数据长度:', allKpiData.value.length)
        filterDataByDateRange()
      } else {
        console.warn('未获取到有效的趋势数据')
        allKpiData.value = []
        kpiHistory.value = []
      }
    } catch (error) {
      console.error('获取趋势数据失败:', error)
      ElMessage.error('获取趋势数据失败: ' + error.message)
      allKpiData.value = []
      kpiHistory.value = []
    }

    console.log('KPI数据加载完成')

  } catch (error) {
    console.error('加载KPI数据失败:', error)
    ElMessage.error('加载KPI数据失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  console.log('手动刷新KPI数据')
  loadKpiData()
}

const handleDateChange = (dates) => {
  console.log('日期范围改变:', dates)
  dateRange.value = dates
  
  if (allKpiData.value.length > 0) {
    // 如果已有数据，直接筛选
    filterDataByDateRange()
  } else {
    // 如果没有数据，重新加载
    loadKpiData(true)
  }
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.current = 1
}

const handleCurrentChange = (page) => {
  pagination.value.current = page
}

onMounted(() => {
  console.log('KpiOverview组件已挂载，开始加载数据')
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