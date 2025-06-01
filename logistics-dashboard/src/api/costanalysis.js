import api from './index.js'

export const costAnalysisApi = {
  // 根据城市查询成本分析
  getCostAnalysisByCity: (city, startDate, endDate) => 
    api.get(`/api/cost-analysis/city/${city}`, { 
      params: { startDate, endDate } 
    }),

  // 根据区域查询成本分析
  getCostAnalysisByRegion: (regionId, startDate, endDate) => 
    api.get(`/api/cost-analysis/region/${regionId}`, { 
      params: { startDate, endDate } 
    }),

  // 多条件查询成本分析
  searchCostAnalysis: (params) => 
    api.get('/api/cost-analysis/search', { params }),

  // 获取城市成本趋势
  getCostTrend: (city, startDate) => 
    api.get(`/api/cost-analysis/trend/city/${city}`, { 
      params: { startDate } 
    }),

  // 获取区域成本排行
  getRegionCostRanking: (city, startDate, limit = 10) => 
    api.get('/api/cost-analysis/ranking/region', { 
      params: { city, startDate, limit } 
    }),

  // 获取分析类型统计
  getAnalysisTypeStats: (city, startDate) => 
    api.get('/api/cost-analysis/stats/analysis-type', { 
      params: { city, startDate } 
    }),

  // 获取高成本告警
  getHighCostAlerts: (threshold, date, limit = 10) => 
    api.get('/api/cost-analysis/alerts/high-cost', { 
      params: { threshold, date, limit } 
    }),

  // 获取成本汇总统计
  getCostSummary: (city, startDate) => 
    api.get(`/api/cost-analysis/summary/${city}`, { 
      params: { startDate } 
    }),

  // 统计城市成本分析数量
  getCostAnalysisCount: (city) => 
    api.get(`/api/cost-analysis/count/${city}`),

  // 保存成本分析数据
  saveCostAnalysis: (data) => 
    api.post('/api/cost-analysis', data),

  // 更新成本分析数据
  updateCostAnalysis: (data) => 
    api.put('/api/cost-analysis', data),

  // 批量保存成本分析数据
  batchSaveCostAnalysis: (dataArray) => 
    api.post('/api/cost-analysis/batch', dataArray),

  // 清理旧数据
  cleanupOldData: (cutoffDate) => 
    api.delete('/api/cost-analysis/cleanup', { 
      params: { cutoffDate } 
    }),

  // 组合查询方法 - 获取成本分析概览数据
  getCostOverview: async (city, startDate, endDate) => {
    try {
      const [analysis, trend, summary, ranking] = await Promise.all([
        costAnalysisApi.getCostAnalysisByCity(city, startDate, endDate),
        costAnalysisApi.getCostTrend(city, startDate),
        costAnalysisApi.getCostSummary(city, startDate),
        costAnalysisApi.getRegionCostRanking(city, startDate, 10)
      ])
      
      return {
        analysis,
        trend,
        summary,
        ranking
      }
    } catch (error) {
      console.error('获取成本分析概览失败:', error)
      throw error
    }
  },

  // 获取成本分析仪表板数据
  getDashboardData: async (city, dateRange) => {
    try {
      const [startDate, endDate] = dateRange
      
      const [
        costData,
        trendData,
        summaryData,
        rankingData,
        alertsData,
        statsData
      ] = await Promise.all([
        costAnalysisApi.getCostAnalysisByCity(city, startDate, endDate),
        costAnalysisApi.getCostTrend(city, startDate),
        costAnalysisApi.getCostSummary(city, startDate),
        costAnalysisApi.getRegionCostRanking(city, startDate, 10),
        costAnalysisApi.getHighCostAlerts(1000, endDate, 5), // 假设1000为高成本阈值
        costAnalysisApi.getAnalysisTypeStats(city, startDate)
      ])
      
      return {
        costData,
        trendData,
        summaryData,
        rankingData,
        alertsData,
        statsData
      }
    } catch (error) {
      console.error('获取成本分析仪表板数据失败:', error)
      throw error
    }
  },

  // 获取成本分析报告数据
  getReportData: async (city, dateRange, analysisType = null) => {
    try {
      const [startDate, endDate] = dateRange
      
      const searchParams = {
        city,
        startDate,
        endDate
      }
      
      if (analysisType) {
        searchParams.analysisType = analysisType
      }
      
      const [
        searchResults,
        summary,
        trend,
        ranking,
        alerts
      ] = await Promise.all([
        costAnalysisApi.searchCostAnalysis(searchParams),
        costAnalysisApi.getCostSummary(city, startDate),
        costAnalysisApi.getCostTrend(city, startDate),
        costAnalysisApi.getRegionCostRanking(city, startDate, 20),
        costAnalysisApi.getHighCostAlerts(800, endDate, 10)
      ])
      
      return {
        searchResults,
        summary,
        trend,
        ranking,
        alerts
      }
    } catch (error) {
      console.error('获取成本分析报告数据失败:', error)
      throw error
    }
  }
}

// 成本分析数据类型定义（用于TypeScript或文档）
export const CostAnalysisTypes = {
  // 成本分析指标结构
  CostAnalysisMetrics: {
    city: 'string',           // 城市
    regionId: 'number',       // 区域ID
    date: 'string',           // 日期
    totalCost: 'number',      // 总成本
    totalFuelCost: 'number',  // 总燃料成本
    totalTimeCost: 'number',  // 总时间成本
    totalOrders: 'number',    // 总订单数
    totalDistance: 'number',  // 总距离
    costPerOrder: 'number',   // 每订单成本
    costPerKm: 'number',      // 每公里成本
    fuelCostRatio: 'number',  // 燃料成本比率
    analysisType: 'string'    // 分析类型
  }
}

// 常用的查询参数预设
export const CostAnalysisPresets = {
  // 常用分析类型
  ANALYSIS_TYPES: {
    DAILY: 'daily',
    WEEKLY: 'weekly', 
    MONTHLY: 'monthly',
    QUARTERLY: 'quarterly'
  },
  
  // 默认查询参数
  DEFAULT_PARAMS: {
    limit: 10,
    threshold: 1000
  },
  
  // 支持的城市列表
  CITIES: ['Shanghai', 'Chongqing', 'Yantai', 'Hangzhou', 'Jilin']
}