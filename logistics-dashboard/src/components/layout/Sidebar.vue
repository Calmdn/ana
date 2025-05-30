<template>
  <el-menu
    :default-active="activeIndex"
    class="sidebar-menu"
    router
  >
    <el-menu-item index="/">
      <el-icon><Odometer /></el-icon>
      <span>总览</span>
    </el-menu-item>
    
    <el-menu-item index="/kpi">
      <el-icon><TrendCharts /></el-icon>
      <span>KPI监控</span>
    </el-menu-item>
    
    <el-menu-item index="/alerts">
      <el-icon><Warning /></el-icon>
      <span>告警管理</span>
    </el-menu-item>
    
    <el-menu-item index="/time-efficiency">
      <el-icon><Timer /></el-icon>
      <span>时间效率</span>
    </el-menu-item>
    
    <el-menu-item index="/spatial-analysis">
      <el-icon><Location /></el-icon>
      <span>空间分析</span>
    </el-menu-item>
    
    <el-menu-item index="/predictive-analysis">
      <el-icon><DataAnalysis /></el-icon>
      <span>预测分析</span>
    </el-menu-item>
    
    <el-menu-item index="/operational-efficiency">
      <el-icon><Operation /></el-icon>
      <span>运营效率</span>
    </el-menu-item>
    
    <el-menu-item index="/reports">
      <el-icon><Document /></el-icon>
      <span>报告中心</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { 
  // Dashboard, 
  Odometer,
  TrendCharts, 
  Warning, 
  Timer, 
  Location, 
  DataAnalysis, 
  Operation, 
  Document 
} from '@element-plus/icons-vue'

const route = useRoute()

const activeIndex = computed(() => route.path)
</script>

<style scoped>
.sidebar-menu {
  height: 100%;
  border-right: none;
}
</style>