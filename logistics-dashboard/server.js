const express = require('express')
const path = require('path')
const fs = require('fs')

const app = express()
const PORT = process.env.PORT || 3000

// 判断是否在 PKG 环境中运行
const isPkg = typeof process.pkg !== 'undefined'

// 设置正确的静态文件路径
let distPath
if (isPkg) {
  // PKG 环境：使用 process.cwd() 获取当前工作目录
  distPath = path.join(process.cwd(), 'dist')
  console.log('PKG 环境，dist 路径:', distPath)
} else {
  // 开发环境：使用 __dirname
  distPath = path.join(__dirname, 'dist')
  console.log('开发环境，dist 路径:', distPath)
}

// 检查 dist 目录是否存在
if (!fs.existsSync(distPath)) {
  console.error('❌ 错误: dist 目录不存在')
  console.error('当前查找路径:', distPath)
  console.error('请确保 dist 目录与 exe 文件在同一目录下')
  console.log('按任意键退出...')
  process.stdin.setRawMode(true)
  process.stdin.resume()
  process.stdin.on('data', process.exit.bind(process, 0))
  return
}

// 静态文件服务
app.use(express.static(distPath))

// 处理 Vue Router 的 history 模式
app.get('*', (req, res) => {
  const indexPath = path.join(distPath, 'index.html')
  if (fs.existsSync(indexPath)) {
    res.sendFile(indexPath)
  } else {
    res.status(404).send('index.html 文件未找到')
  }
})

// 启动服务器
const server = app.listen(PORT, () => {
  console.log(`🚀 物流大数据可视化平台运行在: http://localhost:${PORT}`)
  console.log(`📊 请在浏览器中打开上述地址查看应用`)
  console.log(`按 Ctrl+C 停止服务器`)
}).on('error', (err) => {
  if (err.code === 'EADDRINUSE') {
    console.error(`❌ 端口 ${PORT} 已被占用，请关闭占用该端口的程序或修改端口号`)
  } else {
    console.error('❌ 服务器启动失败:', err.message)
  }
  console.log('按任意键退出...')
  process.stdin.setRawMode(true)
  process.stdin.resume()
  process.stdin.on('data', process.exit.bind(process, 0))
})

process.on('SIGINT', () => {
  console.log('\n正在关闭服务器...')
  server.close(() => {
    process.exit(0)
  })
})