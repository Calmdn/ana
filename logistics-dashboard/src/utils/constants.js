// 应用常量定义

/* ========== 系统配置常量 ========== */

// 应用信息
export const APP_INFO = {
  NAME: '物流大数据可视化平台',
  VERSION: '1.0.0',
  DESCRIPTION: '基于Vue3 + ECharts的物流大数据可视化系统',
  AUTHOR: '开发团队',
  BUILD_TIME: new Date().toISOString()
}

// API配置
export const API_CONFIG = {
  BASE_URL: import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080',
  WS_URL: import.meta.env.VITE_APP_WS_URL || 'ws://localhost:8080/ws',
  TIMEOUT: 30000,
  RETRY_TIMES: 3,
  RETRY_DELAY: 1000
}

// 存储键名
export const STORAGE_KEYS = {
  TOKEN: 'auth_token',
  USER_INFO: 'user_info',
  USER_PREFERENCES: 'user_preferences',
  DASHBOARD_LAYOUT: 'dashboard_layout',
  THEME: 'app_theme',
  LANGUAGE: 'app_language',
  SELECTED_CITY: 'selected_city',
  DATE_RANGE: 'date_range',
  CHART_SETTINGS: 'chart_settings'
}

/* ========== 业务常量 ========== */

// 城市列表
export const CITIES = [
  { label: '烟台', value: 'yantai', code: '370600' },
  { label: '上海', value: 'shanghai', code: '310000' },
  { label: '杭州', value: 'hangzhou', code: '330100' },
  { label: '吉林', value: 'jilin', code: '220200' },
  { label: '重庆', value: 'chongqing', code: '500000' }
]

// 告警严重程度
export const ALERT_SEVERITY = {
  HIGH: { label: '高', value: 'HIGH', color: '#F56C6C', priority: 3 },
  MEDIUM: { label: '中', value: 'MEDIUM', color: '#E6A23C', priority: 2 },
  LOW: { label: '低', value: 'LOW', color: '#67C23A', priority: 1 }
}


// 告警状态
export const ALERT_STATUS = {
  UNRESOLVED: { label: '未解决', value: 'UNRESOLVED', color: '#F56C6C' },
  RESOLVED: { label: '已解决', value: 'RESOLVED', color: '#67C23A' },
  IN_PROGRESS: { label: '处理中', value: 'IN_PROGRESS', color: '#E6A23C' },
  IGNORED: { label: '已忽略', value: 'IGNORED', color: '#909399' }
}

// KPI指标类型
export const KPI_TYPES = {
  TOTAL_ORDERS: { label: '订单总数', value: 'total_orders', unit: '个', icon: 'Document' },
  ACTIVE_COURIERS: { label: '活跃配送员', value: 'active_couriers', unit: '人', icon: 'User' },
  AVG_DELIVERY_TIME: { label: '平均配送时间', value: 'avg_delivery_time', unit: '分钟', icon: 'Timer' },
  DELIVERY_SUCCESS_RATE: { label: '配送成功率', value: 'delivery_success_rate', unit: '%', icon: 'CircleCheck' },
  CUSTOMER_SATISFACTION: { label: '客户满意度', value: 'customer_satisfaction', unit: '分', icon: 'Star' },
  TOTAL_DISTANCE: { label: '总配送距离', value: 'total_distance', unit: 'km', icon: 'Location' },
  FUEL_CONSUMPTION: { label: '燃油消耗', value: 'fuel_consumption', unit: 'L', icon: 'Oil' },
  COST_PER_ORDER: { label: '单均成本', value: 'cost_per_order', unit: '元', icon: 'Money' }
}

// Spark作业状态
export const SPARK_JOB_STATUS = {
  RUNNING: { label: '运行中', value: 'RUNNING', color: '#409EFF' },
  SUCCEEDED: { label: '成功', value: 'SUCCEEDED', color: '#67C23A' },
  FAILED: { label: '失败', value: 'FAILED', color: '#F56C6C' },
  PENDING: { label: '等待中', value: 'PENDING', color: '#E6A23C' },
  CANCELLED: { label: '已取消', value: 'CANCELLED', color: '#909399' }
}

