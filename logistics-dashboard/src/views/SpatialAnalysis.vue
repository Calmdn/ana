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
              <p><strong>推荐路径：</strong>{{ routeOptimization.optimizedRoute || '暂无数据' }}</p>
              <p><strong>预计时间：</strong>{{ routeOptimization.duration || 0 }} 分钟</p>
              <p><strong>距离：</strong>{{ routeOptimization.distance || 0 }} 公里</p>
              <p><strong>节省时间：</strong>{{ routeOptimization.savings || 0 }} 分钟</p>
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

// 扩展城市坐标范围配置，包含行政区划分
const CITY_COORDINATES = {
  yantai: { 
    lng: [121.0, 121.8], 
    lat: [37.2, 37.8], 
    regions: ['芝罘区', '莱山区', '福山区', '牟平区', '开发区'],
    adminDivisions: [
      { name: '芝罘区', lng: [121.35, 121.45], lat: [37.50, 37.60] },
      { name: '莱山区', lng: [121.45, 121.55], lat: [37.45, 37.55] },
      { name: '福山区', lng: [121.20, 121.35], lat: [37.45, 37.55] },
      { name: '牟平区', lng: [121.55, 121.70], lat: [37.35, 37.50] },
      { name: '开发区', lng: [121.25, 121.40], lat: [37.40, 37.50] }
    ]
  },
  shanghai: { 
    lng: [121.1, 121.9], 
    lat: [30.7, 31.5], 
    regions: ['浦东新区', '黄浦区', '徐汇区', '长宁区', '静安区', '普陀区'],
    adminDivisions: [
      { name: '浦东新区', lng: [121.50, 121.90], lat: [31.00, 31.40] },
      { name: '黄浦区', lng: [121.45, 121.52], lat: [31.20, 31.25] },
      { name: '徐汇区', lng: [121.40, 121.50], lat: [31.15, 31.22] },
      { name: '长宁区', lng: [121.35, 121.45], lat: [31.18, 31.25] },
      { name: '静安区', lng: [121.42, 121.48], lat: [31.22, 31.28] },
      { name: '普陀区', lng: [121.35, 121.45], lat: [31.22, 31.30] }
    ]
  },
  hangzhou: { 
    lng: [119.8, 120.6], 
    lat: [29.8, 30.6], 
    regions: ['西湖区', '拱墅区', '江干区', '下城区', '上城区', '滨江区'],
    adminDivisions: [
      { name: '西湖区', lng: [120.05, 120.20], lat: [30.20, 30.35] },
      { name: '拱墅区', lng: [120.10, 120.25], lat: [30.25, 30.40] },
      { name: '江干区', lng: [120.20, 120.35], lat: [30.20, 30.35] },
      { name: '下城区', lng: [120.15, 120.25], lat: [30.25, 30.32] },
      { name: '上城区', lng: [120.15, 120.25], lat: [30.22, 30.28] },
      { name: '滨江区', lng: [120.18, 120.28], lat: [30.15, 30.25] }
    ]
  },
  jilin: { 
    lng: [126.2, 127.0], 
    lat: [43.5, 44.3], 
    regions: ['昌邑区', '龙潭区', '船营区', '丰满区', '高新区'],
    adminDivisions: [
      { name: '昌邑区', lng: [126.50, 126.65], lat: [43.80, 43.95] },
      { name: '龙潭区', lng: [126.50, 126.70], lat: [43.65, 43.85] },
      { name: '船营区', lng: [126.45, 126.60], lat: [43.80, 43.95] },
      { name: '丰满区', lng: [126.55, 126.75], lat: [43.75, 43.90] },
      { name: '高新区', lng: [126.60, 126.75], lat: [43.85, 44.00] }
    ]
  },
  chongqing: { 
    lng: [106.0, 107.0], 
    lat: [29.0, 30.0], 
    regions: ['渝中区', '江北区', '南岸区', '渝北区', '沙坪坝区', '九龙坡区'],
    adminDivisions: [
      { name: '渝中区', lng: [106.55, 106.60], lat: [29.55, 29.60] },
      { name: '江北区', lng: [106.55, 106.65], lat: [29.58, 29.68] },
      { name: '南岸区', lng: [106.55, 106.65], lat: [29.48, 29.58] },
      { name: '渝北区', lng: [106.60, 106.75], lat: [29.60, 29.75] },
      { name: '沙坪坝区', lng: [106.40, 106.55], lat: [29.52, 29.67] },
      { name: '九龙坡区', lng: [106.45, 106.60], lat: [29.45, 29.60] }
    ]
  }
}

