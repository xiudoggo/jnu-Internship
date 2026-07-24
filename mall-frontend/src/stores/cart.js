import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const badgeCount = ref(0)  // 轻量角标计数，由 fetchCartCount 更新

  const totalCount = computed(() => items.value.reduce((sum, i) => sum + i.quantity, 0))
  const selectedItems = computed(() => items.value.filter(i => i.selected))
  const totalPrice = computed(() =>
    selectedItems.value.reduce((sum, i) => sum + i.price * i.quantity, 0)
  )
  const isAllSelected = computed(
    () => items.value.length > 0 && items.value.every(i => i.selected)
  )

  // 轻量：仅获取购物车商品总数（导航栏角标用，登录后调用）
  async function fetchCartCount() {
    try {
      const res = await axios.get('/api/cart/count')
      if (res.data.code === 200) {
        badgeCount.value = res.data.data.count
      }
    } catch { /* ignore */ }
  }

  // 从后端拉取完整购物车列表（进入购物车页面时调用）
  async function fetchCart() {
    try {
      const res = await axios.get('/api/cart/list')
      if (res.data.code === 200) {
        items.value = res.data.data
        badgeCount.value = totalCount.value
      }
    } catch {
      items.value = []
    }
  }

  // 添加商品到购物车（调用后端接口 + 本地乐观更新角标）
  async function addToCart(product, quantity = 1) {
    try {
      const res = await axios.post('/api/cart/add', {
        productId: product.id,
        quantity
      })
      if (res.data.code === 200 && res.data.data) {
        const d = res.data.data
        const exist = items.value.find(i => i.productId === d.productId)
        if (exist) {
          exist.quantity = d.quantity
        } else {
          items.value.unshift({
            id: d.id, productId: d.productId,
            name: d.name, image: d.image,
            price: d.price, quantity: d.quantity, selected: true
          })
        }
        badgeCount.value = totalCount.value
      }
    } catch {
      // 网络失败回退：本地添加
      const exist = items.value.find(i => i.productId === product.id)
      if (exist) exist.quantity += quantity
      else items.value.push({
        id: Date.now().toString(), productId: product.id,
        name: product.name, image: product.coverImage || product.image,
        price: product.price, quantity, selected: true
      })
      badgeCount.value = totalCount.value
    }
  }

  async function removeItem(productId) {
    const item = items.value.find(i => i.productId === productId)
    if (!item) return
    try {
      await axios.delete('/api/cart/' + item.id)
    } catch { /* */ }
    items.value = items.value.filter(i => i.productId !== productId)
    badgeCount.value = totalCount.value
  }

  async function updateQuantity(productId, qty) {
    const item = items.value.find(i => i.productId === productId)
    if (!item) return
    item.quantity = Math.max(1, qty)
    try {
      await axios.put('/api/cart/' + item.id, { quantity: item.quantity })
    } catch { /* */ }
    badgeCount.value = totalCount.value
  }

  function toggleSelect(productId) {
    const item = items.value.find(i => i.productId === productId)
    if (item) item.selected = !item.selected
  }

  function toggleSelectAll(checked) {
    items.value.forEach(i => (i.selected = checked))
  }

  async function clearSelected() {
    const selected = items.value.filter(i => i.selected)
    for (const item of selected) {
      try { await axios.delete('/api/cart/' + item.id) } catch { /* */ }
    }
    items.value = items.value.filter(i => !i.selected)
    badgeCount.value = totalCount.value
  }

  function clearCart() {
    items.value = []
    badgeCount.value = 0
  }

  return {
    items, badgeCount, totalCount, selectedItems, totalPrice, isAllSelected,
    fetchCartCount, fetchCart, addToCart, removeItem, updateQuantity,
    toggleSelect, toggleSelectAll, clearSelected, clearCart
  }
})
