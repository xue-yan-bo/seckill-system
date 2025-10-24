<template>
  <div class="home">
    <el-carousel height="400px" class="banner">
      <el-carousel-item v-for="item in 3" :key="item">
        <div class="banner-item" :style="{ backgroundColor: getColor(item) }">
          <h1>电商秒杀系统</h1>
          <p>高并发、分布式、限流、性能优化</p>
        </div>
      </el-carousel-item>
    </el-carousel>
    
    <div class="section">
      <h2 class="section-title">热门秒杀</h2>
      <div class="product-grid">
        <el-card v-for="item in seckillList.slice(0, 4)" :key="item.id" class="product-card" shadow="hover">
          <div class="product-image">
            <el-image :src="item.productImage || 'https://via.placeholder.com/200'" fit="cover" />
            <div class="seckill-tag">秒杀中</div>
          </div>
          <div class="product-info">
            <h3>{{ item.productName }}</h3>
            <div class="price">
              <span class="seckill-price">¥{{ item.seckillPrice }}</span>
              <span class="original-price">¥{{ item.originalPrice }}</span>
            </div>
            <el-button type="danger" @click="router.push('/seckill')" class="buy-btn">
              立即抢购
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
    
    <div class="section">
      <h2 class="section-title">推荐商品</h2>
      <div class="product-grid">
        <el-card v-for="item in productList.slice(0, 4)" :key="item.id" class="product-card" shadow="hover">
          <div class="product-image">
            <el-image :src="item.image || 'https://via.placeholder.com/200'" fit="cover" />
          </div>
          <div class="product-info">
            <h3>{{ item.name }}</h3>
            <div class="price">
              <span class="normal-price">¥{{ item.price }}</span>
            </div>
            <el-button type="primary" @click="router.push('/product')" class="buy-btn">
              查看详情
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSeckillList } from '@/api/seckill'
import { getProductList } from '@/api/product'

const router = useRouter()

const seckillList = ref([])
const productList = ref([])

const getColor = (index) => {
  const colors = ['#409eff', '#67c23a', '#e6a23c']
  return colors[index - 1]
}

onMounted(async () => {
  try {
    const seckillRes = await getSeckillList()
    if (seckillRes.code === 200) {
      seckillList.value = seckillRes.data || []
    }
  } catch (error) {
    console.error('获取秒杀列表失败', error)
  }
  
  try {
    const productRes = await getProductList({ pageNum: 1, pageSize: 4 })
    if (productRes.code === 200) {
      productList.value = productRes.data?.records || []
    }
  } catch (error) {
    console.error('获取商品列表失败', error)
  }
})
</script>

<style scoped>
.home {
  padding: 0;
}

.banner {
  margin: -20px -20px 40px;
}

.banner-item {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
}

.banner-item h1 {
  font-size: 48px;
  margin-bottom: 20px;
}

.banner-item p {
  font-size: 24px;
}

.section {
  margin-bottom: 60px;
}

.section-title {
  margin-bottom: 30px;
  font-size: 28px;
  color: #333;
  position: relative;
  padding-left: 15px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background-color: #409eff;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 15px;
}

.seckill-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  background-color: #ff4d4f;
  color: white;
  padding: 5px 15px;
  border-radius: 4px;
  font-size: 14px;
}

.product-info h3 {
  font-size: 16px;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price {
  margin-bottom: 15px;
}

.seckill-price {
  font-size: 24px;
  color: #ff4d4f;
  font-weight: bold;
  margin-right: 10px;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.normal-price {
  font-size: 20px;
  color: #409eff;
  font-weight: bold;
}

.buy-btn {
  width: 100%;
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>

