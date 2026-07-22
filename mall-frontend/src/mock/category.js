import Mock from 'mockjs'

Mock.mock('/api/category/tree', 'get', () => {
  return {
    code: 200,
    data: [
      {
        id: 1, name: '数码电子', icon: 'Monitor', sort: 1,
        children: [
          { id: 11, name: '手机', parentId: 1, sort: 1 },
          { id: 12, name: '电脑', parentId: 1, sort: 2 },
          { id: 13, name: '耳机音箱', parentId: 1, sort: 3 },
          { id: 14, name: '智能穿戴', parentId: 1, sort: 4 }
        ]
      },
      {
        id: 2, name: '潮流服饰', icon: 'Present', sort: 2,
        children: [
          { id: 21, name: '男装', parentId: 2, sort: 1 },
          { id: 22, name: '女装', parentId: 2, sort: 2 },
          { id: 23, name: '鞋靴', parentId: 2, sort: 3 },
          { id: 24, name: '箱包', parentId: 2, sort: 4 }
        ]
      },
      {
        id: 3, name: '美妆护肤', icon: 'MagicStick', sort: 3,
        children: [
          { id: 31, name: '面部护理', parentId: 3, sort: 1 },
          { id: 32, name: '彩妆', parentId: 3, sort: 2 },
          { id: 33, name: '香水', parentId: 3, sort: 3 }
        ]
      },
      {
        id: 4, name: '食品饮料', icon: 'Food', sort: 4,
        children: [
          { id: 41, name: '休闲零食', parentId: 4, sort: 1 },
          { id: 42, name: '饮料冲调', parentId: 4, sort: 2 },
          { id: 43, name: '生鲜水果', parentId: 4, sort: 3 }
        ]
      },
      {
        id: 5, name: '家居生活', icon: 'House', sort: 5,
        children: [
          { id: 51, name: '家具', parentId: 5, sort: 1 },
          { id: 52, name: '家纺', parentId: 5, sort: 2 },
          { id: 53, name: '厨具', parentId: 5, sort: 3 }
        ]
      },
      {
        id: 6, name: '运动户外', icon: 'Bicycle', sort: 6,
        children: [
          { id: 61, name: '运动装备', parentId: 6, sort: 1 },
          { id: 62, name: '户外用品', parentId: 6, sort: 2 },
          { id: 63, name: '健身器材', parentId: 6, sort: 3 }
        ]
      }
    ]
  }
})
