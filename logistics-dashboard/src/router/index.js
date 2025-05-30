import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '@/views/Dashboard.vue'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/kpi',
    name: 'KpiOverview',
    component: () => import('@/views/KpiOverview.vue')
  },
  {
    path: '/alerts',
    name: 'AlertsManagement', 
    component: () => import('@/views/AlertsManagement.vue')
  },
  {
    path: '/time-efficiency',
    name: 'TimeEfficiencyAnalysis',
    component: () => import('@/views/TimeEfficiencyAnalysis.vue')
  },
  {
    path: '/spatial-analysis',
    name: 'SpatialAnalysis',
    component: () => import('@/views/SpatialAnalysis.vue')
  },
  {
    path: '/predictive-analysis',
    name: 'PredictiveAnalysis',
    component: () => import('@/views/PredictiveAnalysis.vue')
  },
  {
    path: '/operational-efficiency',
    name: 'OperationalEfficiency',
    component: () => import('@/views/OperationalEfficiency.vue')
  },
  {
    path: '/reports',
    name: 'Reports',
    component: () => import('@/views/Reports.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router