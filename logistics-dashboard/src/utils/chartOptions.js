// ECharts图表配置选项工具

import { CHART_COLORS } from './constants.js'

/* ========== 基础配置 ========== */

// 默认主题色
const defaultColors = CHART_COLORS.PRIMARY

// 通用网格配置
const defaultGrid = {
  left: '3%',
  right: '4%',
  bottom: '3%',
  containLabel: true
}

// 通用图例配置
const defaultLegend = {
  type: 'scroll',
  orient: 'horizontal',
  left: 'center',
  top: 'top',
  textStyle: {
    fontSize: 12,
    color: '#666'
  }
}

// 通用工具箱配置
const defaultToolbox = {
  feature: {
    saveAsImage: {
      title: '保存为图片',
      name: 'chart',
      pixelRatio: 2
    },
    restore: {
      title: '还原'
    },
    dataZoom: {
      title: {
        zoom: '区域缩放',
        back: '区域缩放还原'
      }
    }
  },
  right: 20,
  top: 20
}

/* ========== 折线图配置 ========== */

/**
 * 创建折线图配置
 * @param {Object} options - 配置选项
 * @returns {Object} ECharts配置对象
 */
export const createLineChartOptions = (options = {}) => {
  const {
    title = '',
    subtitle = '',
    xAxisData = [],
    series = [],
    colors = defaultColors,
    smooth = true,
    showArea = false,
    showSymbol = true,
    symbolSize = 6,
    lineWidth = 2,
    grid = defaultGrid,
    legend = defaultLegend,
    showToolbox = true,
    animation = true,
    animationDuration = 1000
  } = options

  return {
    color: colors,
    title: title ? {
      text: title,
      subtext: subtitle,
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#333'
      },
      subtextStyle: {
        fontSize: 12,
        color: '#666'
      }
    } : undefined,
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff'
      },
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    },
    legend: series.length > 1 ? legend : undefined,
    toolbox: showToolbox ? defaultToolbox : undefined,
    grid,
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisLine: {
        lineStyle: {
          color: '#ddd'
        }
      },
      axisTick: {
        alignWithLabel: true
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: '#ddd'
        }
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0',
          type: 'dashed'
        }
      }
    },
    series: series.map((item, index) => ({
      name: item.name,
      type: 'line',
      data: item.data,
      smooth,
      symbol: showSymbol ? 'circle' : 'none',
      symbolSize,
      lineStyle: {
        width: lineWidth
      },
      areaStyle: showArea ? {
        opacity: 0.3
      } : undefined,
      itemStyle: {
        color: colors[index % colors.length]
      }
    })),
    animation,
    animationDuration
  }
}

/* ========== 柱状图配置 ========== */

/**
 * 创建柱状图配置
 * @param {Object} options - 配置选项
 * @returns {Object} ECharts配置对象
 */
export const createBarChartOptions = (options = {}) => {
  const {
    title = '',
    subtitle = '',
    xAxisData = [],
    series = [],
    colors = defaultColors,
    barWidth = '60%',
    stack = '',
    direction = 'vertical', // vertical 或 horizontal
    grid = defaultGrid,
    legend = defaultLegend,
    showToolbox = true,
    animation = true,
    animationDuration = 1000
  } = options

  const isHorizontal = direction === 'horizontal'

  return {
    color: colors,
    title: title ? {
      text: title,
      subtext: subtitle,
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#333'
      }
    } : undefined,
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff'
      }
    },
    legend: series.length > 1 ? legend : undefined,
    toolbox: showToolbox ? defaultToolbox : undefined,
    grid,
    xAxis: {
      type: isHorizontal ? 'value' : 'category',
      data: isHorizontal ? undefined : xAxisData,
      axisLine: {
        lineStyle: {
          color: '#ddd'
        }
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      },
      splitLine: isHorizontal ? {
        lineStyle: {
          color: '#f0f0f0',
          type: 'dashed'
        }
      } : undefined
    },
    yAxis: {
      type: isHorizontal ? 'category' : 'value',
      data: isHorizontal ? xAxisData : undefined,
      axisLine: {
        lineStyle: {
          color: '#ddd'
        }
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      },
      splitLine: !isHorizontal ? {
        lineStyle: {
          color: '#f0f0f0',
          type: 'dashed'
        }
      } : undefined
    },
    series: series.map((item, index) => ({
      name: item.name,
      type: 'bar',
      data: item.data,
      barWidth,
      stack,
      itemStyle: {
        color: colors[index % colors.length],
        borderRadius: stack ? 0 : [4, 4, 0, 0]
      },
      emphasis: {
        itemStyle: {
          opacity: 0.8
        }
      }
    })),
    animation,
    animationDuration
  }
}

