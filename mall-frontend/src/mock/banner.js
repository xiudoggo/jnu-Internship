import Mock from 'mockjs'

Mock.mock('/api/banner/list', 'get', () => {
  return {
    code: 200,
    data: [
      { id: 1, title: '618年中大促', imageUrl: 'https://picsum.photos/seed/banner1/1200/400', linkUrl: '/products?keyword=618' },
      { id: 2, title: '数码新品首发', imageUrl: 'https://picsum.photos/seed/banner2/1200/400', linkUrl: '/products?categoryId=1' },
      { id: 3, title: '夏季穿搭指南', imageUrl: 'https://picsum.photos/seed/banner3/1200/400', linkUrl: '/products?categoryId=2' }
    ]
  }
})
