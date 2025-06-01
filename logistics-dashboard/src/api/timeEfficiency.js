import api from './index.js'

export const timeEfficiencyApi = {
  // 获取今日时间效率数据
  getTodayTimeEfficiency: (city) => {
    return api.get(`/api/time-efficiency/today/${city}`)
  },

  // 获取指定日期的时间效率
  getTimeEfficiencyByDate: (city, date) => {
    return api.get(`/api/time-efficiency/date/${city}`, { 
      params: { date } 
    })
  },

  // 获取效率趋势分析
  getEfficiencyTrend: (city, days = 30) => {
    return api.get(`/api/time-efficiency/trend/${city}`, { 
      params: { days } 
    })
  },

  // 获取效率趋势统计
  getTrendStats: (city, startDate) => {
    return api.get(`/api/time-efficiency/trend-stats/${city}`, { 
      params: { startDate } 
    })
  },

  // 获取指定时间范围的时间效率
  getTimeEfficiencyByRange: (city, startDate, endDate) => {
    return api.get(`/api/time-efficiency/range/${city}`, { 
      params: { startDate, endDate } 
    })
  },

  // 多条件搜索时间效率
  searchTimeEfficiency: (params) => {
    return api.get('/api/time-efficiency/search', { params })
  },

  // 获取效率分布统计
  getDistributionStats: (city, startDate) => {
    return api.get(`/api/time-efficiency/distribution/${city}`, { 
      params: { startDate } 
    })
  },

  // 获取时间效率排行 - 修正数组参数序列化
  getEfficiencyRanking: (cities, startDate, limit = 10) => {
    // 确保 cities 是数组格式
    const citiesArray = Array.isArray(cities) ? cities : [cities]
    
    return api.get('/api/time-efficiency/ranking', { 
      params: { 
        cities: citiesArray,
        startDate, 
        limit 
      },
      paramsSerializer: {
        indexes: null 
      }
    })
  },

  // 获取慢配送分析
  getSlowDeliveryAnalysis: (city, threshold, startDate, limit = 10) => {
    return api.get(`/api/time-efficiency/slow-delivery/${city}`, { 
      params: { threshold, startDate, limit } 
    })
  },

  // 获取快速配送分析
  getFastDeliveryAnalysis: (city, threshold, startDate, limit = 10) => {
    return api.get(`/api/time-efficiency/fast-delivery/${city}`, { 
      params: { threshold, startDate, limit } 
    })
  },

  // 获取时间效率汇总统计
  getSummaryStats: (city, startDate) => {
    return api.get(`/api/time-efficiency/summary/${city}`, { 
      params: { startDate } 
    })
  },

  // 获取最新时间效率数据
  getLatestTimeEfficiency: (city) => {
    return api.get(`/api/time-efficiency/latest/${city}`)
  },

  // 获取城市间时间效率对比 - 同样修正
  getCityComparison: (cities, startDate, endDate) => {
    const citiesArray = Array.isArray(cities) ? cities : [cities]
    
    return api.get('/api/time-efficiency/comparison', { 
      params: { 
        cities: citiesArray,
        startDate, 
        endDate 
      },
      paramsSerializer: {
        indexes: null
      }
    })
  },

  // 统计记录数
  getTimeEfficiencyCount: (city) => {
    return api.get(`/api/time-efficiency/count/${city}`)
  },

  // 保存时间效率数据
  saveTimeEfficiency: (data) => {
    return api.post('/api/time-efficiency', data)
  },

  // 更新时间效率数据
  updateTimeEfficiency: (data) => {
    return api.put('/api/time-efficiency', data)
  },

  // 批量保存时间效率数据
  batchSaveTimeEfficiency: (dataList) => {
    return api.post('/api/time-efficiency/batch', dataList)
  },

  // 清理旧数据
  cleanupOldData: (cutoffDate) => {
    return api.delete('/api/time-efficiency/cleanup', {
      params: { cutoffDate }
    })
  }
}