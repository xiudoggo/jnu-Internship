import Mock from 'mockjs'

// 模拟用户数据库
const users = [
  { id: 1, phone: '13800000001', nickname: '潮人小明', password: '123456', avatar: '' },
  { id: 2, phone: '13800000002', nickname: '购物达人', password: '123456', avatar: '' }
]
let nextId = 3

// 登录
Mock.mock('/api/user/login', 'post', (options) => {
  const { phone, password } = JSON.parse(options.body)
  const user = users.find(u => u.phone === phone && u.password === password)
  if (user) {
    return {
      code: 200,
      message: '登录成功',
      data: {
        token: `mock_token_${user.id}_${Date.now()}`,
        userInfo: {
          id: user.id,
          nickname: user.nickname,
          phone: user.phone,
          avatar: user.avatar || `https://picsum.photos/seed/avatar${user.id}/100/100`
        }
      }
    }
  }
  return { code: 401, message: '手机号或密码错误', data: null }
})

// 注册
Mock.mock('/api/user/register', 'post', (options) => {
  const { phone, nickname, password } = JSON.parse(options.body)
  if (users.find(u => u.phone === phone)) {
    return { code: 400, message: '该手机号已注册', data: null }
  }
  const newUser = { id: nextId++, phone, nickname, password, avatar: '' }
  users.push(newUser)
  return { code: 200, message: '注册成功', data: null }
})

// 获取用户信息
Mock.mock('/api/user/info', 'get', () => {
  return {
    code: 200,
    data: {
      id: 1,
      nickname: '潮人小明',
      phone: '13800000001',
      avatar: 'https://picsum.photos/seed/avatar1/100/100',
      email: 'xiaoming@chaogou.com',
      createTime: '2025-01-15'
    }
  }
})