// 根据经纬度判断所属行政区域
const getRegionByCoordinate = (lng, lat, city) => {
  const cityConfig = CITY_COORDINATES[city] || CITY_COORDINATES.shanghai
  
  for (const division of cityConfig.adminDivisions) {
    if (lng >= division.lng[0] && lng <= division.lng[1] && 
        lat >= division.lat[0] && lat <= division.lat[1]) {
      return division.name
    }
  }
  
  // 如果不在任何已定义区域内，返回最近的区域
  let minDistance = Infinity
  let nearestRegion = cityConfig.adminDivisions[0].name
  
  for (const division of cityConfig.adminDivisions) {
    const centerLng = (division.lng[0] + division.lng[1]) / 2
    const centerLat = (division.lat[0] + division.lat[1]) / 2
    const distance = Math.sqrt(Math.pow(lng - centerLng, 2) + Math.pow(lat - centerLat, 2))
    
    if (distance < minDistance) {
      minDistance = distance
      nearestRegion = division.name
    }
  }
  
  return nearestRegion
}

const spatialStats = ref({
  totalAreas: 0,
  hotspots: 0,
  avgDensity: 0,
  totalRoutes: 0
})
const heatmapData = ref([])
const regionalStats = ref([])
const routeOptimization = ref(null)
const trafficData = ref([])
const densityData = ref([])

// 根据城市生成对应的模拟数据
const generateCityHeatmapData = (city) => {
  const coords = CITY_COORDINATES[city] || CITY_COORDINATES.shanghai
  return Array.from({ length: 50 }, () => [
    Math.random() * (coords.lng[1] - coords.lng[0]) + coords.lng[0], // 该城市经度范围
    Math.random() * (coords.lat[1] - coords.lat[0]) + coords.lat[0], // 该城市纬度范围
    Math.random() * 100 + 10 // 数值
  ])
}

const generateCityRegionalData = (city) => {
  const coords = CITY_COORDINATES[city] || CITY_COORDINATES.shanghai
  return coords.regions.map(region => ({
    region,
    orders: Math.floor(Math.random() * 200) + 80
  }))
}

const generateMockTrafficData = () => {
  const timeSlots = ['早高峰(7-9时)', '上午(9-12时)', '午间(12-14时)', '下午(14-17时)', '晚高峰(17-19时)', '夜间(19-7时)']
  return timeSlots.map(slot => ({
    area: slot,
    congestionLevel: Math.random() * 0.8 + 0.2
  }))
}

const generateMockDensityData = () => {
  return Array.from({ length: 24 }, (_, hour) => ({
    hour,
    orderDensity: Math.sin(hour / 24 * Math.PI * 2 + Math.PI/2) * 40 + 60 + Math.random() * 20,
    courierDensity: Math.sin((hour - 2) / 24 * Math.PI * 2 + Math.PI/2) * 20 + 30 + Math.random() * 10,
    responseTime: Math.sin((hour + 1) / 24 * Math.PI * 2) * 8 + 25 + Math.random() * 5
  }))
}