/* ========== 饼图配置 ========== */

/**
 * 创建饼图配置
 * @param {Object} options - 配置选项
 * @returns {Object} ECharts配置对象
 */
export const createPieChartOptions = (options = {}) => {
  const {
    title = '',
    subtitle = '',
    data = [],
    colors = defaultColors,
    radius = '70%',
    center = ['50%', '50%'],
    roseType = '', // 'radius' 为玫瑰图
    showLabel = true,
    showLabelLine = true,
    showLegend = true,
    legendPosition = 'right',
    showToolbox = true,
    animation = true,
    animationDuration = 1000
  } = options

  const legendConfig = showLegend ? {
    ...defaultLegend,
    orient: legendPosition === 'right' || legendPosition === 'left' ? 'vertical' : 'horizontal',
    left: legendPosition === 'left' ? 'left' : legendPosition === 'right' ? 'right' : 'center',
    top: legendPosition === 'top' ? 'top' : legendPosition === 'bottom' ? 'bottom' : 'middle'
  } : undefined

  return {
    color: colors,
    title: title ? {
      text: title,
      subtext: subtitle,
      left: 'center',
      top: '20px',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#333'
      }
    } : undefined,
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff'
      },
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: legendConfig,
    toolbox: showToolbox ? defaultToolbox : undefined,
    series: [{
      name: '数据',
      type: 'pie',
      radius,
      center,
      roseType,
      data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      label: {
        show: showLabel,
        formatter: '{b}: {d}%',
        fontSize: 12,
        color: '#666'
      },
      labelLine: {
        show: showLabelLine
      }
    }],
    animation,
    animationDuration
  }
}

/* ========== 热力图配置 ========== */

/**
 * 创建热力图配置
 * @param {Object} options - 配置选项
 * @returns {Object} ECharts配置对象
 */
export const createHeatmapOptions = (options = {}) => {
  const {
    title = '',
    subtitle = '',
    xAxisData = [],
    yAxisData = [],
    data = [],
    colors = CHART_COLORS.HEATMAP,
    min = 0,
    max = 100,
    showVisualMap = true,
    showLabel = false,
    grid = { ...defaultGrid, height: '60%', top: '10%' },
    showToolbox = true,
    animation = true
  } = options

  return {
    title: title ? {
      text: title,
      subtext: subtitle,
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#333'
      }
    } : undefined,
    tooltip: {
      position: 'top',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff'
      },
      formatter: function(params) {
        return `${params.name}: ${params.value[2]}`
      }
    },
    toolbox: showToolbox ? defaultToolbox : undefined,
    grid,
    xAxis: {
      type: 'category',
      data: xAxisData,
      splitArea: {
        show: true
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      }
    },
    yAxis: {
      type: 'category',
      data: yAxisData,
      splitArea: {
        show: true
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      }
    },
    visualMap: showVisualMap ? {
      min,
      max,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '5%',
      inRange: {
        color: colors
      }
    } : undefined,
    series: [{
      name: '热力图',
      type: 'heatmap',
      data,
      label: {
        show: showLabel,
        color: '#fff',
        fontSize: 12
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }],
    animation
  }
}

/* ========== 仪表盘配置 ========== */

/**
 * 创建仪表盘配置
 * @param {Object} options - 配置选项
 * @returns {Object} ECharts配置对象
 */
export const createGaugeOptions = (options = {}) => {
  const {
    title = '',
    value = 0,
    name = '',
    min = 0,
    max = 100,
    unit = '%',
    colors = [
      [0.3, '#FF6E76'],
      [0.7, '#FDDD60'],
      [1, '#58D9F9']
    ],
    showProgress = true,
    animation = true,
    animationDuration = 2000
  } = options

  return {
    title: title ? {
      text: title,
      left: 'center',
      top: '20px',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#333'
      }
    } : undefined,
    tooltip: {
      formatter: '{a} <br/>{b} : {c}' + unit
    },
    series: [{
      name: name,
      type: 'gauge',
      min,
      max,
      progress: {
        show: showProgress
      },
      detail: {
        valueAnimation: true,
        formatter: `{value}${unit}`,
        fontSize: 20,
        fontWeight: 'bold',
        color: 'auto'
      },
      data: [{
        value,
        name
      }],
      axisLine: {
        lineStyle: {
          width: 30,
          color: colors
        }
      },
      pointer: {
        itemStyle: {
          color: 'auto'
        }
      },
      axisTick: {
        distance: -30,
        length: 8,
        lineStyle: {
          color: '#fff',
          width: 2
        }
      },
      splitLine: {
        distance: -30,
        length: 30,
        lineStyle: {
          color: '#fff',
          width: 4
        }
      },
      axisLabel: {
        color: 'auto',
        distance: 40,
        fontSize: 16
      }
    }],
    animation,
    animationDuration
  }
}

