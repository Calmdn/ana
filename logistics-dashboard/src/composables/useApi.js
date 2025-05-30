import { ref, reactive } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'

export function useApi() {
  const loading = ref(false)
  const error = ref(null)
  const data = ref(null)
  
  // 请求状态管理
  const state = reactive({
    pending: false,
    success: false,
    error: false
  })
  
  // 执行API请求的通用方法
  const execute = async (apiCall, options = {}) => {
    const {
      loadingMessage = '',
      successMessage = '',
      errorMessage = '请求失败',
      showLoading = true,
      showSuccess = false,
      showError = true,
      transform = null // 数据转换函数
    } = options
    
    try {
      // 重置状态
      loading.value = showLoading
      error.value = null
      state.pending = true
      state.success = false
      state.error = false
      
      if (loadingMessage && showLoading) {
        ElMessage.info(loadingMessage)
      }
      
      // 执行API调用
      const response = await apiCall()
      
      // 数据转换
      data.value = transform ? transform(response) : response
      
      // 成功状态
      state.success = true
      
      if (successMessage && showSuccess) {
        ElMessage.success(successMessage)
      }
      
      return data.value
      
    } catch (err) {
      console.error('API Error:', err)
      error.value = err
      state.error = true
      
      if (showError) {
        const message = err.response?.data?.message || err.message || errorMessage
        ElMessage.error(message)
      }
      
      throw err
      
    } finally {
      loading.value = false
      state.pending = false
    }
  }
  
  // 批量执行多个API请求
  const executeAll = async (apiCalls, options = {}) => {
    const {
      concurrent = false, // 是否并发执行
      stopOnError = false, // 遇到错误时是否停止
      showProgress = false
    } = options
    
    const results = []
    const errors = []
    
    loading.value = true
    state.pending = true
    
    try {
      if (concurrent) {
        // 并发执行
        const promises = apiCalls.map(async (apiCall, index) => {
          try {
            const result = await apiCall()
            return { index, result, error: null }
          } catch (err) {
            return { index, result: null, error: err }
          }
        })
        
        const responses = await Promise.all(promises)
        
        responses.forEach(({ index, result, error }) => {
          if (error) {
            errors[index] = error
            if (stopOnError) throw error
          } else {
            results[index] = result
          }
        })
        
      } else {
        // 顺序执行
        for (let i = 0; i < apiCalls.length; i++) {
          try {
            if (showProgress) {
              ElNotification.info({
                title: '执行中',
                message: `正在执行第 ${i + 1}/${apiCalls.length} 个请求...`,
                duration: 1000
              })
            }
            
            const result = await apiCalls[i]()
            results[i] = result
            
          } catch (err) {
            errors[i] = err
            if (stopOnError) throw err
          }
        }
      }
      
      data.value = results
      state.success = true
      
      return { results, errors }
      
    } catch (err) {
      error.value = err
      state.error = true
      throw err
      
    } finally {
      loading.value = false
      state.pending = false
    }
  }
  
  // 轮询请求
  const poll = (apiCall, interval = 5000, options = {}) => {
    const {
      immediate = true,
      maxRetries = -1, // -1 表示无限重试
      onSuccess = null,
      onError = null,
      condition = null // 停止轮询的条件函数
    } = options
    
    let retryCount = 0
    let timerId = null
    const isPolling = ref(false)
    
    const startPolling = async () => {
      if (isPolling.value) return
      
      isPolling.value = true
      
      const pollFunction = async () => {
        try {
          const result = await apiCall()
          data.value = result
          
          if (onSuccess) onSuccess(result)
          
          // 检查停止条件
          if (condition && condition(result)) {
            stopPolling()
            return
          }
          
          // 继续轮询
          if (isPolling.value && (maxRetries === -1 || retryCount < maxRetries)) {
            timerId = setTimeout(pollFunction, interval)
            retryCount++
          } else {
            stopPolling()
          }
          
        } catch (err) {
          error.value = err
          if (onError) onError(err)
          
          // 失败后也继续轮询（除非达到最大重试次数）
          if (isPolling.value && (maxRetries === -1 || retryCount < maxRetries)) {
            timerId = setTimeout(pollFunction, interval)
            retryCount++
          } else {
            stopPolling()
          }
        }
      }
      
      if (immediate) {
        await pollFunction()
      } else {
        timerId = setTimeout(pollFunction, interval)
      }
    }
    
    const stopPolling = () => {
      isPolling.value = false
      if (timerId) {
        clearTimeout(timerId)
        timerId = null
      }
    }
    
    return {
      startPolling,
      stopPolling,
      isPolling
    }
  }
  
  // 缓存请求结果
  const cache = new Map()
  
  const executeWithCache = async (key, apiCall, options = {}) => {
    const {
      ttl = 300000, // 缓存时间（毫秒），默认5分钟
      forceRefresh = false
    } = options
    
    // 检查缓存
    if (!forceRefresh && cache.has(key)) {
      const cached = cache.get(key)
      const now = Date.now()
      
      if (now - cached.timestamp < ttl) {
        data.value = cached.data
        return cached.data
      } else {
        cache.delete(key)
      }
    }
    
    // 执行请求并缓存结果
    const result = await execute(apiCall, options)
    cache.set(key, {
      data: result,
      timestamp: Date.now()
    })
    
    return result
  }
  
  // 清除缓存
  const clearCache = (key = null) => {
    if (key) {
      cache.delete(key)
    } else {
      cache.clear()
    }
  }
  
  // 重试机制
  const retry = async (apiCall, maxRetries = 3, delay = 1000, options = {}) => {
    const { exponentialBackoff = true } = options
    
    for (let i = 0; i <= maxRetries; i++) {
      try {
        return await execute(apiCall, options)
      } catch (err) {
        if (i === maxRetries) {
          throw err
        }
        
        // 计算延迟时间
        const retryDelay = exponentialBackoff ? delay * Math.pow(2, i) : delay
        
        ElMessage.warning(`请求失败，${retryDelay / 1000}秒后重试... (${i + 1}/${maxRetries})`)
        
        await new Promise(resolve => setTimeout(resolve, retryDelay))
      }
    }
  }
  
  return {
    // 状态
    loading,
    error,
    data,
    state,
    
    // 方法
    execute,
    executeAll,
    poll,
    executeWithCache,
    clearCache,
    retry
  }
}

