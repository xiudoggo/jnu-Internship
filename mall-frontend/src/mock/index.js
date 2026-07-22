import Mock from 'mockjs'

// 200-500ms 延迟，模拟真实网络
Mock.setup({ timeout: '200-400' })

import './user'
import './product'
import './cart'
import './order'
import './banner'
import './category'
import './search'
import './favorite'
