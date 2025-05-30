import api from './index.js'

export const operationalEfficiencyApi = {
  // 获取运营效率概览
  getOverview: (city) => 
    api.get(`/api/operational/overview/${city}`),
  
  // 获取配送员效率分析
  getCourierEfficiency: (city, dateRange) => 
    api.get(`/api/operational/courier-efficiency/${city}`, { params: { startDate: dateRange[0], endDate: dateRange[1] } }),
  
  // 获取车辆利用率
  getVehicleUtilization: (city, date) => 
    api.get(`/api/operational/vehicle-utilization/${city}`, { params: { date } }),
  
  // 获取仓库效率分析
  getWarehouseEfficiency: (city, warehouseId) => 
    api.get(`/api/operational/warehouse-efficiency/${city}`, { params: { warehouseId } }),
  
  // 获取订单处理效率
  getOrderProcessingEfficiency: (city, dateRange) => 
    api.get(`/api/operational/order-processing/${city}`, { params: { startDate: dateRange[0], endDate: dateRange[1] } }),
  
  // 获取成本分析
  getCostAnalysis: (city, period = 'month') => 
    api.get(`/api/operational/cost-analysis/${city}`, { params: { period } }),
  
  // 获取资源优化建议
  getOptimizationSuggestions: (city) => 
    api.get(`/api/operational/optimization/${city}`),
  
  // 获取服务质量指标
  getServiceQualityMetrics: (city, dateRange) => 
    api.get(`/api/operational/service-quality/${city}`, { params: { startDate: dateRange[0], endDate: dateRange[1] } }),
  
  // 获取容量vs需求分析
  getCapacityDemandAnalysis: (city, period = 'week') => 
    api.get(`/api/operational/capacity-demand/${city}`, { params: { period } }),
  
  // 获取路线优化分析
  getRouteOptimizationAnalysis: (city, date) => 
    api.get(`/api/operational/route-optimization/${city}`, { params: { date } }),
  
  // 获取客户满意度分析
  getCustomerSatisfactionAnalysis: (city, dateRange) => 
    api.get(`/api/operational/customer-satisfaction/${city}`, { params: { startDate: dateRange[0], endDate: dateRange[1] } }),
  
  // 获取运营效率趋势
  getEfficiencyTrend: (city, days = 30) => 
    api.get(`/api/operational/efficiency-trend/${city}`, { params: { days } })
}