// 创建特定API的hook
export function useKpiApi() {
  const { execute, executeAll, poll, loading, error, data } = useApi()
  
  const getTodayKpi = (city) => {
    return execute(
      () => import('@/api/kpi').then(m => m.kpiApi.getTodayKpi(city)),
      {
        loadingMessage: '正在获取今日KPI数据...',
        successMessage: '',
        showSuccess: false
      }
    )
  }
  
  const getKpiTrend = (city, days = 7) => {
    return execute(
      () => import('@/api/kpi').then(m => m.kpiApi.getRecentKpi(city, days)),
      {
        transform: (data) => {
          // 数据转换逻辑
          return data?.map(item => ({
            date: item.date,
            orders: item.totalOrders,
            couriers: item.activeCouriers,
            avgDeliveryTime: item.avgDeliveryTime
          })) || []
        }
      }
    )
  }
  
  return {
    loading,
    error,
    data,
    getTodayKpi,
    getKpiTrend
  }
}

// 告警API的hook
export function useAlertsApi() {
  const { execute, poll, loading, error, data } = useApi()
  
  const getRecentAlerts = (limit = 50) => {
    return execute(
      () => import('@/api/alerts').then(m => m.alertsApi.getRecentAlerts(limit))
    )
  }
  
  const pollAlerts = (city, interval = 30000) => {
    return poll(
      () => import('@/api/alerts').then(m => m.alertsApi.getTodayAlerts(city)),
      interval,
      {
        onSuccess: (alerts) => {
          if (alerts?.length > 0) {
            ElNotification.warning({
              title: '新告警',
              message: `${city}有${alerts.length}条新告警`,
              duration: 5000
            })
          }
        }
      }
    )
  }
  
  return {
    loading,
    error,
    data,
    getRecentAlerts,
    pollAlerts
  }
}