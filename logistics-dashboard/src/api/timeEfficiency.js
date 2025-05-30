import api from './index.js'

export const timeEfficiencyApi = {
  // 获取时间效率概览
  getOverview: (city, dateRange) => 
    api.get(`/api/time-efficiency/overview/${city}`, { params: { startDate: dateRange[0], endDate: dateRange[1] } }),
  
  // 获取配送时间分布
  getDeliveryTimeDistribution: (city, date) => 
    api.get(`/api/time-efficiency/delivery-distribution/${city}`, { params: { date } }),
  
  // 获取时段效率分析
  getHourlyEfficiency: (city, date) => 
    api.get(`/api/time-efficiency/hourly/${city}`, { params: { date } }),
  
  // 获取配送员效率排名
  getCourierEfficiencyRanking: (city, limit = 20) => 
    api.get(`/api/time-efficiency/courier-ranking/${city}`, { params: { limit } }),
  
  // 获取区域效率对比
  getRegionalEfficiency: (city) => 
    api.get(`/api/time-efficiency/regional/${city}`),
  
  // 获取延迟配送分析
  getDelayAnalysis: (city, dateRange) => 
    api.get(`/api/time-efficiency/delays/${city}`, { params: { startDate: dateRange[0], endDate: dateRange[1] } }),
  
  // 获取效率趋势
  getEfficiencyTrend: (city, days = 30) => 
    api.get(`/api/time-efficiency/trend/${city}`, { params: { days } }),
  
  // 获取平均配送时间
  getAverageDeliveryTime: (city, groupBy = 'hour') => 
    api.get(`/api/time-efficiency/average/${city}`, { params: { groupBy } })
}