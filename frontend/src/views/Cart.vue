<template>
  <div class="page-container">
    <div class="page-heading">
      <div>
        <h2>我的购物车</h2>
        <p>确认想要购买的图书，统一填写交付信息后提交订单。</p>
      </div>
    </div>

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
            class="cart-cover"
            fit="cover"
            lazy
          />
          <div v-else class="cart-cover placeholder">
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

    <el-dialog v-model="checkoutVisible" title="填写交付信息" width="420px">
      <el-form :model="checkoutForm" label-width="90px">
        <el-form-item label="联系方式" required>
          <el-input v-model="checkoutForm.contactInfo" placeholder="手机号 / 微信号" />
        </el-form-item>
        <el-form-item label="交付地址" required>
          <el-input v-model="checkoutForm.deliveryAddress" placeholder="宿舍楼 / 教学楼 / 校内地点" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="checkoutForm.remark" type="textarea" :rows="3" placeholder="可填写交易时间、其他说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkoutVisible = false">取消</el-button>
        <el-button type="primary" :loading="checkingOut" @click="confirmCheckout">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCart, updateCartItem, removeCartItem, createOrder } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartItems = ref([])
const checkoutVisible = ref(false)
const checkingOut = ref(false)
const checkoutForm = reactive({
  contactInfo: '',
  deliveryAddress: '',
  remark: ''
})

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
  checkoutVisible.value = true
}

const confirmCheckout = async () => {
  const selected = cartItems.value.filter(item => item.selected === 1)
  if (!checkoutForm.contactInfo.trim() || !checkoutForm.deliveryAddress.trim()) {
    ElMessage.warning('请填写联系方式和交付地址')
    return
  }
  checkingOut.value = true
  try {
    for (const item of selected) {
      await createOrder({
        bookId: item.bookId,
        quantity: item.quantity,
        contactInfo: checkoutForm.contactInfo.trim(),
        deliveryAddress: checkoutForm.deliveryAddress.trim(),
        remark: checkoutForm.remark.trim()
      })
    }
    
    for (const item of selected) {
      await removeCartItem(item.id)
    }
  
    ElMessage.success('下单成功')
    checkoutVisible.value = false
    router.push('/orders')
  } catch {
    ElMessage.error('下单失败，请重试')
  } finally {
    checkingOut.value = false
  }
}
</script>

<style scoped>
.page-container {
  max-width: 980px;
}

.page-heading {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 18px;
}

.page-heading h2 {
  margin: 0 0 6px;
  font-size: 22px;
  color: var(--text-main);
}

.page-heading p {
  margin: 0;
  color: var(--text-muted);
  font-size: 14px;
}

.cart-container {
  background: var(--surface);
  border: 1px solid rgba(132, 153, 160, 0.22);
  border-radius: var(--radius);
  box-shadow: var(--shadow-sm);
  padding: 18px;
}

.cart-header {
  padding-bottom: 12px;
  border-bottom: 1px solid var(--line);
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #edf1f2;
}

.cart-book-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 2;
  cursor: pointer;
}

.cart-cover {
  width: 80px;
  height: 100px;
  border-radius: var(--radius);
  background: #eef3f4;
  object-fit: cover;
  flex: 0 0 auto;
}

.cart-cover.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  font-size: 12px;
}

.book-title {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 8px;
  color: var(--text-main);
}

.book-price {
  color: var(--danger);
  font-size: 14px;
  font-weight: 700;
}

.cart-quantity {
  flex: 1;
  text-align: center;
}

.cart-subtotal {
  flex: 1;
  text-align: center;
  font-weight: bold;
  color: var(--danger);
}

.cart-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid var(--line);
}

.total-text {
  font-size: 15px;
}

.total-price {
  font-size: 20px;
  color: var(--danger);
}

@media (max-width: 720px) {
  .cart-item {
    align-items: flex-start;
    flex-wrap: wrap;
  }

  .cart-book-info {
    min-width: calc(100% - 40px);
  }

  .cart-quantity,
  .cart-subtotal {
    text-align: left;
  }

  .cart-footer {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
