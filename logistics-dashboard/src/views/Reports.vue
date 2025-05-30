<template>
  <div class="reports">
    <div class="page-header">
      <h2>报告中心</h2>
      <div class="header-controls">
        <el-button type="primary" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          生成报告
        </el-button>
        <el-button @click="refreshReports" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 报告类型快捷入口 -->
    <el-row :gutter="20" class="report-types">
      <el-col :span="6" v-for="(type, index) in reportTypes" :key="index">
        <el-card class="report-type-card" shadow="hover" @click="quickGenerate(type)">
          <div class="type-content">
            <div class="type-icon" :style="{ background: type.color }">
              <el-icon :size="32">
                <component :is="type.icon" />
              </el-icon>
            </div>
            <div class="type-info">
              <div class="type-title">{{ type.title }}</div>
              <div class="type-description">{{ type.description }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 报告列表 -->
    <el-card title="报告列表" class="reports-list">
      <template #header>
        <div class="card-header">
          <span>报告列表</span>
          <div class="header-filters">
            <el-select v-model="filters.type" placeholder="报告类型" clearable style="width: 120px;">
              <el-option label="日报" value="daily" />
              <el-option label="周报" value="weekly" />
              <el-option label="月报" value="monthly" />
              <el-option label="年报" value="yearly" />
              <el-option label="自定义" value="custom" />
            </el-select>
            <el-select v-model="filters.status" placeholder="状态" clearable style="width: 100px;">
              <el-option label="生成中" value="generating" />
              <el-option label="已完成" value="completed" />
              <el-option label="失败" value="failed" />
            </el-select>
            <el-button @click="applyFilters">筛选</el-button>
          </div>
        </div>
      </template>

      <el-table :data="displayedReports" v-loading="loading" stripe>
        <el-table-column prop="id" label="报告ID" width="100" />
        <el-table-column prop="title" label="报告标题" min-width="200" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" size="small">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="150">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="size" label="文件大小" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 'completed'"
              type="primary" 
              size="small" 
              @click="downloadReport(row)"
            >
              下载
            </el-button>
            <el-button 
              v-if="row.status === 'completed'"
              type="info" 
              size="small" 
              @click="previewReport(row)"
            >
              预览
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="deleteReport(row.id)"
            >
              删除
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

    <!-- 报告模板管理 -->
    <el-card title="报告模板" class="templates-section">
      <el-row :gutter="20">
        <el-col :span="8" v-for="(template, index) in reportTemplates" :key="index">
          <el-card class="template-card" shadow="hover">
            <div class="template-header">
              <h4>{{ template.name }}</h4>
              <el-tag :type="template.isDefault ? 'success' : 'info'" size="small">
                {{ template.isDefault ? '默认' : '自定义' }}
              </el-tag>
            </div>
            <div class="template-description">
              {{ template.description }}
            </div>
            <div class="template-actions">
              <el-button size="small" @click="useTemplate(template)">
                使用模板
              </el-button>
              <el-button v-if="!template.isDefault" size="small" type="danger" @click="deleteTemplate(template.id)">
                删除
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 生成报告对话框 -->
    <el-dialog v-model="showCreateDialog" title="生成报告" width="600px">
      <el-form :model="reportForm" label-width="100px">
        <el-form-item label="报告标题" required>
          <el-input v-model="reportForm.title" placeholder="请输入报告标题" />
        </el-form-item>
        <el-form-item label="报告类型" required>
          <el-select v-model="reportForm.type" placeholder="选择报告类型">
            <el-option label="日报" value="daily" />
            <el-option label="周报" value="weekly" />
            <el-option label="月报" value="monthly" />
            <el-option label="年报" value="yearly" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <DateRangePicker v-model="reportForm.dateRange" />
        </el-form-item>
        <el-form-item label="包含数据">
          <el-checkbox-group v-model="reportForm.dataTypes">
            <el-checkbox label="kpi">KPI指标</el-checkbox>
            <el-checkbox label="efficiency">运营效率</el-checkbox>
            <el-checkbox label="alerts">告警统计</el-checkbox>
            <el-checkbox label="spatial">空间分析</el-checkbox>
            <el-checkbox label="prediction">预测分析</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="输出格式">
          <el-radio-group v-model="reportForm.format">
            <el-radio label="pdf">PDF</el-radio>
            <el-radio label="excel">Excel</el-radio>
            <el-radio label="word">Word</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="城市">
          <el-select v-model="reportForm.city" placeholder="选择城市">
            <el-option label="全部城市" value="" />
            <el-option label="北京" value="北京" />
            <el-option label="上海" value="上海" />
            <el-option label="广州" value="广州" />
            <el-option label="深圳" value="深圳" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="generateReport" :loading="generating">
            生成报告
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 报告预览对话框 -->
    <el-dialog v-model="showPreviewDialog" title="报告预览" width="80%" top="5vh">
      <div v-if="previewContent" class="preview-content">
        <iframe 
          v-if="previewContent.type === 'pdf'"
          :src="previewContent.url" 
          style="width: 100%; height: 600px; border: none;"
        />
        <div v-else-if="previewContent.type === 'html'" v-html="previewContent.content" />
        <div v-else class="preview-placeholder">
          <el-empty description="暂不支持此格式的预览" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { reportsApi } from '@/api/reports'
import { Plus, Refresh, Document, TrendCharts, Warning, DataAnalysis } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import DateRangePicker from '@/components/common/DateRangePicker.vue'

const dashboardStore = useDashboardStore()

const loading = ref(false)
const generating = ref(false)
const showCreateDialog = ref(false)
const showPreviewDialog = ref(false)

// 报告类型配置
const reportTypes = ref([
  {
    title: '日报',
    description: '每日运营数据汇总',
    type: 'daily',
    color: '#409EFF',
    icon: 'Document'
  },
  {
    title: '周报',
    description: '每周运营趋势分析',
    type: 'weekly',
    color: '#67C23A',
    icon: 'TrendCharts'
  },
  {
    title: '月报',
    description: '月度综合分析报告',
    type: 'monthly',
    color: '#E6A23C',
    icon: 'DataAnalysis'
  },
  {
    title: '异常报告',
    description: '异常事件分析报告',
    type: 'anomaly',
    color: '#F56C6C',
    icon: 'Warning'
  }
])

// 报告列表数据
const reports = ref([])
const reportTemplates = ref([
  {
    id: 1,
    name: '标准日报模板',
    description: '包含基础KPI指标、运营效率分析的标准日报模板',
    isDefault: true
  },
  {
    id: 2,
    name: '详细月报模板',
    description: '包含完整分析图表和趋势预测的详细月报模板',
    isDefault: true
  },
  {
    id: 3,
    name: '异常事件模板',
    description: '专门用于异常事件分析和处理建议的报告模板',
    isDefault: true
  }
])
const previewContent = ref(null)

// 过滤器
const filters = ref({
  type: '',
  status: ''
})

// 分页
const pagination = ref({
  current: 1,
  pageSize: 20,
  total: 0
})

// 报告表单
const reportForm = ref({
  title: '',
  type: 'daily',
  dateRange: [],
  dataTypes: ['kpi', 'efficiency'],
  format: 'pdf',
  city: ''
})

const displayedReports = computed(() => {
  let filtered = reports.value
  
  if (filters.value.type) {
    filtered = filtered.filter(report => report.type === filters.value.type)
  }
  
  if (filters.value.status) {
    filtered = filtered.filter(report => report.status === filters.value.status)
  }
  
  const start = (pagination.value.current - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  
  return filtered.slice(start, end)
})

const getTypeTagType = (type) => {
  const typeMap = {
    daily: 'primary',
    weekly: 'success',
    monthly: 'warning',
    yearly: 'info',
    custom: 'danger'
  }
  return typeMap[type] || 'info'
}

const getTypeLabel = (type) => {
  const labelMap = {
    daily: '日报',
    weekly: '周报',
    monthly: '月报',
    yearly: '年报',
    custom: '自定义'
  }
  return labelMap[type] || type
}

const getStatusTagType = (status) => {
  const typeMap = {
    generating: 'warning',
    completed: 'success',
    failed: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusLabel = (status) => {
  const labelMap = {
    generating: '生成中',
    completed: '已完成',
    failed: '失败'
  }
  return labelMap[status] || status
}

const formatTime = (timeStr) => {
  return new Date(timeStr).toLocaleString('zh-CN')
}

const loadReports = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟报告数据
    reports.value = [
      {
        id: 'RPT001',
        title: '物流运营日报 - 2024-01-15',
        type: 'daily',
        status: 'completed',
        createdAt: '2024-01-15T09:00:00Z',
        size: '2.5MB',
        format: 'pdf'
      },
      {
        id: 'RPT002',
        title: '配送效率周报 - 第2周',
        type: 'weekly',
        status: 'completed',
        createdAt: '2024-01-14T10:30:00Z',
        size: '4.8MB',
        format: 'excel'
      },
      {
        id: 'RPT003',
        title: '异常事件分析报告',
        type: 'custom',
        status: 'generating',
        createdAt: '2024-01-15T11:20:00Z',
        size: '0MB',
        format: 'pdf'
      },
      {
        id: 'RPT004',
        title: '12月运营月报',
        type: 'monthly',
        status: 'failed',
        createdAt: '2024-01-01T14:15:00Z',
        size: '0MB',
        format: 'word'
      }
    ]
    
    pagination.value.total = reports.value.length
    
  } catch (error) {
    console.error('Failed to load reports:', error)
    ElMessage.error('加载报告列表失败')
  } finally {
    loading.value = false
  }
}

const refreshReports = () => {
  loadReports()
}

const applyFilters = () => {
  pagination.value.current = 1
  // 实际应用中这里会重新调用API
}

const quickGenerate = (type) => {
  reportForm.value.type = type.type
  reportForm.value.title = `${type.title} - ${new Date().toLocaleDateString()}`
  
  // 根据类型设置默认日期范围
  const today = new Date()
  switch (type.type) {
    case 'daily':
      reportForm.value.dateRange = [today, today]
      break
    case 'weekly':
      const weekStart = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000)
      reportForm.value.dateRange = [weekStart, today]
      break
    case 'monthly':
      const monthStart = new Date(today.getFullYear(), today.getMonth(), 1)
      reportForm.value.dateRange = [monthStart, today]
      break
    default:
      reportForm.value.dateRange = []
  }
  
  showCreateDialog.value = true
}

const generateReport = async () => {
  if (!reportForm.value.title) {
    ElMessage.warning('请输入报告标题')
    return
  }
  
  generating.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success('报告生成任务已提交，请稍后查看')
    showCreateDialog.value = false
    
    // 重置表单
    reportForm.value = {
      title: '',
      type: 'daily',
      dateRange: [],
      dataTypes: ['kpi', 'efficiency'],
      format: 'pdf',
      city: ''
    }
    
    // 刷新报告列表
    setTimeout(() => {
      loadReports()
    }, 1000)
    
  } catch (error) {
    console.error('Failed to generate report:', error)
    ElMessage.error('生成报告失败')
  } finally {
    generating.value = false
  }
}

