import api from './index.js'

export const operationalEfficiencyApi = {
  // 获取今日运营效率数据
  getTodayOperationalData: (city) => {
    return api.get(`/api/operational-efficiency/today/${city}`)
  },

  // 获取指定日期的运营效率
  getOperationalDataByDate: (city, date) => {
    return api.get(`/api/operational-efficiency/date/${city}`, { params: { date } })
  },

  // 获取指定时间范围的运营效率
  getOperationalDataByRange: (city, startDate, endDate) => {
    return api.get(`/api/operational-efficiency/range/${city}`, { 
      params: { startDate, endDate } 
    })
  },

  // 获取配送员效率数据
  getCourierEfficiency: (courierId, startDate, endDate) => {
    return api.get(`/api/operational-efficiency/courier/${courierId}`, { 
      params: { startDate, endDate } 
    })
  },

  // 获取区域效率数据
  getRegionEfficiency: (regionId, startDate, endDate) => {
    return api.get(`/api/operational-efficiency/region/${regionId}`, { 
      params: { startDate, endDate } 
    })
  },

  // 多条件搜索效率数据
  searchOperationalData: (params) => {
    return api.get('/api/operational-efficiency/search', { params })
  },

  // 获取城市效率趋势
  getEfficiencyTrend: (city, startDate) => {
    return api.get(`/api/operational-efficiency/trend/${city}`, { 
      params: { startDate } 
    })
  },

  // 获取配送员效率排行
  getCourierRanking: (city, startDate, limit = 10) => {
    return api.get('/api/operational-efficiency/ranking/courier', { 
      params: { city, startDate, limit } 
    })
  },

  // 获取区域效率排行
  getRegionRanking: (city, startDate, limit = 10) => {
    return api.get('/api/operational-efficiency/ranking/region', { 
      params: { city, startDate, limit } 
    })
  },

  // 获取效率分布统计
  getDistributionStats: (city, startDate) => {
    return api.get(`/api/operational-efficiency/distribution/${city}`, { 
      params: { startDate } 
    })
  },

  // 获取低效率告警
  getLowEfficiencyAlerts: (threshold, startDate, limit = 10) => {
    return api.get('/api/operational-efficiency/alerts/low-efficiency', { 
      params: { threshold, startDate, limit } 
    })
  },

  // 获取高效率表现
  getHighEfficiencyPerformance: (threshold, startDate, limit = 10) => {
    return api.get('/api/operational-efficiency/performance/high-efficiency', { 
      params: { threshold, startDate, limit } 
    })
  },

  // 获取运营效率汇总统计
  getSummaryStats: (city, startDate) => {
    return api.get(`/api/operational-efficiency/summary/${city}`, { 
      params: { startDate } 
    })
  },

  // 获取最新运营效率数据
  getLatestOperationalData: (city) => {
    return api.get(`/api/operational-efficiency/latest/${city}`)
  },

  // 获取配送员最新效率数据
  getLatestCourierData: (courierId) => {
    return api.get(`/api/operational-efficiency/latest/courier/${courierId}`)
  },

  // 获取城市间效率对比
  getCityComparison: (cities, startDate, endDate) => {
    return api.get('/api/operational-efficiency/comparison', { 
      params: { cities, startDate, endDate } 
    })
  },

  // 统计城市效率记录数
  getOperationalDataCount: (city) => {
    return api.get(`/api/operational-efficiency/count/${city}`)
  },

  // 统计配送员记录数
  getCourierDataCount: (courierId) => {
    return api.get(`/api/operational-efficiency/count/courier/${courierId}`)
  },

  // 保存运营效率数据
  saveOperationalData: (data) => {
    return api.post('/api/operational-efficiency', data)
  },

  // 更新运营效率数据
  updateOperationalData: (data) => {
    return api.put('/api/operational-efficiency', data)
  },

  // 批量保存运营效率数据
  batchSaveOperationalData: (dataList) => {
    return api.post('/api/operational-efficiency/batch', dataList)
  },

  // 清理旧数据
  cleanupOldData: (cutoffDate) => {
    return api.delete('/api/operational-efficiency/cleanup', {
      params: { cutoffDate }
    })
  }
}