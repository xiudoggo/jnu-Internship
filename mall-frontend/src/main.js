import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'

import axios from 'axios'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

// 全局 axios 默认地址 → 后端 8080
axios.defaults.baseURL = 'http://localhost:8080'

import './styles/global.scss'
// import './mock'  // 已切换后端，禁用 MockJS

const app = createApp(App)

// 全局错误处理
app.config.errorHandler = (err, vm, info) => {
  console.error('Vue Error:', err)
  document.getElementById('app').innerHTML +=
    `<div style="background:red;color:white;padding:20px;margin:10px;">
      <h2>运行时错误</h2>
      <pre>${err}</pre>
      <p>来源: ${info}</p>
    </div>`
}

// 只注册实际用到的图标，避免全部加载
const usedIcons = ['ShoppingCart', 'Tickets', 'Star', 'StarFilled', 'ArrowDown',
  'TrendCharts', 'Present', 'Search', 'Phone', 'Lock', 'User', 'Delete',
  'Goods', 'Location', 'Monitor', 'MagicStick', 'Food', 'House', 'Bicycle']
for (const key of usedIcons) {
  if (ElementPlusIconsVue[key]) {
    app.component(key, ElementPlusIconsVue[key])
  }
}

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

app.use(ElementPlus, { locale: zhCn })
app.use(router)
app.use(pinia)

try {
  app.mount('#app')
} catch (err) {
  document.getElementById('app').innerHTML =
    `<div style="background:red;color:white;padding:20px;">
      <h2>Mount 失败</h2>
      <pre>${err.message}\n${err.stack}</pre>
    </div>`
}
