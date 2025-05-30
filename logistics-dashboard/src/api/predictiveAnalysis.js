import api from './index.js'

export const predictiveAnalysisApi = {
  // 获取需求预测
  getDemandForecast: (city, days = 7) => 
    api.get(`/api/predictive/demand-forecast/${city}`, { params: { days } }),
  
  // 获取配送时间预测
  getDeliveryTimeForecast: (city, orderData) => 
    api.post(`/api/predictive/delivery-time/${city}`, orderData),
  
  // 获取异常检测结果
  getAnomalyDetection: (city, dateRange) => 
    api.get(`/api/predictive/anomaly/${city}`, { params: { startDate: dateRange[0], endDate: dateRange[1] } }),
  
  // 获取容量规划建议
  getCapacityPlanning: (city, forecastPeriod = 30) => 
    api.get(`/api/predictive/capacity-planning/${city}`, { params: { forecastPeriod } }),
  
  // 获取风险评估
  getRiskAssessment: (city) => 
    api.get(`/api/predictive/risk-assessment/${city}`),
  
  // 获取季节性分析
  getSeasonalAnalysis: (city, year) => 
    api.get(`/api/predictive/seasonal/${city}`, { params: { year } }),
  
  // 获取机器学习模型性能
  getModelPerformance: (modelType) => 
    api.get(`/api/predictive/model-performance`, { params: { modelType } }),
  
  // 训练预测模型
  trainModel: (modelConfig) => 
    api.post('/api/predictive/train-model', modelConfig),
  
  // 获取预测准确度
  getForecastAccuracy: (city, period = 30) => 
    api.get(`/api/predictive/accuracy/${city}`, { params: { period } })
}