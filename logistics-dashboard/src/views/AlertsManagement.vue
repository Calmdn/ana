<template>
  <div class="alerts-management">
    <div class="page-header">
      <h2>告警管理</h2>
      <div class="header-controls">
        <el-button type="primary" @click="refreshAlerts" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button @click="exportAlerts">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
      </div>
    </div>

    <!-- 告警统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card high">
          <div class="stat-content">
            <div class="stat-number">{{ alertStats.high || 0 }}</div>
            <div class="stat-label">高危告警</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card medium">
          <div class="stat-content">
            <div class="stat-number">{{ alertStats.medium || 0 }}</div>
            <div class="stat-label">中危告警</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card low">
          <div class="stat-content">
            <div class="stat-number">{{ alertStats.low || 0 }}</div>
            <div class="stat-label">低危告警</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card total">
          <div class="stat-content">
            <div class="stat-number">{{ alertStats.total || 0 }}</div>
            <div class="stat-label">总告警数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 过滤器 -->
    <el-card class="filter-card">
      <el-form :model="filters" inline>
        <el-form-item label="城市">
          <el-select v-model="filters.city" placeholder="选择城市" clearable>
            <el-option label="烟台" value="yantai" />
            <el-option label="上海" value="shanghai" />
            <el-option label="重庆" value="chongqing" />
            <el-option label="杭州" value="shenzhen" />
            <el-option label="吉林" value="jilin" />
          </el-select>
        </el-form-item>
        <el-form-item label="严重程度">
          <el-select v-model="filters.severity" placeholder="选择严重程度" clearable>
            <el-option label="高危" value="HIGH" />
            <el-option label="中危" value="MEDIUM" />
            <el-option label="低危" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="选择状态" clearable>
            <el-option label="未解决" value="unresolved" />
            <el-option label="已解决" value="resolved" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <DateRangePicker v-model="filters.dateRange" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="applyFilters">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 告警列表 -->
    <el-card class="alerts-list">
      <template #header>
        <div class="card-header">
          <span>告警列表</span>
          <el-button-group>
            <el-button 
              :type="realTimeEnabled ? 'primary' : 'default'" 
              @click="toggleRealTime"
            >
              <el-icon><VideoCameraFilled /></el-icon>
              {{ realTimeEnabled ? '关闭实时' : '开启实时' }}
            </el-button>
            <el-button @click="resolveSelectedAlerts" :disabled="!selectedAlerts.length">
              批量解决
            </el-button>
          </el-button-group>
        </div>
      </template>

      <el-table 
        :data="displayedAlerts" 
        v-loading="loading" 
        @selection-change="handleSelectionChange"
        stripe
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="city" label="城市" width="80" />
        <el-table-column prop="anomalyType" label="告警类型" width="120" />
        <el-table-column prop="anomalySeverity" label="严重程度" width="100">
          <template #default="{ row }">
            <el-tag 
              :type="getSeverityTagType(row.anomalySeverity)"
              size="small"
            >
              {{ getSeverityLabel(row.anomalySeverity) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="isResolved" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isResolved ? 'success' : 'danger'" size="small">
              {{ row.isResolved ? '已解决' : '未解决' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="150">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="!row.isResolved"
              type="primary" 
              size="small" 
              @click="resolveAlert(row.id)"
            >
              解决
            </el-button>
            <el-button type="info" size="small" @click="viewDetails(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>

    <!-- 告警详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="告警详情" width="600px">
      <div v-if="selectedAlert">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="告警ID">{{ selectedAlert.id }}</el-descriptions-item>
          <el-descriptions-item label="城市">{{ selectedAlert.city }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ selectedAlert.anomalyType }}</el-descriptions-item>
          <el-descriptions-item label="严重程度">
            <el-tag :type="getSeverityTagType(selectedAlert.anomalySeverity)">
              {{ getSeverityLabel(selectedAlert.anomalySeverity) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态" :span="2">
            <el-tag :type="selectedAlert.isResolved ? 'success' : 'danger'">
              {{ selectedAlert.isResolved ? '已解决' : '未解决' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">
            {{ selectedAlert.description }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatTime(selectedAlert.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="解决时间">
            {{ selectedAlert.resolvedAt ? formatTime(selectedAlert.resolvedAt) : '未解决' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useAlertsStore } from '@/stores/alerts'
import { Refresh, Download, VideoCameraFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import DateRangePicker from '@/components/common/DateRangePicker.vue'

const alertsStore = useAlertsStore()

const loading = ref(false)
const realTimeEnabled = ref(false)
const selectedAlerts = ref([])
const detailDialogVisible = ref(false)
const selectedAlert = ref(null)
let realTimeTimer = null

const filters = ref({
  city: '',
  severity: '',
  status: '',
  dateRange: []
})

const pagination = ref({
  current: 1,
  pageSize: 20,
  total: 0
})

const alertStats = computed(() => ({
  high: alertsStore.alerts.filter(a => a.anomalySeverity === 'HIGH').length,
  medium: alertsStore.alerts.filter(a => a.anomalySeverity === 'MEDIUM').length,
  low: alertsStore.alerts.filter(a => a.anomalySeverity === 'LOW').length,
  total: alertsStore.alerts.length
}))

const displayedAlerts = computed(() => {
  let filtered = alertsStore.filteredAlerts
  
  const start = (pagination.value.current - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  
  return filtered.slice(start, end)
})

const getSeverityTagType = (severity) => {
  const typeMap = {
    'HIGH': 'danger',
    'MEDIUM': 'warning',
    'LOW': 'success'
  }
  return typeMap[severity] || 'info'
}

const getSeverityLabel = (severity) => {
  const labelMap = {
    'HIGH': '高危',
    'MEDIUM': '中危',
    'LOW': '低危'
  }
  return labelMap[severity] || severity
}

const formatTime = (timeStr) => {
  return new Date(timeStr).toLocaleString('zh-CN')
}

const refreshAlerts = async () => {
  loading.value = true
  try {
    await alertsStore.fetchAlerts({ refresh: true })
  } finally {
    loading.value = false
  }
}

const applyFilters = () => {
  Object.keys(filters.value).forEach(key => {
    alertsStore.setFilter(key, filters.value[key])
  })
  pagination.value.current = 1
}

const resetFilters = () => {
  filters.value = {
    city: '',
    severity: '',
    status: '',
    dateRange: []
  }
  alertsStore.resetFilters()
}

const handleSelectionChange = (selection) => {
  selectedAlerts.value = selection
}

const resolveAlert = async (alertId) => {
  try {
    await alertsStore.resolveAlert(alertId)
    ElMessage.success('告警已解决')
  } catch (error) {
    ElMessage.error('解决告警失败')
  }
}

const resolveSelectedAlerts = async () => {
  if (!selectedAlerts.value.length) return
  
  try {
    await ElMessageBox.confirm('确定要批量解决选中的告警吗？', '确认操作')
    const alertIds = selectedAlerts.value.map(alert => alert.id)
    await alertsStore.resolveAlertsInBatch(alertIds)
    ElMessage.success('批量解决成功')
    selectedAlerts.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量解决失败')
    }
  }
}

const viewDetails = (alert) => {
  selectedAlert.value = alert
  detailDialogVisible.value = true
}

const exportAlerts = () => {
  const data = alertsStore.exportAlerts('csv')
  const blob = new Blob([data], { type: 'text/csv' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'alerts.csv'
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

const toggleRealTime = () => {
  realTimeEnabled.value = !realTimeEnabled.value
  
  if (realTimeEnabled.value) {
    alertsStore.enableRealTime()
    realTimeTimer = setInterval(() => {
      refreshAlerts()
    }, 30000)
    ElMessage.success('已开启实时更新')
  } else {
    alertsStore.disableRealTime()
    if (realTimeTimer) {
      clearInterval(realTimeTimer)
      realTimeTimer = null
    }
    ElMessage.info('已关闭实时更新')
  }
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.current = 1
}

const handleCurrentChange = (page) => {
  pagination.value.current = page
}

onMounted(() => {
  refreshAlerts()
})

onUnmounted(() => {
  if (realTimeTimer) {
    clearInterval(realTimeTimer)
  }
})
</script>

<style scoped>
.alerts-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-controls {
  display: flex;
  gap: 12px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-card.high {
  border-left: 4px solid #F56C6C;
}

.stat-card.medium {
  border-left: 4px solid #E6A23C;
}

.stat-card.low {
  border-left: 4px solid #67C23A;
}

.stat-card.total {
  border-left: 4px solid #409EFF;
}

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.filter-card {
  margin-bottom: 20px;
}

.alerts-list {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>