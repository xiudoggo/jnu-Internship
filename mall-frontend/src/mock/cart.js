import Mock from 'mockjs'

// 购物车数据存内存
let cartItems = []

// 获取购物车列表
Mock.mock('/api/cart/list', 'get', () => {
  return { code: 200, data: cartItems }
})

// 添加商品到购物车
Mock.mock('/api/cart/add', 'post', (options) => {
  const { productId, name, image, price, quantity = 1 } = JSON.parse(options.body)
  const exist = cartItems.find(i => i.productId === productId)
  if (exist) {
    exist.quantity += quantity
  } else {
    cartItems.push({
      id: Date.now().toString(),
      productId,
      name,
      image,
      price,
      quantity,
      selected: true
    })
  }
  return { code: 200, message: '添加成功', data: null }
})

// 修改数量
Mock.mock(/\/api\/cart\/\d+/, 'put', (options) => {
  const id = options.url.match(/\/api\/cart\/(\d+)/)[1]
  const { quantity } = JSON.parse(options.body)
  const item = cartItems.find(i => i.id === id)
  if (item) {
    item.quantity = Math.max(1, quantity)
  }
  return { code: 200, message: '修改成功', data: null }
})

// 删除
Mock.mock(/\/api\/cart\/\d+/, 'delete', (options) => {
  const id = options.url.match(/\/api\/cart\/(\d+)/)[1]
  cartItems = cartItems.filter(i => i.id !== id)
  return { code: 200, message: '已删除', data: null }
})
