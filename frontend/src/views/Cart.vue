<template>
  <div class="page-container">
    <h2>我的购物车</h2>

    <!-- 空购物车 -->
    <el-empty v-if="!cartItems.length" description="购物车还是空的，快去逛逛吧" />

    <!-- 购物车列表 -->
    <div v-else class="cart-container">
      <!-- 全选 -->
      <div class="cart-header">
        <el-checkbox :model-value="selectAll" @change="handleSelectAll">全选</el-checkbox>
      </div>

      <!-- 商品列表 -->
      <div v-for="item in cartItems" :key="item.id" class="cart-item">
        <el-checkbox v-model="item.selected" :true-value="1" :false-value="0" @change="handleCheckChange(item)" />
        
        <div class="cart-book-info" @click="$router.push(`/book/${item.bookId}`)">
          <el-image
            v-if="item.cover"
            :src="item.cover"
            style="width: 80px; height: 100px; border-radius: 6px; object-fit: cover"
            fit="cover"
          />
          <div v-else style="width: 80px; height: 100px; background: #f0f0f0; border-radius: 6px; display: flex; align-items: center; justify-content: center; color: #999; font-size: 12px">
            暂无封面
          </div>
          <div class="book-detail">
            <div class="book-title">{{ item.bookName || '未知书名' }}</div>
            <div class="book-price">¥{{ (item.price || 0).toFixed(2) }}</div>
          </div>
        </div>

        <div class="cart-quantity">
          <el-input-number
            v-model="item.quantity"
            :min="1"
            :max="99"
            size="small"
            @change="handleQuantityChange(item)"
          />
        </div>

        <div class="cart-subtotal">
          ¥{{ ((item.price || 0) * item.quantity).toFixed(2) }}
        </div>

        <el-button type="danger" link @click="handleRemove(item.id)">删除</el-button>
      </div>

      <!-- 底部结算栏 -->
      <div class="cart-footer">
        <span class="total-text">
          已选 <strong>{{ selectedCount }}</strong> 件，
          合计：<strong class="total-price">¥{{ totalPrice }}</strong>
        </span>
        <el-button type="primary" size="large" @click="handleCheckout" :disabled="selectedCount === 0">
          去结算
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCart, updateCartItem, removeCartItem, createOrder } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartItems = ref([])

// ===== 数据加载 =====
const loadCart = async () => {
  try {
    const res = await getCart()
    cartItems.value = (res.data || []).map(item => ({
      ...item,
      selected: item.selected ?? 1,  // 默认选中
      quantity: item.quantity || 1
    }))
  } catch {
    ElMessage.error('加载购物车失败')
  }
}

onMounted(() => loadCart())

// ===== 全选 =====
const selectAll = computed(() =>
  cartItems.value.length > 0 && cartItems.value.every(item => item.selected === 1)
)

const handleSelectAll = (val) => {
  cartItems.value.forEach(item => {
    item.selected = val ? 1 : 0
    updateItemSelection(item)
  })
}

// ===== 计算属性 =====
const selectedCount = computed(() =>
  cartItems.value.filter(item => item.selected === 1).reduce((sum, item) => sum + item.quantity, 0)
)

const totalPrice = computed(() =>
  cartItems.value
    .filter(item => item.selected === 1)
    .reduce((sum, item) => sum + (item.price || 0) * item.quantity, 0)
    .toFixed(2)
)

// ===== 操作函数 =====
const handleCheckChange = (item) => {
  updateItemSelection(item)
}

const updateItemSelection = async (item) => {
  try {
    await updateCartItem(item.id, { selected: item.selected, quantity: item.quantity })
  } catch {
    ElMessage.error('更新失败')
  }
}

const handleQuantityChange = async (item) => {
  try {
    await updateCartItem(item.id, { selected: item.selected, quantity: item.quantity })
  } catch {
    ElMessage.error('更新数量失败')
  }
}

const handleRemove = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', { type: 'warning' })
    await removeCartItem(id)
    ElMessage.success('已删除')
    await loadCart()
  } catch {
    // 取消删除
  }
}

// ===== 结算 =====
const handleCheckout = async () => {
  const selected = cartItems.value.filter(item => item.selected === 1)
  if (!selected.length) {
    ElMessage.warning('请先选择要购买的商品')
    return
  }
  try {
    for (const item of selected) {
      await createOrder({
        bookId: item.bookId,
        quantity: item.quantity
      })
    }
    
    for (const item of selected) {
      await removeCartItem(item.id)
    }
  
    ElMessage.success('下单成功')
    router.push('/orders')
  } catch {
    ElMessage.error('下单失败，请重试')
  }
}
</script>

<style scoped>
.page-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 20px;
}

h2 {
  margin-bottom: 24px;
}

.cart-container {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

.cart-header {
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}

.cart-book-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 2;
  cursor: pointer;
}

.book-title {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 8px;
}

.book-price {
  color: #e74c3c;
  font-size: 14px;
}

.cart-quantity {
  flex: 1;
  text-align: center;
}

.cart-subtotal {
  flex: 1;
  text-align: center;
  font-weight: bold;
  color: #e74c3c;
}

.cart-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 2px solid #eee;
}

.total-text {
  font-size: 15px;
}

.total-price {
  font-size: 20px;
  color: #e74c3c;
}
</style>