// 热力图配置
const heatmapOptions = computed(() => {
  if (!heatmapData.value || heatmapData.value.length === 0) {
    return {
      title: { text: '暂无数据' },
      series: []
    }
  }
  
  const maxValue = Math.max(...heatmapData.value.map(item => item[2]))
  const city = dashboardStore.selectedCity
  const coords = CITY_COORDINATES[city] || CITY_COORDINATES.shanghai
  
  return {
    title: { 
      text: `${city === 'yantai' ? '烟台' : city === 'shanghai' ? '上海' : city === 'hangzhou' ? '杭州' : city === 'jilin' ? '吉林' : '重庆'}配送热力图`,
      left: 'center',
      top: 20
    },
    tooltip: {
      trigger: 'item',
      formatter: function (params) {
        // 添加安全检查
        if (!params || !params.value || !Array.isArray(params.value)) {
          return '暂无数据'
        }
        
        const [lng, lat, value] = params.value
        const safeLng = parseFloat(lng) || 0
        const safeLat = parseFloat(lat) || 0
        const safeValue = parseFloat(value) || 0
        
        return `位置: [${safeLng.toFixed(3)}, ${safeLat.toFixed(3)}]<br/>数值: ${safeValue.toFixed(1)}`
      }
    },
    visualMap: {
      min: 0,
      max: maxValue,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '8%',
      inRange: {
        color: ['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8', 
                '#ffffcc', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026']
      },
      text: ['高', '低'],
      textStyle: {
        color: '#333'
      }
    },
    grid: {
      top: '15%',
      bottom: '20%',
      left: '10%',
      right: '10%'
    },
    xAxis: {
      type: 'value',
      name: '经度',
      nameLocation: 'middle',
      nameGap: 30,
      min: coords.lng[0],
      max: coords.lng[1],
      splitLine: { 
        show: true,
        lineStyle: {
          color: '#eee'
        }
      }
    },
    yAxis: {
      type: 'value',
      name: '纬度',
      nameLocation: 'middle',
      nameGap: 40,
      min: coords.lat[0],
      max: coords.lat[1],
      splitLine: { 
        show: true,
        lineStyle: {
          color: '#eee'
        }
      }
    },
    series: [{
      name: selectedMetric.value,
      type: 'scatter',
      data: heatmapData.value,
      symbolSize: function (data) {
        if (!Array.isArray(data) || data.length < 3) return 8
        return Math.max(8, data[2] / maxValue * 30)
      },
      itemStyle: {
        shadowBlur: 10,
        shadowColor: 'rgba(0, 0, 0, 0.3)',
        shadowOffsetY: 5,
        color: function(params) {
          if (!params || !params.value || !Array.isArray(params.value)) {
            return '#313695'
          }
          
          const ratio = params.value[2] / maxValue
          if (ratio > 0.8) return '#d73027'
          if (ratio > 0.6) return '#f46d43'
          if (ratio > 0.4) return '#fdae61'
          if (ratio > 0.2) return '#74add1'
          return '#313695'
        }
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 20,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
})

const regionalStatsOptions = computed(() => ({
  title: { 
    text: '区域配送分布',
    left: 'center',
    top: 20
  },
  tooltip: { 
    trigger: 'item',
    formatter: '{b}: {c} 单 ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    top: 'middle'
  },
  series: [{
    name: '配送量',
    type: 'pie',
    radius: ['40%', '70%'],
    center: ['60%', '50%'],
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
    },
    labelLine: {
      show: false
    },
    label: {
      show: true,
      position: 'inside',
      formatter: '{d}%'
    }
  }]
}))

// 修复交通分析配置 - 添加数据类型检查
const trafficAnalysisOptions = computed(() => ({
  title: { text: '交通拥堵分析' },
  tooltip: { 
    trigger: 'axis',
    formatter: function(params) {
      if (!params || !params[0] || !params[0].value) {
        return '暂无数据'
      }
      
      const value = parseFloat(params[0].value) || 0 // 确保是数字
      let level = '轻微'
      if (value > 80) level = '严重'
      else if (value > 60) level = '中等'
      else if (value > 40) level = '一般'
      
      return `${params[0].name}<br/>拥堵指数: ${value.toFixed(1)}<br/>拥堵程度: ${level}`
    }
  },
  xAxis: {
    type: 'category',
    data: trafficData.value.map(item => item.area),
    axisLabel: {
      rotate: 45
    }
  },
  yAxis: { 
    type: 'value', 
    name: '拥堵指数',
    max: 100
  },
  series: [{
    name: '拥堵指数',
    type: 'bar',
    data: trafficData.value.map(item => parseFloat((item.congestionLevel * 100).toFixed(1))), // 确保是数字
    itemStyle: {
      color: function(params) {
        const value = parseFloat(params.value) || 0
        if (value > 80) return '#F56C6C'
        if (value > 60) return '#E6A23C'
        if (value > 40) return '#fac858'
        return '#67C23A'
      }
    },
    markLine: {
      data: [
        { yAxis: 60, name: '拥堵警戒线', lineStyle: { color: '#E6A23C' } }
      ]
    }
  }]
}))

const densityAnalysisOptions = computed(() => ({
  title: { text: '配送密度时段分析' },
  tooltip: { trigger: 'axis' },
  legend: { 
    data: ['订单密度', '配送员密度', '平均响应时间'],
    top: 30
  },
  grid: {
    top: 80,
    bottom: 60
  },
  xAxis: {
    type: 'category',
    data: densityData.value.map((_, index) => `${index.toString().padStart(2, '0')}:00`),
    axisLabel: {
      interval: 2
    }
  },
  yAxis: [
    { 
      type: 'value', 
      name: '密度',
      position: 'left'
    },
    { 
      type: 'value', 
      name: '时间(分钟)', 
      position: 'right'
    }
  ],
  series: [
    {
      name: '订单密度',
      type: 'line',
      data: densityData.value.map(item => item.orderDensity.toFixed(1)),
      smooth: true,
      itemStyle: { color: '#409EFF' },
      lineStyle: { width: 3 }
    },
    {
      name: '配送员密度',
      type: 'line',
      data: densityData.value.map(item => item.courierDensity.toFixed(1)),
      smooth: true,
      itemStyle: { color: '#67C23A' },
      lineStyle: { width: 3 }
    },
    {
      name: '平均响应时间',
      type: 'line',
      yAxisIndex: 1,
      data: densityData.value.map(item => item.responseTime.toFixed(1)),
      smooth: true,
      itemStyle: { color: '#E6A23C' },
      lineStyle: { width: 3 }
    }
  ]
}))

// 修复数据加载函数 - 根据API数据结构处理
const loadSpatialData = async () => {
  loading.value = true
  try {
    const city = dashboardStore.selectedCity
    const today = new Date().toISOString().split('T')[0]
    
    // 城市名称映射 - 将中文城市名转换为API需要的英文名
    const cityMapping = {
      'yantai': 'Yantai',
      'shanghai': 'Shanghai', 
      'hangzhou': 'Hangzhou',
      'jilin': 'Jilin',
      'chongqing': 'Chongqing'
    }
    
    const englishCity = cityMapping[city] || 'Shanghai'
    
    console.log('🔍 开始加载空间分析数据，城市:', city, '→', englishCity)

    // 先设置城市对应的默认数据
    heatmapData.value = generateCityHeatmapData(city)
    regionalStats.value = generateCityRegionalData(city)
    trafficData.value = generateMockTrafficData()
    densityData.value = generateMockDensityData()

    // 计算统计数据
    spatialStats.value = {
      totalAreas: regionalStats.value.length,
      hotspots: regionalStats.value.filter(item => item.orders > 120).length,
      avgDensity: Math.round(regionalStats.value.reduce((sum, item) => sum + item.orders, 0) / regionalStats.value.length),
      totalRoutes: Math.floor(Math.random() * 50) + 80
    }

    console.log('📊 默认数据设置完成，开始尝试加载真实数据...')

    await new Promise(resolve => setTimeout(resolve, 200))
    
    try {
      // 1. 获取热力图数据 - 使用正确的城市名称
      let heatmap = null
      console.log('🌍 正在请求城市数据:', englishCity)
      
      if (selectedMetric.value === 'delivery_time') {
        heatmap = await spatialAnalysisApi.getDeliveryTimeHeatmap(englishCity, today)
      } else {
        heatmap = await spatialAnalysisApi.getHeatmapData(englishCity, today, 1000)
      }
      
      if (heatmap && Array.isArray(heatmap) && heatmap.length > 0) {
        console.log('🔥 获取到真实热力图数据:', heatmap.length, '条')
        console.log('📝 原始数据样例:', heatmap.slice(0, 3))
        console.log('🏙️ 数据归属城市:', heatmap[0]?.city)
        
        // 验证数据是否属于当前选择的城市
        const isCorrectCity = heatmap.some(item => 
          item.city === englishCity || 
          item.city?.toLowerCase() === englishCity.toLowerCase()
        )
        
        if (!isCorrectCity) {
          console.warn('⚠️ 返回的数据不属于当前选择的城市，可能API不支持该城市或数据不存在')
          ElMessage.warning(`${englishCity} 城市暂无数据，显示模拟数据`)
        } else {
          // 根据实际API数据结构解析
          const processedData = []
          let validCount = 0
          let invalidCount = 0
          
          heatmap.forEach((item, index) => {
            // 根据你提供的数据结构，直接使用正确的字段名
            const lng = parseFloat(item.lngGrid)
            const lat = parseFloat(item.latGrid)
            
            // 根据选择的指标决定使用哪个数值字段
            let value = 0
            switch (selectedMetric.value) {
              case 'delivery_time':
                value = parseFloat(item.avgDeliveryTime)
                break
              case 'couriers':
                value = parseFloat(item.uniqueCouriers)
                break
              case 'orders':
              default:
                value = parseFloat(item.deliveryCount)
                break
            }
            
            // 根据城市调整经纬度验证范围
            const cityConfig = CITY_COORDINATES[city] || CITY_COORDINATES.shanghai
            const isInCityRange = lng >= cityConfig.lng[0] && lng <= cityConfig.lng[1] && 
                                lat >= cityConfig.lat[0] && lat <= cityConfig.lat[1]
            
            // 验证数据有效性 - 放宽地理范围限制，优先使用城市配置的范围
            if (!isNaN(lng) && !isNaN(lat) && !isNaN(value) && 
                lng > 0 && lat > 0 && value > 0) {
              processedData.push([lng, lat, value])
              validCount++
              
              if (!isInCityRange && index < 3) {
                console.log(`📍 数据点 #${index} 超出城市范围但仍保留:`, { lng, lat, value, expectedRange: cityConfig })
              }
            } else {
              invalidCount++
              if (index < 5) { // 只记录前5个无效数据的详情
                console.warn(`⚠️ 无效数据 #${index}:`, {
                  original: item,
                  parsed: { lng, lat, value },
                  isValidLng: !isNaN(lng) && lng > 0,
                  isValidLat: !isNaN(lat) && lat > 0,
                  isValidValue: !isNaN(value) && value > 0
                })
              }
            }
          })
          
          console.log(`📊 数据处理结果: 有效 ${validCount} 条, 无效 ${invalidCount} 条`)
          
          if (processedData.length > 0) {
            heatmapData.value = processedData
            console.log('🗺️ 使用真实热力图数据:', processedData.length, '条')
            console.log('🎯 处理后数据样例:', processedData.slice(0, 3))
          } else {
            console.warn('⚠️ 所有数据都无效，保持默认数据')
          }
        }
      } else {
        console.log('📝 未获取到有效的热力图数据，使用默认数据')
        ElMessage.info(`${englishCity} 城市暂无热力图数据`)
      }
    } catch (error) {
      console.warn('⚠️ 热力图数据API调用失败:', error.message)
      ElMessage.warning(`获取 ${englishCity} 热力图数据失败: ${error.message}`)
    }

    try {
      // 2. 获取区域统计数据 - 使用正确的城市名称
      console.log('📊 正在获取区域统计数据:', englishCity)
      const todayData = await spatialAnalysisApi.getTodaySpatialData(englishCity)
      
      if (todayData && Array.isArray(todayData) && todayData.length > 0) {
        console.log('📊 获取到今日空间数据:', todayData.length, '条')
        console.log('🏙️ 区域数据归属城市:', todayData[0]?.city)
        
        // 验证数据是否属于当前选择的城市
        const isCorrectCity = todayData.some(item => 
          item.city === englishCity || 
          item.city?.toLowerCase() === englishCity.toLowerCase()
        )
        
        if (!isCorrectCity) {
          console.warn('⚠️ 区域数据不属于当前选择的城市')
          ElMessage.warning(`${englishCity} 城市暂无区域数据，显示模拟数据`)
        } else {
          // 按行政区域聚合数据
          const regionStats = {}
          todayData.forEach(item => {
            const lng = parseFloat(item.lngGrid)
            const lat = parseFloat(item.latGrid)
            
            if (!isNaN(lng) && !isNaN(lat)) {
              // 根据经纬度确定行政区域
              const regionName = getRegionByCoordinate(lng, lat, city)
              
              if (!regionStats[regionName]) {
                regionStats[regionName] = {
                  region: regionName,
                  orders: 0,
                  couriers: 0,
                  avgDeliveryTime: 0,
                  totalDeliveryTime: 0,
                  count: 0
                }
              }
              
              regionStats[regionName].orders += item.deliveryCount || 0
              regionStats[regionName].couriers += item.uniqueCouriers || 0
              regionStats[regionName].totalDeliveryTime += item.avgDeliveryTime || 0
              regionStats[regionName].count += 1
            }
          })
          
          // 计算平均值并转换为最终格式
          const processedRegional = Object.values(regionStats)
            .map(region => ({
              region: region.region,
              orders: region.orders,
              couriers: region.couriers,
              avgDeliveryTime: region.count > 0 ? (region.totalDeliveryTime / region.count).toFixed(1) : 0
            }))
            .filter(item => item.orders > 0)
            .sort((a, b) => b.orders - a.orders)
            .slice(0, 8) // 只取前8个区域
          
          if (processedRegional.length > 0) {
            regionalStats.value = processedRegional
            console.log('📍 使用真实区域数据:', processedRegional)
            
            // 重新计算统计数据
            spatialStats.value = {
              totalAreas: regionalStats.value.length,
              hotspots: regionalStats.value.filter(item => item.orders > 20).length,
              avgDensity: Math.round(regionalStats.value.reduce((sum, item) => sum + item.orders, 0) / regionalStats.value.length),
              totalRoutes: Math.floor(Math.random() * 50) + 80
            }
          }
        }
      } else {
        console.log('📝 未获取到有效的区域数据，使用默认数据')
        ElMessage.info(`${englishCity} 城市暂无区域数据`)
      }
    } catch (error) {
      console.warn('⚠️ 今日数据API调用失败:', error.message)
      ElMessage.warning(`获取 ${englishCity} 区域数据失败: ${error.message}`)
    }

    try {
      // 3. 获取密度分析数据 - 使用正确的城市名称
      console.log('⏰ 正在获取密度分析数据:', englishCity)
      const todayData = await spatialAnalysisApi.getTodaySpatialData(englishCity)
      
      if (todayData && Array.isArray(todayData) && todayData.length > 0) {
        console.log('📍 获取到真实今日数据用于密度分析:', todayData.length, '条')
        
        // 按小时聚合数据
        const hourlyData = Array.from({ length: 24 }, (_, hour) => {
          const hourData = todayData.filter(item => {
            // 从日期字段中提取小时，如果没有小时信息则随机分配
            const date = new Date(item.date || Date.now())
            return date.getHours() === hour
          })
          
          const totalOrders = hourData.reduce((sum, item) => sum + (item.deliveryCount || 0), 0)
          const totalCouriers = hourData.reduce((sum, item) => sum + (item.uniqueCouriers || 0), 0)
          const avgResponseTime = hourData.length > 0 
            ? hourData.reduce((sum, item) => sum + (item.avgDeliveryTime || 0), 0) / hourData.length
            : Math.random() * 30 + 10
          
          return {
            hour,
            orderDensity: totalOrders > 0 ? totalOrders : Math.sin(hour / 24 * Math.PI * 2 + Math.PI/2) * 40 + 60,
            courierDensity: totalCouriers > 0 ? totalCouriers : Math.sin((hour - 2) / 24 * Math.PI * 2 + Math.PI/2) * 20 + 30,
            responseTime: avgResponseTime > 0 ? avgResponseTime : Math.random() * 30 + 10
          }
        })
        
        densityData.value = hourlyData
        console.log('⏰ 使用处理后的密度数据')
      }
    } catch (error) {
      console.warn('⚠️ 密度分析数据API调用失败:', error.message)
    }

    console.log('✅ 空间分析数据加载完成')
    console.log('📊 最终统计数据:', spatialStats.value)
    console.log('🔥 最终热力图数据点数:', heatmapData.value.length)
    console.log('📍 最终区域数据条数:', regionalStats.value.length)
    
    ElMessage.success(`${englishCity} 空间分析数据加载完成`)
    
  } catch (error) {
    console.error('❌ 加载空间分析数据失败:', error)
    ElMessage.error('加载空间分析数据失败，显示默认数据')
  } finally {
    loading.value = false
  }
}

// 添加 refreshData 方法
const refreshData = async () => {
  console.log('🔄 手动刷新数据...')
  await loadSpatialData()
}

// 改进指标切换函数 - 避免ECharts冲突
const handleMetricChange = async () => {
  console.log('📈 指标切换为:', selectedMetric.value)
  
  // 立即更新模拟数据
  const city = dashboardStore.selectedCity
  if (selectedMetric.value === 'delivery_time') {
    heatmapData.value = generateCityHeatmapData(city).map(item => [
      item[0], item[1], Math.random() * 60 + 10 // 10-70分钟
    ])
  } else if (selectedMetric.value === 'couriers') {
    heatmapData.value = generateCityHeatmapData(city).map(item => [
      item[0], item[1], Math.random() * 20 + 1 // 1-21个配送员
    ])
  } else {
    heatmapData.value = generateCityHeatmapData(city)
  }
  
  // 等待图表重新渲染后再加载API数据
  await new Promise(resolve => setTimeout(resolve, 300))
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
    
    if (result) {
      routeOptimization.value = result
      ElMessage.success('获取路径优化建议成功')
    } else {
      routeOptimization.value = {
        optimizedRoute: `${routeStart.value} → 中转点A → 中转点B → ${routeEnd.value}`,
        duration: Math.floor(Math.random() * 30) + 20,
        distance: (Math.random() * 10 + 5).toFixed(1),
        savings: Math.floor(Math.random() * 15) + 5
      }
      ElMessage.success('获取路径优化建议成功（模拟数据）')
    }
  } catch (error) {
    console.error('Failed to get route optimization:', error)
    routeOptimization.value = {
      optimizedRoute: `${routeStart.value} → ${routeEnd.value}（直达路线）`,
      duration: Math.floor(Math.random() * 30) + 15,
      distance: (Math.random() * 8 + 3).toFixed(1),
      savings: Math.floor(Math.random() * 10) + 3
    }
    ElMessage.warning('API调用失败，显示模拟优化建议')
  }
}

onMounted(() => {
  console.log('🚀 空间分析组件已挂载')
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