const downloadReport = async (report) => {
  try {
    // 模拟下载
    ElMessage.success(`正在下载报告: ${report.title}`)
    
    // 实际实现中这里会调用下载API
    // const blob = await reportsApi.downloadReport(report.id, report.format || 'pdf')
    // const url = URL.createObjectURL(blob)
    // const a = document.createElement('a')
    // a.href = url
    // a.download = `${report.title}.${report.format || 'pdf'}`
    // a.click()
    // URL.revokeObjectURL(url)
    
  } catch (error) {
    console.error('Failed to download report:', error)
    ElMessage.error('下载报告失败')
  }
}

const previewReport = async (report) => {
  try {
    // 模拟预览内容
    previewContent.value = {
      type: 'html',
      content: `
        <div style="padding: 20px; font-family: Arial, sans-serif;">
          <h1 style="color: #333; border-bottom: 2px solid #409EFF; padding-bottom: 10px;">
            ${report.title}
          </h1>
          <div style="margin: 20px 0;">
            <p><strong>报告ID:</strong> ${report.id}</p>
            <p><strong>生成时间:</strong> ${formatTime(report.createdAt)}</p>
            <p><strong>报告类型:</strong> ${getTypeLabel(report.type)}</p>
          </div>
          <div style="background: #f5f5f5; padding: 15px; border-radius: 5px; margin: 20px 0;">
            <h3>摘要</h3>
            <p>这是一个示例报告预览。实际的报告内容将包含详细的数据分析、图表和建议。</p>
          </div>
          <div style="margin: 20px 0;">
            <h3>主要指标</h3>
            <ul>
              <li>总订单数: 1,234</li>
              <li>配送成功率: 98.5%</li>
              <li>平均配送时间: 25分钟</li>
              <li>客户满意度: 4.8/5.0</li>
            </ul>
          </div>
        </div>
      `
    }
    showPreviewDialog.value = true
  } catch (error) {
    console.error('Failed to preview report:', error)
    ElMessage.error('预览报告失败')
  }
}

