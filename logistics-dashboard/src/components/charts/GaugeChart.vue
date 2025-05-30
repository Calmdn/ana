<template>
  <div ref="chartContainer" class="chart-container"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {
    type: [Number, Object],
    default: 0
  },
  options: {
    type: Object,
    default: () => ({})
  },
  height: {
    type: String,
    default: '300px'
  }
})

const chartContainer = ref(null)
let chartInstance = null

const defaultOptions = {
  title: { text: '' },
  tooltip: {
    formatter: '{a} <br/>{b} : {c}%'
  },
  series: [{
    name: '业务指标',
    type: 'gauge',
    progress: {
      show: true
    },
    detail: {
      valueAnimation: true,
      formatter: '{value}%'
    },
    data: [{
      value: 0,
      name: '完成度'
    }],
    axisLine: {
      lineStyle: {
        width: 30,
        color: [
          [0.3, '#FF6E76'],
          [0.7, '#FDDD60'],
          [1, '#58D9F9']
        ]
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
      fontSize: 20
    },
    detail: {
      valueAnimation: true,
      formatter: '{value}%',
      color: 'auto'
    }
  }]
}

const initChart = () => {
  if (chartContainer.value && !chartInstance) {
    chartInstance = echarts.init(chartContainer.value)
    updateChart()
  }
}

const updateChart = () => {
  if (chartInstance) {
    const mergedOptions = Object.assign({}, defaultOptions, props.options)
    
    // 处理数据
    if (typeof props.data === 'number') {
      mergedOptions.series[0].data[0].value = props.data
    } else if (typeof props.data === 'object') {
      mergedOptions.series[0].data = [props.data]
    }
    
    chartInstance.setOption(mergedOptions, true)
  }
}

const resizeChart = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

watch(() => props.data, () => {
  nextTick(() => {
    updateChart()
  })
}, { deep: true })

watch(() => props.options, () => {
  nextTick(() => {
    updateChart()
  })
}, { deep: true })

onMounted(() => {
  nextTick(() => {
    initChart()
    window.addEventListener('resize', resizeChart)
  })
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  window.removeEventListener('resize', resizeChart)
})
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: v-bind(height);
}
</style>