import api from './index.js'

export const sparkJobsApi = {
  // 获取所有Spark作业
  getAllJobs: () => 
    api.get('/api/spark/jobs'),
  
  // 获取作业详情
  getJobById: (jobId) => 
    api.get(`/api/spark/jobs/${jobId}`),
  
  // 获取正在运行的作业
  getRunningJobs: () => 
    api.get('/api/spark/jobs/running'),
  
  // 获取已完成的作业
  getCompletedJobs: (limit = 50) => 
    api.get('/api/spark/jobs/completed', { params: { limit } }),
  
  // 获取失败的作业
  getFailedJobs: (limit = 20) => 
    api.get('/api/spark/jobs/failed', { params: { limit } }),
  
  // 提交新的Spark作业
  submitJob: (jobConfig) => 
    api.post('/api/spark/jobs/submit', jobConfig),
  
  // 停止作业
  stopJob: (jobId) => 
    api.post(`/api/spark/jobs/${jobId}/stop`),
  
  // 重启作业
  restartJob: (jobId) => 
    api.post(`/api/spark/jobs/${jobId}/restart`),
  
  // 获取作业日志
  getJobLogs: (jobId, logType = 'stdout') => 
    api.get(`/api/spark/jobs/${jobId}/logs`, { params: { logType } }),
  
  // 获取作业执行统计
  getJobStats: (dateRange) => 
    api.get('/api/spark/jobs/stats', { params: { startDate: dateRange[0], endDate: dateRange[1] } }),
  
  // 获取集群资源使用情况
  getClusterResources: () => 
    api.get('/api/spark/cluster/resources'),
  
  // 获取作业性能指标
  getJobMetrics: (jobId) => 
    api.get(`/api/spark/jobs/${jobId}/metrics`)
}