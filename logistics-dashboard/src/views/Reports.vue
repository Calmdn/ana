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
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="reportForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="包含数据">
          <el-checkbox-group v-model="reportForm.dataTypes">
            <el-checkbox label="kpi">KPI指标</el-checkbox>
            <el-checkbox label="efficiency">运营效率</el-checkbox>
            <el-checkbox label="timeEfficiency">时间效率</el-checkbox>
            <el-checkbox label="spatial">空间分析</el-checkbox>
            <el-checkbox label="alerts">告警统计</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="输出格式">
          <el-radio-group v-model="reportForm.format">
            <el-radio label="html">HTML</el-radio>
            <el-radio label="excel">Excel</el-radio>
            <el-radio label="json">JSON</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="城市">
          <el-select v-model="reportForm.city" placeholder="选择城市">
            <el-option label="上海" value="Shanghai" />
            <el-option label="重庆" value="Chongqing" />
            <el-option label="烟台" value="Yantai" />
            <el-option label="杭州" value="Hangzhou" />
            <el-option label="吉林" value="Jilin" />
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
        <div v-if="previewContent.type === 'html'" v-html="previewContent.content" />
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
import { kpiApi } from '@/api/kpi'
import { timeEfficiencyApi } from '@/api/timeEfficiency'
import { operationalEfficiencyApi } from '@/api/operationalEfficiency'
import { spatialAnalysisApi } from '@/api/spatialAnalysis'
import { alertsApi } from '@/api/alerts'
import { Plus, Refresh, Document, TrendCharts, Warning, DataAnalysis } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

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
    type: 'custom',
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
  format: 'html',
  city: 'Shanghai'
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
    custom: 'danger'
  }
  return typeMap[type] || 'info'
}