/* ========== 散点图配置 ========== */

/**
 * 创建散点图配置
 * @param {Object} options - 配置选项
 * @returns {Object} ECharts配置对象
 */
export const createScatterOptions = (options = {}) => {
  const {
    title = '',
    subtitle = '',
    series = [],
    colors = defaultColors,
    symbolSize = 10,
    grid = defaultGrid,
    legend = defaultLegend,
    showToolbox = true,
    animation = true
  } = options

  return {
    color: colors,
    title: title ? {
      text: title,
      subtext: subtitle,
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#333'
      }
    } : undefined,
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff'
      },
      formatter: function(params) {
        return `${params.seriesName}<br/>${params.value[0]}, ${params.value[1]}`
      }
    },
    legend: series.length > 1 ? legend : undefined,
    toolbox: showToolbox ? defaultToolbox : undefined,
    grid,
    xAxis: {
      type: 'value',
      splitLine: {
        lineStyle: {
          color: '#f0f0f0',
          type: 'dashed'
        }
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      splitLine: {
        lineStyle: {
          color: '#f0f0f0',
          type: 'dashed'
        }
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      }
    },
    series: series.map((item, index) => ({
      name: item.name,
      data: item.data,
      type: 'scatter',
      symbolSize: item.symbolSize || symbolSize,
      itemStyle: {
        color: colors[index % colors.length]
      }
    })),
    animation
  }
}

/* ========== 雷达图配置 ========== */

/**
 * 创建雷达图配置
 * @param {Object} options - 配置选项
 * @returns {Object} ECharts配置对象
 */
export const createRadarOptions = (options = {}) => {
  const {
    title = '',
    subtitle = '',
    indicator = [],
    series = [],
    colors = defaultColors,
    showLegend = true,
    showToolbox = true,
    animation = true
  } = options

  return {
    color: colors,
    title: title ? {
      text: title,
      subtext: subtitle,
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#333'
      }
    } : undefined,
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff'
      }
    },
    legend: showLegend ? {
      ...defaultLegend,
      bottom: 20
    } : undefined,
    toolbox: showToolbox ? defaultToolbox : undefined,
    radar: {
      indicator,
      radius: '60%',
      splitNumber: 5,
      axisLine: {
        lineStyle: {
          color: '#ddd'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0'
        }
      },
      splitArea: {
        show: false
      }
    },
    series: [{
      name: '雷达图',
      type: 'radar',
      data: series,
      symbol: 'circle',
      symbolSize: 4,
      lineStyle: {
        width: 2
      },
      areaStyle: {
        opacity: 0.3
      }
    }],
    animation
  }
}

/* ========== 漏斗图配置 ========== */

/**
 * 创建漏斗图配置
 * @param {Object} options - 配置选项
 * @returns {Object} ECharts配置对象
 */
export const createFunnelOptions = (options = {}) => {
  const {
    title = '',
    subtitle = '',
    data = [],
    colors = defaultColors,
    sort = 'descending', // 'descending' 或 'ascending'
    gap = 2,
    showLegend = true,
    showToolbox = true,
    animation = true
  } = options

  return {
    color: colors,
    title: title ? {
      text: title,
      subtext: subtitle,
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#333'
      }
    } : undefined,
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff'
      },
      formatter: '{a} <br/>{b} : {c}%'
    },
    legend: showLegend ? {
      ...defaultLegend,
      bottom: 20
    } : undefined,
    toolbox: showToolbox ? defaultToolbox : undefined,
    series: [{
      name: '漏斗图',
      type: 'funnel',
      left: '10%',
      width: '80%',
      sort,
      gap,
      data,
      label: {
        show: true,
        position: 'inside',
        fontSize: 12,
        color: '#fff'
      },
      labelLine: {
        show: false
      },
      itemStyle: {
        borderColor: '#fff',
        borderWidth: 1
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }],
    animation
  }
}

/* ========== 导出默认配置 ========== */

export default {
  line: createLineChartOptions,
  bar: createBarChartOptions,
  pie: createPieChartOptions,
  heatmap: createHeatmapOptions,
  gauge: createGaugeOptions,
  scatter: createScatterOptions,
  radar: createRadarOptions,
  funnel: createFunnelOptions,
  
  // 通用配置
  defaultColors,
  defaultGrid,
  defaultLegend,
  defaultToolbox
}