import api from './index.js'

export const alertsApi = {
  // 获取告警详情
  getAlertById: (id) => api.get(`/api/alerts/${id}`),
  
  // 获取今日告警
  getTodayAlerts: (city) => api.get(`/api/alerts/today/${city}`),
  
  // 获取未解决告警
  getUnresolvedAlerts: (city) => api.get(`/api/alerts/unresolved/${city}`),
  
  // 获取高风险告警
  getHighRiskAlerts: (city) => api.get(`/api/alerts/high-risk/${city}`),
  
  // 按严重程度获取告警
  getAlertsBySeverity: (severity) => api.get(`/api/alerts/severity/${severity}`),
  
  // 按异常类型获取告警
  getAlertsByType: (anomalyType) => api.get(`/api/alerts/type/${anomalyType}`),
  
  // 按时间范围获取告警
  getAlertsByRange: (city, startDate, endDate) => 
    api.get(`/api/alerts/range/${city}`, { params: { startDate, endDate } }),
  
  // 获取最近告警
  getRecentAlerts: (limit = 50) => api.get('/api/alerts/recent', { params: { limit } }),
  
  // 解决告警
  resolveAlert: (alertId) => api.put(`/api/alerts/resolve/${alertId}`),
  
  // 批量解决告警
  resolveAlertsInBatch: (alertIds) => api.put('/api/alerts/resolve/batch', alertIds),
  
  // 获取告警统计
  getAlertsStats: (startDate, endDate, groupBy = 'type_severity') => 
    api.get('/api/alerts/stats', { params: { startDate, endDate, groupBy } }),
  
  // 获取告警趋势
  getAlertsTrend: (city, startDate, endDate) => 
    api.get(`/api/alerts/trend/${city}`, { params: { startDate, endDate } }),
  
  // 获取小时级告警分布
  getHourlyDistribution: (city, date) => 
    api.get(`/api/alerts/distribution/hourly/${city}`, { params: { date } })
}