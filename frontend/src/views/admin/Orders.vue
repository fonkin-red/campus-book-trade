<template>
  <div v-loading="loading">
    <h2>订单管理</h2>
    <el-table :data="orders" stripe>
      <el-table-column label="订单号" prop="orderNo" width="180" />
      <el-table-column label="书名" prop="bookTitle" />
      <el-table-column label="金额" width="100">
        <template #default="{ row }">&yen;{{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]">{{ statusText[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" type="primary" size="small" @click="handleShip(row.id)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminOrders, shipOrder } from '@/api'
import { ElMessage } from 'element-plus'

const orders = ref([])
const loading = ref(false)
const statusText = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }
const statusType = { 0: 'warning', 1: 'info', 2: '', 3: 'success', 4: 'info' }

onMounted(async () => {
  loading.value = true
  try {
    const res = await getAdminOrders()
    orders.value = res.data || []
  } finally { loading.value = false }
})

const handleShip = async (id) => {
  await shipOrder(id)
  ElMessage.success('已发货')
  const order = orders.value.find(o => o.id === id)
  if (order) order.status = 2
}
</script>