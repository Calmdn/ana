import api from './index.js'

export const reportsApi = {
  // 获取日报
  getDailyReport: (city, date) => 
    api.get(`/api/reports/daily/${city}`, { params: { date } }),
  
  // 获取周报
  getWeeklyReport: (city, weekStart) => 
    api.get(`/api/reports/weekly/${city}`, { params: { weekStart } }),
  
  // 获取月报
  getMonthlyReport: (city, year, month) => 
    api.get(`/api/reports/monthly/${city}`, { params: { year, month } }),
  
  // 获取年报
  getYearlyReport: (city, year) => 
    api.get(`/api/reports/yearly/${city}`, { params: { year } }),
  
  // 获取自定义报告
  getCustomReport: (reportConfig) => 
    api.post('/api/reports/custom', reportConfig),
  
  // 生成报告
  generateReport: (reportType, parameters) => 
    api.post('/api/reports/generate', { reportType, parameters }),
  
  // 下载报告
  downloadReport: (reportId, format = 'pdf') => 
    api.get(`/api/reports/${reportId}/download`, { params: { format }, responseType: 'blob' }),
  
  // 获取报告列表
  getReportsList: (page = 1, pageSize = 20) => 
    api.get('/api/reports/list', { params: { page, pageSize } }),
  
  // 删除报告
  deleteReport: (reportId) => 
    api.delete(`/api/reports/${reportId}`),
  
  // 获取报告模板
  getReportTemplates: () => 
    api.get('/api/reports/templates'),
  
  // 创建报告模板
  createReportTemplate: (templateData) => 
    api.post('/api/reports/templates', templateData),
  
  // 获取KPI总结报告
  getKpiSummary: (city, dateRange) => 
    api.get(`/api/reports/kpi-summary/${city}`, { params: { startDate: dateRange[0], endDate: dateRange[1] } }),
  
  // 获取异常事件报告
  getAnomalyReport: (city, dateRange) => 
    api.get(`/api/reports/anomaly/${city}`, { params: { startDate: dateRange[0], endDate: dateRange[1] } })
}