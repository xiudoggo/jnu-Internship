import Mock from 'mockjs'
import { products } from './product'

Mock.mock('/api/search', 'get', (options) => {
  const url = new URL(options.url, 'http://localhost')
  const keyword = (url.searchParams.get('q') || '').trim()
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 12

  if (!keyword) {
    return { code: 200, data: { total: 0, list: [] } }
  }

  // 从真实商品列表中按名称/描述/分类过滤
  const filtered = products.filter(p =>
    p.name.includes(keyword) ||
    (p.description && p.description.includes(keyword)) ||
    (p.categoryName && p.categoryName.includes(keyword))
  )

  const total = filtered.length
  const start = (page - 1) * pageSize
  const list = filtered.slice(start, start + pageSize)

  return {
    code: 200,
    data: { total, list, page, pageSize }
  }
})
