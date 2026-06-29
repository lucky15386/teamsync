class WebSocketClient {
  constructor(url) {
    this.url = url
    this.ws = null
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectInterval = 3000
    this.heartbeatInterval = null
    this.heartbeatTime = 30000
    this.callbacks = {}
  }

  connect() {
    try {
      this.ws = new WebSocket(this.url)
      
      this.ws.onopen = () => {
        console.log('WebSocket连接成功')
        this.reconnectAttempts = 0
        this.startHeartbeat()
        if (this.callbacks.onOpen) this.callbacks.onOpen()
      }

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          if (this.callbacks.onMessage) this.callbacks.onMessage(data)
        } catch (e) {
          console.error('解析WebSocket消息失败', e)
        }
      }

      this.ws.onclose = (event) => {
        console.log('WebSocket连接关闭', event)
        this.stopHeartbeat()
        if (this.callbacks.onClose) this.callbacks.onClose(event)
        this.reconnect()
      }

      this.ws.onerror = (error) => {
        console.error('WebSocket错误', error)
        if (this.callbacks.onError) this.callbacks.onError(error)
      }
    } catch (error) {
      console.error('WebSocket连接失败', error)
    }
  }

  reconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      console.log(`正在重连... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`)
      setTimeout(() => {
        this.connect()
      }, this.reconnectInterval)
    }
  }

  startHeartbeat() {
    this.heartbeatInterval = setInterval(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(JSON.stringify({ type: 'heartbeat' }))
      }
    }, this.heartbeatTime)
  }

  stopHeartbeat() {
    if (this.heartbeatInterval) {
      clearInterval(this.heartbeatInterval)
      this.heartbeatInterval = null
    }
  }

  send(data) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(data))
    } else {
      console.error('WebSocket未连接，无法发送消息')
    }
  }

  on(event, callback) {
    this.callbacks[`on${event.charAt(0).toUpperCase()}${event.slice(1)}`] = callback
  }

  close() {
    this.stopHeartbeat()
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }
}

export default WebSocketClient
