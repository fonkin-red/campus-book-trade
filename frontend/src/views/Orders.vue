<template>
  <div class="page-container" v-loading="loading">
    <h2>我的订单</h2>

    <el-tabs v-model="activeTab" @tab-change="fetchOrders">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待付款" name="0" />
      <el-tab-pane label="待发货" name="1" />
      <el-tab-pane label="待收货" name="2" />
      <el-tab-pane label="已完成" name="3" />
    </el-tabs>

    <el-empty v-if="!filteredOrders.length" description="暂无订单" />

    <div v-else class="order-list">
      <el-card v-for="order in filteredOrders" :key="order.id" class="order-card">
        <div class="order-header">
          <span>订单号：{{ order.orderNo }}</span>
          <el-tag :type="statusType[order.status]">{{ statusText[order.status] }}</el-tag>
        </div>
        <div class="order-body">
          <span class="book-title">{{ order.bookTitle }}</span>
          <span class="order-price">&yen;{{ order.totalAmount }}</span>
          <span class="order-qty">x{{ order.quantity }}</span>
        </div>
        <div class="order-footer">
          <span class="order-time">{{ order.createTime }}</span>
          <div class="order-actions">
            <el-button v-if="order.status === 0" type="primary" size="small" @click="handlePay(order.id)">付款</el-button>
            <el-button v-if="order.status === 1 && isSeller(order)" type="primary" size="small" @click="handleShip(order.id)">发货</el-button>
            <el-button v-if="order.status === 2" type="success" size="small" @click="handleConfirm(order.id)">确认收货</el-button>
            <el-button v-if="order.status < 3" size="small" @click="handleCancel(order.id)">取消</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getOrders, payOrder, confirmOrder, cancelOrder, shipOrder } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const orders = ref([])
const loading = ref(false)
const activeTab = ref('all')

const statusText = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }
const statusType = { 0: 'warning', 1: 'info', 2: '', 3: 'success', 4: 'info' }

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  return orders.value.filter(o => o.status === Number(activeTab.value))
})

const isSeller = (order) => order.sellerId === userStore.user.id

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await getOrders()
    orders.value = res.data || []
  } finally { loading.value = false }
}

onMounted(fetchOrders)

const handlePay = async (id) => {
  await payOrder(id)
  ElMessage.success('付款成功')
  fetchOrders()
}
const handleShip = async (id) => {
  await shipOrder(id)
  ElMessage.success('已发货')
  fetchOrders()
}
const handleConfirm = async (id) => {
  try {
    await ElMessageBox.confirm('确认已收到货？', '提示', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',        
      type: 'warning'
    })
  } catch { return }
  await confirmOrder(id)
  ElMessage.success('已确认收货')
  fetchOrders()
}
const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定取消订单？', '提示', {
      confirmButtonText: '确定取消',
      cancelButtonText: '我再想想',    
    })
  } catch { return }
  await cancelOrder(id)
  ElMessage.success('已取消')
  fetchOrders()
}
</script>

<style scoped>
.page-container { max-width: 900px; margin: 0 auto; padding: 30px 20px; }
h2 { margin-bottom: 20px; }
.order-card { margin-bottom: 16px; }
.order-header { display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 13px; color: #909399; }
.order-body { display: flex; align-items: center; gap: 16px; margin-bottom: 12px; }
.book-title { font-size: 15px; font-weight: 500; flex: 1; }
.order-price { color: #e74c3c; font-weight: bold; font-size: 16px; }
.order-qty { color: #909399; }
.order-footer { display: flex; justify-content: space-between; align-items: center; }
.order-time { font-size: 12px; color: #c0c4cc; }
.order-actions { display: flex; gap: 8px; }
</style>