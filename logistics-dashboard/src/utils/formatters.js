// 数据格式化工具函数

import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import relativeTime from 'dayjs/plugin/relativeTime'
import duration from 'dayjs/plugin/duration'
import customParseFormat from 'dayjs/plugin/customParseFormat'

// 配置dayjs
dayjs.extend(relativeTime)
dayjs.extend(duration)
dayjs.extend(customParseFormat)
dayjs.locale('zh-cn')

/*数字格式化*/

/**
 * 格式化数字，添加千分位分隔符
 * @param {number} num - 数字
 * @param {number} decimals - 小数位数
 * @returns {string} 格式化后的数字字符串
 */
export const formatNumber = (num, decimals = 0) => {
  if (num === null || num === undefined || isNaN(num)) {
    return '--'
  }
  
  const number = Number(num)
  return number.toLocaleString('zh-CN', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals
  })
}

/**
 * 格式化百分比
 * @param {number} num - 数字（0-1之间或0-100之间）
 * @param {number} decimals - 小数位数
 * @param {boolean} isDecimal - 是否为小数形式（0-1之间）
 * @returns {string} 百分比字符串
 */
export const formatPercentage = (num, decimals = 1, isDecimal = true) => {
  if (num === null || num === undefined || isNaN(num)) {
    return '--'
  }
  
  const number = Number(num)
  const percentage = isDecimal ? number * 100 : number
  
  return `${percentage.toFixed(decimals)}%`
}

/**
 * 格式化货币
 * @param {number} amount - 金额
 * @param {string} currency - 货币符号
 * @param {number} decimals - 小数位数
 * @returns {string} 货币字符串
 */
export const formatCurrency = (amount, currency = '¥', decimals = 2) => {
  if (amount === null || amount === undefined || isNaN(amount)) {
    return '--'
  }
  
  const number = Number(amount)
  return `${currency}${number.toLocaleString('zh-CN', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals
  })}`
}

/**
 * 格式化文件大小
 * @param {number} bytes - 字节数
 * @param {number} decimals - 小数位数
 * @returns {string} 文件大小字符串
 */