const deleteReport = async (reportId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个报告吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 模拟删除
    reports.value = reports.value.filter(report => report.id !== reportId)
    ElMessage.success('报告删除成功')
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除报告失败')
    }
  }
}

const useTemplate = (template) => {
  reportForm.value.title = `${template.name} - ${new Date().toLocaleDateString()}`
  
  // 根据模板设置默认配置
  if (template.name.includes('日报')) {
    reportForm.value.type = 'daily'
    reportForm.value.dataTypes = ['kpi', 'efficiency']
  } else if (template.name.includes('月报')) {
    reportForm.value.type = 'monthly'
    reportForm.value.dataTypes = ['kpi', 'efficiency', 'alerts', 'spatial', 'prediction']
  } else if (template.name.includes('异常')) {
    reportForm.value.type = 'custom'
    reportForm.value.dataTypes = ['alerts', 'efficiency']
  }
  
  showCreateDialog.value = true
  ElMessage.success(`已应用模板: ${template.name}`)
}

const deleteTemplate = async (templateId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个模板吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    reportTemplates.value = reportTemplates.value.filter(template => template.id !== templateId)
    ElMessage.success('模板删除成功')
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除模板失败')
    }
  }
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.current = 1
  loadReports()
}

const handleCurrentChange = (page) => {
  pagination.value.current = page
  loadReports()
}

onMounted(() => {
  loadReports()
})
</script>

<style scoped>
.reports {
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

.report-types {
  margin-bottom: 20px;
}

.report-type-card {
  cursor: pointer;
  transition: transform 0.2s;
  height: 140px;
}

.report-type-card:hover {
  transform: translateY(-2px);
}

.type-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 10px;
}

.type-icon {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 15px;
  flex-shrink: 0;
}

.type-info {
  flex: 1;
}

.type-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.type-description {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.reports-list {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-filters {
  display: flex;
  gap: 10px;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.templates-section {
  margin-bottom: 20px;
}

.template-card {
  height: 180px;
  margin-bottom: 20px;
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.template-header h4 {
  margin: 0;
  color: #333;
}

.template-description {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 15px;
  flex: 1;
}

.template-actions {
  display: flex;
  gap: 8px;
}

.preview-content {
  max-height: 70vh;
  overflow-y: auto;
}

.preview-placeholder {
  text-align: center;
  padding: 50px;
  color: #999;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>