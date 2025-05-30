import api from './index.js'

export const spatialAnalysisApi = {
  // 获取热力图数据
  getHeatmapData: (city, date, metricType = 'orders') => 
    api.get(`/api/spatial/heatmap/${city}`, { params: { date, metricType } }),
  
  // 获取区域配送统计
  getRegionalStats: (city) => 
    api.get(`/api/spatial/regional-stats/${city}`),
  
  // 获取配送路径优化建议
  getRouteOptimization: (city, startPoint, endPoint) => 
    api.get(`/api/spatial/route-optimization/${city}`, { params: { startPoint, endPoint } }),
  
  // 获取交通拥堵分析
  getTrafficAnalysis: (city, timeRange) => 
    api.get(`/api/spatial/traffic/${city}`, { params: { startTime: timeRange[0], endTime: timeRange[1] } }),
  
  // 获取配送密度分析
  getDeliveryDensity: (city, date) => 
    api.get(`/api/spatial/density/${city}`, { params: { date } }),
  
  // 获取仓库覆盖分析
  getWarehouseCoverage: (city) => 
    api.get(`/api/spatial/warehouse-coverage/${city}`),
  
  // 获取地理围栏数据
  getGeofenceData: (city) => 
    api.get(`/api/spatial/geofence/${city}`),
  
  // 获取空间聚类分析
  getClusterAnalysis: (city, date, clusterType = 'kmeans') => 
    api.get(`/api/spatial/cluster/${city}`, { params: { date, clusterType } })
}