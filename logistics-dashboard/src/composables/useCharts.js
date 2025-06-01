import { ref, reactive, nextTick, onUnmounted } from 'vue'
import * as echarts from 'echarts'

export function useCharts() {
  const charts = reactive(new Map())
  const loading = ref(false)
  
  // 注册图表实例
  const registerChart = (id, chartInstance) => {
    charts.set(id, chartInstance)
  }
  
  // 注销图表实例
  const unregisterChart = (id) => {
    const chart = charts.get(id)
    if (chart) {
      chart.dispose()
      charts.delete(id)
    }
  }
  
  // 获取图表实例
  const getChart = (id) => {
    return charts.get(id)
  }
  
  // 调整所有图表大小
  const resizeAllCharts = () => {
    charts.forEach(chart => {
      if (chart && !chart.isDisposed()) {
        chart.resize()
      }
    })
  }
  
  // 销毁所有图表
  const disposeAllCharts = () => {
    charts.forEach(chart => {
      if (chart && !chart.isDisposed()) {
        chart.dispose()
      }
    })
    charts.clear()
  }
  
  // 通用图表配置生成器
  const createChartOptions = (type, data, customOptions = {}) => {
    const baseOptions = {
      animation: true,
      animationDuration: 1000,
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(0, 0, 0, 0.8)',
        borderColor: 'transparent',
        textStyle: {
          color: '#fff'
        }
      }
    }
    
    let typeSpecificOptions = {}
    
    switch (type) {
      case 'line':
        typeSpecificOptions = createLineChartOptions(data)
        break
      case 'bar':
        typeSpecificOptions = createBarChartOptions(data)
        break
      case 'pie':
        typeSpecificOptions = createPieChartOptions(data)
        break
      case 'heatmap':
        typeSpecificOptions = createHeatmapOptions(data)
        break
      case 'gauge':
        typeSpecificOptions = createGaugeOptions(data)
        break
      case 'scatter':
        typeSpecificOptions = createScatterOptions(data)
        break
      default:
        console.warn(`Unsupported chart type: ${type}`)
    }
    
    return mergeOptions(baseOptions, typeSpecificOptions, customOptions)
  }
  
  // 折线图配置
  const createLineChartOptions = (data) => {
    const { categories = [], series = [] } = data
    
    return {
      xAxis: {
        type: 'category',
        data: categories,
        axisTick: {
          alignWithLabel: true
        }
      },
      yAxis: {
        type: 'value'
      },
      series: series.map(item => ({
        name: item.name,
        type: 'line',
        data: item.data,
        smooth: true,
        symbolSize: 6,
        lineStyle: {
          width: 2
        }
      }))
    }
  }
  
  // 柱状图配置
  const createBarChartOptions = (data) => {
    const { categories = [], series = [] } = data
    
    return {
      xAxis: {
        type: 'category',
        data: categories
      },
      yAxis: {
        type: 'value'
      },
      series: series.map(item => ({
        name: item.name,
        type: 'bar',
        data: item.data,
        barWidth: '60%',
        itemStyle: {
          borderRadius: [4, 4, 0, 0]
        }
      }))
    }
  }
  
  // 饼图配置
  const createPieChartOptions = (data) => {
    const { series = [] } = data
    
    return {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: series.map(item => ({
        name: item.name,
        type: 'pie',
        radius: item.radius || '50%',
        data: item.data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          show: true,
          formatter: '{b}: {d}%'
        }
      }))
    }
  }
  
  // 热力图配置
  const createHeatmapOptions = (data) => {
    const { xAxis = [], yAxis = [], series = [] } = data
    
    return {
      tooltip: {
        position: 'top'
      },
      grid: {
        height: '50%',
        top: '10%'
      },
      xAxis: {
        type: 'category',
        data: xAxis,
        splitArea: {
          show: true
        }
      },
      yAxis: {
        type: 'category',
        data: yAxis,
        splitArea: {
          show: true
        }
      },
      visualMap: {
        min: 0,
        max: Math.max(...series.flatMap(s => s.data.map(d => d[2]))),
        calculable: true,
        orient: 'horizontal',
        left: 'center',
        bottom: '15%'
      },
      series: [{
        name: '热力图',
        type: 'heatmap',
        data: series[0]?.data || [],
        label: {
          show: true
        }
      }]
    }
  }
  
  // 仪表盘配置
  const createGaugeOptions = (data) => {
    const { value = 0, name = '', max = 100 } = data
    
    return {
      series: [{
        name: name,
        type: 'gauge',
        progress: {
          show: true
        },
        detail: {
          valueAnimation: true,
          formatter: '{value}%'
        },
        data: [{
          value: value,
          name: name
        }],
        max: max
      }]
    }
  }
  
  // 散点图配置
  const createScatterOptions = (data) => {
    const { series = [] } = data
    
    return {
      xAxis: {
        type: 'value',
        splitLine: {
          lineStyle: {
            type: 'dashed'
          }
        }
      },
      yAxis: {
        type: 'value',
        splitLine: {
          lineStyle: {
            type: 'dashed'
          }
        }
      },
      series: series.map(item => ({
        name: item.name,
        data: item.data,
        type: 'scatter',
        symbolSize: item.symbolSize || 10
      }))
    }
  }
  
  // 深度合并配置对象
  const mergeOptions = (...options) => {
    return options.reduce((merged, option) => {
      return deepMerge(merged, option)
    }, {})
  }
  
  const deepMerge = (target, source) => {
    const result = { ...target }
    
    for (const key in source) {
      if (source[key] && typeof source[key] === 'object' && !Array.isArray(source[key])) {
        result[key] = deepMerge(result[key] || {}, source[key])
      } else {
        result[key] = source[key]
      }
    }
    
    return result
  }
  
  // 数据格式化工具
  const formatChartData = {
    // 时间序列数据格式化
    timeSeries: (data, dateField = 'date', valueField = 'value') => {
      return {
        categories: data.map(item => item[dateField]),
        series: [{
          name: '数值',
          data: data.map(item => item[valueField])
        }]
      }
    },
    
    // 分组数据格式化
    grouped: (data, categoryField = 'category', groups = []) => {
      const categories = [...new Set(data.map(item => item[categoryField]))]
      const series = groups.map(group => ({
        name: group.name,
        data: categories.map(category => {
          const item = data.find(d => d[categoryField] === category)
          return item ? item[group.field] : 0
        })
      }))
      
      return { categories, series }
    },
    
    // 饼图数据格式化
    pie: (data, nameField = 'name', valueField = 'value') => {
      return {
        series: [{
          name: '数据',
          data: data.map(item => ({
            name: item[nameField],
            value: item[valueField]
          }))
        }]
      }
    },
    
    // 热力图数据格式化
    heatmap: (data, xField = 'x', yField = 'y', valueField = 'value') => {
      const xAxis = [...new Set(data.map(item => item[xField]))].sort()
      const yAxis = [...new Set(data.map(item => item[yField]))].sort()
      
      const heatmapData = data.map(item => [
        xAxis.indexOf(item[xField]),
        yAxis.indexOf(item[yField]),
        item[valueField]
      ])
      
      return {
        xAxis,
        yAxis,
        series: [{ data: heatmapData }]
      }
    }
  }
  
  // 图表动画控制
  const animateChart = (chartId, options = {}) => {
    const chart = getChart(chartId)
    if (!chart) return
    
    const {
      duration = 1000,
      easing = 'cubicOut',
      delay = 0
    } = options
    
    chart.setOption({
      animation: true,
      animationDuration: duration,
      animationEasing: easing,
      animationDelay: delay
    }, true)
  }
  
  // 图表主题切换
  const switchTheme = (chartId, theme = 'default') => {
    const chart = getChart(chartId)
    if (!chart) return
    
    // 预定义主题
    const themes = {
      dark: {
        backgroundColor: '#1e1e1e',
        textStyle: {
          color: '#fff'
        }
      },
      light: {
        backgroundColor: '#fff',
        textStyle: {
          color: '#333'
        }
      }
    }
    
    const themeOptions = themes[theme] || {}
    chart.setOption(themeOptions, true)
  }
  
  // 图表导出
  const exportChart = (chartId, options = {}) => {
    const chart = getChart(chartId)
    if (!chart) return null
    
    const {
      type = 'png',
      pixelRatio = 1,
      backgroundColor = '#fff'
    } = options
    
    return chart.getDataURL({
      type: `image/${type}`,
      pixelRatio,
      backgroundColor
    })
  }
  
  // 监听窗口大小变化
  let resizeTimer = null
  const handleResize = () => {
    if (resizeTimer) clearTimeout(resizeTimer)
    resizeTimer = setTimeout(() => {
      resizeAllCharts()
    }, 100)
  }
  
  // 自动调整大小
  if (typeof window !== 'undefined') {
    window.addEventListener('resize', handleResize)
  }
  
  // 清理函数
  onUnmounted(() => {
    if (typeof window !== 'undefined') {
      window.removeEventListener('resize', handleResize)
    }
    disposeAllCharts()
  })
  
  return {
    // 状态
    charts,
    loading,
    
    // 图表管理
    registerChart,
    unregisterChart,
    getChart,
    resizeAllCharts,
    disposeAllCharts,
    
    // 配置生成
    createChartOptions,
    createLineChartOptions,
    createBarChartOptions,
    createPieChartOptions,
    createHeatmapOptions,
    createGaugeOptions,
    createScatterOptions,
    
    // 数据格式化
    formatChartData,
    
    // 图表操作
    animateChart,
    switchTheme,
    exportChart,
    
    // 工具方法
    mergeOptions,
    deepMerge
  }
}

// 专用图表hook
export function useLineChart(data, options = {}) {
  const { createChartOptions, formatChartData } = useCharts()
  
  const chartOptions = ref({})
  
  const updateChart = (newData, customOptions = {}) => {
    const formattedData = formatChartData.timeSeries(newData)
    chartOptions.value = createChartOptions('line', formattedData, {
      ...options,
      ...customOptions
    })
  }
  
  // 初始化
  if (data) {
    updateChart(data)
  }
  
  return {
    chartOptions,
    updateChart
  }
}

export function usePieChart(data, options = {}) {
  const { createChartOptions, formatChartData } = useCharts()
  
  const chartOptions = ref({})
  
  const updateChart = (newData, customOptions = {}) => {
    const formattedData = formatChartData.pie(newData)
    chartOptions.value = createChartOptions('pie', formattedData, {
      ...options,
      ...customOptions
    })
  }
  
  if (data) {
    updateChart(data)
  }
  
  return {
    chartOptions,
    updateChart
  }
}