// 时间范围快捷选项
export const DATE_SHORTCUTS = [
  {
    text: '今天',
    value: () => {
      const today = new Date()
      return [today, today]
    }
  },
  {
    text: '昨天',
    value: () => {
      const yesterday = new Date()
      yesterday.setDate(yesterday.getDate() - 1)
      return [yesterday, yesterday]
    }
  },
  {
    text: '最近3天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 2)
      return [start, end]
    }
  },
  {
    text: '最近7天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 6)
      return [start, end]
    }
  },
  {
    text: '最近30天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 29)
      return [start, end]
    }
  },
  {
    text: '本月',
    value: () => {
      const now = new Date()
      const start = new Date(now.getFullYear(), now.getMonth(), 1)
      const end = new Date(now.getFullYear(), now.getMonth() + 1, 0)
      return [start, end]
    }
  },
  {
    text: '上月',
    value: () => {
      const now = new Date()
      const start = new Date(now.getFullYear(), now.getMonth() - 1, 1)
      const end = new Date(now.getFullYear(), now.getMonth(), 0)
      return [start, end]
    }
  }
]

/* ========== UI配置常量 ========== */

// 主题配置
export const THEMES = {
  LIGHT: { label: '浅色模式', value: 'light' },
  DARK: { label: '深色模式', value: 'dark' },
  AUTO: { label: '跟随系统', value: 'auto' }
}

// 语言配置
export const LANGUAGES = {
  ZH_CN: { label: '简体中文', value: 'zh-CN' },
  ZH_TW: { label: '繁體中文', value: 'zh-TW' },
  EN_US: { label: 'English', value: 'en-US' }
}

// 分页配置
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 20,
  PAGE_SIZES: [10, 20, 50, 100],
  SMALL_PAGE_SIZE: 10,
  LARGE_PAGE_SIZE: 100
}

// 图表颜色配置
export const CHART_COLORS = {
  PRIMARY: ['#5470C6', '#91CC75', '#FAC858', '#EE6666', '#73C0DE', '#3BA272', '#FC8452', '#9A60B4', '#EA7CCC'],
  SUCCESS: ['#67C23A', '#85CE61', '#A4DA74', '#C2E687', '#E1F3D8'],
  WARNING: ['#E6A23C', '#ELBF63', '#F0D584', '#F5DAB1', '#FCF6E8'],
  DANGER: ['#F56C6C', '#F78989', '#F9A6A6', '#FAB6B6', '#FDE2E2'],
  INFO: ['#909399', '#A6A9AD', '#BCBEC2', '#D3D4D6', '#F4F4F5'],
  HEATMAP: ['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8', '#ffffcc', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026']
}

// 图表尺寸
export const CHART_SIZES = {
  SMALL: { width: '100%', height: '200px' },
  MEDIUM: { width: '100%', height: '300px' },
  LARGE: { width: '100%', height: '400px' },
  EXTRA_LARGE: { width: '100%', height: '500px' }
}

/* ========== 数据验证常量 ========== */

// 正则表达式
export const REGEX_PATTERNS = {
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  PHONE: /^1[3-9]\d{9}$/,
  ID_CARD: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/,
  LICENSE_PLATE: /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-Z0-9]{4}[A-Z0-9挂学警港澳]$/,
  PASSWORD: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,}$/
}

// 文件类型限制
export const FILE_TYPES = {
  IMAGE: ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'],
  DOCUMENT: ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'],
  DATA: ['csv', 'json', 'xml', 'txt'],
  ARCHIVE: ['zip', 'rar', '7z', 'tar', 'gz']
}

// 文件大小限制（字节）
export const FILE_SIZE_LIMITS = {
  IMAGE: 5 * 1024 * 1024, // 5MB
  DOCUMENT: 20 * 1024 * 1024, // 20MB
  DATA: 10 * 1024 * 1024, // 10MB
  ARCHIVE: 50 * 1024 * 1024 // 50MB
}

