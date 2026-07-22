import Mock from 'mockjs'

// 收藏数据存内存
let favorites = []

Mock.mock('/api/favorite/list', 'get', () => {
  return { code: 200, data: favorites }
})

Mock.mock('/api/favorite/toggle', 'post', (options) => {
  const { productId, name, image, price } = JSON.parse(options.body)
  const idx = favorites.findIndex(f => f.productId === productId)
  if (idx > -1) {
    favorites.splice(idx, 1)
    return { code: 200, message: '已取消收藏', data: { isFavorited: false } }
  } else {
    favorites.unshift({
      id: Date.now().toString(),
      productId,
      name,
      image,
      price,
      createTime: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss')
    })
    return { code: 200, message: '收藏成功', data: { isFavorited: true } }
  }
})
