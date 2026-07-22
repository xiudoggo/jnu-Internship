import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])

  const totalCount = computed(() => items.value.reduce((sum, i) => sum + i.quantity, 0))
  const selectedItems = computed(() => items.value.filter(i => i.selected))
  const totalPrice = computed(() =>
    selectedItems.value.reduce((sum, i) => sum + i.price * i.quantity, 0)
  )
  const isAllSelected = computed(
    () => items.value.length > 0 && items.value.every(i => i.selected)
  )

  // 从服务端拉取购物车数据
  async function fetchCart() {
    try {
      const res = await axios.get('/api/cart/list')
      if (res.data.code === 200) {
        items.value = res.data.data
      }
    } catch {
      items.value = []
    }
  }

  // 添加商品到购物车
  function addToCart(product, quantity = 1) {
    const exist = items.value.find(i => i.productId === product.id)
    if (exist) {
      exist.quantity += quantity
    } else {
      items.value.push({
        id: Date.now().toString(),
        productId: product.id,
        name: product.name,
        image: product.coverImage || product.image,
        price: product.price,
        quantity,
        selected: true
      })
    }
  }

  function removeItem(productId) {
    const idx = items.value.findIndex(i => i.productId === productId)
    if (idx > -1) items.value.splice(idx, 1)
  }

  function updateQuantity(productId, qty) {
    const item = items.value.find(i => i.productId === productId)
    if (item) {
      item.quantity = Math.max(1, qty)
    }
  }

  function toggleSelect(productId) {
    const item = items.value.find(i => i.productId === productId)
    if (item) {
      item.selected = !item.selected
    }
  }

  function toggleSelectAll(checked) {
    items.value.forEach(i => (i.selected = checked))
  }

  function clearSelected() {
    items.value = items.value.filter(i => !i.selected)
  }

  function clearCart() {
    items.value = []
  }

  return {
    items, totalCount, selectedItems, totalPrice, isAllSelected,
    fetchCart, addToCart, removeItem, updateQuantity,
    toggleSelect, toggleSelectAll, clearSelected, clearCart
  }
})
