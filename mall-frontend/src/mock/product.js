import Mock from 'mockjs'

const categories = ['数码电子', '潮流服饰', '美妆护肤', '食品饮料', '家居生活', '运动户外']
const brands = ['华为', '小米', 'Apple', 'Nike', 'Adidas', '兰蔻', '三只松鼠', '宜家', '安踏', '戴森']

// 生成 60 条商品
const products = Mock.mock({
  'list|60': [
    {
      'id|+1': 1,
      name: function () {
        const prefix = ['2024新款', '爆款', '热卖', '经典', '限定', '旗舰', '', '', '', '']
        const brand = Mock.Random.pick(brands)
        const noun = Mock.Random.pick([
          '智能手机', 'T恤', '口红', '坚果礼盒', '台灯', '跑鞋', '双肩包', '无线耳机',
          '连衣裙', '面膜套装', '薯片大礼包', '收纳箱', '运动裤', '蓝牙音箱', '衬衫',
          '粉底液', '牛肉干', '书架', '瑜伽垫', '充电宝', '卫衣', '精华液', '巧克力',
          '落地灯', '篮球', '数据线', '牛仔裤', '防晒霜', '咖啡豆', '地毯', '羽毛球拍',
          '平板电脑', '羽绒服', '洗面奶', '茶叶礼盒', '窗帘', '滑板', '智能手表', '墨镜'
        ])
        return Mock.Random.pick(prefix) + brand + noun
      },
      'price|15-8999.99': 1,
      originalPrice: function () {
        return (this.price * Mock.Random.float(1.2, 2.5, 2, 2)).toFixed(2)
      },
      coverImage: function () {
        return `https://picsum.photos/seed/product${this.id}/400/400`
      },
      'images|3-5': [
        function () {
          const idx = Mock.Random.integer(1, 99)
          return `https://picsum.photos/seed/detail${this.id}${idx}/800/800`
        }
      ],
      description: '@cparagraph(1, 3)',
      'stock|0-999': 1,
      'sales|0-9999': 1,
      'isHot|0-1': true,
      'isNew|0-1': true,
      categoryId: function () {
        return Math.ceil(this.id / 10)
      },
      categoryName: function () {
        return categories[Math.floor((this.id - 1) / 10) % categories.length]
      },
      'brand|1': brands,
      'rating|3-5': 1,
      'reviewCount|0-999': 1
    }
  ]
}).list
// 确保价格保留两位小数
products.forEach(p => {
  p.price = Number(p.price).toFixed(2)
  p.originalPrice = Number(p.originalPrice).toFixed(2)
})

// 商品列表
Mock.mock(/\/api\/product\/list/, 'get', (options) => {
  const url = new URL(options.url, 'http://localhost')
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 12
  const categoryId = url.searchParams.get('categoryId')
  const keyword = url.searchParams.get('keyword')
  const sort = url.searchParams.get('sort') || 'default'

  let filtered = [...products]

  if (categoryId) {
    filtered = filtered.filter(p => p.categoryId === parseInt(categoryId))
  }
  if (keyword) {
    filtered = filtered.filter(
      p => p.name.includes(keyword) || p.description.includes(keyword)
    )
  }

  // 排序
  if (sort === 'price-asc') filtered.sort((a, b) => a.price - b.price)
  if (sort === 'price-desc') filtered.sort((a, b) => b.price - a.price)
  if (sort === 'sales') filtered.sort((a, b) => b.sales - a.sales)

  const total = filtered.length
  const start = (page - 1) * pageSize
  const list = filtered.slice(start, start + pageSize)

  return {
    code: 200,
    data: { total, list, page, pageSize }
  }
})

// 热门商品
Mock.mock('/api/product/hot', 'get', () => {
  const hot = products.filter(p => p.isHot).slice(0, 8)
  return { code: 200, data: hot }
})

// 新品
Mock.mock('/api/product/new', 'get', () => {
  const news = products.filter(p => p.isNew).slice(0, 8)
  return { code: 200, data: news }
})

// 商品详情
Mock.mock(/\/api\/product\/\d+$/, 'get', (options) => {
  const id = parseInt(options.url.match(/\/api\/product\/(\d+)/)[1])
  const product = products.find(p => p.id === id)
  if (!product) return { code: 404, message: '商品不存在', data: null }
  return { code: 200, data: product }
})

// 商品评论
Mock.mock(/\/api\/product\/\d+\/reviews/, 'get', (options) => {
  const productId = parseInt(options.url.match(/\/api\/product\/(\d+)\/reviews/)[1])
  const reviews = Mock.mock({
    [`list|5-${10}`]: [
      {
        'id|+1': 1,
        userId: '@integer(1, 999)',
        nickname: '@cname',
        avatar: '@image("40x40", "@color", "#fff", "@first")',
        'star|3-5': 1,
        content: '@csentence(10, 40)',
        images: [],
        createTime: '@datetime("yyyy-MM-dd HH:mm:ss")',
        productId: productId
      }
    ]
  }).list
  return { code: 200, data: reviews }
})

// 导出给其他 mock 模块使用
export { products }
