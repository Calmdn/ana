<template>
  <div class="date-range-picker">
    <el-date-picker
      v-model="dateRange"
      type="daterange"
      :range-separator="rangeSeparator"
      :start-placeholder="startPlaceholder"
      :end-placeholder="endPlaceholder"
      :format="format"
      :value-format="valueFormat"
      :shortcuts="shortcuts"
      :disabled-date="disabledDate"
      :clearable="clearable"
      :size="size"
      @change="handleChange"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  rangeSeparator: {
    type: String,
    default: '至'
  },
  startPlaceholder: {
    type: String,
    default: '开始日期'
  },
  endPlaceholder: {
    type: String,
    default: '结束日期'
  },
  format: {
    type: String,
    default: 'YYYY-MM-DD'
  },
  valueFormat: {
    type: String,
    default: 'YYYY-MM-DD'
  },
  clearable: {
    type: Boolean,
    default: true
  },
  size: {
    type: String,
    default: 'default'
  },
  maxDays: {
    type: Number,
    default: null
  },
  showShortcuts: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const dateRange = ref(props.modelValue)

const shortcuts = props.showShortcuts ? [
  {
    text: '今天',
    value: () => {
      const today = new Date()
      return [today, today]
    }
  },
  {
    text: '昨天',
    value: () => {
      const yesterday = new Date()
      yesterday.setDate(yesterday.getDate() - 1)
      return [yesterday, yesterday]
    }
  },
  {
    text: '最近7天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 6)
      return [start, end]
    }
  },
  {
    text: '最近30天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 29)
      return [start, end]
    }
  },
  {
    text: '本月',
    value: () => {
      const now = new Date()
      const start = new Date(now.getFullYear(), now.getMonth(), 1)
      const end = new Date(now.getFullYear(), now.getMonth() + 1, 0)
      return [start, end]
    }
  },
  {
    text: '上月',
    value: () => {
      const now = new Date()
      const start = new Date(now.getFullYear(), now.getMonth() - 1, 1)
      const end = new Date(now.getFullYear(), now.getMonth(), 0)
      return [start, end]
    }
  }
] : []

const disabledDate = (date) => {
  // 禁用未来日期
  if (date > new Date()) {
    return true
  }
  
  // 如果设置了最大天数限制
  if (props.maxDays && dateRange.value && dateRange.value.length === 1) {
    const selectedDate = new Date(dateRange.value[0])
    const diffDays = Math.abs((date - selectedDate) / (1000 * 60 * 60 * 24))
    return diffDays > props.maxDays
  }
  
  return false
}

const handleChange = (value) => {
  dateRange.value = value
  emit('update:modelValue', value)
  emit('change', value)
}

watch(() => props.modelValue, (newValue) => {
  dateRange.value = newValue
}, { deep: true })
</script>

<style scoped>
.date-range-picker {
  display: inline-block;
}
</style>