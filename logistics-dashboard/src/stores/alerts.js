import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { alertsApi } from '@/api/alerts'
import { ElNotification } from 'element-plus'

export const useAlertsStore = defineStore('alerts', () => {
  // 状态
  const alerts = ref([])
  const recentAlerts = ref([])
  const alertStats = ref({})
  const loading = ref(false)
  const error = ref(null)
  
  // 过滤器状态
  const filters = ref({
    city: '',
    severity: '',
    type: '',
    status: '', // resolved, unresolved, all
    dateRange: []
  })
  
  // 分页状态
  const pagination = ref({
    current: 1,
    pageSize: 20,
    total: 0
  })
  
  // 实时更新配置
  const realTimeConfig = ref({
    enabled: true,
    interval: 30000, // 30秒
    lastUpdate: null
  })
  
  // 计算属性
  const filteredAlerts = computed(() => {
    let result = alerts.value
    
    if (filters.value.city) {
      result = result.filter(alert => alert.city === filters.value.city)
    }
    
    if (filters.value.severity) {
      result = result.filter(alert => alert.anomalySeverity === filters.value.severity)
    }
    
    if (filters.value.type) {
      result = result.filter(alert => alert.anomalyType === filters.value.type)
    }
    
    if (filters.value.status) {
      if (filters.value.status === 'resolved') {
        result = result.filter(alert => alert.isResolved)
      } else if (filters.value.status === 'unresolved') {
        result = result.filter(alert => !alert.isResolved)
      }
    }
    
    if (filters.value.dateRange && filters.value.dateRange.length === 2) {
      const [startDate, endDate] = filters.value.dateRange
      result = result.filter(alert => {
        const alertDate = new Date(alert.createdAt)
        return alertDate >= new Date(startDate) && alertDate <= new Date(endDate)
      })
    }
    
    return result
  })
  
  const unresolvedAlerts = computed(() => {
    return alerts.value.filter(alert => !alert.isResolved)
  })
  
  const highSeverityAlerts = computed(() => {
    return alerts.value.filter(alert => alert.anomalySeverity === 'HIGH')
  })
  
  const alertsByCity = computed(() => {
    const cityMap = new Map()
    alerts.value.forEach(alert => {
      const city = alert.city
      if (!cityMap.has(city)) {
        cityMap.set(city, {
          total: 0,
          unresolved: 0,
          high: 0,
          medium: 0,
          low: 0
        })
      }
      const cityStats = cityMap.get(city)
      cityStats.total++
      if (!alert.isResolved) cityStats.unresolved++
      cityStats[alert.anomalySeverity.toLowerCase()]++
    })
    
    return Object.fromEntries(cityMap)
  })
  
  const alertTrend = computed(() => {
    // 按小时统计最近24小时的告警趋势
    const now = new Date()
    const hours = Array.from({ length: 24 }, (_, i) => {
      const hour = new Date(now)
      hour.setHours(now.getHours() - i, 0, 0, 0)
      return hour
    }).reverse()
    
    return hours.map(hour => {
      const nextHour = new Date(hour)
      nextHour.setHours(hour.getHours() + 1)
      
      const count = alerts.value.filter(alert => {
        const alertTime = new Date(alert.createdAt)
        return alertTime >= hour && alertTime < nextHour
      }).length
      
      return {
        time: hour.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
        count
      }
    })
  })
  
  // Actions
  const fetchAlerts = async (options = {}) => {
    loading.value = true
    error.value = null
    
    try {
      const { 
        page = pagination.value.current,
        pageSize = pagination.value.pageSize,
        city = filters.value.city,
        refresh = false
      } = options
      
      let alertsData = []
      
      if (city) {
        alertsData = await alertsApi.getTodayAlerts(city)
      } else {
        alertsData = await alertsApi.getRecentAlerts(pageSize * page)
      }
      
      if (refresh) {
        alerts.value = alertsData || []
      } else {
        // 合并新数据，避免重复
        const existingIds = new Set(alerts.value.map(a => a.id))
        const newAlerts = (alertsData || []).filter(a => !existingIds.has(a.id))
        alerts.value = [...alerts.value, ...newAlerts]
      }
      
      pagination.value.total = alerts.value.length
      realTimeConfig.value.lastUpdate = new Date()
      
    } catch (err) {
      error.value = err
      console.error('Failed to fetch alerts:', err)
      throw err
    } finally {
      loading.value = false
    }
  }
  
  const fetchRecentAlerts = async (limit = 10) => {
    try {
      const data = await alertsApi.getRecentAlerts(limit)
      recentAlerts.value = data || []
      return data
    } catch (err) {
      console.error('Failed to fetch recent alerts:', err)
      throw err
    }
  }
  
  const fetchAlertStats = async (options = {}) => {
    try {
      const { startDate, endDate, groupBy = 'type_severity' } = options
      const stats = await alertsApi.getAlertsStats(startDate, endDate, groupBy)
      alertStats.value = stats || {}
      return stats
    } catch (err) {
      console.error('Failed to fetch alert stats:', err)
      throw err
    }
  }
  
  const resolveAlert = async (alertId) => {
    try {
      await alertsApi.resolveAlert(alertId)
      
      // 更新本地状态
      const alert = alerts.value.find(a => a.id === alertId)
      if (alert) {
        alert.isResolved = true
        alert.resolvedAt = new Date().toISOString()
      }
      
      ElNotification.success({
        title: '成功',
        message: '告警已解决'
      })
      
    } catch (err) {
      ElNotification.error({
        title: '错误',
        message: '解决告警失败'
      })
      throw err
    }
  }
  
  const resolveAlertsInBatch = async (alertIds) => {
    try {
      await alertsApi.resolveAlertsInBatch(alertIds)
      
      // 更新本地状态
      alertIds.forEach(id => {
        const alert = alerts.value.find(a => a.id === id)
        if (alert) {
          alert.isResolved = true
          alert.resolvedAt = new Date().toISOString()
        }
      })
      
      ElNotification.success({
        title: '成功',
        message: `已解决 ${alertIds.length} 条告警`
      })
      
    } catch (err) {
      ElNotification.error({
        title: '错误',
        message: '批量解决告警失败'
      })
      throw err
    }
  }
  
  const addAlert = (alert) => {
    // 添加新告警到列表开头
    alerts.value.unshift(alert)
    
    // 如果是高严重级别，显示通知
    if (alert.anomalySeverity === 'HIGH') {
      ElNotification.warning({
        title: '高严重级别告警',
        message: `${alert.city}: ${alert.anomalyType}`,
        duration: 0 // 不自动关闭
      })
    }
  }
  
  const updateAlert = (updatedAlert) => {
    const index = alerts.value.findIndex(a => a.id === updatedAlert.id)
    if (index !== -1) {
      alerts.value[index] = { ...alerts.value[index], ...updatedAlert }
    }
  }
  
  const removeAlert = (alertId) => {
    const index = alerts.value.findIndex(a => a.id === alertId)
    if (index !== -1) {
      alerts.value.splice(index, 1)
    }
  }
  
  // 过滤器操作
  const setFilter = (key, value) => {
    filters.value[key] = value
    // 重置分页
    pagination.value.current = 1
  }
  
  const resetFilters = () => {
    filters.value = {
      city: '',
      severity: '',
      type: '',
      status: '',
      dateRange: []
    }
    pagination.value.current = 1
  }
  
  const setPagination = (page, pageSize) => {
    pagination.value.current = page
    if (pageSize) {
      pagination.value.pageSize = pageSize
    }
  }
  
  // 实时更新控制
  const enableRealTime = () => {
    realTimeConfig.value.enabled = true
  }
  
  const disableRealTime = () => {
    realTimeConfig.value.enabled = false
  }
  
  const setRealTimeInterval = (interval) => {
    realTimeConfig.value.interval = interval
  }
  
  // 数据清理
  const clearAlerts = () => {
    alerts.value = []
    recentAlerts.value = []
    alertStats.value = {}
    pagination.value.current = 1
    pagination.value.total = 0
  }
  
  // 导出数据
  const exportAlerts = (format = 'json') => {
    const data = filteredAlerts.value
    
    if (format === 'json') {
      return JSON.stringify(data, null, 2)
    } else if (format === 'csv') {
      const headers = ['ID', '城市', '类型', '严重程度', '状态', '创建时间', '解决时间']
      const rows = data.map(alert => [
        alert.id,
        alert.city,
        alert.anomalyType,
        alert.anomalySeverity,
        alert.isResolved ? '已解决' : '未解决',
        alert.createdAt,
        alert.resolvedAt || ''
      ])
      
      return [headers, ...rows].map(row => row.join(',')).join('\n')
    }
    
    return data
  }
  
  // 统计信息
  const getStatsSummary = () => {
    return {
      total: alerts.value.length,
      unresolved: unresolvedAlerts.value.length,
      resolved: alerts.value.filter(a => a.isResolved).length,
      high: alerts.value.filter(a => a.anomalySeverity === 'HIGH').length,
      medium: alerts.value.filter(a => a.anomalySeverity === 'MEDIUM').length,
      low: alerts.value.filter(a => a.anomalySeverity === 'LOW').length,
      cities: Object.keys(alertsByCity.value).length,
      lastUpdate: realTimeConfig.value.lastUpdate
    }
  }
  
  return {
    // 状态
    alerts,
    recentAlerts,
    alertStats,
    loading,
    error,
    filters,
    pagination,
    realTimeConfig,
    
    // 计算属性
    filteredAlerts,
    unresolvedAlerts,
    highSeverityAlerts,
    alertsByCity,
    alertTrend,
    
    // Actions
    fetchAlerts,
    fetchRecentAlerts,
    fetchAlertStats,
    resolveAlert,
    resolveAlertsInBatch,
    addAlert,
    updateAlert,
    removeAlert,
    
    // 过滤器操作
    setFilter,
    resetFilters,
    setPagination,
    
    // 实时更新
    enableRealTime,
    disableRealTime,
    setRealTimeInterval,
    
    // 工具方法
    clearAlerts,
    exportAlerts,
    getStatsSummary
  }
}, {
  // 持久化配置
  persist: {
    filters: true,
    pagination: true,
    realTimeConfig: true
  }
})