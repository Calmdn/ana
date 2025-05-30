import api from './index.js'

export const kpiApi = {
  // 健康检查
  healthCheck: () => api.get('/api/kpi/health'),
  
  // 获取今日KPI
  getTodayKpi: (city) => api.get(`/api/kpi/today/${city}`),
  
  // 获取指定日期KPI
  getKpiByDate: (city, date) => api.get(`/api/kpi/date/${city}`, { params: { date } }),
  
  // 获取最近几天KPI
  getRecentKpi: (city, days = 7) => api.get(`/api/kpi/recent/${city}`, { params: { days } }),
  
  // 统计记录数
  getKpiCount: (city) => api.get(`/api/kpi/count/${city}`),
  
  // 清理旧数据
  cleanupOldData: (cutoffDate) => api.delete('/api/kpi/cleanup', { params: { cutoffDate } })
}