const getTypeLabel = (type) => {
  const labelMap = {
    daily: '日报',
    weekly: '周报',
    monthly: '月报',
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

// 基于现有API生成报告
const generateReport = async () => {
  if (!reportForm.value.title) {
    ElMessage.warning('请输入报告标题')
    return
  }
  
  if (!reportForm.value.dataTypes || reportForm.value.dataTypes.length === 0) {
    ElMessage.warning('请选择至少一种数据类型')
    return
  }

  generating.value = true
  try {
    console.log('🔍 开始生成报告，参数:', reportForm.value)
    
    const city = reportForm.value.city
    
    // 处理日期范围
    const dateRange = reportForm.value.dateRange && reportForm.value.dateRange.length === 2 
      ? reportForm.value.dateRange
      : [
          new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
          new Date().toISOString().split('T')[0]
        ]
    
    console.log('📍 生成报告城市:', city)
    console.log('📅 报告日期范围:', dateRange)
    
    // 基于选择的数据类型，并行调用相应的API
    const reportData = {}
    const apiPromises = []
    
    if (reportForm.value.dataTypes.includes('kpi')) {
      console.log('📊 获取KPI数据...')
      apiPromises.push(
        loadKpiData(city, dateRange).then(data => {
          reportData.kpi = data
        }).catch(error => {
          console.warn('KPI数据获取失败:', error)
          reportData.kpi = null
        })
      )
    }
    
    if (reportForm.value.dataTypes.includes('timeEfficiency')) {
      console.log('⚡ 获取时间效率数据...')
      apiPromises.push(
        loadTimeEfficiencyData(city, dateRange).then(data => {
          reportData.timeEfficiency = data
        }).catch(error => {
          console.warn('时间效率数据获取失败:', error)
          reportData.timeEfficiency = null
        })
      )
    }
    
    if (reportForm.value.dataTypes.includes('efficiency')) {
      console.log('🔧 获取运营效率数据...')
      apiPromises.push(
        loadOperationalEfficiencyData(city, dateRange).then(data => {
          reportData.operational = data
        }).catch(error => {
          console.warn('运营效率数据获取失败:', error)
          reportData.operational = null
        })
      )
    }
    
    if (reportForm.value.dataTypes.includes('spatial')) {
      console.log('🗺️ 获取空间分析数据...')
      apiPromises.push(
        loadSpatialAnalysisData(city, dateRange).then(data => {
          reportData.spatial = data
        }).catch(error => {
          console.warn('空间分析数据获取失败:', error)
          reportData.spatial = null
        })
      )
    }
    
    if (reportForm.value.dataTypes.includes('alerts')) {
      console.log('🚨 获取告警数据...')
      apiPromises.push(
        loadAlertsData(city, dateRange).then(data => {
          reportData.alerts = data
        }).catch(error => {
          console.warn('告警数据获取失败:', error)
          reportData.alerts = null
        })
      )
    }
    
    // 等待所有API调用完成
    await Promise.all(apiPromises)
    
    console.log('📊 报告数据收集完成:', reportData)
    
    // 生成报告摘要
    const reportSummary = generateReportSummary(reportData, city, dateRange)
    
    // 创建新报告对象
    const newReport = {
      id: `RPT${Date.now()}`,
      title: reportForm.value.title,
      type: reportForm.value.type,
      status: 'completed',
      createdAt: new Date().toISOString(),
      size: Math.round(Math.random() * 5 + 1) + 'MB',
      format: reportForm.value.format,
      city: city,
      dataTypes: reportForm.value.dataTypes,
      dateRange: dateRange,
      data: reportData,
      summary: reportSummary
    }
    
    // 添加到报告列表
    reports.value.unshift(newReport)
    pagination.value.total = reports.value.length
    
    ElMessage.success(`报告 "${reportForm.value.title}" 生成完成`)
    showCreateDialog.value = false
    
    // 重置表单
    reportForm.value = {
      title: '',
      type: 'daily',
      dateRange: [],
      dataTypes: ['kpi', 'efficiency'],
      format: 'html',
      city: 'Shanghai'
    }
    
  } catch (error) {
    console.error('❌ 生成报告失败:', error)
    ElMessage.error(`生成报告失败: ${error.message}`)
  } finally {
    generating.value = false
  }
}

// 加载KPI数据
const loadKpiData = async (city, dateRange) => {
  try {
    const [startDate, endDate] = dateRange
    
    const [summary, trend, distribution] = await Promise.all([
      kpiApi.getKpiSummary(city, startDate, endDate),
      kpiApi.getKpiTrend(city, startDate, endDate),
      kpiApi.getKpiDistribution(city, startDate)
    ])
    
    return {
      summary: summary || {},
      trend: trend || [],
      distribution: distribution || []
    }
  } catch (error) {
    console.error('加载KPI数据失败:', error)
    return null
  }
}

// 加载时间效率数据
const loadTimeEfficiencyData = async (city, dateRange) => {
  try {
    const [startDate, endDate] = dateRange
    
    const [summary, trend, distribution, ranking] = await Promise.all([
      timeEfficiencyApi.getDeliverySummary(city, startDate, endDate),
      timeEfficiencyApi.getDeliveryTrend(city, startDate, endDate),
      timeEfficiencyApi.getTimeDistribution(city, startDate),
      timeEfficiencyApi.getEfficiencyRanking(['Shanghai', 'Jilin', 'Hangzhou', 'Yantai', 'Chongqing'], startDate, 10)
    ])
    
    return {
      summary: summary || {},
      trend: trend || [],
      distribution: distribution || [],
      ranking: ranking || []
    }
  } catch (error) {
    console.error('加载时间效率数据失败:', error)
    return null
  }
}

// 加载运营效率数据
const loadOperationalEfficiencyData = async (city, dateRange) => {
  try {
    const [startDate, endDate] = dateRange
    
    const [summary, trend, distribution] = await Promise.all([
      operationalEfficiencyApi.getEfficiencySummary(city, startDate, endDate),
      operationalEfficiencyApi.getEfficiencyTrend(city, startDate, endDate),
      operationalEfficiencyApi.getEfficiencyDistribution(city, startDate)
    ])
    
    return {
      summary: summary || {},
      trend: trend || [],
      distribution: distribution || []
    }
  } catch (error) {
    console.error('加载运营效率数据失败:', error)
    return null
  }
}

// 加载空间分析数据
const loadSpatialAnalysisData = async (city, dateRange) => {
  try {
    const [startDate, endDate] = dateRange
    
    const [heatmap, clustering, aoi] = await Promise.all([
      spatialAnalysisApi.getDeliveryHeatmap(city, startDate, endDate),
      spatialAnalysisApi.getDeliveryClustering(city, startDate, endDate),
      spatialAnalysisApi.getAoiAnalysis(city, startDate, endDate)
    ])
    
    return {
      heatmap: heatmap || [],
      clustering: clustering || [],
      aoi: aoi || []
    }
  } catch (error) {
    console.error('加载空间分析数据失败:', error)
    return null
  }
}

// 加载告警数据
const loadAlertsData = async (city, dateRange) => {
  try {
    const [startDate, endDate] = dateRange
    
    const [alerts, statistics] = await Promise.all([
      alertsApi.getAlerts({ city, startDate, endDate, status: 'all' }),
      alertsApi.getAlertStatistics(city, startDate, endDate)
    ])
    
    return {
      alerts: alerts || [],
      statistics: statistics || {}
    }
  } catch (error) {
    console.error('加载告警数据失败:', error)
    return null
  }
}

// 生成报告摘要
const generateReportSummary = (reportData, city, dateRange) => {
  const summary = {
    city: city,
    dateRange: dateRange,
    generatedAt: new Date().toISOString(),
    dataTypes: [],
    keyMetrics: {},
    insights: []
  }
  
  // KPI摘要
  if (reportData.kpi && reportData.kpi.summary) {
    summary.dataTypes.push('KPI监控')
    summary.keyMetrics.totalOrders = reportData.kpi.summary.totalOrders || 0
    summary.keyMetrics.deliveryRate = reportData.kpi.summary.deliveryRate || 0
    summary.keyMetrics.avgDeliveryTime = reportData.kpi.summary.avgDeliveryTime || 0
    
    if (reportData.kpi.summary.deliveryRate > 95) {
      summary.insights.push('配送成功率优秀，超过95%')
    }
  }
  
  // 时间效率摘要
  if (reportData.timeEfficiency && reportData.timeEfficiency.summary) {
    summary.dataTypes.push('时间效率分析')
    summary.keyMetrics.fastDeliveryRate = reportData.timeEfficiency.summary.fastDeliveryRate || 0
    summary.keyMetrics.slowDeliveryRate = reportData.timeEfficiency.summary.slowDeliveryRate || 0
    
    if (reportData.timeEfficiency.summary.fastDeliveryRate > 80) {
      summary.insights.push('快速配送表现优秀，超过80%')
    }
  }
  
  // 运营效率摘要
  if (reportData.operational && reportData.operational.summary) {
    summary.dataTypes.push('运营效率分析')
    summary.keyMetrics.avgEfficiencyScore = reportData.operational.summary.avgEfficiencyScore || 0
    summary.keyMetrics.ordersPerHour = reportData.operational.summary.ordersPerHour || 0
  }
  
  // 空间分析摘要
  if (reportData.spatial) {
    summary.dataTypes.push('空间分析')
    if (reportData.spatial.clustering && reportData.spatial.clustering.length > 0) {
      summary.insights.push(`发现${reportData.spatial.clustering.length}个配送热点区域`)
    }
  }
  
  // 告警摘要
  if (reportData.alerts && reportData.alerts.statistics) {
    summary.dataTypes.push('告警管理')
    summary.keyMetrics.totalAlerts = reportData.alerts.statistics.total || 0
    summary.keyMetrics.criticalAlerts = reportData.alerts.statistics.critical || 0
    
    if (reportData.alerts.statistics.critical > 0) {
      summary.insights.push(`发现${reportData.alerts.statistics.critical}个严重告警需要关注`)
    }
  }
  
  return summary
}

// 预览报告
const previewReport = async (report) => {
  try {
    console.log('👁️ 预览报告:', report.title)
    
    const htmlContent = generateReportHTML(report)
    
    previewContent.value = {
      type: 'html',
      content: htmlContent
    }
    
    showPreviewDialog.value = true
    
  } catch (error) {
    console.error('预览报告失败:', error)
    ElMessage.error('预览报告失败')
  }
}

// 生成报告HTML内容
const generateReportHTML = (report) => {
  const { data, summary } = report
  
  let html = `
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="UTF-8">
      <title>${report.title}</title>
      <style>
        body { font-family: 'Microsoft YaHei', Arial, sans-serif; margin: 20px; color: #333; line-height: 1.6; }
        h1 { color: #333; border-bottom: 3px solid #409EFF; padding-bottom: 10px; margin-bottom: 30px; }
        h2 { color: #606266; border-left: 4px solid #409EFF; padding-left: 15px; margin-top: 30px; }
        h3 { color: #909399; margin-top: 20px; }
        .report-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 8px; margin-bottom: 30px; }
        .meta-info { background: #f5f7fa; padding: 20px; border-radius: 8px; margin: 20px 0; }
        .metrics-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin: 20px 0; }
        .metric-card { background: white; border: 1px solid #e4e7ed; border-radius: 8px; padding: 15px; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .metric-value { font-size: 24px; font-weight: bold; color: #409EFF; margin-bottom: 5px; }
        .metric-label { font-size: 14px; color: #606266; }
        .insights { background: #f0f9ff; border-left: 4px solid #409EFF; padding: 15px; margin: 20px 0; }
        .data-section { margin: 30px 0; }
        table { width: 100%; border-collapse: collapse; margin: 15px 0; }
        th, td { border: 1px solid #e4e7ed; padding: 12px; text-align: left; }
        th { background-color: #f5f7fa; font-weight: 600; }
      </style>
    </head>
    <body>
      <div class="report-header">
        <h1 style="color: white; border: none; margin: 0;">${report.title}</h1>
        <p style="margin: 10px 0 0 0; opacity: 0.9;">物流配送数据分析报告</p>
      </div>
      
      <div class="meta-info">
        <h3 style="margin-top: 0;">报告信息</h3>
        <p><strong>报告ID:</strong> ${report.id}</p>
        <p><strong>生成时间:</strong> ${new Date(report.createdAt).toLocaleString('zh-CN')}</p>
        <p><strong>报告类型:</strong> ${getTypeLabel(report.type)}</p>
        <p><strong>目标城市:</strong> ${report.city}</p>
        <p><strong>数据范围:</strong> ${report.dateRange ? report.dateRange.join(' 至 ') : '未指定'}</p>
        <p><strong>数据类型:</strong> ${summary?.dataTypes?.join(', ') || '未指定'}</p>
      </div>
  `
  
  // 关键指标概览
  if (summary?.keyMetrics && Object.keys(summary.keyMetrics).length > 0) {
    html += `
      <h2>关键指标概览</h2>
      <div class="metrics-grid">
    `
    
    const metrics = summary.keyMetrics
    if (metrics.totalOrders !== undefined) {
      html += `
        <div class="metric-card">
          <div class="metric-value">${metrics.totalOrders.toLocaleString()}</div>
          <div class="metric-label">总订单数</div>
        </div>
      `
    }
    
    if (metrics.deliveryRate !== undefined) {
      html += `
        <div class="metric-card">
          <div class="metric-value">${metrics.deliveryRate.toFixed(1)}%</div>
          <div class="metric-label">配送成功率</div>
        </div>
      `
    }
    
    if (metrics.avgDeliveryTime !== undefined) {
      html += `
        <div class="metric-card">
          <div class="metric-value">${metrics.avgDeliveryTime.toFixed(1)}</div>
          <div class="metric-label">平均配送时间(分钟)</div>
        </div>
      `
    }
    
    if (metrics.fastDeliveryRate !== undefined) {
      html += `
        <div class="metric-card">
          <div class="metric-value">${metrics.fastDeliveryRate.toFixed(1)}%</div>
          <div class="metric-label">快速配送率</div>
        </div>
      `
    }
    
    html += `</div>`
  }
  
  // 洞察分析
  if (summary?.insights && summary.insights.length > 0) {
    html += `
      <h2>关键洞察</h2>
      <div class="insights">
        <ul>
    `
    summary.insights.forEach(insight => {
      html += `<li>${insight}</li>`
    })
    html += `
        </ul>
      </div>
    `
  }
  
  // 数据详情部分
  if (data) {
    // KPI数据
    if (data.kpi) {
      html += `
        <h2>KPI监控数据</h2>
        <div class="data-section">
      `
      
      if (data.kpi.summary) {
        html += `<h3>汇总数据</h3>`
        html += `<pre style="background: #f5f7fa; padding: 15px; border-radius: 5px; overflow: auto;">${JSON.stringify(data.kpi.summary, null, 2)}</pre>`
      }
      
      if (data.kpi.trend && data.kpi.trend.length > 0) {
        html += `<h3>趋势数据 (${data.kpi.trend.length} 条记录)</h3>`
        html += generateTableFromArray(data.kpi.trend.slice(0, 10), '仅显示前10条记录')
      }
      
      html += `</div>`
    }
    
    // 时间效率数据
    if (data.timeEfficiency) {
      html += `
        <h2>时间效率分析数据</h2>
        <div class="data-section">
      `
      
      if (data.timeEfficiency.summary) {
        html += `<h3>汇总数据</h3>`
        html += `<pre style="background: #f5f7fa; padding: 15px; border-radius: 5px; overflow: auto;">${JSON.stringify(data.timeEfficiency.summary, null, 2)}</pre>`
      }
      
      if (data.timeEfficiency.ranking && data.timeEfficiency.ranking.length > 0) {
        html += `<h3>城市排行</h3>`
        html += generateTableFromArray(data.timeEfficiency.ranking)
      }
      
      html += `</div>`
    }
    
    // 运营效率数据
    if (data.operational) {
      html += `
        <h2>运营效率分析数据</h2>
        <div class="data-section">
      `
      
      if (data.operational.summary) {
        html += `<h3>汇总数据</h3>`
        html += `<pre style="background: #f5f7fa; padding: 15px; border-radius: 5px; overflow: auto;">${JSON.stringify(data.operational.summary, null, 2)}</pre>`
      }
      
      html += `</div>`
    }
    
    // 告警数据
    if (data.alerts) {
      html += `
        <h2>告警管理数据</h2>
        <div class="data-section">
      `
      
      if (data.alerts.statistics) {
        html += `<h3>告警统计</h3>`
        html += `<pre style="background: #f5f7fa; padding: 15px; border-radius: 5px; overflow: auto;">${JSON.stringify(data.alerts.statistics, null, 2)}</pre>`
      }
      
      if (data.alerts.alerts && data.alerts.alerts.length > 0) {
        html += `<h3>告警详情 (${data.alerts.alerts.length} 条记录)</h3>`
        html += generateTableFromArray(data.alerts.alerts.slice(0, 10), '仅显示前10条记录')
      }
      
      html += `</div>`
    }
  }
  
  html += `
      <div style="margin-top: 50px; padding-top: 20px; border-top: 1px solid #e4e7ed; color: #909399; text-align: center;">
        <p>本报告由物流数据分析系统自动生成 • 生成时间: ${new Date().toLocaleString('zh-CN')}</p>
      </div>
    </body>
    </html>
  `
  
  return html
}

// 从数组生成表格HTML
const generateTableFromArray = (dataArray, note = '') => {
  if (!Array.isArray(dataArray) || dataArray.length === 0) {
    return '<p>暂无数据</p>'
  }
  
  const headers = Object.keys(dataArray[0])
  let html = '<table>'
  
  // 表头
  html += '<thead><tr>'
  headers.forEach(header => {
    html += `<th>${header}</th>`
  })
  html += '</tr></thead>'
  
  // 数据行
  html += '<tbody>'
  dataArray.forEach(row => {
    html += '<tr>'
    headers.forEach(header => {
      let value = row[header]
      if (typeof value === 'object' && value !== null) {
        value = JSON.stringify(value)
      }
      html += `<td>${value || '-'}</td>`
    })
    html += '</tr>'
  })
  html += '</tbody></table>'
  
  if (note) {
    html += `<p style="color: #909399; font-size: 12px; margin-top: 5px;">${note}</p>`
  }
  
  return html
}

// 下载报告
const downloadReport = async (report) => {
  try {
    console.log('📥 开始下载报告:', report.title)
    
    if (report.data) {
      let content = ''
      let mimeType = ''
      let fileName = `${report.title}.${report.format}`
      
      switch (report.format) {
        case 'json':
          content = JSON.stringify(report.data, null, 2)
          mimeType = 'application/json'
          break
          
        case 'excel':
          content = convertToCSV(report.data)
          mimeType = 'application/vnd.ms-excel'
          fileName = `${report.title}.csv`
          break
          
        case 'html':
        default:
          content = generateReportHTML(report)
          mimeType = 'text/html'
          fileName = `${report.title}.html`
          break
      }
      
      // 创建下载链接
      const blob = new Blob([content], { type: mimeType })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = fileName
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
      
      ElMessage.success(`报告 "${report.title}" 下载完成`)
      
    } else {
      ElMessage.warning('报告数据不完整，无法下载')
    }
    
  } catch (error) {
    console.error('下载报告失败:', error)
    ElMessage.error('下载报告失败')
  }
}

// 数据转换为CSV格式
const convertToCSV = (data) => {
  let csvContent = ''
  
  Object.keys(data).forEach(key => {
    if (Array.isArray(data[key]) && data[key].length > 0) {
      csvContent += `\n${key.toUpperCase()}\n`
      const headers = Object.keys(data[key][0])
      csvContent += headers.join(',') + '\n'
      
      data[key].forEach(row => {
        const values = headers.map(header => {
          const value = row[header]
          return typeof value === 'string' && value.includes(',') 
            ? `"${value}"` 
            : value
        })
        csvContent += values.join(',') + '\n'
      })
      csvContent += '\n'
    }
  })
  
  return csvContent || '暂无数据'
}

const loadReports = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟一些示例报告
    reports.value = [
      {
        id: 'RPT001',
        title: '物流运营日报 - 示例',
        type: 'daily',
        status: 'completed',
        createdAt: '2024-01-15T09:00:00Z',
        size: '2.5MB',
        format: 'html'
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
}

const quickGenerate = (type) => {
  reportForm.value.type = type.type
  reportForm.value.title = `${type.title} - ${new Date().toLocaleDateString()}`
  
  const today = new Date()
  switch (type.type) {
    case 'daily':
      reportForm.value.dateRange = [
        today.toISOString().split('T')[0],
        today.toISOString().split('T')[0]
      ]
      break
    case 'weekly':
      const weekStart = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000)
      reportForm.value.dateRange = [
        weekStart.toISOString().split('T')[0],
        today.toISOString().split('T')[0]
      ]
      break
    case 'monthly':
      const monthStart = new Date(today.getFullYear(), today.getMonth(), 1)
      reportForm.value.dateRange = [
        monthStart.toISOString().split('T')[0],
        today.toISOString().split('T')[0]
      ]
      break
    default:
      reportForm.value.dateRange = []
  }
  
  showCreateDialog.value = true
}

const deleteReport = async (reportId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个报告吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    reports.value = reports.value.filter(report => report.id !== reportId)
    pagination.value.total = reports.value.length
    ElMessage.success('报告删除成功')
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除报告失败')
    }
  }
}

const useTemplate = (template) => {
  reportForm.value.title = `${template.name} - ${new Date().toLocaleDateString()}`
  
  if (template.name.includes('日报')) {
    reportForm.value.type = 'daily'
    reportForm.value.dataTypes = ['kpi', 'efficiency']
  } else if (template.name.includes('月报')) {
    reportForm.value.type = 'monthly'
    reportForm.value.dataTypes = ['kpi', 'efficiency', 'alerts', 'spatial', 'timeEfficiency']
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
}

const handleCurrentChange = (page) => {
  pagination.value.current = page
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