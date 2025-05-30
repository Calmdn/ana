<template>
  <div class="header">
    <div class="header-left">
      <h1 class="title">物流大数据可视化平台</h1>
    </div>
    
    <div class="header-right">
      <el-select 
        v-model="selectedCity" 
        placeholder="选择城市"
        @change="onCityChange"
        style="width: 120px; margin-right: 20px;"
      >
        <el-option
          v-for="city in cities"
          :key="city.value"
          :label="city.label"
          :value="city.value"
        />
      </el-select>
      
      <el-badge :value="unreadAlerts" class="item">
        <el-button :icon="Bell" circle />
      </el-badge>
      
      <span class="time">{{ currentTime }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { useDashboardStore } from '@/stores/dashboard'

const dashboardStore = useDashboardStore()

const selectedCity = ref('北京')
const currentTime = ref('')
const unreadAlerts = ref(0)

const cities = [
  { label: '北京', value: '北京' },
  { label: '上海', value: '上海' },
  { label: '广州', value: '广州' },
  { label: '深圳', value: '深圳' },
  { label: '杭州', value: '杭州' },
  { label: '南京', value: '南京' }
]

const onCityChange = (city) => {
  dashboardStore.setSelectedCity(city)
}

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN')
}

let timer = null

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.title {
  color: white;
  margin: 0;
  font-size: 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.time {
  color: white;
  margin-left: 20px;
}
</style>