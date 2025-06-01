import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useDashboardStore = defineStore('dashboard', () => {
  const selectedCity = ref('上海')
  const selectedDateRange = ref([])
  
  const setSelectedCity = (city) => {
    selectedCity.value = city
  }
  
  const setSelectedDateRange = (dateRange) => {
    selectedDateRange.value = dateRange
  }
  
  return {
    selectedCity,
    selectedDateRange,
    setSelectedCity,
    setSelectedDateRange
  }
})