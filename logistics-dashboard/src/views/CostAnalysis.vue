<template>
    <div class="cost-analysis">
        <!-- 页面头部 -->
        <div class="page-header">
            <h2>成本分析</h2>
            <div class="header-controls">
                <el-select v-model="selectedCity" @change="handleCityChange" style="width: 150px;">
                    <el-option v-for="city in cities" :key="city.value" :label="city.label" :value="city.value" />
                </el-select>
                <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
                    end-placeholder="结束日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" @change="handleDateChange"
                    style="width: 240px;" />
                <el-button type="primary" @click="refreshData" :loading="loading">
                    <el-icon>
                        <Refresh />
                    </el-icon>
                    刷新数据
                </el-button>
                <el-button @click="exportReport">
                    <el-icon>
                        <Download />
                    </el-icon>
                    导出报告
                </el-button>
            </div>
        </div>

        <!-- 关键指标卡片 -->
        <el-row :gutter="20" class="metrics-cards">
            <el-col :span="6">
                <el-card class="metric-card">
                    <div class="metric-content">
                        <div class="metric-icon total-cost">
                            <el-icon>
                                <Money />
                            </el-icon>
                        </div>
                        <div class="metric-info">
                            <div class="metric-value">¥{{ formatNumber(metrics.totalCost) }}</div>
                            <div class="metric-label">总成本</div>
                            <div class="metric-change"
                                :class="{ positive: metrics.costChange > 0, negative: metrics.costChange < 0 }">
                                <el-icon v-if="metrics.costChange > 0">
                                    <ArrowUp />
                                </el-icon>
                                <el-icon v-else-if="metrics.costChange < 0">
                                    <ArrowDown />
                                </el-icon>
                                {{ Math.abs(metrics.costChange) }}%
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card class="metric-card">
                    <div class="metric-content">
                        <div class="metric-icon fuel-cost">
                            <el-icon>
                                <Van />
                            </el-icon>
                        </div>
                        <div class="metric-info">
                            <div class="metric-value">¥{{ formatNumber(metrics.fuelCost) }}</div>
                            <div class="metric-label">燃料成本</div>
                            <div class="metric-ratio">占总成本 {{ metrics.fuelRatio }}%</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card class="metric-card">
                    <div class="metric-content">
                        <div class="metric-icon time-cost">
                            <el-icon>
                                <Timer />
                            </el-icon>
                        </div>
                        <div class="metric-info">
                            <div class="metric-value">¥{{ formatNumber(metrics.timeCost) }}</div>
                            <div class="metric-label">时间成本</div>
                            <div class="metric-ratio">占总成本 {{ metrics.timeRatio }}%</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card class="metric-card">
                    <div class="metric-content">
                        <div class="metric-icon unit-cost">
                            <el-icon>
                                <Box />
                            </el-icon>
                        </div>
                        <div class="metric-info">
                            <div class="metric-value">¥{{ formatNumber(metrics.costPerOrder) }}</div>
                            <div class="metric-label">单订单成本</div>
                            <div class="metric-sub">¥{{ formatNumber(metrics.costPerKm) }}/公里</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 成本趋势图表 -->
        <el-row :gutter="20" class="charts-section">
            <el-col :span="16">
                <el-card title="成本趋势分析" class="chart-card">
                    <template #header>
                        <div class="card-header">
                            <span>成本趋势分析</span>
                            <div class="chart-controls">
                                <el-radio-group v-model="trendType" @change="handleTrendTypeChange" size="small">
                                    <el-radio-button label="total">总成本</el-radio-button>
                                    <el-radio-button label="fuel">燃料成本</el-radio-button>
                                    <el-radio-button label="time">时间成本</el-radio-button>
                                    <el-radio-button label="unit">单位成本</el-radio-button>
                                </el-radio-group>
                            </div>
                        </div>
                    </template>
                    <div ref="trendChart" class="chart-container" v-loading="chartLoading"></div>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card title="成本构成分析" class="chart-card">
                    <div ref="pieChart" class="chart-container" v-loading="chartLoading"></div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 区域成本分析 -->
        <el-row :gutter="20" class="analysis-section">
            <el-col :span="24">
                <el-card title="区域成本排行" class="ranking-card">
                    <template #header>
                        <div class="card-header">
                            <span>区域成本排行</span>
                            <el-button size="small" text @click="refreshRanking">
                                <el-icon>
                                    <Refresh />
                                </el-icon>
                            </el-button>
                        </div>
                    </template>
                    <div class="ranking-list" v-loading="rankingLoading">
                        <div v-for="(item, index) in regionRanking" :key="item.regionId" class="ranking-item">
                            <div class="rank-badge" :class="`rank-${index + 1}`">{{ index + 1 }}</div>
                            <div class="region-info">
                                <div class="region-name">{{ item.regionName }}</div>
                                <div class="region-stats">
                                    订单: {{ item.totalOrders }} | 距离: {{ formatNumber(item.totalDistance) }}km
                                </div>
                            </div>
                            <div class="cost-info">
                                <div class="total-cost">¥{{ formatNumber(item.totalCost) }}</div>
                                <div class="unit-cost">¥{{ formatNumber(item.costPerOrder) }}/单</div>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 详细数据表格 -->
        <el-card title="成本明细" class="data-table-card">
            <template #header>
                <div class="card-header">
                    <span>成本明细</span>
                    <div class="table-controls">
                        <el-input v-model="searchKeyword" placeholder="搜索区域..." style="width: 200px;" clearable
                            @input="handleSearch">
                            <template #prefix>
                                <el-icon>
                                    <Search />
                                </el-icon>
                            </template>
                        </el-input>
                        <el-select v-model="analysisTypeFilter" placeholder="分析类型" clearable style="width: 150px;">
                            <el-option label="日分析" value="daily" />
                            <el-option label="周分析" value="weekly" />
                            <el-option label="月分析" value="monthly" />
                        </el-select>
                    </div>
                </div>
            </template>

            <el-table :data="filteredTableData" v-loading="tableLoading" stripe @sort-change="handleSortChange"
                class="cost-table">
                <el-table-column prop="regionId" label="区域ID" width="80" />
                <el-table-column prop="regionName" label="区域名称" min-width="120" />
                <el-table-column prop="date" label="日期" width="100" />
                <el-table-column prop="totalCost" label="总成本" width="120" sortable="custom" align="right">
                    <template #default="{ row }">
                        <span class="cost-value">¥{{ formatNumber(row.totalCost) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="totalFuelCost" label="燃料成本" width="120" sortable="custom" align="right">
                    <template #default="{ row }">
                        <span class="fuel-cost">¥{{ formatNumber(row.totalFuelCost) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="totalTimeCost" label="时间成本" width="120" sortable="custom" align="right">
                    <template #default="{ row }">
                        <span class="time-cost">¥{{ formatNumber(row.totalTimeCost) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="totalOrders" label="订单数" width="100" sortable="custom" align="right" />
                <el-table-column prop="totalDistance" label="总距离(km)" width="120" sortable="custom" align="right">
                    <template #default="{ row }">
                        {{ formatNumber(row.totalDistance) }}
                    </template>
                </el-table-column>
                <el-table-column prop="costPerOrder" label="单订单成本" width="120" sortable="custom" align="right">
                    <template #default="{ row }">
                        <span class="unit-cost">¥{{ formatNumber(row.costPerOrder) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="costPerKm" label="单公里成本" width="120" sortable="custom" align="right">
                    <template #default="{ row }">
                        <span class="unit-cost">¥{{ formatNumber(row.costPerKm) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="analysisType" label="分析类型" width="100">
                    <template #default="{ row }">
                        <el-tag :type="getAnalysisTypeColor(row.analysisType)" size="small">
                            {{ getAnalysisTypeLabel(row.analysisType) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" fixed="right">
                    <template #default="{ row }">
                        <el-button size="small" type="primary" text @click="viewDetail(row)">
                            详情
                        </el-button>
                        <el-button size="small" type="warning" text @click="analyzeRegion(row)">
                            分析
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.pageSize"
                :total="pagination.total" :page-sizes="[20, 50, 100, 200]"
                layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" class="pagination" />
        </el-card>

        <!-- 详情对话框 -->
        <el-dialog v-model="showDetailDialog" title="成本分析详情" width="800px">
            <div v-if="selectedDetail" class="detail-content">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="区域ID">{{ selectedDetail.regionId }}</el-descriptions-item>
                    <el-descriptions-item label="区域名称">{{ selectedDetail.regionName }}</el-descriptions-item>
                    <el-descriptions-item label="日期">{{ selectedDetail.date }}</el-descriptions-item>
                    <el-descriptions-item label="分析类型">
                        <el-tag :type="getAnalysisTypeColor(selectedDetail.analysisType)">
                            {{ getAnalysisTypeLabel(selectedDetail.analysisType) }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="总成本">
                        <span class="detail-cost">¥{{ formatNumber(selectedDetail.totalCost) }}</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="燃料成本">
                        <span class="fuel-cost">¥{{ formatNumber(selectedDetail.totalFuelCost) }}</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="时间成本">
                        <span class="time-cost">¥{{ formatNumber(selectedDetail.totalTimeCost) }}</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="燃料成本比率">
                        {{ (selectedDetail.fuelCostRatio * 100).toFixed(1) }}%
                    </el-descriptions-item>
                    <el-descriptions-item label="总订单数">{{ selectedDetail.totalOrders }}</el-descriptions-item>
                    <el-descriptions-item label="总距离">{{ formatNumber(selectedDetail.totalDistance) }}
                        km</el-descriptions-item>
                    <el-descriptions-item label="单订单成本">
                        <span class="unit-cost">¥{{ formatNumber(selectedDetail.costPerOrder) }}</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="单公里成本">
                        <span class="unit-cost">¥{{ formatNumber(selectedDetail.costPerKm) }}</span>
                    </el-descriptions-item>
                </el-descriptions>
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { costAnalysisApi } from '@/api/costanalysis'
import * as echarts from 'echarts'
import {
    Refresh,
    Download,
    Money,
    Van,
    Timer,
    Box,
    ArrowUp,
    ArrowDown,
    Warning,
    InfoFilled,
    Search
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const handleTrendTypeChange = () => {
    if (trendChartInstance && window.currentTrendData) {
        updateTrendChart(window.currentTrendData)
    } else {
        // 如果没有缓存数据，重新加载
        loadChartData()
    }
}

const dashboardStore = useDashboardStore()

// 基础数据
const loading = ref(false)
const chartLoading = ref(false)
const rankingLoading = ref(false)
const alertsLoading = ref(false)
const tableLoading = ref(false)

const selectedCity = ref('Shanghai')
const dateRange = ref([
    new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    new Date().toISOString().split('T')[0]
])

const cities = ref([
    { label: '上海', value: 'Shanghai' },
    { label: '重庆', value: 'Chongqing' },
    { label: '烟台', value: 'Yantai' },
    { label: '杭州', value: 'Hangzhou' },
    { label: '吉林', value: 'Jilin' }
])

// 关键指标数据
const metrics = reactive({
    totalCost: 0,
    fuelCost: 0,
    timeCost: 0,
    costPerOrder: 0,
    costPerKm: 0,
    fuelRatio: 0,
    timeRatio: 0,
    costChange: 0
})

// 图表数据
const trendType = ref('total')
const trendChart = ref(null)
const pieChart = ref(null)
let trendChartInstance = null
let pieChartInstance = null

// 区域排行数据
const regionRanking = ref([])

// 告警数据
const costAlerts = ref([])
const alertCount = computed(() => costAlerts.value.length)

// 表格数据
const tableData = ref([])
const searchKeyword = ref('')
const analysisTypeFilter = ref('')
const sortConfig = reactive({
    prop: '',
    order: ''
})

// 分页
const pagination = reactive({
    current: 1,
    pageSize: 20,
    total: 0
})

// 详情对话框
const showDetailDialog = ref(false)
const selectedDetail = ref(null)

// 计算属性
const filteredTableData = computed(() => {
    let data = tableData.value

    // 搜索过滤
    if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase()
        data = data.filter(item =>
            item.regionName?.toLowerCase().includes(keyword) ||
            item.regionId?.toString().includes(keyword)
        )
    }

    // 分析类型过滤
    if (analysisTypeFilter.value) {
        data = data.filter(item => item.analysisType === analysisTypeFilter.value)
    }

    // 排序
    if (sortConfig.prop) {
        data = [...data].sort((a, b) => {
            const aVal = a[sortConfig.prop]
            const bVal = b[sortConfig.prop]

            if (sortConfig.order === 'ascending') {
                return aVal > bVal ? 1 : -1
            } else {
                return aVal < bVal ? 1 : -1
            }
        })
    }

    // 分页
    const start = (pagination.current - 1) * pagination.pageSize
    const end = start + pagination.pageSize

    pagination.total = data.length
    return data.slice(start, end)
})

// 工具函数
const formatNumber = (num) => {
    if (!num && num !== 0) return '0'
    return Number(num).toLocaleString('zh-CN', { maximumFractionDigits: 2 })
}

const formatTime = (timeStr) => {
    return new Date(timeStr).toLocaleString('zh-CN')
}

const getAnalysisTypeColor = (type) => {
    const colorMap = {
        daily: 'primary',
        weekly: 'success',
        monthly: 'warning',
        quarterly: 'danger'
    }
    return colorMap[type] || 'info'
}

const getAnalysisTypeLabel = (type) => {
    const labelMap = {
        daily: '日分析',
        weekly: '周分析',
        monthly: '月分析',
        quarterly: '季分析'
    }
    return labelMap[type] || type
}

// 数据加载方法
const loadMetrics = async () => {
    try {
        const [startDate, endDate] = dateRange.value
        const summary = await costAnalysisApi.getCostSummary(selectedCity.value, startDate)

        console.log('API返回的成本汇总数据:', summary)

        if (summary) {
            let summaryData = summary
            if (summary.data) {
                summaryData = summary.data
            } else if (Array.isArray(summary) && summary.length > 0) {
                summaryData = summary[0]
            }

            console.log('处理后的汇总数据:', summaryData)
            metrics.totalCost = (summaryData.total_orders || summaryData.totalOrders || 0) * (summaryData.avg_cost_per_order || summaryData.avgCostPerOrder || 0)
            metrics.fuelCost = metrics.totalCost * (summaryData.avg_fuel_cost_ratio || summaryData.avgFuelCostRatio || 0)
            metrics.timeCost = metrics.totalCost * 0.3
            metrics.costPerOrder = summaryData.avg_cost_per_order || summaryData.avgCostPerOrder || 0
            metrics.costPerKm = summaryData.avg_cost_per_km || summaryData.avgCostPerKm || 0

            // 计算比率
            if (metrics.totalCost > 0) {
                metrics.fuelRatio = ((summaryData.avg_fuel_cost_ratio || summaryData.avgFuelCostRatio || 0) * 100).toFixed(1)
                metrics.timeRatio = (metrics.timeCost / metrics.totalCost * 100).toFixed(1)
            } else {
                metrics.fuelRatio = 0
                metrics.timeRatio = 0
            }

            metrics.costChange = summaryData.costChange || 0

            console.log('更新后的指标:', metrics)
        }
    } catch (error) {
        console.error('加载关键指标失败:', error)
        ElMessage.error('加载成本指标失败')
    }
}

const loadChartData = async () => {
    chartLoading.value = true
    try {
        const [startDate, endDate] = dateRange.value
        const [trendData, analysisData] = await Promise.all([
            costAnalysisApi.getCostTrend(selectedCity.value, startDate),
            costAnalysisApi.getCostAnalysisByCity(selectedCity.value, startDate, endDate)
        ])

        console.log('趋势数据:', trendData)
        console.log('分析数据:', analysisData)

        updateTrendChart(trendData)
        updatePieChart(analysisData)
    } catch (error) {
        console.error('加载图表数据失败:', error)
        ElMessage.error('加载图表数据失败')
    } finally {
        chartLoading.value = false
    }
}

// 修改 loadRankingData 方法 - 根据你的实际API数据结构
const loadRankingData = async () => {
    rankingLoading.value = true
    try {
        const [startDate] = dateRange.value
        const ranking = await costAnalysisApi.getRegionCostRanking(selectedCity.value, startDate, 10)

        console.log('API返回的排行数据:', ranking)

        if (ranking && Array.isArray(ranking)) {
            regionRanking.value = ranking.map(item => ({
                ...item,
                regionId: item.region_id,
                regionName: item.regionName || `区域${item.region_id}`,
                totalCost: item.total_orders * item.avg_cost_per_order,
                totalOrders: Number(item.total_orders || 0),
                totalDistance: Number(item.avg_total_distance || 0),
                costPerOrder: Number(item.avg_cost_per_order || 0)
            }))
        } else {
            regionRanking.value = []
        }
    } catch (error) {
        console.error('加载排行数据失败:', error)
        regionRanking.value = []
    } finally {
        rankingLoading.value = false
    }
}

const loadAlertsData = async () => {
    alertsLoading.value = true
    try {
        const [, endDate] = dateRange.value
        const alerts = await costAnalysisApi.getHighCostAlerts(1000, endDate, 10)

        costAlerts.value = alerts || []
    } catch (error) {
        console.error('加载告警数据失败:', error)
    } finally {
        alertsLoading.value = false
    }
}

const loadTableData = async () => {
    tableLoading.value = true
    try {
        const [startDate, endDate] = dateRange.value
        const searchParams = {
            city: selectedCity.value,
            startDate,
            endDate,
            limit: 1000
        }

        console.log('搜索参数:', searchParams)
        const data = await costAnalysisApi.searchCostAnalysis(searchParams)
        console.log('API返回的表格数据:', data)

        if (data && Array.isArray(data)) {
            tableData.value = data.map((item, index) => ({
                ...item,
                regionId: item.regionId || index + 1,
                regionName: item.regionName || `区域${index + 1}`,
                date: item.date || item.analysisDate,
                

                totalCost: Number(item.totalCost || 0),
                totalFuelCost: Number(item.totalFuelCost || 0),
                totalTimeCost: Number(item.totalTimeCost || 0),
                totalOrders: Number(item.totalOrders || 0),
                totalDistance: Number(item.totalDistance || 0),
                costPerOrder: Number(item.costPerOrder || 0),
                costPerKm: Number(item.costPerKm || 0),
                fuelCostRatio: Number(item.fuelCostRatio || 0),
                analysisType: item.analysisType || 'daily'
            }))
        } else {
            tableData.value = []
        }
    } catch (error) {
        console.error('加载表格数据失败:', error)
        ElMessage.error('加载数据失败')
        tableData.value = []
    } finally {
        tableLoading.value = false
    }
}

// 图表更新方法
const updateTrendChart = (data) => {
    if (!trendChartInstance) return

    console.log('更新趋势图表，原始数据:', data)

    // 缓存数据供切换类型时使用
    window.currentTrendData = data

    let chartData = []
    if (Array.isArray(data)) {
        chartData = data
    } else if (data && typeof data === 'object') {
        chartData = data.data || data.trend || data.list || []
    } else {
        console.warn('趋势数据格式不正确:', data)
        chartData = []
    }

    console.log('处理后的图表数据:', chartData)

    if (chartData.length === 0) {
        const option = {
            title: {
                text: '暂无趋势数据',
                left: 'center',
                textStyle: { fontSize: 14, color: '#999' }
            },
            xAxis: { type: 'category', data: [] },
            yAxis: { type: 'value' },
            series: [{ data: [], type: 'line' }]
        }
        trendChartInstance.setOption(option)
        return
    }

    const dates = chartData.map(item => item.date || item.analysisDate || '')

    let seriesData = []
    let title = '成本趋势'

    switch (trendType.value) {
        case 'total':
            seriesData = chartData.map(item => (item.total_orders || 0) * (item.avg_cost_per_order || 0))
            title = '总成本趋势'
            break
        case 'fuel':
            seriesData = chartData.map(item => (item.total_orders || 0) * (item.avg_cost_per_order || 0) * (item.avg_fuel_cost_ratio || 0))
            title = '燃料成本趋势'
            break
        case 'time':
            seriesData = chartData.map(item => (item.total_orders || 0) * (item.avg_cost_per_order || 0) * 0.3)
            title = '时间成本趋势'
            break
        case 'unit':
            seriesData = chartData.map(item => Number(item.avg_cost_per_order || 0))
            title = '单订单成本趋势'
            break
    }

    const option = {
        title: {
            text: title,
            left: 'center',
            textStyle: { fontSize: 14 }
        },
        tooltip: {
            trigger: 'axis',
            formatter: (params) => {
                const param = params[0]
                return `${param.axisValue}<br/>成本: ¥${formatNumber(param.value)}`
            }
        },
        xAxis: {
            type: 'category',
            data: dates,
            axisLabel: { 
                interval: 'auto', 
                rotate: 0,        
                fontSize: 12,     
                margin: 8        
            }
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: (value) => `¥${formatNumber(value)}`
            }
        },
        series: [{
            data: seriesData,
            type: 'line',
            smooth: true,
            itemStyle: { color: '#409EFF' },
            areaStyle: {
                color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 0, y2: 1,
                    colorStops: [
                        { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                        { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
                    ]
                }
            }
        }],
        grid: { left: '10%', right: '10%', bottom: '15%', top: '15%' }
    }

    trendChartInstance.setOption(option)
}

const updatePieChart = (data) => {
    if (!pieChartInstance) return

    console.log('更新饼图，原始数据:', data)

    let summary = {}

    if (Array.isArray(data) && data.length > 0) {
        summary = data[0]
    } else if (data && data.data) {
        summary = data.data
    } else if (data && typeof data === 'object') {
        summary = data
    }

    console.log('处理后的汇总数据:', summary)

    // 直接使用API返回的字段，不计算
    const total = Number(summary.totalCost || 0)
    const fuel = Number(summary.totalFuelCost || 0)
    const time = Number(summary.totalTimeCost || 0)
    const other = Math.max(0, total - fuel - time)

    if (total === 0) {
        const option = {
            title: {
                text: '暂无成本构成数据',
                left: 'center',
                textStyle: { fontSize: 14, color: '#999' }
            },
            series: [{
                type: 'pie',
                radius: ['40%', '70%'],
                center: ['50%', '50%'],
                data: []
            }]
        }
        pieChartInstance.setOption(option)
        return
    }

    const option = {
        title: {
            text: '成本构成',
            left: 'center',
            textStyle: { fontSize: 14 }
        },
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
        },
        legend: {
            bottom: '10%',
            left: 'center'
        },
        series: [{
            name: '成本构成',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '45%'],
            data: [
                { value: fuel, name: '燃料成本', itemStyle: { color: '#FF6B6B' } },
                { value: time, name: '时间成本', itemStyle: { color: '#4ECDC4' } },
                { value: other, name: '其他成本', itemStyle: { color: '#45B7D1' } }
            ],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }]
    }

    pieChartInstance.setOption(option)
}

// 事件处理方法
const handleCityChange = () => {
    refreshData()
}

const handleDateChange = () => {
    refreshData()
}

const handleSearch = () => {
    pagination.current = 1
}

const handleSortChange = ({ prop, order }) => {
    sortConfig.prop = prop
    sortConfig.order = order
}

const handleSizeChange = (size) => {
    pagination.pageSize = size
    pagination.current = 1
}

const handleCurrentChange = (page) => {
    pagination.current = page
}

const viewDetail = (row) => {
    selectedDetail.value = row
    showDetailDialog.value = true
}

const analyzeRegion = (row) => {
    ElMessage.info(`开始分析区域 ${row.regionName} 的成本详情`)
}

const refreshData = async () => {
    loading.value = true
    try {
        await Promise.all([
            loadMetrics(),
            loadChartData(),
            loadRankingData(),
            loadAlertsData(),
            loadTableData()
        ])
        ElMessage.success('数据刷新完成')
    } catch (error) {
        console.error('刷新数据失败:', error)
        ElMessage.error('刷新数据失败')
    } finally {
        loading.value = false
    }
}

const refreshRanking = () => {
    loadRankingData()
}

const exportReport = () => {
    ElMessage.info('正在导出成本分析报告...')
    // 实现报告导出功能
}

// 图表初始化
const initCharts = () => {
    nextTick(() => {
        if (trendChart.value) {
            trendChartInstance = echarts.init(trendChart.value)
            window.addEventListener('resize', () => {
                trendChartInstance?.resize()
            })
        }

        if (pieChart.value) {
            pieChartInstance = echarts.init(pieChart.value)
            window.addEventListener('resize', () => {
                pieChartInstance?.resize()
            })
        }

        loadChartData()
    })
}

// 生命周期
onMounted(() => {
    initCharts()
    refreshData()
})
</script>

<style scoped>
.cost-analysis {
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
    align-items: center;
}

.metrics-cards {
    margin-bottom: 20px;
}

.metric-card {
    height: 120px;
}

.metric-content {
    display: flex;
    align-items: center;
    height: 100%;
    padding: 20px;
}

.metric-icon {
    width: 60px;
    height: 60px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 24px;
    margin-right: 20px;
}

.metric-icon.total-cost {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.metric-icon.fuel-cost {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.metric-icon.time-cost {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.metric-icon.unit-cost {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.metric-info {
    flex: 1;
}

.metric-value {
    font-size: 28px;
    font-weight: bold;
    color: #333;
    line-height: 1;
    margin-bottom: 8px;
}

.metric-label {
    font-size: 14px;
    color: #666;
    margin-bottom: 4px;
}

.metric-change {
    display: flex;
    align-items: center;
    font-size: 12px;
    font-weight: 500;
}

.metric-change.positive {
    color: #f56c6c;
}

.metric-change.negative {
    color: #67c23a;
}

.metric-ratio,
.metric-sub {
    font-size: 12px;
    color: #999;
}

.charts-section {
    margin-bottom: 20px;
}

.chart-card {
    height: 400px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.chart-container {
    height: 320px;
    width: 100%;
}

.analysis-section {
    margin-bottom: 20px;
}

.ranking-card,
.alerts-card {
    height: 500px;
}

.ranking-list {
    max-height: 420px;
    overflow-y: auto;
}

.ranking-item {
    display: flex;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
}

.ranking-item:last-child {
    border-bottom: none;
}

.rank-badge {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: bold;
    margin-right: 15px;
}

.rank-badge.rank-1 {
    background: #FFD700;
}

.rank-badge.rank-2 {
    background: #C0C0C0;
}

.rank-badge.rank-3 {
    background: #CD7F32;
}

.rank-badge:not(.rank-1):not(.rank-2):not(.rank-3) {
    background: #909399;
}

.region-info {
    flex: 1;
    margin-right: 15px;
}

.region-name {
    font-weight: 500;
    color: #333;
    margin-bottom: 4px;
}

.region-stats {
    font-size: 12px;
    color: #666;
}

.cost-info {
    text-align: right;
}

.total-cost {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 4px;
}

.unit-cost {
    font-size: 12px;
    color: #666;
}

.alerts-list {
    max-height: 420px;
    overflow-y: auto;
}

.alert-item {
    display: flex;
    align-items: flex-start;
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
}

.alert-item:last-child {
    border-bottom: none;
}

.alert-icon {
    margin-right: 12px;
    margin-top: 2px;
}

.alert-item.high .alert-icon {
    color: #f56c6c;
}

.alert-item:not(.high) .alert-icon {
    color: #e6a23c;
}

.alert-content {
    flex: 1;
    margin-right: 15px;
}

.alert-title {
    font-weight: 500;
    color: #333;
    margin-bottom: 4px;
}

.alert-description {
    font-size: 13px;
    color: #666;
    margin-bottom: 4px;
}

.alert-time {
    font-size: 12px;
    color: #999;
}

.alert-cost {
    text-align: right;
}

.cost-value {
    font-size: 14px;
    font-weight: bold;
    color: #f56c6c;
    margin-bottom: 2px;
}

.threshold {
    font-size: 12px;
    color: #999;
}

.data-table-card {
    margin-bottom: 20px;
}

.table-controls {
    display: flex;
    gap: 12px;
    align-items: center;
}

.cost-table .cost-value {
    font-weight: 500;
    color: #333;
}

.cost-table .fuel-cost {
    color: #f56c6c;
}

.cost-table .time-cost {
    color: #409eff;
}

.cost-table .unit-cost {
    color: #67c23a;
}

.pagination {
    margin-top: 20px;
    text-align: right;
}

.detail-content {
    padding: 20px 0;
}

.detail-cost {
    font-size: 16px;
    font-weight: bold;
    color: #333;
}

.fuel-cost {
    color: #f56c6c;
}

.time-cost {
    color: #409eff;
}

.unit-cost {
    color: #67c23a;
}
</style>