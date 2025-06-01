import { ref, onUnmounted } from 'vue'

export function useWebSocket(options = {}) {
  const {
    url,
    onOpen = () => {},
    onMessage = () => {},
    onClose = () => {},
    onError = () => {},
    reconnectAttempts = 5,
    reconnectInterval = 3000
  } = options
  
  const socket = ref(null)
  const isConnected = ref(false)
  let reconnectCount = 0
  let reconnectTimer = null
  
  const connect = () => {
    if (!url) {
      console.error('WebSocket URL is required')
      return
    }
    
    socket.value = new WebSocket(url)
    
    socket.value.onopen = (event) => {
      isConnected.value = true
      reconnectCount = 0
      onOpen(event)
    }
    
    socket.value.onmessage = (event) => {
      onMessage(JSON.parse(event.data))
    }
    
    socket.value.onclose = (event) => {
      isConnected.value = false
      onClose(event)
      
      if (reconnectCount < reconnectAttempts) {
        reconnectTimer = setTimeout(() => {
          reconnectCount++
          connect()
        }, reconnectInterval)
      }
    }
    
    socket.value.onerror = (event) => {
      onError(event)
    }
  }
  
  const send = (data) => {
    if (socket.value && isConnected.value) {
      socket.value.send(typeof data === 'string' ? data : JSON.stringify(data))
    }
  }
  
  const close = () => {
    if (socket.value) {
      socket.value.close()
    }
  }
  
  onUnmounted(() => {
    if (reconnectTimer) {
      clearTimeout(reconnectTimer)
    }
    close()
  })
  
  return {
    socket,
    isConnected,
    connect,
    send,
    close
  }
}