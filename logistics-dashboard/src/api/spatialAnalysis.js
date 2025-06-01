import api from './index.js'

export const spatialAnalysisApi = {
  // 获取今日空间分析数据
  getTodaySpatialData: (city) => {
    return api.get(`/api/spatial-analysis/today/${city}`)
  },

  // 获取指定日期的空间分析数据
  getSpatialDataByDate: (city, date) => {
    return api.get(`/api/spatial-analysis/date/${city}`, { params: { date } })
  },

  // 获取热点区域分析
  getHotspots: (city, date = null, limit = 20) => {
    const params = { limit }
    if (date) params.date = date
    return api.get(`/api/spatial-analysis/hotspots/${city}`, { params })
  },

  // 获取密度分析 - 用作热力图数据
  getHeatmapData: (city, date = null, metricType = 'orders') => {
    const params = {}
    if (date) params.date = date
    return api.get(`/api/spatial-analysis/density/${city}`, { params })
  },

  // 获取配送密度热点
  getDeliveryDensityHeatmap: (city, startDate, limit = 50) => {
    return api.get(`/api/spatial-analysis/heatmap/density/${city}`, { 
      params: { startDate, limit } 
    })
  },

  // 获取配送时间热图数据
  getDeliveryTimeHeatmap: (city, startDate) => {
    return api.get(`/api/spatial-analysis/heatmap/delivery-time/${city}`, { 
      params: { startDate } 
    })
  },

  // 获取空间分布统计 - 用作区域配送统计
  getRegionalStats: (city, startDate = null) => {
    const today = new Date().toISOString().split('T')[0]
    const params = { startDate: startDate || today }
    return api.get(`/api/spatial-analysis/stats/distribution/${city}`, { params })
  },

  // 获取网格聚合数据
  getGridAggregation: (city, date, gridSize = 0.01) => {
    return api.get(`/api/spatial-analysis/grid-aggregation/${city}`, { 
      params: { date, gridSize } 
    })
  },

  // 获取空间汇总统计
  getSpatialSummary: (city, startDate) => {
    return api.get(`/api/spatial-analysis/summary/${city}`, { 
      params: { startDate } 
    })
  },

  // 获取配送员空间分布
  getCourierDistribution: (city, startDate, limit = 30) => {
    return api.get(`/api/spatial-analysis/courier-distribution/${city}`, { 
      params: { startDate, limit } 
    })
  },

  // 获取指定时间范围的空间分析
  getSpatialDataByRange: (city, startDate, endDate) => {
    return api.get(`/api/spatial-analysis/range/${city}`, { 
      params: { startDate, endDate } 
    })
  },

  // 根据地理范围获取空间分析
  getSpatialDataByGeoRange: (city, bounds, startDate, endDate) => {
    return api.get(`/api/spatial-analysis/geo-range/${city}`, { 
      params: { 
        minLng: bounds.minLng,
        maxLng: bounds.maxLng,
        minLat: bounds.minLat,
        maxLat: bounds.maxLat,
        startDate,
        endDate
      } 
    })
  },

  // 获取城市间空间对比
  getCityComparison: (cities, startDate, endDate) => {
    return api.get('/api/spatial-analysis/comparison', { 
      params: { 
        cities: cities,
        startDate,
        endDate
      } 
    })
  },

  // 统计记录数
  getSpatialDataCount: (city) => {
    return api.get(`/api/spatial-analysis/count/${city}`)
  },

  // 以下是为了兼容旧代码而保留的方法，内部调用新的API
  
  // 获取区域配送统计 (兼容方法)
  getRegionalStatistics: (city) => {
    return spatialAnalysisApi.getRegionalStats(city)
  },

  // 获取配送路径优化建议 (模拟实现)
  getRouteOptimization: (city, startPoint, endPoint) => {
    console.warn('路径优化功能暂未提供API，返回模拟数据')
    return Promise.resolve({
      optimizedRoute: [startPoint, endPoint],
      distance: Math.random() * 10 + 5,
      duration: Math.random() * 30 + 15,
      savings: Math.random() * 20 + 5
    })
  },

  // 获取交通拥堵分析 (模拟实现)
  getTrafficAnalysis: (city, timeRange) => {
    console.warn('交通分析功能暂未提供API，返回模拟数据')
    return Promise.resolve([
      { area: '区域A', congestionLevel: Math.random() },
      { area: '区域B', congestionLevel: Math.random() },
      { area: '区域C', congestionLevel: Math.random() }
    ])
  },

  // 获取配送密度分析 (兼容方法)
  getDeliveryDensity: (city, date) => {
    return spatialAnalysisApi.getHeatmapData(city, date)
  },

  // 获取仓库覆盖分析 (模拟实现)
  getWarehouseCoverage: (city) => {
    console.warn('仓库覆盖分析功能暂未提供API，返回模拟数据')
    return Promise.resolve([
      { warehouseId: 'WH001', coverageArea: 15.5, utilization: 0.85 },
      { warehouseId: 'WH002', coverageArea: 12.3, utilization: 0.72 },
      { warehouseId: 'WH003', coverageArea: 18.7, utilization: 0.91 }
    ])
  },

  // 获取地理围栏数据 (模拟实现)
  getGeofenceData: (city) => {
    console.warn('地理围栏功能暂未提供API，返回模拟数据')
    return Promise.resolve([
      { 
        id: 'GF001', 
        name: '核心配送区',
        coordinates: [[121.4, 31.2], [121.5, 31.2], [121.5, 31.3], [121.4, 31.3]],
        type: 'delivery'
      }
    ])
  },

  // 获取空间聚类分析 (使用热点分析代替)
  getClusterAnalysis: (city, date, clusterType = 'kmeans') => {
    console.warn('聚类分析使用热点分析数据')
    return spatialAnalysisApi.getHotspots(city, date, 20)
  }
}