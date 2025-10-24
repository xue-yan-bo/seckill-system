<template>
  <div class="order">
    <h1 class="page-title">我的订单</h1>
    
    <el-table :data="orderList" style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" width="180" />
      
      <el-table-column label="商品信息" width="300">
        <template #default="scope">
          <div class="product-info">
            <el-image
              :src="scope.row.productImage || 'https://via.placeholder.com/60'"
              style="width: 60px; height: 60px"
              fit="cover"
            />
            <span>{{ scope.row.productName }}</span>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column prop="quantity" label="数量" width="80" />
      
      <el-table-column label="单价" width="120">
        <template #default="scope">
          <span class="price">¥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      
      <el-table-column label="总价" width="120">
        <template #default="scope">
          <span class="total-price">¥{{ scope.row.totalAmount }}</span>
        </template>
      </el-table-column>
      
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="创建时间" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      
      <el-table-column label="操作" fixed="right">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 0"
            type="primary"
            size="small"
            @click="handlePay(scope.row)"
          >
            支付
          </el-button>
          <el-button
            v-if="scope.row.status === 0"
            type="danger"
            size="small"
            @click="handleCancel(scope.row)"
          >
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-if="total > 0"
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadOrderList"
      @current-change="loadOrderList"
      class="pagination"
    />
    
    <el-empty v-if="orderList.length === 0" description="暂无订单" />
    
    <!-- 支付二维码对话框 -->
    <el-dialog
      v-model="showPaymentDialog"
      title="扫码支付"
      width="500px"
      :before-close="handleClosePaymentDialog"
      center
    >
      <div class="payment-dialog">
        <div class="payment-info">
          <p><strong>订单号：</strong>{{ paymentOrderNo }}</p>
          <p class="payment-amount"><strong>支付金额：</strong>¥{{ paymentAmount }}</p>
        </div>
        
        <div class="qrcode-container">
          <img :src="paymentQRCode" alt="支付二维码" class="qrcode-image" />
          <p class="qrcode-tip">请使用微信/支付宝扫码支付</p>
        </div>
        
        <div class="payment-tips">
          <p>💡 支付说明：</p>
          <p>1. 请使用微信或支付宝扫描上方二维码</p>
          <p>2. 支付成功后页面将自动更新</p>
          <p>3. 如长时间未到账，请联系客服</p>
        </div>
        
        <!-- 演示用：模拟支付按钮 -->
        <div class="demo-payment">
          <el-button type="success" @click="handleSimulatePay" size="large">
            🎯 模拟支付成功（演示用）
          </el-button>
          <p class="demo-tip">⚠️ 实际应用中不需要此按钮，扫码支付后自动完成</p>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="handleClosePaymentDialog">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, cancelOrder, payOrder, generatePaymentQRCode, checkPaymentStatus, simulatePaymentSuccess } from '@/api/order'

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const orderList = ref([])

// 二维码支付对话框相关
const showPaymentDialog = ref(false)
const paymentQRCode = ref('')
const paymentId = ref('')
const currentOrder = ref(null)
const paymentAmount = ref(0)
const paymentOrderNo = ref('')
let paymentCheckTimer = null

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'success'
  }
  return typeMap[status]
}

const getStatusText = (status) => {
  const textMap = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已完成'
  }
  return textMap[status]
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 开始检查支付状态
const startCheckPaymentStatus = () => {
  paymentCheckTimer = setInterval(async () => {
    try {
      const res = await checkPaymentStatus(paymentId.value)
      if (res.code === 200 && res.data.status === 'success') {
        // 支付成功
        clearInterval(paymentCheckTimer)
        showPaymentDialog.value = false
        ElMessage.success('支付成功！')
        loadOrderList()
      }
    } catch (error) {
      console.error('检查支付状态失败', error)
    }
  }, 2000) // 每2秒检查一次
}

// 停止检查支付状态
const stopCheckPaymentStatus = () => {
  if (paymentCheckTimer) {
    clearInterval(paymentCheckTimer)
    paymentCheckTimer = null
  }
}

// 处理支付
const handlePay = async (order) => {
  try {
    currentOrder.value = order
    
    // 生成支付二维码
    const res = await generatePaymentQRCode(order.id)
    if (res.code === 200) {
      paymentQRCode.value = res.data.qrcode
      paymentId.value = res.data.paymentId
      paymentAmount.value = res.data.amount
      paymentOrderNo.value = res.data.orderNo
      showPaymentDialog.value = true
      
      // 开始轮询检查支付状态
      startCheckPaymentStatus()
    } else {
      ElMessage.error(res.message || '生成支付二维码失败')
    }
  } catch (error) {
    console.error('生成支付二维码失败', error)
    ElMessage.error('生成支付二维码失败')
  }
}

// 模拟支付（用于演示）
const handleSimulatePay = async () => {
  try {
    const res = await simulatePaymentSuccess(paymentId.value)
    if (res.code === 200) {
      stopCheckPaymentStatus()
      showPaymentDialog.value = false
      ElMessage.success('支付成功！')
      loadOrderList()
    }
  } catch (error) {
    console.error('模拟支付失败', error)
  }
}

// 关闭支付对话框
const handleClosePaymentDialog = () => {
  stopCheckPaymentStatus()
  showPaymentDialog.value = false
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确认取消该订单？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await cancelOrder(order.id)
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      loadOrderList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败', error)
    }
  }
}

const loadOrderList = async () => {
  try {
    const res = await getOrderList({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    
    if (res.code === 200) {
      orderList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取订单列表失败', error)
  }
}

onMounted(() => {
  loadOrderList()
})

onUnmounted(() => {
  stopCheckPaymentStatus()
})
</script>

<style scoped>
.order {
  padding: 20px;
}

.page-title {
  margin-bottom: 30px;
  font-size: 32px;
  color: #333;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.price {
  color: #ff4d4f;
  font-weight: bold;
}

.total-price {
  color: #ff4d4f;
  font-size: 18px;
  font-weight: bold;
}

.pagination {
  margin-top: 30px;
  justify-content: center;
}

/* 支付对话框样式 */
.payment-dialog {
  text-align: center;
  padding: 20px;
}

.payment-info {
  margin-bottom: 20px;
  font-size: 16px;
}

.payment-amount {
  color: #ff4d4f;
  font-size: 24px;
  font-weight: bold;
  margin-top: 10px;
}

.qrcode-container {
  margin: 30px 0;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 10px;
}

.qrcode-image {
  width: 300px;
  height: 300px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  background: white;
  padding: 10px;
}

.qrcode-tip {
  margin-top: 15px;
  color: #666;
  font-size: 14px;
}

.payment-tips {
  text-align: left;
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 8px;
  padding: 15px;
  margin: 20px 0;
}

.payment-tips p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.payment-tips p:first-child {
  font-weight: bold;
  color: #fa8c16;
  margin-bottom: 10px;
}

.demo-payment {
  margin-top: 20px;
  padding: 15px;
  background: #f0f9ff;
  border: 1px dashed #1890ff;
  border-radius: 8px;
}

.demo-payment .el-button {
  width: 100%;
  margin-bottom: 10px;
}

.demo-tip {
  color: #999;
  font-size: 12px;
  margin: 0;
}
</style>

