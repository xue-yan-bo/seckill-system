<template>
  <div class="seckill">
    <h1 class="page-title">秒杀活动</h1>
    
    <div class="seckill-grid">
      <el-card v-for="item in seckillList" :key="item.id" class="seckill-card" shadow="hover">
        <div class="card-content">
          <div class="product-image" :style="{ background: getProductImage(item).startsWith('linear') ? getProductImage(item) : 'transparent' }">
            <el-image v-if="!getProductImage(item).startsWith('linear')" :src="getProductImage(item)" fit="cover">
              <template #error>
                <div class="image-placeholder">
                  <el-icon :size="60"><Picture /></el-icon>
                  <p>{{ item.productName }}</p>
                </div>
              </template>
            </el-image>
            <div v-else class="gradient-placeholder">
              <el-icon :size="80" color="white"><ShoppingBag /></el-icon>
              <p>{{ item.productName }}</p>
            </div>
            <div class="status-tag">进行中</div>
          </div>
          
          <div class="product-info">
            <h3 class="product-name">商品ID: {{ item.productId }}</h3>
            
            <div class="price-section">
              <div class="seckill-price">
                <span class="label">秒杀价</span>
                <span class="price">¥{{ item.seckillPrice }}</span>
              </div>
            </div>
            
            <div class="stock-section">
              <span>剩余库存：{{ item.seckillStock }}</span>
              <el-progress
                :percentage="getStockPercentage(item)"
                :color="getProgressColor(item)"
              />
            </div>
            
            <div class="time-section">
              <div class="time-item">
                <span class="label">开始时间</span>
                <span>{{ formatTime(item.startTime) }}</span>
              </div>
              <div class="time-item">
                <span class="label">结束时间</span>
                <span>{{ formatTime(item.endTime) }}</span>
              </div>
            </div>
            
            <el-button
              type="danger"
              size="large"
              @click="handleSeckill(item)"
              :loading="loadingMap[item.id]"
              :disabled="item.seckillStock <= 0"
              class="seckill-btn"
            >
              {{ item.seckillStock > 0 ? '立即抢购' : '已抢光' }}
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <el-empty v-if="seckillList.length === 0" description="暂无秒杀活动" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getSeckillList, doSeckill, checkUserSeckill } from '@/api/seckill'

const userStore = useUserStore()

const seckillList = ref([])
const loadingMap = reactive({})

const getProductImage = (item) => {
  if (item.productImage && item.productImage !== 'https://via.placeholder.com/300') {
    return item.productImage
  }
  // 使用渐变色背景作为备用方案
  const colors = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
  ]
  return colors[item.id % colors.length]
}

const getStockPercentage = (item) => {
  // 假设初始库存是当前库存的两倍（这里可以根据实际情况调整）
  return Math.min(100, (item.seckillStock / 100) * 100)
}

const getProgressColor = (item) => {
  const percentage = getStockPercentage(item)
  if (percentage < 20) return '#f56c6c'
  if (percentage < 50) return '#e6a23c'
  return '#67c23a'
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const handleSeckill = async (item) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    // 检查用户是否已参与
    const checkRes = await checkUserSeckill(item.id)
    if (checkRes.data) {
      ElMessage.warning('您已参与过该秒杀活动')
      return
    }
    
    // 确认秒杀
    await ElMessageBox.confirm('确认参与秒杀活动？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    loadingMap[item.id] = true
    
    const res = await doSeckill(item.id)
    if (res.code === 200) {
      ElMessage.success('秒杀成功！订单创建中...')
      // 刷新列表
      loadSeckillList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('秒杀失败', error)
    }
  } finally {
    loadingMap[item.id] = false
  }
}

const loadSeckillList = async () => {
  try {
    const res = await getSeckillList()
    if (res.code === 200) {
      seckillList.value = res.data || []
    }
  } catch (error) {
    console.error('获取秒杀列表失败', error)
  }
}

onMounted(() => {
  loadSeckillList()
})
</script>

<style scoped>
.seckill {
  padding: 20px;
}

.page-title {
  margin-bottom: 30px;
  font-size: 32px;
  color: #333;
}

.seckill-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.seckill-card {
  transition: transform 0.3s;
}

.seckill-card:hover {
  transform: translateY(-5px);
}

.card-content {
  display: flex;
  flex-direction: column;
}

.product-image {
  position: relative;
  height: 300px;
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-placeholder, .gradient-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  color: #999;
}

.gradient-placeholder {
  color: white;
  text-align: center;
}

.gradient-placeholder p {
  margin-top: 20px;
  font-size: 18px;
  font-weight: bold;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.status-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #ff4d4f;
  color: white;
  padding: 5px 15px;
  border-radius: 4px;
  font-weight: bold;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 18px;
  margin-bottom: 15px;
  color: #333;
}

.price-section {
  margin-bottom: 20px;
}

.seckill-price {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.seckill-price .label {
  font-size: 14px;
  color: #666;
}

.seckill-price .price {
  font-size: 32px;
  color: #ff4d4f;
  font-weight: bold;
}

.stock-section {
  margin-bottom: 20px;
}

.stock-section > span {
  display: block;
  margin-bottom: 10px;
  color: #666;
}

.time-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.time-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.time-item:last-child {
  margin-bottom: 0;
}

.time-item .label {
  color: #666;
}

.seckill-btn {
  width: 100%;
  font-size: 18px;
  height: 50px;
}
</style>