/* ========== 系统配置常量 ========== */

// 刷新间隔（毫秒）
export const REFRESH_INTERVALS = {
  REAL_TIME: 5000, // 5秒
  FAST: 30000, // 30秒
  NORMAL: 60000, // 1分钟
  SLOW: 300000, // 5分钟
  VERY_SLOW: 900000 // 15分钟
}

// 缓存时间（毫秒）
export const CACHE_DURATIONS = {
  SHORT: 60000, // 1分钟
  MEDIUM: 300000, // 5分钟
  LONG: 900000, // 15分钟
  VERY_LONG: 3600000 // 1小时
}

// HTTP状态码
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  METHOD_NOT_ALLOWED: 405,
  CONFLICT: 409,
  INTERNAL_SERVER_ERROR: 500,
  BAD_GATEWAY: 502,
  SERVICE_UNAVAILABLE: 503,
  GATEWAY_TIMEOUT: 504
}

// 错误类型
export const ERROR_TYPES = {
  NETWORK_ERROR: 'NETWORK_ERROR',
  TIMEOUT_ERROR: 'TIMEOUT_ERROR',
  VALIDATION_ERROR: 'VALIDATION_ERROR',
  AUTHENTICATION_ERROR: 'AUTHENTICATION_ERROR',
  AUTHORIZATION_ERROR: 'AUTHORIZATION_ERROR',
  SERVER_ERROR: 'SERVER_ERROR',
  UNKNOWN_ERROR: 'UNKNOWN_ERROR'
}

/* ========== 地理信息常量 ========== */

// 省份代码映射
export const PROVINCE_CODES = {
  '北京': '110000',
  '天津': '120000',
  '河北': '130000',
  '山西': '140000',
  '内蒙古': '150000',
  '辽宁': '210000',
  '吉林': '220000',
  '黑龙江': '230000',
  '上海': '310000',
  '江苏': '320000',
  '浙江': '330000',
  '安徽': '340000',
  '福建': '350000',
  '江西': '360000',
  '山东': '370000',
  '河南': '410000',
  '湖北': '420000',
  '湖南': '430000',
  '广东': '440000',
  '广西': '450000',
  '海南': '460000',
  '重庆': '500000',
  '四川': '510000',
  '贵州': '520000',
  '云南': '530000',
  '西藏': '540000',
  '陕西': '610000',
  '甘肃': '620000',
  '青海': '630000',
  '宁夏': '640000',
  '新疆': '650000'
}

// 时区配置
export const TIMEZONES = {
  'Asia/Shanghai': '中国标准时间 (UTC+8)',
  'Asia/Hong_Kong': '香港时间 (UTC+8)',
  'Asia/Taipei': '台北时间 (UTC+8)',
  'UTC': '协调世界时 (UTC+0)',
  'America/New_York': '纽约时间 (UTC-5)',
  'Europe/London': '伦敦时间 (UTC+0)',
  'Asia/Tokyo': '东京时间 (UTC+9)'
}

/* ========== 导出工具函数 ========== */

// 根据值获取标签
export const getLabelByValue = (options, value) => {
  const option = Object.values(options).find(opt => opt.value === value)
  return option ? option.label : value
}

// 根据值获取颜色
export const getColorByValue = (options, value) => {
  const option = Object.values(options).find(opt => opt.value === value)
  return option ? option.color : '#909399'
}

// 根据值获取图标
export const getIconByValue = (options, value) => {
  const option = Object.values(options).find(opt => opt.value === value)
  return option ? option.icon : 'QuestionFilled'
}

// 检查是否为生产环境
export const isProduction = () => {
  return import.meta.env.MODE === 'production'
}

// 检查是否为开发环境
export const isDevelopment = () => {
  return import.meta.env.MODE === 'development'
}

// 获取环境变量
export const getEnvVar = (key, defaultValue = '') => {
  return import.meta.env[key] || defaultValue
}