export const formatFileSize = (bytes, decimals = 2) => {
  if (bytes === 0) return '0 B'
  if (bytes === null || bytes === undefined || isNaN(bytes)) {
    return '--'
  }
  
  const k = 1024
  const dm = decimals < 0 ? 0 : decimals
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB']
  
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`
}

/**
 * 格式化大数字（K, M, B等）
 * @param {number} num - 数字
 * @param {number} decimals - 小数位数
 * @returns {string} 格式化后的数字字符串
 */
export const formatLargeNumber = (num, decimals = 1) => {
  if (num === null || num === undefined || isNaN(num)) {
    return '--'
  }
  
  const number = Math.abs(Number(num))
  const sign = num < 0 ? '-' : ''
  
  if (number >= 1e9) {
    return `${sign}${(number / 1e9).toFixed(decimals)}B`
  } else if (number >= 1e6) {
    return `${sign}${(number / 1e6).toFixed(decimals)}M`
  } else if (number >= 1e3) {
    return `${sign}${(number / 1e3).toFixed(decimals)}K`
  } else {
    return `${sign}${number.toFixed(decimals)}`
  }
}

/*时间格式化*/

/**
 * 格式化日期时间
 * @param {string|Date|number} date - 日期
 * @param {string} format - 格式字符串
 * @returns {string} 格式化后的日期字符串
 */
export const formatDateTime = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!date) return '--'
  
  return dayjs(date).format(format)
}

/**
 * 格式化日期
 * @param {string|Date|number} date - 日期
 * @param {string} format - 格式字符串
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (date, format = 'YYYY-MM-DD') => {
  if (!date) return '--'
  
  return dayjs(date).format(format)
}

/**
 * 格式化时间
 * @param {string|Date|number} date - 日期
 * @param {string} format - 格式字符串
 * @returns {string} 格式化后的时间字符串
 */
export const formatTime = (date, format = 'HH:mm:ss') => {
  if (!date) return '--'
  
  return dayjs(date).format(format)
}

/**
 * 格式化相对时间
 * @param {string|Date|number} date - 日期
 * @returns {string} 相对时间字符串
 */
export const formatRelativeTime = (date) => {
  if (!date) return '--'
  
  return dayjs(date).fromNow()
}

/**
 * 格式化持续时间
 * @param {number} milliseconds - 毫秒数
 * @param {string} format - 格式字符串
 * @returns {string} 持续时间字符串
 */
export const formatDuration = (milliseconds, format = 'auto') => {
  if (milliseconds === null || milliseconds === undefined || isNaN(milliseconds)) {
    return '--'
  }
  
  const duration = dayjs.duration(milliseconds)
  
  if (format === 'auto') {
    const days = duration.days()
    const hours = duration.hours()
    const minutes = duration.minutes()
    const seconds = duration.seconds()
    
    if (days > 0) {
      return `${days}天${hours}小时${minutes}分钟`
    } else if (hours > 0) {
      return `${hours}小时${minutes}分钟`
    } else if (minutes > 0) {
      return `${minutes}分钟${seconds}秒`
    } else {
      return `${seconds}秒`
    }
  }
  
  return duration.format(format)
}

/**
 * 格式化倒计时
 * @param {number} endTime - 结束时间戳
 * @returns {string} 倒计时字符串
 */
export const formatCountdown = (endTime) => {
  if (!endTime) return '--'
  
  const now = Date.now()
  const diff = endTime - now
  
  if (diff <= 0) return '已结束'
  
  const duration = dayjs.duration(diff)
  const days = Math.floor(duration.asDays())
  const hours = duration.hours()
  const minutes = duration.minutes()
  const seconds = duration.seconds()
  
  if (days > 0) {
    return `${days}天${hours}小时${minutes}分钟`
  } else if (hours > 0) {
    return `${hours}小时${minutes}分钟${seconds}秒`
  } else if (minutes > 0) {
    return `${minutes}分钟${seconds}秒`
  } else {
    return `${seconds}秒`
  }
}

/* 字符串格式化 */

/**
 * 格式化手机号
 * @param {string} phone - 手机号
 * @returns {string} 格式化后的手机号
 */
export const formatPhone = (phone) => {
  if (!phone) return '--'
  
  const cleaned = phone.replace(/\D/g, '')
  
  if (cleaned.length === 11) {
    return cleaned.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3')
  }
  
  return phone
}

/**
 * 脱敏手机号
 * @param {string} phone - 手机号
 * @returns {string} 脱敏后的手机号
 */
export const maskPhone = (phone) => {
  if (!phone) return '--'
  
  const cleaned = phone.replace(/\D/g, '')
  
  if (cleaned.length === 11) {
    return cleaned.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
  }
  
  return phone
}

/**
 * 脱敏身份证号
 * @param {string} idCard - 身份证号
 * @returns {string} 脱敏后的身份证号
 */
export const maskIdCard = (idCard) => {
  if (!idCard) return '--'
  
  if (idCard.length === 18) {
    return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
  } else if (idCard.length === 15) {
    return idCard.replace(/(\d{6})\d{6}(\d{3})/, '$1******$2')
  }
  
  return idCard
}

/**
 * 脱敏邮箱
 * @param {string} email - 邮箱
 * @returns {string} 脱敏后的邮箱
 */
export const maskEmail = (email) => {
  if (!email) return '--'
  
  const atIndex = email.indexOf('@')
  if (atIndex === -1) return email
  
  const username = email.substring(0, atIndex)
  const domain = email.substring(atIndex)
  
  if (username.length <= 2) {
    return `${username.charAt(0)}*${domain}`
  } else {
    const maskedUsername = username.charAt(0) + '*'.repeat(username.length - 2) + username.charAt(username.length - 1)
    return `${maskedUsername}${domain}`
  }
}

/**
 * 截断文本
 * @param {string} text - 文本
 * @param {number} maxLength - 最大长度
 * @param {string} suffix - 后缀
 * @returns {string} 截断后的文本
 */
export const truncateText = (text, maxLength = 50, suffix = '...') => {
  if (!text) return '--'
  
  if (text.length <= maxLength) return text
  
  return text.substring(0, maxLength - suffix.length) + suffix
}

/**
 * 首字母大写
 * @param {string} str - 字符串
 * @returns {string} 首字母大写的字符串
 */
export const capitalize = (str) => {
  if (!str) return ''
  
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase()
}

/**
 * 下划线转驼峰
 * @param {string} str - 下划线字符串
 * @returns {string} 驼峰字符串
 */
export const toCamelCase = (str) => {
  if (!str) return ''
  
  return str.replace(/_([a-z])/g, (match, letter) => letter.toUpperCase())
}

/**
 * 驼峰转下划线
 * @param {string} str - 驼峰字符串
 * @returns {string} 下划线字符串
 */
export const toSnakeCase = (str) => {
  if (!str) return ''
  
  return str.replace(/([A-Z])/g, '_$1').toLowerCase()
}

/*地址格式化*/

/**
 * 格式化地址
 * @param {Object} address - 地址对象
 * @returns {string} 格式化后的地址
 */
export const formatAddress = (address) => {
  if (!address) return '--'
  
  const { province, city, district, street, detail } = address
  
  const parts = [province, city, district, street, detail].filter(Boolean)
  
  return parts.join('')
}

/**
 * 格式化坐标
 * @param {number} longitude - 经度
 * @param {number} latitude - 纬度
 * @param {number} precision - 精度
 * @returns {string} 格式化后的坐标
 */
export const formatCoordinates = (longitude, latitude, precision = 6) => {
  if (longitude === null || longitude === undefined || 
      latitude === null || latitude === undefined) {
    return '--'
  }
  
  return `${Number(longitude).toFixed(precision)}, ${Number(latitude).toFixed(precision)}`
}

/*业务特定格式化 */

/**
 * 格式化订单号
 * @param {string} orderNo - 订单号
 * @returns {string} 格式化后的订单号
 */
export const formatOrderNo = (orderNo) => {
  if (!orderNo) return '--'
  
  // 每4位添加一个空格
  return orderNo.replace(/(.{4})/g, '$1 ').trim()
}

/**
 * 格式化车牌号
 * @param {string} plateNo - 车牌号
 * @returns {string} 格式化后的车牌号
 */
export const formatPlateNo = (plateNo) => {
  if (!plateNo) return '--'
  
  // 车牌号格式：京A12345 或 京A123456
  if (plateNo.length === 7) {
    return plateNo.substring(0, 2) + '·' + plateNo.substring(2)
  } else if (plateNo.length === 8) {
    return plateNo.substring(0, 2) + '·' + plateNo.substring(2, 7) + '·' + plateNo.substring(7)
  }
  
  return plateNo
}

/**
 * 格式化运单状态
 * @param {string} status - 状态
 * @returns {Object} 状态对象
 */
export const formatShipmentStatus = (status) => {
  const statusMap = {
    'pending': { label: '待处理', color: '#E6A23C' },
    'picked_up': { label: '已揽收', color: '#409EFF' },
    'in_transit': { label: '运输中', color: '#409EFF' },
    'out_for_delivery': { label: '派送中', color: '#409EFF' },
    'delivered': { label: '已送达', color: '#67C23A' },
    'failed': { label: '派送失败', color: '#F56C6C' },
    'returned': { label: '已退回', color: '#909399' }
  }
  
  return statusMap[status] || { label: status || '--', color: '#909399' }
}

/**
 * 格式化评分
 * @param {number} rating - 评分
 * @param {number} maxRating - 最高分
 * @returns {string} 格式化后的评分
 */
export const formatRating = (rating, maxRating = 5) => {
  if (rating === null || rating === undefined || isNaN(rating)) {
    return '--'
  }
  
  return `${Number(rating).toFixed(1)}/${maxRating}`
}

/*数组和对象格式化*/

/**
 * 格式化数组为字符串
 * @param {Array} arr - 数组
 * @param {string} separator - 分隔符
 * @param {Function} formatter - 格式化函数
 * @returns {string} 格式化后的字符串
 */
export const formatArrayToString = (arr, separator = ', ', formatter = null) => {
  if (!Array.isArray(arr) || arr.length === 0) return '--'
  
  const items = formatter ? arr.map(formatter) : arr
  
  return items.join(separator)
}

/**
 * 格式化对象为查询字符串
 * @param {Object} obj - 对象
 * @returns {string} 查询字符串
 */
export const formatObjectToQueryString = (obj) => {
  if (!obj || typeof obj !== 'object') return ''
  
  const params = new URLSearchParams()
  
  Object.entries(obj).forEach(([key, value]) => {
    if (value !== null && value !== undefined && value !== '') {
      params.append(key, String(value))
    }
  })
  
  return params.toString()
}

/* 导出默认格式化器*/

export default {
  // 数字
  number: formatNumber,
  percentage: formatPercentage,
  currency: formatCurrency,
  fileSize: formatFileSize,
  largeNumber: formatLargeNumber,
  
  // 时间
  dateTime: formatDateTime,
  date: formatDate,
  time: formatTime,
  relativeTime: formatRelativeTime,
  duration: formatDuration,
  countdown: formatCountdown,
  
  // 字符串
  phone: formatPhone,
  maskPhone: maskPhone,
  maskIdCard: maskIdCard,
  maskEmail: maskEmail,
  truncateText: truncateText,
  capitalize: capitalize,
  toCamelCase: toCamelCase,
  toSnakeCase: toSnakeCase,
  
  // 地址
  address: formatAddress,
  coordinates: formatCoordinates,
  
  // 业务
  orderNo: formatOrderNo,
  plateNo: formatPlateNo,
  shipmentStatus: formatShipmentStatus,
  rating: formatRating,
  
  // 数组对象
  arrayToString: formatArrayToString,
  objectToQueryString: formatObjectToQueryString
}