<template>
  <div class="footer">
    <div class="footer-content">
      <div class="footer-left">
        <span class="copyright">© 2024 物流大数据可视化平台</span>
        <span class="version">版本 v{{ version }}</span>
      </div>
      
      <div class="footer-center">
        <div class="system-status">
          <el-icon :class="['status-icon', systemStatus.toLowerCase()]">
            <component :is="getStatusIcon()" />
          </el-icon>
          <span class="status-text">系统状态: {{ getStatusText() }}</span>
        </div>
      </div>
      
      <div class="footer-right">
        <div class="stats">
          <span class="stat-item">
            <el-icon><User /></el-icon>
            在线用户: {{ onlineUsers }}
          </span>
          <span class="stat-item">
            <el-icon><Monitor /></el-icon>
            CPU: {{ cpuUsage }}%
          </span>
          <span class="stat-item">
            <el-icon><Coin /></el-icon>
            内存: {{ memoryUsage }}%
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { User, Monitor, Coin, CircleCheck, Warning, CircleClose } from '@element-plus/icons-vue'

const version = ref('1.0.0')
const systemStatus = ref('HEALTHY') // HEALTHY, WARNING, ERROR
const onlineUsers = ref(0)
const cpuUsage = ref(0)
const memoryUsage = ref(0)

let statusUpdateTimer = null

const getStatusIcon = () => {
  switch (systemStatus.value) {
    case 'HEALTHY':
      return CircleCheck
    case 'WARNING':
      return Warning
    case 'ERROR':
      return CircleClose
    default:
      return CircleCheck
  }
}

const getStatusText = () => {
  switch (systemStatus.value) {
    case 'HEALTHY':
      return '正常'
    case 'WARNING':
      return '警告'
    case 'ERROR':
      return '异常'
    default:
      return '未知'
  }
}

const updateSystemStats = () => {
  // 模拟系统状态更新
  onlineUsers.value = Math.floor(Math.random() * 50) + 10
  cpuUsage.value = Math.floor(Math.random() * 30) + 20
  memoryUsage.value = Math.floor(Math.random() * 40) + 30
  
  // 根据CPU和内存使用率判断系统状态
  if (cpuUsage.value > 80 || memoryUsage.value > 80) {
    systemStatus.value = 'ERROR'
  } else if (cpuUsage.value > 60 || memoryUsage.value > 60) {
    systemStatus.value = 'WARNING'
  } else {
    systemStatus.value = 'HEALTHY'
  }
}

onMounted(() => {
  updateSystemStats()
  statusUpdateTimer = setInterval(updateSystemStats, 30000) // 每30秒更新一次
})

onUnmounted(() => {
  if (statusUpdateTimer) {
    clearInterval(statusUpdateTimer)
  }
})
</script>

<style scoped>
.footer {
  background: #f5f5f5;
  border-top: 1px solid #e8e8e8;
  padding: 12px 20px;
  font-size: 12px;
  color: #666;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.copyright {
  font-weight: 500;
}

.version {
  color: #999;
}

.footer-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.system-status {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-icon {
  font-size: 14px;
}

.status-icon.healthy {
  color: #67C23A;
}

.status-icon.warning {
  color: #E6A23C;
}

.status-icon.error {
  color: #F56C6C;
}

.footer-right .stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-item .el-icon {
  font-size: 12px;
  color: #999;
}

@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    gap: 8px;
  }
  
  .footer-center,
  .footer-right {
    width: 100%;
    justify-content: center;
  }
  
  .stats {
    justify-content: center;
  }
}
</style>