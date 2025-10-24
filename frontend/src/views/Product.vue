<template>
  <div class="product">
    <h1 class="page-title">商品列表</h1>
    
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索商品名称或描述"
        @keyup.enter="handleSearch"
        clearable
        style="max-width: 400px"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>
    </div>
    
    <div class="product-grid">
      <el-card v-for="item in productList" :key="item.id" class="product-card" shadow="hover">
        <div class="product-image">
          <el-image :src="item.image || 'https://via.placeholder.com/250'" fit="cover" />
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ item.name }}</h3>
          <p class="product-desc">{{ item.description }}</p>
          <div class="product-footer">
            <span class="price">¥{{ item.price }}</span>
            <div class="stats">
              <span>库存: {{ item.stock }}</span>
              <span>销量: {{ item.sales }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    
    <el-pagination
      v-if="total > 0"
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadProductList"
      @current-change="loadProductList"
      class="pagination"
    />
    
    <el-empty v-if="productList.length === 0" description="暂无商品" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getProductList } from '@/api/product'

const searchKeyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const productList = ref([])

const handleSearch = () => {
  pageNum.value = 1
  loadProductList()
}

const loadProductList = async () => {
  try {
    const res = await getProductList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    })
    
    if (res.code === 200) {
      productList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取商品列表失败', error)
  }
}

onMounted(() => {
  loadProductList()
})
</script>

<style scoped>
.product {
  max-width: 1400px !important;
  margin: 0 auto !important;
  padding: 20px !important;
}

.page-title {
  text-align: center !important;
  margin-bottom: 30px !important;
  font-size: 32px !important;
  color: #333 !important;
  font-weight: 600 !important;
}

.search-bar {
  margin-bottom: 30px !important;
  display: flex !important;
  justify-content: center !important;
}

.product-grid {
  display: grid !important;
  grid-template-columns: repeat(4, 1fr) !important;
  gap: 24px !important;
  margin-bottom: 40px !important;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr) !important;
  }
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
}

@media (max-width: 480px) {
  .product-grid {
    grid-template-columns: 1fr !important;
  }
}

.product-card {
  transition: all 0.3s ease !important;
  cursor: pointer !important;
  border-radius: 12px !important;
  overflow: hidden !important;
}

.product-card:hover {
  transform: translateY(-8px) !important;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15) !important;
}

.product-image {
  height: 240px !important;
  margin-bottom: 16px !important;
  border-radius: 8px !important;
  overflow: hidden !important;
  background: #f5f5f5 !important;
}

.product-info {
  display: flex !important;
  flex-direction: column !important;
  gap: 12px !important;
  padding: 0 8px !important;
}

.product-name {
  font-size: 16px !important;
  color: #333 !important;
  font-weight: 500 !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
  margin-bottom: 4px !important;
}

.product-desc {
  font-size: 13px !important;
  color: #666 !important;
  height: 38px !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  display: -webkit-box !important;
  -webkit-line-clamp: 2 !important;
  -webkit-box-orient: vertical !important;
  line-height: 1.5 !important;
}

.product-footer {
  display: flex !important;
  justify-content: space-between !important;
  align-items: center !important;
  padding-top: 8px !important;
  border-top: 1px solid #f0f0f0 !important;
}

.price {
  font-size: 22px !important;
  color: #ff4d4f !important;
  font-weight: bold !important;
}

.stats {
  display: flex !important;
  flex-direction: column !important;
  gap: 4px !important;
  font-size: 12px !important;
  color: #999 !important;
  text-align: right !important;
}

.pagination {
  justify-content: center !important;
  margin-top: 40px !important;
}
</style>

