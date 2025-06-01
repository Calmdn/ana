import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/kpi',
    name: 'KPI',
    component: () => import('@/views/KpiOverview.vue'),
    meta: { title: 'KPI监控' }
  },
  {
    path: '/alerts',
    name: 'Alerts',
    component: () => import('@/views/AlertsManagement.vue'),
    meta: { title: '告警管理' }
  },
  {
    path: '/time-efficiency',
    name: 'TimeEfficiency',
    component: () => import('@/views/TimeEfficiencyAnalysis.vue'),
    meta: { title: '时间效率分析' }
  },
  {
    path: '/spatial-analysis',
    name: 'SpatialAnalysis',
    component: () => import('@/views/SpatialAnalysis.vue'),
    meta: { title: '空间分析' }
  },
  {
    path: '/predictive-analysis',
    name: 'PredictiveAnalysis',
    component: () => import('@/views/PredictiveAnalysis.vue'),
    meta: { title: '预测分析' }
  },
  {
    path: '/operational-efficiency',
    name: 'OperationalEfficiency',
    component: () => import('@/views/OperationalEfficiency.vue'),
    meta: { title: '运营效率分析' }
  },
  {
    path: '/cost-analysis',
    name: 'CostAnalysis',
    component: () => import('@/views/CostAnalysis.vue'),
    meta: { title: '成本分析' }
  },
  {
    path: '/',
    redirect: '/kpi'
  }
]

const router = createRouter({
  history: createWebHistory(),
  // history: createWebHashHistory(),
  routes
})

export default router