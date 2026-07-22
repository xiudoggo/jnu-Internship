import Mock from 'mockjs'

const orders = []
let orderIdCounter = 1000

const statusMap = {
  0: '待支付',
  1: '已支付',
  2: '已发货',
  3: '已完成',
  4: '已取消'
}

// 创建订单
Mock.mock('/api/order/create', 'post', (options) => {
  const { address, items, totalAmount } = JSON.parse(options.body)
  const orderNo = 'CG' + Date.now() + Mock.Random.integer(100, 999)
  const order = {
    id: ++orderIdCounter,
    orderNo,
    totalAmount,
    payAmount: totalAmount,
    status: 0,
    statusText: '待支付',
    receiverName: address.name,
    receiverPhone: address.phone,
    receiverAddress: address.province + address.city + address.district + ' ' + address.detail,
    items: items.map((item, idx) => ({
      id: idx + 1,
      productId: item.productId,
      productName: item.name,
      productImage: item.image,
      price: item.price,
      quantity: item.quantity,
      totalPrice: (item.price * item.quantity).toFixed(2)
    })),
    createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    payTime: null,
    deliveryTime: null,
    completeTime: null
  }
  orders.unshift(order)
  return { code: 200, message: '下单成功', data: { orderId: order.id, orderNo: order.orderNo } }
})

// 预设一些模拟订单
const mockOrders = Mock.mock({
  'list|8': [
    {
      'id|+1': 1001,
      orderNo: function () { return 'CG20250' + Mock.Random.integer(10000, 99999) },
      'totalAmount|50-2000.99': 1,
      payAmount: function () { return this.totalAmount },
      'status|1': [0, 1, 2, 3, 4],
      receiverName: '@cname',
      receiverPhone: /^1[3-9]\d{9}$/,
      receiverAddress: '@county(true) @ctitle(10, 20)',
      'items|1-3': [
        {
          'id|+1': 1,
          'productId|1-60': 1,
          productName: '@ctitle(4, 8)',
          productImage: function () { return `https://picsum.photos/seed/item${this.productId}/80/80` },
          'price|15-2000.99': 1,
          'quantity|1-5': 1,
          totalPrice: function () { return (this.price * this.quantity).toFixed(2) }
        }
      ],
      createTime: '@datetime("yyyy-MM-dd HH:mm:ss")',
      payTime: '@datetime("yyyy-MM-dd HH:mm:ss")',
      deliveryTime: '@datetime("yyyy-MM-dd HH:mm:ss")',
      completeTime: '@datetime("yyyy-MM-dd HH:mm:ss")'
    }
  ]
}).list

mockOrders.forEach(o => {
  o.statusText = statusMap[o.status]
  o.totalAmount = Number(o.totalAmount).toFixed(2)
  o.payAmount = Number(o.payAmount).toFixed(2)
  o.items.forEach(item => {
    item.price = Number(item.price).toFixed(2)
    item.totalPrice = Number(item.totalPrice).toFixed(2)
  })
  orders.push(o)
})

// 订单列表
Mock.mock('/api/order/list', 'get', () => {
  return { code: 200, data: { total: orders.length, list: orders } }
})

// 订单详情
Mock.mock(/\/api\/order\/\d+$/, 'get', (options) => {
  const id = parseInt(options.url.match(/\/api\/order\/(\d+)/)[1])
  const order = orders.find(o => o.id === id)
  if (!order) return { code: 404, message: '订单不存在', data: null }
  return { code: 200, data: order }
})

// 取消订单
Mock.mock(/\/api\/order\/\d+\/cancel/, 'put', (options) => {
  const id = parseInt(options.url.match(/\/api\/order\/(\d+)\/cancel/)[1])
  const order = orders.find(o => o.id === id)
  if (order) {
    order.status = 4
    order.statusText = '已取消'
  }
  return { code: 200, message: '已取消', data: null }
})

// 模拟支付
Mock.mock(/\/api\/order\/\d+\/pay/, 'put', (options) => {
  const id = parseInt(options.url.match(/\/api\/order\/(\d+)\/pay/)[1])
  const order = orders.find(o => o.id === id)
  if (order) {
    order.status = 1
    order.statusText = '已支付'
    order.payTime = Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
  }
  return { code: 200, message: '支付成功', data: null }
})
