import api from './index.js'

// 城市名称映射
const cityMapping = {
  'yantai': 'Yantai',
  'shanghai': 'Shanghai', 
  'hangzhou': 'Hangzhou',
  'jilin': 'Jilin',
  'chongqing': 'Chongqing',
  '烟台': 'Yantai',
  '上海': 'Shanghai',
  '杭州': 'Hangzhou', 
  '吉林': 'Jilin',
  '重庆': 'Chongqing'
}

export const predictiveApi = {
  // 获取需求预测数据 - 使用趋势数据接口
  getDemandForecast: (city, days = 7) => {
    const englishCity = cityMapping[city] || city
    console.log('📈 获取需求预测数据，城市:', city, '→', englishCity)
    return api.get(`/api/predictive-analysis/trends/${englishCity}`)
  },

  // 获取容量分析数据
  getCapacityAnalysis: (city) => {
    const englishCity = cityMapping[city] || city
    console.log('📊 获取容量分析数据，城市:', city, '→', englishCity)
    return api.get(`/api/predictive-analysis/capacity/${englishCity}`)
  },

  // 获取最新预测数据
  getLatestPrediction: (city, dataType = null, limit = 24) => {
    const englishCity = cityMapping[city] || city
    console.log('🔍 获取最新预测数据，城市:', city, '→', englishCity, 'dataType:', dataType)
    
    if (dataType) {
      return api.get(`/api/predictive-analysis/latest/${englishCity}/${dataType}`, {
        params: { limit }
      })
    }
    return api.get(`/api/predictive-analysis/latest/${englishCity}`)
  },

  // 获取订单量趋势
  getOrderVolumeTrend: (city, startDate, dataType = null) => {
    const englishCity = cityMapping[city] || city
    console.log('📈 获取订单量趋势，城市:', city, '→', englishCity, 'startDate:', startDate)
    
    const params = { startDate }
    if (dataType) params.dataType = dataType
    
    return api.get(`/api/predictive-analysis/trend/order-volume/${englishCity}`, { params })
  },

  // 获取效率预测趋势
  getEfficiencyTrend: (city, startDate) => {
    const englishCity = cityMapping[city] || city
    console.log('⚡ 获取效率预测趋势，城市:', city, '→', englishCity, 'startDate:', startDate)
    
    return api.get(`/api/predictive-analysis/trend/efficiency/${englishCity}`, {
      params: { startDate }
    })
  },

  // 获取小时分布分析
  getHourlyDistribution: (city, startDate) => {
    const englishCity = cityMapping[city] || city
    console.log('🕐 获取小时分布分析，城市:', city, '→', englishCity, 'startDate:', startDate)
    
    return api.get(`/api/predictive-analysis/distribution/hourly/${englishCity}`, {
      params: { startDate }
    })
  },

  // 获取预测汇总统计
  getSummaryStats: (city, startDate) => {
    const englishCity = cityMapping[city] || city
    console.log('📊 获取预测汇总统计，城市:', city, '→', englishCity, 'startDate:', startDate)
    
    return api.get(`/api/predictive-analysis/summary/${englishCity}`, {
      params: { startDate }
    })
  },

  // 获取指定日期的预测数据
  getByDate: (city, date) => {
    const englishCity = cityMapping[city] || city
    console.log('📅 获取指定日期预测数据，城市:', city, '→', englishCity, 'date:', date)
    
    return api.get(`/api/predictive-analysis/date/${englishCity}`, {
      params: { date }
    })
  },

  // 获取自定义查询的预测分析
  getCustomPrediction: (city, dataType, startDate, endDate) => {
    const englishCity = cityMapping[city] || city
    console.log('🔧 获取自定义预测分析，城市:', city, '→', englishCity)
    
    return api.get(`/api/predictive-analysis/custom/${englishCity}`, {
      params: { dataType, startDate, endDate }
    })
  },

  // 多条件搜索预测数据
  searchPredictiveData: (params) => {
    console.log('🔍 搜索预测数据，参数:', params)
    
    // 如果有城市参数，进行映射
    if (params.city) {
      params.city = cityMapping[params.city] || params.city
    }
    
    return api.get('/api/predictive-analysis/search', { params })
  },

  // 获取容量分析统计
  getCapacityStats: (city, startDate) => {
    const englishCity = cityMapping[city] || city
    console.log('📈 获取容量分析统计，城市:', city, '→', englishCity)
    
    return api.get(`/api/predictive-analysis/stats/capacity/${englishCity}`, {
      params: { startDate }
    })
  },

  // 获取数据类型统计
  getDataTypeStats: (city, startDate) => {
    const englishCity = cityMapping[city] || city
    console.log('📊 获取数据类型统计，城市:', city, '→', englishCity)
    
    return api.get(`/api/predictive-analysis/stats/data-type/${englishCity}`, {
      params: { startDate }
    })
  },

  // 获取城市间预测对比
  getCityComparison: (cities, dataType = null, startDate, endDate) => {
    // 城市数组映射
    const englishCities = cities.map(city => cityMapping[city] || city)
    console.log('🔄 获取城市间预测对比，城市:', cities, '→', englishCities)
    
    const params = { cities: englishCities, startDate, endDate }
    if (dataType) params.dataType = dataType
    
    return api.get('/api/predictive-analysis/comparison', { params })
  },

  // 统计记录数
  getCount: (city, dataType = null) => {
    const englishCity = cityMapping[city] || city
    console.log('🔢 统计预测数据记录数，城市:', city, '→', englishCity)
    
    const params = {}
    if (dataType) params.dataType = dataType
    
    return api.get(`/api/predictive-analysis/count/${englishCity}`, { params })
  },

  // 清理旧数据
  cleanupOldData: (cutoffDate) => {
    console.log('🧹 清理旧预测数据，截止日期:', cutoffDate)
    
    return api.delete('/api/predictive-analysis/cleanup', {
      params: { cutoffDate }
    })
  }
}