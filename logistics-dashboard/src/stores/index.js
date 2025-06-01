import { createPinia } from 'pinia'

// 创建 pinia 实例
const pinia = createPinia()

// 持久化插件
const persistedState = (context) => {
  const { store, options } = context
  
  // 检查是否需要持久化
  if (options.persist) {
    const storageKey = `pinia-${store.$id}`
    
    // 从 localStorage 恢复状态
    const savedState = localStorage.getItem(storageKey)
    if (savedState) {
      try {
        const parsedState = JSON.parse(savedState)
        store.$patch(parsedState)
      } catch (error) {
        console.warn(`Failed to restore state for store ${store.$id}:`, error)
      }
    }
    
    // 监听状态变化并保存到 localStorage
    store.$subscribe((mutation, state) => {
      try {
        // 只保存需要持久化的字段
        const persistedFields = options.persist === true ? state : 
          Object.keys(options.persist).reduce((acc, key) => {
            if (options.persist[key]) {
              acc[key] = state[key]
            }
            return acc
          }, {})
        
        localStorage.setItem(storageKey, JSON.stringify(persistedFields))
      } catch (error) {
        console.warn(`Failed to persist state for store ${store.$id}:`, error)
      }
    })
  }
}

// 注册插件
pinia.use(persistedState)

export default pinia

// 导出所有 stores
export { useDashboardStore } from './dashboard.js'
export { useAlertsStore } from './alerts.js'
export { useUserStore } from './user.js'
export { useSystemStore } from './system.js'